package com.timeline.backend.user.dto;

import com.timeline.backend.user.User;

import java.util.UUID;

public record UserResponse(UUID id, String username) {

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getId(), user.getUsername());
    }
}
