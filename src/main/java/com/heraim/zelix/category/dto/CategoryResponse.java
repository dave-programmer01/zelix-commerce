package com.heraim.zelix.category.dto;


import com.heraim.zelix.category.entity.Category;
import com.heraim.zelix.category.entity.CategoryType;

import java.util.UUID;

public record CategoryResponse (
        UUID id,
        String name,
        CategoryType categoryType,
        UUID parentId
){
    public static CategoryResponse from(Category category) {

        UUID parentId = category.getParent() != null ? category.getParent().getId() : null;

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getCategoryType(),
                parentId
        );
    }
}