package com.pathpilot.ai.security;

import com.pathpilot.ai.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        String secret = jwtProperties.getJwtSecret();

        if (secret == null
                || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 bytes long."
            );
        }

        if (jwtProperties.getJwtExpirationMs() <= 0) {
            throw new IllegalArgumentException(
                    "JWT expiration must be greater than zero."
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Generate a JWT token
    public String generateToken(String username) {
        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + jwtProperties.getJwtExpirationMs()
        );

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Validate token
    public boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Claims claims = extractAllClaims(token);

            return claims.getSubject() != null
                    && !claims.getSubject().isBlank()
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    // Extract a specific claim
    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    // Read and verify token claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}