package com.heraim.zelix.stores.service;

import com.heraim.zelix.category.service.CategoryService;
import com.heraim.zelix.common.dto.PagedResponse;
import com.heraim.zelix.common.exception.ResourceNotFoundException;
import com.heraim.zelix.common.exception.UnauthorizedAccessException;
import com.heraim.zelix.stores.dto.CreateStoreRequest;
import com.heraim.zelix.stores.dto.StoreResponse;
import com.heraim.zelix.stores.dto.UpdateStoreRequest;
import com.heraim.zelix.stores.entity.Store;
import com.heraim.zelix.stores.repository.StoreRepository;
import com.heraim.zelix.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CategoryService categoryService;

    public StoreResponse create(CreateStoreRequest request, User owner) {
        String slug = generateSlug(request.name());
        String uniqueSlug = generateUniqueSlug(slug);

        Store store = Store.builder()
                .owner(owner)
                .name(request.name())
                .slug(uniqueSlug)
                .description(request.description())
                .phone(request.phone())
                .whatsapp(request.whatsapp())
                .email(request.email())
                .address(request.address())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .deliveryOptions(request.deliveryOptions())
                .acceptedPaymentMethods(request.acceptedPaymentMethods())
                .category(categoryService.getCategoryEntityById(request.categoryId()))
                .build();

        Store savedStore = storeRepository.save(store);
        return StoreResponse.from(savedStore);
    }

    public StoreResponse getById(UUID id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));
        return StoreResponse.from(store);
    }

    public StoreResponse getBySlug(String slug) {
        Store store = storeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with slug: " + slug));
        return StoreResponse.from(store);
    }

    public StoreResponse update(UUID id, UpdateStoreRequest request, User user) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + id));

        if (!store.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not the owner of this store");
        }

        if (request.name() != null) {
            store.setName(request.name());
        }
        if (request.description() != null) {
            store.setDescription(request.description());
        }
        if (request.phone() != null) {
            store.setPhone(request.phone());
        }
        if (request.whatsapp() != null) {
            store.setWhatsapp(request.whatsapp());
        }
        if (request.email() != null) {
            store.setEmail(request.email());
        }
        if (request.address() != null) {
            store.setAddress(request.address());
        }
        if (request.city() != null) {
            store.setCity(request.city());
        }
        if (request.state() != null) {
            store.setState(request.state());
        }
        if (request.country() != null) {
            store.setCountry(request.country());
        }
        if (request.latitude() != null) {
            store.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            store.setLongitude(request.longitude());
        }
        if (request.deliveryOptions() != null) {
            store.setDeliveryOptions(request.deliveryOptions());
        }
        if (request.acceptedPaymentMethods() != null) {
            store.setAcceptedPaymentMethods(request.acceptedPaymentMethods());
        }
        if (request.categoryId() != null) {
            store.setCategory(categoryService.getCategoryEntityById(request.categoryId()));
        }

        Store savedStore = storeRepository.save(store);
        return StoreResponse.from(savedStore);
    }

    public PagedResponse<StoreResponse> search(String q, String city, String state, Pageable pageable) {
        Page<Store> storePage = storeRepository.search(q, city, state, pageable);
        List<StoreResponse> data = storePage.getContent().stream()
                .map(StoreResponse::from)
                .toList();

        return new PagedResponse<>(
                data,
                new PagedResponse.PaginationMetadata(
                        storePage.getNumber() + 1,
                        storePage.getSize(),
                        storePage.getTotalElements(),
                        storePage.getTotalPages()
                )
        );
    }

    private String generateSlug(String name) {
        if (name == null) {
            return "";
        }
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase(Locale.ENGLISH);
    }

    private String generateUniqueSlug(String baseSlug) {
        String candidate = baseSlug;
        int suffix = 2;

        while (storeRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + suffix;
            suffix++;
        }

        return candidate;
    }
}