package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserService class
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "admin@yoga.fr",
                "Test",
                "User",
                "password123!",
                true
        );

        user.setId(USER_ID);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void findById_existingUser_shouldReturnUser() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.findById(USER_ID);

        // Assert
        assertNotNull(foundUser);
        assertEquals("admin@yoga.fr", foundUser.getEmail());
        assertEquals("Test", foundUser.getLastName());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void findById_nonExistingUser_shouldReturnNull() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findById(USER_ID);

        // Assert
        assertNull(foundUser);
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void deleteUser_shouldInvokeRepositoryDeleteById() {
        // Act
        userService.delete(USER_ID);

        // Assert
        verify(userRepository, times(1)).deleteById(USER_ID);
    }
}
