package com.heraim.zelix.category.service;

import com.heraim.zelix.category.dto.CategoryResponse;
import com.heraim.zelix.category.entity.Category;
import com.heraim.zelix.category.repository.CategoryRepository;
import com.heraim.zelix.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getTopLevelCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream()
                .map(CategoryResponse::from)
                .toList();

    }

    public List<CategoryResponse> getChildren(UUID parentId) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + parentId));

        List<Category> children = categoryRepository.findByParent(parent);

        return children.stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public Category getCategoryEntityById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}