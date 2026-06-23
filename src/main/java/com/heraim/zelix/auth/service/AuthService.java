package com.heraim.zelix.auth.service;

import com.heraim.zelix.auth.dto.*;
import com.heraim.zelix.common.exception.DuplicateResourceException;
import com.heraim.zelix.common.exception.InvalidCredentialsException;
import com.heraim.zelix.common.exception.ResourceNotFoundException;
import com.heraim.zelix.common.security.JwtService;
import com.heraim.zelix.users.dto.UserResponse;
import com.heraim.zelix.users.entity.Roles;
import com.heraim.zelix.users.entity.User;
import com.heraim.zelix.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        if (request.role() == Roles.ADMIN) {
            throw new RuntimeException("You are not allowed to perform this action");
        }



        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(request.role())
                .build();
        User savedUser = userRepository.save(user);
        return getAuthResponse(savedUser);

    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> user= userRepository.findByEmail(request.emailOrUsername())
                .or(() -> userRepository.findByUsername(request.emailOrUsername()));

        User foundUser = user.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), foundUser.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return getAuthResponse(foundUser);


    }

    private AuthResponse getAuthResponse(User foundUser) {
        UserResponse userResponse = UserResponse.from(foundUser);

        String accessToken = jwtService.generateAccessToken(foundUser);
        String refreshToken = jwtService.generateRefreshToken(foundUser);

        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    public RefreshResponse refresh(RefreshToken request) {
        String username = jwtService.extractUsername(request.refreshToken());
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        return new RefreshResponse(jwtService.generateAccessToken(user));
    }
}