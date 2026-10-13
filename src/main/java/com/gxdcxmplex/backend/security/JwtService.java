package com.gxdcxmplex.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "my-super-secret-key-that-is-at-least-32-characters-long".getBytes()
    );

    private static final long TOKEN_EXPIRATION_MS = 15 * 60 * 1000;

    public String generateToken(String username){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TOKEN_EXPIRATION_MS);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException exception){
            return false;
        }
    }
}
