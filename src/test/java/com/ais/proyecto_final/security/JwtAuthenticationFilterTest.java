package com.ais.proyecto_final.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_WhenTokenIsValid() throws ServletException, IOException {
        
        String token = "valid-jwt-token";
        String username = "admin";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals(username, ((UserDetails) auth.getPrincipal()).getUsername());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldDoNothing_WhenTokenIsMissing() throws ServletException, IOException {
        
        when(request.getHeader("Authorization")).thenReturn(null);

        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldDoNothing_WhenTokenHasInvalidPrefix() throws ServletException, IOException {
        
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz"); // "Basic user:pass"

        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldDoNothing_WhenTokenIsInvalid() throws ServletException, IOException {
        
        String token = "invalid-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenReturn(false);

        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldCatchException_WhenValidationFails() throws ServletException, IOException {
        
        String token = "valid-token-but-fails";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(token)).thenThrow(new RuntimeException("Test Exception"));

        
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}