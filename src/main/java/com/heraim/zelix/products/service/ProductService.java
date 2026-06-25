package com.heraim.zelix.products.service;

import com.heraim.zelix.analytics.service.SearchLogService;
import com.heraim.zelix.category.service.CategoryService;
import com.heraim.zelix.common.dto.PagedResponse;
import com.heraim.zelix.common.exception.ResourceNotFoundException;
import com.heraim.zelix.common.exception.UnauthorizedAccessException;
import com.heraim.zelix.products.dto.CreateProductRequest;
import com.heraim.zelix.products.dto.UpdateProductRequest;
import com.heraim.zelix.users.entity.User;
import com.heraim.zelix.products.dto.ProductResponse;
import com.heraim.zelix.products.entity.Product;
import com.heraim.zelix.products.entity.ProductAvailabilityStatus;
import com.heraim.zelix.products.repository.ProductRepository;
import com.heraim.zelix.stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final SearchLogService searchLogService;

    public PagedResponse<ProductResponse> search(
            String q,
            UUID categoryId,
            String city,
            String state,
            Double lat,
            Double lon,
            Pageable pageable,
            User user
    ) {
        // 1. Fetch filtered candidates from DB (those that match the query and are not deleted/out-of-stock)
        Page<Product> productPage = productRepository.search(q, categoryId, city, state, ProductAvailabilityStatus.OUT_OF_STOCK, Pageable.unpaged());

        // 2. Score and Sort in memory
        List<Product> allProducts = productPage.getContent();
        List<Product> sortedProducts = allProducts.stream()
                .sorted(Comparator.comparingDouble((Product p) -> calculateScore(p, q, lat, lon)).reversed())
                .toList();

        // 3. Manual Pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedProducts.size());
        
        List<Product> pagedList = List.of();
        if (start < sortedProducts.size()) {
            pagedList = sortedProducts.subList(start, end);
        }

        Page<Product> resultPage = new PageImpl<>(pagedList, pageable, sortedProducts.size());

        List<ProductResponse> data = resultPage.getContent().stream()
                .map(ProductResponse::from)
                .toList();

        searchLogService.logSearch(q, lat, lon, sortedProducts.size(), user);

        return new PagedResponse<>(
                data,
                new PagedResponse.PaginationMetadata(
                        resultPage.getNumber() + 1,
                        resultPage.getSize(),
                        resultPage.getTotalElements(),
                        resultPage.getTotalPages()
                )
        );
    }

    private double calculateScore(Product p, String q, Double lat, Double lon) {
        double relevanceWeight = 0.5;
        double proximityWeight = 0.25;
        double availabilityWeight = 0.15;
        double trustWeight = 0.10;

        return (relevanceWeight * calculateRelevanceScore(p, q)) +
               (proximityWeight * calculateProximityScore(p, lat, lon)) +
               (availabilityWeight * calculateAvailabilityScore(p)) +
               (trustWeight * calculateTrustScore(p));
    }

    private double calculateRelevanceScore(Product p, String q) {
        if (q == null || q.isBlank()) {
            return 100.0; // Max score if no search term
        }
        String query = q.toLowerCase();
        String name = p.getName().toLowerCase();
        String description = p.getDescription() != null ? p.getDescription().toLowerCase() : "";

        if (name.contains(query)) {
            return 100.0; // Direct name match
        } else if (description.contains(query)) {
            return 50.0; // Description match
        }
        return 0.0;
    }

    private double calculateProximityScore(Product p, Double lat, Double lon) {
        if (lat == null || lon == null || p.getStore().getLatitude() == null || p.getStore().getLongitude() == null) {
            return 0.0;
        }

        double distance = haversine(lat, lon, 
                p.getStore().getLatitude().doubleValue(), 
                p.getStore().getLongitude().doubleValue());
        
        // Normalize: closer is better. 100 for 0km, decreases as distance increases.
        return 100.0 / (1.0 + distance);
    }

    private double calculateAvailabilityScore(Product p) {
        if (p.getAvailabilityStatus() == ProductAvailabilityStatus.AVAILABLE) {
            return 100.0;
        } else if (p.getAvailabilityStatus() == ProductAvailabilityStatus.LOW_STOCK) {
            return 50.0;
        } else if (p.getAvailabilityStatus() == ProductAvailabilityStatus.MADE_TO_ORDER) {
            return 70.0;
        }
        return 0.0;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request, User owner) {
        var store = storeService.getStoreEntityById(request.storeId());

        if (!store.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedAccessException("You do not own this store");
        }

        var category = categoryService.getCategoryEntityById(request.categoryId());

        ProductAvailabilityStatus derivedStatus = deriveAvailabilityStatus(request.quantity(), request.availabilityStatus());
        Integer finalQuantity = (derivedStatus == ProductAvailabilityStatus.MADE_TO_ORDER) ? null : request.quantity();

        Product product = Product.builder()
                .store(store)
                .category(category)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .quantity(finalQuantity)
                .availabilityStatus(derivedStatus)
                .isDeleted(false)
                .build();

        Product savedProduct = productRepository.save(product);

        long productCount = productRepository.countByStoreAndIsDeletedFalse(savedProduct.getStore());
        if (productCount >= 3) {
            storeService.activateIfEligible(savedProduct.getStore().getId());
        }

        return ProductResponse.from(savedProduct);
    }

    public ProductResponse getProductResponseById(UUID id) {
        return ProductResponse.from(getProductEntityById(id));
    }

    public Product getProductEntityById(UUID id) {
        return productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request, User owner) {
        Product product = getProductEntityById(id);

        if (!product.getStore().getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedAccessException("You do not own the store for this product");
        }

        if (request.categoryId() != null) {
            product.setCategory(categoryService.getCategoryEntityById(request.categoryId()));
        }
        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }

        // Handle quantity and status derivation
        if (request.quantity() != null || request.availabilityStatus() != null) {
            Integer newQuantity = request.quantity() != null ? request.quantity() : product.getQuantity();
            ProductAvailabilityStatus statusSignal = request.availabilityStatus() != null
                    ? request.availabilityStatus()
                    : product.getAvailabilityStatus();

            ProductAvailabilityStatus derivedStatus = deriveAvailabilityStatus(newQuantity, statusSignal);
            Integer finalQuantity = (derivedStatus == ProductAvailabilityStatus.MADE_TO_ORDER) ? null : newQuantity;

            product.setQuantity(finalQuantity);
            product.setAvailabilityStatus(derivedStatus);
        }

        return ProductResponse.from(productRepository.save(product));
    }

    private ProductAvailabilityStatus deriveAvailabilityStatus(Integer quantity, ProductAvailabilityStatus signal) {
        if (signal == ProductAvailabilityStatus.MADE_TO_ORDER) {
            return ProductAvailabilityStatus.MADE_TO_ORDER;
        }
        if (quantity == null || quantity == 0) {
            return ProductAvailabilityStatus.OUT_OF_STOCK;
        }
        if (quantity <= 5) {
            return ProductAvailabilityStatus.LOW_STOCK;
        }
        return ProductAvailabilityStatus.AVAILABLE;
    }

    @Transactional
    public void deleteProduct(UUID id, User owner) {
        Product product = getProductEntityById(id);

        if (!product.getStore().getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedAccessException("You do not own the store for this product");
        }

        product.setDeleted(true);
        productRepository.save(product);
    }

    private double calculateTrustScore(Product p) {
        // trustScore is already 0-100 (default 50)
        return p.getStore().getTrustScore();
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
