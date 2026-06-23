package com.heraim.zelix.users.dto;

import com.heraim.zelix.users.entity.Roles;
import com.heraim.zelix.users.entity.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String phone,
        String username,
        Roles role,
        String profilePictureUrl,
        String firstName,
        String lastName,
        String institution,
        String faculty,
        String department
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                user.getRole(),
                user.getProfilePictureUrl(),
                user.getFirstName(),
                user.getLastName(),
                user.getInstitution(),
                user.getFaculty(),
                user.getDepartment()
        );
    }
}