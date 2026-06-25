package com.heraim.zelix.products.controller;

import com.heraim.zelix.common.dto.PagedResponse;
import com.heraim.zelix.products.dto.CreateProductRequest;
import com.heraim.zelix.products.dto.ProductResponse;
import com.heraim.zelix.products.dto.UpdateProductRequest;
import com.heraim.zelix.products.service.ProductService;
import com.heraim.zelix.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('VENDOR')")
    public ProductResponse create(
            @Valid @RequestBody CreateProductRequest request,
            @AuthenticationPrincipal User owner
    ) {
        return productService.createProduct(request, owner);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id) {
        return productService.getProductResponseById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ProductResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request,
            @AuthenticationPrincipal User owner
    ) {
        return productService.updateProduct(id, request, owner);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('VENDOR')")
    public void delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User owner
    ) {
        productService.deleteProduct(id, owner);
    }

    @GetMapping("/search")
    public PagedResponse<ProductResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) UUID category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            Pageable pageable,
            @AuthenticationPrincipal User user
    ) {
        return productService.search(q, category, city, state, lat, lon, pageable, user);
    }
}