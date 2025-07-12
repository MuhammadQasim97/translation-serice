package com.digitaltolk.translation.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    
    private JwtTokenProvider jwtTokenProvider;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider("mySecretKey123456789012345678901234567890", 3600000);
    }
    
    @Test
    void generateToken_ShouldReturnValidToken() {
        // When
        String token = jwtTokenProvider.generateToken("admin", "ADMIN");
        
        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }
    
    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        // Given
        String token = jwtTokenProvider.generateToken("admin", "ADMIN");
        
        // When
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    void getUsername_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtTokenProvider.generateToken("admin", "ADMIN");
        
        // When
        String username = jwtTokenProvider.getUsername(token);
        
        // Then
        assertEquals("admin", username);
    }
    
    @Test
    void getRole_ShouldReturnCorrectRole() {
        // Given
        String token = jwtTokenProvider.generateToken("admin", "ADMIN");
        
        // When
        String role = jwtTokenProvider.getRole(token);
        
        // Then
        assertEquals("ADMIN", role);
    }
}
