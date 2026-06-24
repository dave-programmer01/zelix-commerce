package com.heraim.zelix.stores.dto;

import com.heraim.zelix.stores.entity.DeliveryOption;
import com.heraim.zelix.stores.entity.PaymentMethod;
import com.heraim.zelix.stores.repository.StoreRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public record CreateStoreRequest(
        @NotBlank
        String name,
        String description,
        @NotBlank
        String phone,
        String whatsapp,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String address,
        @NotBlank
        String city,
        @NotBlank
        String state,
        @NotBlank
        String country,
        @NotNull
        BigDecimal longitude,
        @NotNull
        BigDecimal latitude,
        @NotNull
        Set<DeliveryOption> deliveryOptions,
        @NotNull
        UUID categoryId,
        @NotNull
        Set<PaymentMethod> acceptedPaymentMethods
) {

}
