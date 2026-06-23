package com.heraim.zelix.auth.dto;

import com.heraim.zelix.users.dto.UserResponse;

public record AuthResponse(
       String accessToken,
       String refreshToken,
       UserResponse user
) {
}
