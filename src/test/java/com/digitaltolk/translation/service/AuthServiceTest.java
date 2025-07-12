package com.digitaltolk.translation.service;

import com.digitaltolk.translation.dto.AuthRequest;
import com.digitaltolk.translation.dto.AuthResponse;
import com.digitaltolk.translation.entity.User;
import com.digitaltolk.translation.repository.UserRepository;
import com.digitaltolk.translation.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @InjectMocks
    private AuthService authService;
    
    private User user;
    private AuthRequest authRequest;
    
    @BeforeEach
    void setUp() {
        user = new User("admin", "admin@example.com", "encodedPassword");
        user.setRole(User.Role.ADMIN);
        authRequest = new AuthRequest("admin", "admin123");
    }
    
    @Test
    void authenticate_ShouldReturnAuthResponse_WhenCredentialsValid() {
        // Given
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken("admin", "ADMIN")).thenReturn("jwt-token");
        
        // When
        AuthResponse response = authService.authenticate(authRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("Bearer", response.getType());
    }
    
    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest));
    }
    
    @Test
    void authenticate_ShouldThrowException_WhenPasswordInvalid() {
        // Given
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(false);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest));
    }
    
    @Test
    void createUser_ShouldReturnUser_WhenValidData() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        User newUser = new User("newuser", "new@example.com", "encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        // When
        User result = authService.createUser("newuser", "new@example.com", "password");
        
        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
    }
    
    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("admin")).thenReturn(true);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
            authService.createUser("admin", "admin@example.com", "password"));
    }
}
