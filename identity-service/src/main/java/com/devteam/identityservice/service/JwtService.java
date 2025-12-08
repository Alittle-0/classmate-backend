package com.devteam.identityservice.service;

import com.devteam.identityservice.security.KeyUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JwtService() throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey("keys/local-only/private-secret.pem");
        this.publicKey = KeyUtils.loadPublicKey("keys/local-only/public-secret.pem");
    }

    public String generateAccessToken(final String username) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "ACCESS_TOKEN");
        System.out.println("Still run generateAccessToken: " + username);
        return createToken(username, claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(final String username) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "REFRESH_TOKEN");
        return createToken(username, claims, this.refreshTokenExpiration);
    }

    public String refreshAccessToken(final String refreshToken) {
        final Claims claims = extractClaims(refreshToken);
        if (!"REFRESH_TOKEN".equals(claims.get(TOKEN_TYPE)))
            throw new RuntimeException("Invalid token type");
        if (isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }

        final String name = claims.getSubject();

        return generateAccessToken(name);

    }

    private String createToken(
            final String username,
            final Map<String, Object> clams,
            final long expiration
            ) {
        System.out.println("Still run createToken: " + username);
        return Jwts.builder()
                .claims(clams)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)
                .compact();
    }

    public boolean isTokenValid(
            final String token,
            final String expectedUsername
            ) {
        final String username = extractUsername(token);
        return username.equals(expectedUsername) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(final String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String extractUsername(final String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token: ", e);
        }
    }

}
