package com.formation.app.service;

import com.formation.app.entity.Role;
import com.formation.app.entity.User;
import com.formation.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("USER001");
        testUser.setLogin("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Role.ADMIN);
    }
    
    @Test
    void testCreateUser_Success() {
        // Given
        when(userRepository.existsByLogin("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // When
        User created = userService.createUser("newuser", "password", Role.ADMIN);
        
        // Then
        assertNotNull(created);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testCreateUser_DuplicateLogin() {
        // Given
        when(userRepository.existsByLogin("existinguser")).thenReturn(true);
        
        // When & Then
        assertThrows(Exception.class, () -> {
            userService.createUser("existinguser", "password", Role.ADMIN);
        });
    }
    
    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById("USER001")).thenReturn(Optional.of(testUser));
        
        // When
        User found = userService.getUserById("USER001");
        
        // Then
        assertNotNull(found);
        assertEquals("USER001", found.getId());
        assertEquals("testuser", found.getLogin());
    }
    
    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById("INVALID")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(Exception.class, () -> {
            userService.getUserById("INVALID");
        });
    }
    
    @Test
    void testAuthenticate_Success() {
        // Given
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        
        // When
        boolean result = userService.authenticate("testuser", "password");
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void testAuthenticate_WrongPassword() {
        // Given
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        
        // When
        boolean result = userService.authenticate("testuser", "wrongpassword");
        
        // Then
        assertFalse(result);
    }
}

