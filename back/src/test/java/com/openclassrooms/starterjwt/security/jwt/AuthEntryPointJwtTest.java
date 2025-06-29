package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthEntryPointJwtTest class
 */
public class AuthEntryPointJwtTest {

    private AuthEntryPointJwt authEntryPointJwt;

    /**
     * Set up the test environment by initializing an instance of AuthEntryPointJwt
     */
    @BeforeEach
    public void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
    }

    /**
     * This test verifies that when the commence method is called with a request, response, and AuthenticationException
     */
    @Test
    public void testCommence() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");

        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticationException authException = mock(AuthenticationException.class);
        when(authException.getMessage()).thenReturn("Unauthorized access");

        authEntryPointJwt.commence(request, response, authException);

        // Check the expected values in the answer
        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());

        // Check JSON content
        ObjectMapper mapper = new ObjectMapper();
        Map responseBody = mapper.readValue(response.getContentAsByteArray(), Map.class);

        assertEquals(401, responseBody.get("status"));
        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Unauthorized access", responseBody.get("message"));
        assertEquals("/api/test", responseBody.get("path"));
    }
}
