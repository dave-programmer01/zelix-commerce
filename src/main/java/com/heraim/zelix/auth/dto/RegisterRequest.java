package com.heraim.zelix.auth.dto;

import com.heraim.zelix.users.entity.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String username,
        @NotBlank
        String phone,
        @NotBlank
        String password,
        @NotNull
        Roles role
) {
}
