package com.timeline.backend.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
