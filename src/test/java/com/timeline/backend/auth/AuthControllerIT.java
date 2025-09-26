package com.timeline.backend.auth;

import com.timeline.backend.AbstractIntegrationTest;
import com.timeline.backend.auth.dto.LoginRequest;
import com.timeline.backend.auth.dto.LoginResponse;
import com.timeline.backend.auth.dto.RefreshRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AuthControllerIT extends AbstractIntegrationTest {

    @Test
    void auth_notFound() {
        ResponseEntity<LoginResponse> response = get("/auth", LoginResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void auth_login_wrongCredentials() {
        String randomString = UUID.randomUUID().toString();
        LoginRequest request = new LoginRequest(randomString, randomString);
        ResponseEntity<LoginResponse> response = postNoAuth(
                "/auth/login",
                request,
                LoginResponse.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void auth_login_badRequest() {
        Function<Object, Void> login = (Object request) -> {
            ResponseEntity<LoginResponse> response = postNoAuth(
                    "/auth/login",
                    request,
                    LoginResponse.class
            );
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            return null;
        };
        String randomString = UUID.randomUUID().toString();
        login.apply(new LoginRequest(randomString, null));
        login.apply(new LoginRequest(randomString, ""));
        login.apply(new LoginRequest(null, randomString));
        login.apply(new LoginRequest("", randomString));
    }

    @Test
    void auth_refresh_validToken() {
        String refreshToken = getRefreshToken();
        RefreshRequest request = new RefreshRequest(refreshToken);
        ResponseEntity<LoginResponse> response = postNoAuth(
                "/auth/refresh",
                request,
                LoginResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().accessToken());
        assertNotNull(response.getBody().refreshToken());
        assertNotEquals("", response.getBody().accessToken());
        assertNotEquals("", response.getBody().refreshToken());
    }

    @Test
    void auth_refresh_invalidToken() {
        RefreshRequest request = new RefreshRequest("invalid_token");
        ResponseEntity<LoginResponse> response = postNoAuth(
                "/auth/refresh",
                request,
                LoginResponse.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void auth_refresh_badRequest() {
        RefreshRequest request = new RefreshRequest(null);
        ResponseEntity<LoginResponse> response = postNoAuth(
                "/auth/refresh",
                request,
                LoginResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
