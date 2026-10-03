package com.sgb.mylibrum.security;

import com.sgb.mylibrum.entities.enums.Funcao;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username) {
        return generateToken(username, null);
    }

    public String generateToken(String username, Funcao funcao) {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationMs);

        var builder = Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiration);
        if (funcao != null) {
            builder.claim("funcao", funcao.name());
        }
        return builder.signWith(signingKey).compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getFuncaoFromToken(String token) {
        return parseClaims(token).get("funcao", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}