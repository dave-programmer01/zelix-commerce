package com.heraim.zelix.stores.dto;

import com.heraim.zelix.stores.entity.DeliveryOption;
import com.heraim.zelix.stores.entity.PaymentMethod;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateStoreRequest(
        String name,
        String description,
        String phone,
        String whatsapp,
        String email,
        String address,
        String city,
        String state,
        String country,
        BigDecimal longitude,
        BigDecimal latitude,
        Set<DeliveryOption> deliveryOptions,
        UUID categoryId,
        Set<PaymentMethod> acceptedPaymentMethods
) {
}
