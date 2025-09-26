package com.timeline.backend;

import com.timeline.backend.auth.dto.LoginRequest;
import com.timeline.backend.auth.dto.LoginResponse;
import com.timeline.backend.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Environment env;

    @LocalServerPort
    private int port;

    private String baseUrl;
    private String userId;
    private String username;
    private String password;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void init() {
        baseUrl = "http://localhost:" + port + "/api/v1";
        username = env.getProperty("test.username", "");
        password = env.getProperty("test.password", "");
        assertFalse(username.isEmpty());
        assertFalse(password.isEmpty());
    }

    private void login() {
        LoginRequest request = new LoginRequest(username, password);
        ResponseEntity<LoginResponse> response = postNoAuth(
                "/auth/login", request, LoginResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().accessToken());
        assertNotNull(response.getBody().refreshToken());
        assertNotEquals("", response.getBody().accessToken());
        assertNotEquals("", response.getBody().refreshToken());
        accessToken = response.getBody().accessToken();
        refreshToken = response.getBody().refreshToken();
    }

    private void userMe() {
        ResponseEntity<UserResponse> response = get(
                "/users/me",
                UserResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(username, response.getBody().username());
        assertNotNull(response.getBody().id());
        userId = response.getBody().id().toString();
        assertNotEquals("", userId);
    }

    protected <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        return exchangeWithAuth(url, HttpMethod.GET, null, responseType);
    }

    protected <T> ResponseEntity<T> post(String url, Object request, Class<T> responseType) {
        return exchangeWithAuth(url, HttpMethod.POST, request, responseType);
    }

    protected <T> ResponseEntity<T> getNoAuth(String url, Class<T> responseType) {
        return exchangeWithoutAuth(url, HttpMethod.GET, null, responseType);
    }

    protected <T> ResponseEntity<T> postNoAuth(String url, Object request, Class<T> responseType) {
        return exchangeWithoutAuth(url, HttpMethod.POST, request, responseType);
    }

    private <T> ResponseEntity<T> exchangeWithAuth(String url, HttpMethod method, Object request, Class<T> responseType) {
        if (accessToken == null) {
            login();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(baseUrl + url, method, entity, responseType);
    }

    private <T> ResponseEntity<T> exchangeWithoutAuth(String url, HttpMethod method, Object request, Class<T> responseType) {
        HttpEntity<?> entity = new HttpEntity<>(request);
        return restTemplate.exchange(baseUrl + url, method, entity, responseType);
    }

    protected String getRefreshToken() {
        if (refreshToken == null) {
            login();
        }
        return refreshToken;
    }

    protected String getUserId() {
        if (userId == null) {
            userMe();
        }
        return userId;
    }

    protected String getUsername() {
        if (username == null) {
            userMe();
        }
        return username;
    }
}
