package com.timeline.backend.auth;

public record LoginRequest(
        String username,
        String password
) {}
