package com.devteam.apigateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class JwtUtil {

    private final PublicKey publicKey;
    
    public JwtUtil() throws Exception {
        this.publicKey = KeyUtils.loadPublicKey("key/public-secret.pem");
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                    .setSigningKey(this.publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }

}
