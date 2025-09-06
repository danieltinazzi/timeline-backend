package com.timeline.backend.auth;

import com.timeline.backend.security.JwtAuthUtil;
import com.timeline.backend.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtAuthUtil jwtAuthUtil;

    public AuthController(AuthService authService, JwtAuthUtil jwtAuthUtil) {
        this.authService = authService;
        this.jwtAuthUtil = jwtAuthUtil;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = authService.authenticate(request.username(), request.password());

        String accessToken = jwtAuthUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtAuthUtil.generateRefreshToken(user.getUsername());

        return new LoginResponse(accessToken, refreshToken);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (jwtAuthUtil.validateRefreshToken(refreshToken)) {
            String username = jwtAuthUtil.extractUsername(refreshToken);
            String newAccessToken = jwtAuthUtil.generateAccessToken(username);
            String newRefreshToken = jwtAuthUtil.generateRefreshToken(username);
            return new LoginResponse(newAccessToken, newRefreshToken);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }
}