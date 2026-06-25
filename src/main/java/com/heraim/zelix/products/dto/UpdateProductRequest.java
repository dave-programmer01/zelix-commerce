package com.heraim.zelix.products.dto;

import com.heraim.zelix.products.entity.ProductAvailabilityStatus;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        UUID categoryId,
        String name,
        String description,
        @Positive BigDecimal price,
        @Positive Integer quantity,
        ProductAvailabilityStatus availabilityStatus
) {}
