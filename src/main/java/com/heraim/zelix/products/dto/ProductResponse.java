package com.heraim.zelix.products.dto;

import com.heraim.zelix.category.dto.CategoryResponse;
import com.heraim.zelix.products.entity.Product;
import com.heraim.zelix.products.entity.ProductAvailabilityStatus;
import com.heraim.zelix.stores.dto.StoreSummary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        StoreSummary store,
        CategoryResponse category,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        ProductAvailabilityStatus availabilityStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                StoreSummary.from(product.getStore()),
                CategoryResponse.from(product.getCategory()),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getAvailabilityStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
