package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the AuthController class
 */
public class AuthControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    /**
     * Sets up the test environment by mocking the necessary objects and setting up the MockMvc
     */
    @BeforeEach
    public void setUp() {
        authenticationManager = org.mockito.Mockito.mock(AuthenticationManager.class);
        passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        jwtUtils = org.mockito.Mockito.mock(JwtUtils.class);
        userRepository = org.mockito.Mockito.mock(UserRepository.class);

        AuthController authController = new AuthController(authenticationManager, passwordEncoder, jwtUtils, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        objectMapper = new ObjectMapper();
    }

    /**
     * Tests the successful authentication of a user
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    public void testAuthenticateUserSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@yoga.fr");
        loginRequest.setPassword("password123!");

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "user@yoga.fr",
                "Test",
                "User",
                true,
                "password123!"
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(any())).thenReturn("mocked-jwt-token");

        User user = new User();
        user.setEmail("user@yoga.fr");
        user.setAdmin(false);

        when(userRepository.findByEmail("user@yoga.fr")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.username").value("user@yoga.fr"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    /**
     * Tests the successful registration of a user
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    public void testRegisterUser_Success() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("newuser@yoga.fr");
        signupRequest.setFirstName("Newtest");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password321!");

        when(userRepository.existsByEmail("newuser@yoga.fr")).thenReturn(false);
        when(passwordEncoder.encode("password321!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    /**
     * Tests the registration of a user when an email already exists
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    public void testRegisterUser_EmailExists() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existinguser@yoga.fr");
        signupRequest.setFirstName("Existingtest");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password213!");

        when(userRepository.existsByEmail("existinguser@yoga.fr")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }
}
