package com.zhalgas.ecommerceorderapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateToken_returnsNotBlankToken() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("testPassword")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_returnsTokenSubject() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = User.builder()
                .username("test@email.com")
                .password("testPassword")
                .roles("USER")
                .build();
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenValid_whenUsernameMatches_returnsTrue() {
        JwtService jwtService = new JwtService();
        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("testPassword")
                .roles("USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        boolean result = jwtService.isTokenValid(token, userDetails);

        assertTrue(result);
    }
}
