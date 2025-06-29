package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the JwtUtilsTest class
 */
public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    /**
     * Method sets up the Mockito annotations and initializes the JwtUtils with default values
     * */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtils = new JwtUtils();

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "mySecretKey12345678901234567890");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60 * 1000);
    }

    /**
     * Method tests the generation of a JWT token using an existing UserDetailsImpl object
     */
    @Test
    public void testGenerateJwtToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "Test",
                "User",
                "Testuser",
                true,
                null
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);
        assertNotNull(token);

        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("Test", username);
    }

    /**
     * Tests the validation of a valid JWT token
     */
    @Test
    public void testValidateJwtTokenValidToken() {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "Test",
                "User",
                "Testuser",
                true,
                null
        );
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    /**
     * Tests the validation of a JWT token that has expired
     *
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testValidateJwtTokenExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "Test",
                "User",
                "Testuser",
                true,
                null
        );
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);
        Thread.sleep(10);

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    /**
     * Tests the validation of a malformed JWT token
     */
    @Test
    public void testValidateJwtToken_malformedToken() {
        String malformedToken = "this.is.not.a.valid.token";
        assertFalse(jwtUtils.validateJwtToken(malformedToken));
    }

    /**
     * Tests the validation of a JWT token with an illegal argument (an empty string)
     */
    @Test
    public void testValidateJwtToken_illegalArgument() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }
}
