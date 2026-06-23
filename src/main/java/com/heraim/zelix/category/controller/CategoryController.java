package com.heraim.zelix.category.controller;

import com.heraim.zelix.category.dto.CategoryResponse;
import com.heraim.zelix.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getTopLevel() {
        // call getTopLevelCategories(), wrap in ResponseEntity.ok(...)
        return ResponseEntity.ok(categoryService.getTopLevelCategories());
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<CategoryResponse>> getChildren(@PathVariable UUID id) {
        // call getChildren(id), wrap in ResponseEntity.ok(...)
        return ResponseEntity.ok(categoryService.getChildren(id));
    }
}
