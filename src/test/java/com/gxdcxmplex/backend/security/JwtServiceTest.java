package com.gxdcxmplex.backend.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generatedTokenContainsCorrectUsername(){
        String token = jwtService.generateToken("TestPlayer");
        String username = jwtService.extractUsername(token);
        assertEquals("TestPlayer", username);
    }

    @Test
    void generatedTokenIsValid(){
        String token = jwtService.generateToken("TestPlayer");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void invalidTokenIsRejected(){
        String token = "not.a.valid.jwt";

        assertFalse(jwtService.isTokenValid(token));
    }
}
