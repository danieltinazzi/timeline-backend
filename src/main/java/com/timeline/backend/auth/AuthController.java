package com.timeline.backend.auth;

import com.timeline.backend.security.JwtService;
import com.timeline.backend.user.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {

        User user = authService.authenticate(loginRequest.username(), loginRequest.password());

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request,
                                 HttpServletResponse response) {
        String refreshToken = request.refreshToken();

        if (jwtService.isRefreshTokenValid(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            String newAccessToken = jwtService.generateAccessToken(username);
            String newRefreshToken = jwtService.generateRefreshToken(username);

            return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefreshToken));
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }
}