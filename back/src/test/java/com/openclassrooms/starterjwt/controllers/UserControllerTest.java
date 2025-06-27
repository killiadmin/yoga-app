package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the UserController class
 */
public class UserControllerTest {

    private MockMvc mockMvc;

    private final UserService userService = mock(UserService.class);
    private final UserMapper userMapper = mock(UserMapper.class);

    private final SecurityContext securityContext = mock(SecurityContext.class);
    private final Authentication authentication = mock(Authentication.class);
    private final UserDetails userDetails = mock(UserDetails.class);

    private User existingUser;
    private UserDto existingUserDto;

    private static final String EXISTING_EMAIL = "user1@yoga.fr";
    private static final String OTHER_EMAIL = "user2@yoga.fr";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        existingUser = new User(
                EXISTING_EMAIL,
                "Test1",
                "User1",
                "password123!",
                false
        );
        existingUser.setId(1L);
        existingUser.setCreatedAt(now);
        existingUser.setUpdatedAt(now);

        existingUserDto = new UserDto(
                1L,
                EXISTING_EMAIL,
                "Test1",
                "User1",
                false,
                "password123!",
                now,
                now
        );

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, userMapper)).build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    // Testing get methods

    @Test
    void findById_withValidId_shouldReturnOk() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(existingUser);
        when(userMapper.toDto(existingUser)).thenReturn(existingUserDto);

        // Act & Assert
        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(1L);
    }

    @Test
    void findById_withUnknownId_shouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    // Testing delete methods

    @Test
    void deleteUser_withAuthorizedUser_shouldReturnOk() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(existingUser);
        when(userDetails.getUsername()).thenReturn(EXISTING_EMAIL);

        // Act & Assert
        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    void deleteUser_withUnknownId_shouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_withUnauthorizedUser_shouldReturnUnauthorized() throws Exception {
        // Arrange
        User anotherUser = new User(OTHER_EMAIL, "Test2", "User2", "password321!", false);
        anotherUser.setId(1L);
        when(userService.findById(1L)).thenReturn(anotherUser);
        when(userDetails.getUsername()).thenReturn(EXISTING_EMAIL);

        // Act & Assert
        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }
}
