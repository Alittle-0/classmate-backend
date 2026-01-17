package com.devteam.identityservice.service;

import com.devteam.identityservice.exception.BusinessException;
import com.devteam.identityservice.exception.ErrorCode;
import com.devteam.identityservice.model.User;
import com.devteam.identityservice.security.KeyUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    private static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final UserService userService;

    public JwtService(UserService userService) throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey("keys/local-only/private-secret.pem");
        this.publicKey = KeyUtils.loadPublicKey("keys/local-only/public-secret.pem");
        this.userService = userService;
    }

    public String generateAccessToken(final User user) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "ACCESS_TOKEN");
        claims.put("role", user.getRole());
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("firstname", user.getFirstname());
        claims.put("lastname", user.getLastname());

        return createToken(user.getUsername(), claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(final String username) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, "REFRESH_TOKEN");
        return createToken(username, claims, this.refreshTokenExpiration);
    }

    public String refreshAccessToken(final String refreshToken) {
        try {
            final Claims claims = extractClaims(refreshToken);
            final String tokenType = (String) claims.get(TOKEN_TYPE);

            if (!"REFRESH_TOKEN".equals(tokenType)) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN_TYPE, "REFRESH_TOKEN", tokenType);
            }

            if (isTokenExpired(refreshToken)) {
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }

            final String name = claims.getSubject();
            final User user = (User) userService.loadUserByUsername(name);

            return generateAccessToken(user);
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.TOKEN_EXPIRED) {
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
            throw e;
        }

    }

    private String createToken(
            final String username,
            final Map<String, Object> clams,
            final long expiration
            ) {
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
        } catch (ExpiredJwtException e) {
            log.error("JWT expired: {}", e.getMessage());
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            log.error("Invalid JWT: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

}
