package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthTokenFilterTest class
 */
public class AuthTokenFilterTest {

    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    /**
     * Set up the test environment by initializing an instance of AuthTokenFilter and setting the required fields with default values
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authTokenFilter = new AuthTokenFilter();
        authTokenFilter.jwtUtils = jwtUtils;
        authTokenFilter.userDetailsService = userDetailsService;

        SecurityContextHolder.clearContext();
    }

    /**
     * Tests the doFilterInternal method with a valid JWT token
     */
    @Test
    public void testDoFilterInternal_validToken() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String username = "Test";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = new User(username, "password123!", Collections.emptyList());

        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Check that the user is authenticated
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests the doFilterInternal method with an invalid token
     */
    @Test
    public void testDoFilterInternal_invalidToken() throws ServletException, IOException {
        String token = "invalid.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(jwtUtils, times(1)).validateJwtToken(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests the doFilterInternal method with no token
     */
    @Test
    public void testDoFilterInternal_noToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Tests the doFilterInternal method with an exception during JWT validation
     */
    @Test
    public void testDoFilterInternal_withException() throws ServletException, IOException {
        String token = "error.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtils.validateJwtToken(token)).thenThrow(new RuntimeException("JWT parsing error"));

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
