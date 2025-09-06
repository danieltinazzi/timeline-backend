package com.timeline.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for generating, parsing, and validating JWT tokens.
 * Uses HMAC signing with a secret key defined in application.properties.
 */
@Component
public class JwtAuthUtil {

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    private final String TYPE_ACCESS_TOKEN = "access";
    private final String TYPE_REFRESH_TOKEN = "refresh";

    /**
     * Initialize the JwtUtil with values from application.properties.
     *
     * @param secret the secret key for signing tokens
     * @param accessExpiration expiration time for access tokens (in ms)
     * @param refreshExpiration expiration time for refresh tokens (in ms)
     */
    public JwtAuthUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.expiration}") long accessExpiration,
            @Value("${jwt.refresh.expiration}") long refreshExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * Generate a short-lived access token
     */
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("token_type", TYPE_ACCESS_TOKEN)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Generate a long-lived refresh token
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("token_type", TYPE_REFRESH_TOKEN)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Extract username (subject) from token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, TYPE_ACCESS_TOKEN);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, TYPE_REFRESH_TOKEN);
    }

    /**
     * Validate token: checks token type, signature and expiration
     */
    private boolean validateToken(String token, String expectedType) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("token_type", String.class);

            return expectedType.equals(type) &&
                    claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
