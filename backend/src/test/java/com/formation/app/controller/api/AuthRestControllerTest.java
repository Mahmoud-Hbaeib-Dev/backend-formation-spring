package com.formation.app.controller.api;

import com.formation.app.security.JwtTokenService;
import com.formation.app.security.UserDetailsServiceImpl;
import com.formation.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour AuthRestController
 */
@WebMvcTest(AuthRestController.class)
class AuthRestControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private AuthenticationManager authenticationManager;
    
    @MockBean
    private JwtTokenService jwtTokenService;
    
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testLogin_Success() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("login", "admin");
        loginRequest.put("password", "admin");
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("admin")
                .password("password")
                .roles("ADMIN")
                .build();
        
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtTokenService.generateToken(any(UserDetails.class))).thenReturn("test-jwt-token");
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("admin"));
    }
    
    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("login", "admin");
        loginRequest.put("password", "wrongpassword");
        
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}

