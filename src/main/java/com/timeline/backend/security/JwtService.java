package com.timeline.backend.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for generating, parsing, and validating JWT tokens.
 * Uses HMAC signing with a secret key defined in application.properties.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    private final String TYPE_ACCESS_TOKEN = "access";
    private final String TYPE_REFRESH_TOKEN = "refresh";

    /**
     * Initialize the JwtService with values from application.properties.
     *
     * @param secret the secret key for signing tokens
     * @param accessExpiration expiration time for access tokens (in ms)
     * @param refreshExpiration expiration time for refresh tokens (in ms)
     */
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.expiration}") long accessExpiration,
            @Value("${jwt.refresh.expiration}") long refreshExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * Generate a short-lived access token
     */
    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("token_type", TYPE_ACCESS_TOKEN)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Generate a long-lived refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
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
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, TYPE_ACCESS_TOKEN);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, TYPE_REFRESH_TOKEN);
    }

    /**
     * Validate token: checks username, token type, signature and expiration
     */
    private boolean isTokenValid(String token, UserDetails userDetails, String expectedType) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            String type = claims.get("token_type", String.class);

            return username.equals(userDetails.getUsername()) &&
                userDetails.isEnabled() &&
                expectedType.equals(type);

        } catch (JwtException e) {
            return false;
        }
    }
}
