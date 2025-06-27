package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SessionServiceTest class
 */
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    /**
     * Initializes the necessary objects for each test
     */
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());
        session.setDate(new Date());
        session.setName("Test Session");
        session.setTeacher(
                new Teacher(
                        1L,
                        "Teacher",
                        "Test",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    /**
     * Tests the creation of a new session
     */
    @Test
    void testCreateSession() {
        when(sessionRepository.save(session)).thenReturn(session);
        Session created = sessionService.create(session);
        assertEquals(session, created);
        verify(sessionRepository).save(session);
    }

    /**
     * Tests the behavior of the {@code SessionService#delete} method
     */
    @Test
    void testDeleteSession() {
        sessionService.delete(1L);
        verify(sessionRepository).deleteById(1L);
    }

    /**
     * Tests the behavior of the {@code SessionService#findAll} method
     */
    @Test
    void testFindAllSessions() {
        when(sessionRepository.findAll()).thenReturn(List.of(session));
        List<Session> sessions = sessionService.findAll();
        assertEquals(1, sessions.size());
    }

    /**
     * Tests the behavior of the {@code SessionService#getById} method when a session is found
     */
    @Test
    void testGetByIdFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        Session found = sessionService.getById(1L);
        assertNotNull(found);
    }

    /**
     * Tests the behavior of the {@code SessionService#getById} method when a session is not found
     */
    @Test
    void testGetByIdNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        Session found = sessionService.getById(1L);
        assertNull(found);
    }

    /**
     * Tests the behavior of the {@code SessionService#update} method
     */
    @Test
    void testUpdateSession() {
        Session updated = new Session();
        updated.setName("Updated Name");

        when(sessionRepository.save(any(Session.class))).thenReturn(updated);

        Session result = sessionService.update(1L, updated);
        assertEquals("Updated Name", result.getName());
        assertEquals(1L, result.getId());
    }

    /**
     * Tests the behavior of the participate method when both session and user are found
     *
     * @throws NotFoundException if the session or user is not found
     * @throws BadRequestException if the user has already participated in this session
     */
    @Test
    void testParticipateOk() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        sessionService.participate(1L, 1L);
        assertTrue(session.getUsers().contains(user));
    }

    /**
     * Tests that the participate method throws a BadRequestException when a user has already participated in this session
     */
    @Test
    void testParticipateAlreadyParticipatingThrows() {
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    }

    /**
     * Tests that the participate method throws a NotFoundException when either session or user is not found
     *
     * @throws NotFoundException if the session or user is not found
     */
    @Test
    void testParticipateSessionOrUserNotFoundThrows() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }

    /**
     * Tests the behavior of the noLongerParticipate method when the user has participated in the session
     *
     * @throws NotFoundException if the session is not found
     */
    @Test
    void testNoLongerParticipateOk() {
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(1L, 1L);
        assertFalse(session.getUsers().contains(user));
    }

    /**
     * Tests that the noLongerParticipate method throws a BadRequestException
     * when the user has not participated in this session
     */
    @Test
    void testNoLongerParticipateNotParticipatingThrows() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }

    /**
     * Tests that the noLongerParticipate method throws a NotFoundException when the session is not found
     */
    @Test
    void testNoLongerParticipateSessionNotFoundThrows() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }
}
