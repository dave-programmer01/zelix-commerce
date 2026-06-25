package com.heraim.zelix.stores.controller;
 
import com.heraim.zelix.common.dto.PagedResponse;
import com.heraim.zelix.followers.service.StoreFollowerService;
import com.heraim.zelix.stores.dto.CreateStoreRequest;
import com.heraim.zelix.stores.dto.StoreResponse;
import com.heraim.zelix.stores.dto.UpdateStoreRequest;
import com.heraim.zelix.stores.service.StoreService;
import com.heraim.zelix.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final StoreFollowerService storeFollowerService;

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<StoreResponse> create(
            @Valid @RequestBody CreateStoreRequest request,
            @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(storeService.create(request, user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(storeService.getById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<StoreResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(storeService.getBySlug(slug));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StoreResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateStoreRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(storeService.update(id, request, user));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<StoreResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            Pageable pageable,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(storeService.search(q, city, state, pageable, user));
    }

    @PostMapping("/{id}/follow")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Void> follow(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        storeFollowerService.follow(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/follow")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Void> unfollow(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        storeFollowerService.unfollow(id, user);
        return ResponseEntity.noContent().build();
    }
}
