package com.timeline.backend.user;

import com.timeline.backend.AbstractIntegrationTest;
import com.timeline.backend.auth.dto.LoginResponse;
import com.timeline.backend.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UserControllerIT extends AbstractIntegrationTest {

    @Test
    void users_notFound() {
        ResponseEntity<LoginResponse> response = get("/users", LoginResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void users_me_noAuth() {
        ResponseEntity<UserResponse> response = getNoAuth(
                "/users/me",
                UserResponse.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void users_id_noAuth() {
        String userId = getUserId();
        ResponseEntity<UserResponse> response = getNoAuth(
                "/users/" + userId,
                UserResponse.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void users_id_invalidId() {
        UUID invalidUserId = UUID.randomUUID();
        ResponseEntity<UserResponse> response = get(
                "/users/" + invalidUserId,
                UserResponse.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void users_id_validId() {
        String userId = getUserId();
        ResponseEntity<UserResponse> response = get(
                "/users/" + userId,
                UserResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertNotNull(response.getBody().username());
        assertNotEquals("", response.getBody().id().toString());
        assertNotEquals("", response.getBody().username());
    }
}


