package com.sgb.mylibrum.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private static final String SECRET = "bXlsaWJydW0tc2VjcmV0LWtleS1mb3ItdGVzdC0zMi1ieXRlcy1taW5pbXVt";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 3600000);

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtUtil.generateToken("usuario@teste.com");

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("usuario@teste.com", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void shouldRejectTokenWithDifferentSignature() {
        String token = jwtUtil.generateToken("usuario@teste.com");
        JwtUtil anotherJwtUtil = new JwtUtil(
                "bXlsaWJydW0tc2VjcmV0LWtleS1mb3ItdGVzdC0zMi1ieXRlcy1vdGhlcg==", 3600000);

        assertFalse(anotherJwtUtil.validateToken(token));
    }
}