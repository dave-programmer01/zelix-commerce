package com.heraim.zelix.stores.dto;

import com.heraim.zelix.category.dto.CategoryResponse;
import com.heraim.zelix.stores.entity.DeliveryOption;
import com.heraim.zelix.stores.entity.PaymentMethod;
import com.heraim.zelix.stores.entity.Store;
import com.heraim.zelix.stores.entity.VerificationTier;
import com.heraim.zelix.users.dto.UserResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record StoreResponse(
        UUID id,
        UserResponse owner,
        String name,
        String slug,
        String description,
        String phone,
        String whatsapp,
        String email,
        String address,
        String city,
        String state,
        String country,
        BigDecimal latitude,
        BigDecimal longitude,
        String logoUrl,
        String bannerUrl,
        int followerCount,
        int productCount,
        boolean isActive,
        CategoryResponse category,
        Set<DeliveryOption> deliveryOptions,
        Set<PaymentMethod> acceptedPaymentMethods,
        VerificationTier verificationTier,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                UserResponse.from(store.getOwner()),
                store.getName(),
                store.getSlug(),
                store.getDescription(),
                store.getPhone(),
                store.getWhatsapp(),
                store.getEmail(),
                store.getAddress(),
                store.getCity(),
                store.getState(),
                store.getCountry(),
                store.getLatitude(),
                store.getLongitude(),
                store.getLogoUrl(),
                store.getBannerUrl(),
                store.getFollowerCount(),
                store.getProductCount(),
                store.isActive(),
                CategoryResponse.from(store.getCategory()),
                store.getDeliveryOptions(),
                store.getAcceptedPaymentMethods(),
                store.getVerificationTier(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }
}
