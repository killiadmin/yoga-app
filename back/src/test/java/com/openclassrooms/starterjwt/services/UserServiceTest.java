package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the UserService class
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
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

    /**
     * Tests the behavior of the findById method in the UserService class when an existing user ID is provided.
     */
    @Test
    void findByIdExistingUserShouldReturnUser() {
        // Arrange
        User savedUser = userRepository.save(user);

        // Act
        User foundUser = userService.findById(savedUser.getId());

        // Assert
        assertNotNull(foundUser);
        assertEquals("admin@yoga.fr", foundUser.getEmail());
        assertEquals("Test", foundUser.getLastName());
    }

    /**
     * Tests the behavior of the {@code findById} method in the {@code UserService} class
     * when a non-existing user ID is provided.
     */
    @Test
    void findByIdNonExistingUserShouldReturnNull() {
        // Act
        User foundUser = userService.findById(999L);

        // Assert
        assertNull(foundUser);
    }

    /**
     * Tests the behavior of the {@code delete} method in the {@code UserService} class.
     */
    @Test
    void deleteUserShouldInvokeRepositoryDeleteById() {
        // Arrange
        User savedUser = userRepository.save(user);

        // Act
        userService.delete(savedUser.getId());

        // Assert
        assertFalse(userRepository.existsById(savedUser.getId()));
    }
}