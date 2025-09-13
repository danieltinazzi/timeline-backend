package com.timeline.backend.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
