package com.heraim.zelix.users.controller;

import com.heraim.zelix.followers.service.StoreFollowerService;
import com.heraim.zelix.stores.dto.StoreSummary;
import com.heraim.zelix.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final StoreFollowerService storeFollowerService;

    @GetMapping("/me/following")
    public ResponseEntity<List<StoreSummary>> getFollowing(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(storeFollowerService.getFollowing(user));
    }
}
