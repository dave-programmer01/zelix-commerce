package com.heraim.zelix.products.dto;

import com.heraim.zelix.products.entity.ProductAvailabilityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotNull UUID storeId,
        @NotNull UUID categoryId,
        @NotBlank String name,
        String description,
        @NotNull @Positive BigDecimal price,
        @Positive Integer quantity,
        ProductAvailabilityStatus availabilityStatus
) {}