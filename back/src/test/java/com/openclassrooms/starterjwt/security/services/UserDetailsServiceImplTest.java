package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Unit tests for the UserDetailsServiceImplTest class
 */
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Initializes the test with a fresh set of mocks
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the loadUserByUsername method of the UserDetailsService when a user exists
     *
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Test
    void testLoadUserByUsernameUserExistsReturnsUserDetails() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@yoga.fr");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setPassword("password123!");

        when(userRepository.findByEmail("user@yoga.fr"))
                .thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("user@yoga.fr");

        // Assert
        assertNotNull(userDetails);
        assertEquals("user@yoga.fr", userDetails.getUsername());
        assertEquals("password123!", userDetails.getPassword());
        assertInstanceOf(UserDetailsImpl.class, userDetails);
    }

    /**
     * Tests the loadUserByUsername method of the UserDetailsService when a user is not found.
     *
     * @throws UsernameNotFoundException if no user exists with the given email
     */
    @Test
    void testLoadUserByUsernameUserNotFoundThrowsException() {
        // Arrange
        when(userRepository.findByEmail("notfound@yoga.fr"))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("notfound@yoga.fr")
        );

        assertEquals("User Not Found with email: notfound@yoga.fr", exception.getMessage());
    }
}
