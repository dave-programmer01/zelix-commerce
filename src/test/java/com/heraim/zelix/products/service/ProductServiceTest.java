package com.heraim.zelix.products.service;

import com.heraim.zelix.analytics.service.SearchLogService;
import com.heraim.zelix.category.entity.Category;
import com.heraim.zelix.category.entity.CategoryType;
import com.heraim.zelix.category.service.CategoryService;
import com.heraim.zelix.common.exception.ResourceNotFoundException;
import com.heraim.zelix.common.exception.UnauthorizedAccessException;
import com.heraim.zelix.products.dto.CreateProductRequest;
import com.heraim.zelix.products.dto.UpdateProductRequest;
import com.heraim.zelix.products.entity.Product;
import com.heraim.zelix.products.entity.ProductAvailabilityStatus;
import com.heraim.zelix.products.repository.ProductRepository;
import com.heraim.zelix.stores.entity.Store;
import com.heraim.zelix.stores.service.StoreService;
import com.heraim.zelix.users.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SearchLogService searchLogService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Store store;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(UUID.randomUUID())
                .name("Electronics")
                .categoryType(CategoryType.PHYSICAL)
                .build();

        store = Store.builder()
                .trustScore(80)
                .latitude(new BigDecimal("6.5244"))
                .longitude(new BigDecimal("3.3792"))
                .build();

        product = Product.builder()
                .name("Zelix Phone")
                .description("Latest Zelix phone with amazing features")
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE)
                .store(store)
                .category(category)
                .build();
    }

    @Test
    void calculateRelevanceScore_NameMatch_ReturnsMax() throws Exception {
        double score = invokeCalculateRelevanceScore(product, "phone");
        assertEquals(100.0, score);
    }

    @Test
    void calculateRelevanceScore_DescriptionMatch_ReturnsHalf() throws Exception {
        double score = invokeCalculateRelevanceScore(product, "amazing");
        assertEquals(50.0, score);
    }

    @Test
    void calculateRelevanceScore_NoMatch_ReturnsZero() throws Exception {
        double score = invokeCalculateRelevanceScore(product, "samsung");
        assertEquals(0.0, score);
    }

    @Test
    void calculateRelevanceScore_NullQuery_ReturnsMax() throws Exception {
        double score = invokeCalculateRelevanceScore(product, null);
        assertEquals(100.0, score);
    }

    @Test
    void calculateAvailabilityScore_Available_ReturnsMax() throws Exception {
        product.setAvailabilityStatus(ProductAvailabilityStatus.AVAILABLE);
        double score = invokeCalculateAvailabilityScore(product);
        assertEquals(100.0, score);
    }

    @Test
    void calculateAvailabilityScore_LowStock_ReturnsHalf() throws Exception {
        product.setAvailabilityStatus(ProductAvailabilityStatus.LOW_STOCK);
        double score = invokeCalculateAvailabilityScore(product);
        assertEquals(50.0, score);
    }

    @Test
    void calculateAvailabilityScore_MadeToOrder_ReturnsSeventy() throws Exception {
        product.setAvailabilityStatus(ProductAvailabilityStatus.MADE_TO_ORDER);
        double score = invokeCalculateAvailabilityScore(product);
        assertEquals(70.0, score);
    }

    @Test
    void createProduct_SavesAndChecksActivation() {
        UUID storeId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User owner = User.builder().id(userId).build();
        store.setId(storeId);
        store.setOwner(owner);
        category.setId(categoryId);

        CreateProductRequest request = new CreateProductRequest(
                storeId,
                categoryId,
                "Zelix Phone",
                "Description",
                new BigDecimal("99.99"),
                10,
                null
        );

        when(storeService.getStoreEntityById(storeId)).thenReturn(store);
        when(categoryService.getCategoryEntityById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productRepository.countByStoreAndIsDeletedFalse(store)).thenReturn(3L);

        productService.createProduct(request, owner);

        verify(productRepository).save(any(Product.class));
        verify(storeService).activateIfEligible(storeId);
    }

    @Test
    void createProduct_DerivesStatusCorrectly() {
        UUID storeId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User owner = User.builder().id(userId).build();
        store.setId(storeId);
        store.setOwner(owner);

        when(storeService.getStoreEntityById(storeId)).thenReturn(store);
        when(categoryService.getCategoryEntityById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        // 1. Available (> 5)
        CreateProductRequest req1 = new CreateProductRequest(storeId, categoryId, "P1", null, BigDecimal.TEN, 10, null);
        assertEquals(ProductAvailabilityStatus.AVAILABLE, productService.createProduct(req1, owner).availabilityStatus());

        // 2. Low Stock (1-5)
        CreateProductRequest req2 = new CreateProductRequest(storeId, categoryId, "P2", null, BigDecimal.TEN, 5, null);
        assertEquals(ProductAvailabilityStatus.LOW_STOCK, productService.createProduct(req2, owner).availabilityStatus());

        // 3. Out of Stock (0)
        CreateProductRequest req3 = new CreateProductRequest(storeId, categoryId, "P3", null, BigDecimal.TEN, 0, null);
        assertEquals(ProductAvailabilityStatus.OUT_OF_STOCK, productService.createProduct(req3, owner).availabilityStatus());

        // 4. Made to order (quantity should be forced to null even if provided)
        CreateProductRequest req4 = new CreateProductRequest(storeId, categoryId, "P4", null, BigDecimal.TEN, 10, ProductAvailabilityStatus.MADE_TO_ORDER);
        var resp4 = productService.createProduct(req4, owner);
        assertEquals(ProductAvailabilityStatus.MADE_TO_ORDER, resp4.availabilityStatus());
        assertNull(resp4.quantity());
    }

    @Test
    void updateProduct_DerivesStatusOnQuantityChange() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User owner = User.builder().id(userId).build();
        store.setOwner(owner);
        product.setId(id);
        product.setStore(store);
        product.setDeleted(false);
        product.setQuantity(10);
        product.setAvailabilityStatus(ProductAvailabilityStatus.AVAILABLE);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Update quantity to 3 -> should become LOW_STOCK
        UpdateProductRequest req = new UpdateProductRequest(null, null, null, null, 3, null);
        var response = productService.updateProduct(id, req, owner);

        assertEquals(3, response.quantity());
        assertEquals(ProductAvailabilityStatus.LOW_STOCK, response.availabilityStatus());

        // Update to MADE_TO_ORDER -> quantity should become null
        UpdateProductRequest req2 = new UpdateProductRequest(null, null, null, null, 15, ProductAvailabilityStatus.MADE_TO_ORDER);
        var response2 = productService.updateProduct(id, req2, owner);
        assertEquals(ProductAvailabilityStatus.MADE_TO_ORDER, response2.availabilityStatus());
        assertNull(response2.quantity());
    }

    @Test
    void createProduct_UnauthorizedUser_ThrowsException() {
        UUID storeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        
        User owner = User.builder().id(userId).build();
        User otherUser = User.builder().id(otherUserId).build();
        
        store.setId(storeId);
        store.setOwner(owner);

        CreateProductRequest request = new CreateProductRequest(
                storeId,
                UUID.randomUUID(),
                "Zelix Phone",
                null,
                new BigDecimal("99.99"),
                10,
                null
        );

        when(storeService.getStoreEntityById(storeId)).thenReturn(store);

        org.junit.jupiter.api.Assertions.assertThrows(UnauthorizedAccessException.class, () -> {
            productService.createProduct(request, otherUser);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductResponseById_ExistingProduct_ReturnsResponse() {
        UUID id = UUID.randomUUID();
        product.setId(id);
        product.setDeleted(false);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        var response = productService.getProductResponseById(id);

        assertNotNull(response);
        assertEquals(product.getName(), response.name());
    }

    @Test
    void getProductResponseById_DeletedProduct_ThrowsException() {
        UUID id = UUID.randomUUID();
        product.setId(id);
        product.setDeleted(true);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductResponseById(id));
    }

    @Test
    void updateProduct_SuccessfulUpdate_ReturnsResponse() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User owner = User.builder().id(userId).build();
        store.setOwner(owner);
        product.setId(id);
        product.setStore(store);
        product.setDeleted(false);

        UpdateProductRequest request = new UpdateProductRequest(
                null, "Updated Name", null, null, null, null
        );

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        var response = productService.updateProduct(id, request, owner);

        assertEquals("Updated Name", response.name());
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_SoftDeletes_SavesProduct() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User owner = User.builder().id(userId).build();
        store.setOwner(owner);
        product.setId(id);
        product.setStore(store);
        product.setDeleted(false);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        productService.deleteProduct(id, owner);

        assertTrue(product.isDeleted());
        verify(productRepository).save(product);
    }

    @Test
    void calculateProximityScore_Close_ReturnsHigh() throws Exception {
        // Same location
        double score = invokeCalculateProximityScore(product, 6.5244, 3.3792);
        assertEquals(100.0, score);
    }

    @Test
    void calculateProximityScore_Far_ReturnsLow() throws Exception {
        // Far away (roughly London)
        double score = invokeCalculateProximityScore(product, 51.5074, -0.1278);
        assertTrue(score < 1.0);
    }

    @Test
    void calculateScore_WeightsCorrectly() throws Exception {
        // Relevance (phone) = 100 * 0.5 = 50
        // Proximity (same loc) = 100 * 0.25 = 25
        // Availability (AVAILABLE) = 100 * 0.15 = 15
        // Trust (80) = 80 * 0.10 = 8
        // Total = 50 + 25 + 15 + 8 = 98
        
        double score = invokeCalculateScore(product, "phone", 6.5244, 3.3792);
        assertEquals(98.0, score, 0.001);
    }

    @Test
    void search_FiltersAndScoresCorrectly() {
        String q = "test";
        UUID category = UUID.randomUUID();
        String city = "Lagos";
        String state = "Lagos";
        Double lat = 6.5244;
        Double lon = 3.3792;
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();

        when(productRepository.search(eq(q), eq(category), eq(city), eq(state), any(), any()))
                .thenReturn(new PageImpl<>(List.of(product)));

        var response = productService.search(q, category, city, state, lat, lon, pageable, user);

        assertNotNull(response);
        assertEquals(1, response.data().size());
        verify(productRepository).search(eq(q), eq(category), eq(city), eq(state), eq(ProductAvailabilityStatus.OUT_OF_STOCK), any());
        verify(searchLogService).logSearch(eq(q), eq(lat), eq(lon), eq(1), eq(user));
    }

    private double invokeCalculateRelevanceScore(Product p, String q) throws Exception {
        Method method = ProductService.class.getDeclaredMethod("calculateRelevanceScore", Product.class, String.class);
        method.setAccessible(true);
        return (double) method.invoke(productService, p, q);
    }

    private double invokeCalculateAvailabilityScore(Product p) throws Exception {
        Method method = ProductService.class.getDeclaredMethod("calculateAvailabilityScore", Product.class);
        method.setAccessible(true);
        return (double) method.invoke(productService, p);
    }

    private double invokeCalculateProximityScore(Product p, Double lat, Double lon) throws Exception {
        Method method = ProductService.class.getDeclaredMethod("calculateProximityScore", Product.class, Double.class, Double.class);
        method.setAccessible(true);
        return (double) method.invoke(productService, p, lat, lon);
    }

    private double invokeCalculateScore(Product p, String q, Double lat, Double lon) throws Exception {
        Method method = ProductService.class.getDeclaredMethod("calculateScore", Product.class, String.class, Double.class, Double.class);
        method.setAccessible(true);
        return (double) method.invoke(productService, p, q, lat, lon);
    }
}
