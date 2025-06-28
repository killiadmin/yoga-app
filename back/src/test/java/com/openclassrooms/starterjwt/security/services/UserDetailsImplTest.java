package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserDetailsImplTest class
 */
public class UserDetailsImplTest {


    /**
     * This test method tests the builder and getters of UserDetailsImpl
     */
    @Test
    void testBuilderAndGetters() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("Test")
                .firstName("User")
                .lastName("Testuser")
                .password("password123!")
                .admin(true)
                .build();

        assertEquals(1L, userDetails.getId());
        assertEquals("Test", userDetails.getUsername());
        assertEquals("User", userDetails.getFirstName());
        assertEquals("Testuser", userDetails.getLastName());
        assertEquals("password123!", userDetails.getPassword());
        assertTrue(userDetails.getAdmin());
    }

    /**
     * Tests that a user's account is not expired
     */
    @Test
    void testAccountNonExpired() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().build();
        assertTrue(userDetails.isAccountNonExpired());
    }

    /**
     * Tests that a user's account is not locked
     *
     * @see UserDetailsImpl#isAccountNonLocked()
     */
    @Test
    void testAccountNonLocked() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().build();
        assertTrue(userDetails.isAccountNonLocked());
    }

    /**
     * Tests that a user's credentials are not expired
     */
    @Test
    void testCredentialsNonExpired() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().build();
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    /**
     * Tests whether the user account is enabled
     */
    @Test
    void testIsEnabled() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().build();
        assertTrue(userDetails.isEnabled());
    }

    /**
     * Tests that the getAuthorities method of a newly created UserDetailsImpl returns an empty set
     *
     * @see UserDetailsImpl#getAuthorities()
     */
    @Test
    void testGetAuthoritiesReturnsEmptySet() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().build();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
        assertEquals(new HashSet<>(), authorities);
    }

    /**
     * Test that two UserDetailsImpl objects with the same ID are equal
     *
     * @see UserDetailsImpl#equals(Object)
     */
    @Test
    void testEquals_sameId_shouldBeEqual() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).build();
        assertEquals(user1, user2);
    }

    /**
     * Verifies that two UserDetailsImpl objects with different IDs are not equal
     */
    @Test
    void testEquals_differentId_shouldNotBeEqual() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(2L).build();
        assertNotEquals(user1, user2);
    }

    /**
     * Tests that a UserDetailsImpl object is not equal to an object of a different class
     */
    @Test
    void testEquals_differentClass_shouldNotBeEqual() {
        UserDetailsImpl user = UserDetailsImpl.builder().id(1L).build();
        assertNotEquals(user, new Object());
    }
}
