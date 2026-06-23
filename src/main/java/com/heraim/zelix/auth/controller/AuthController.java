package com.heraim.zelix.auth.controller;

import com.heraim.zelix.auth.dto.*;
import com.heraim.zelix.auth.service.AuthService;
import com.heraim.zelix.users.dto.UserResponse;
import com.heraim.zelix.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(authService.register(registerRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@Valid @RequestBody RefreshToken refreshToken){
        return new ResponseEntity<>(authService.refresh(refreshToken), HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(UserResponse.from(user), HttpStatus.OK);
    }
}
