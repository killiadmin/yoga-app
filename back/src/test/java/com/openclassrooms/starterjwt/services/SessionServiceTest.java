package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the SessionService class
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SessionServiceTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SessionService sessionService;

    @PersistenceContext
    private EntityManager entityManager;

    private Session session;
    private User user;
    private Teacher teacher;

    /**
     * Initializes the necessary objects for each test
     */
    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM participate").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM sessions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM teachers").executeUpdate();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now();

        // Create and save teacher
        teacher = new Teacher();
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);
        teacher = teacherRepository.save(teacher);

        // Create and save user
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user = userRepository.save(user);

        // Create session
        session = new Session();
        session.setName("Test Session");
        session.setDate(new Date());
        session.setDescription("Test Description");
        session.setTeacher(teacher);
        session.setUsers(new ArrayList<>());
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
    }

    /**
     * Tests the creation of a new session
     */
    @Test
    void testCreateSession() {
        Session created = sessionService.create(session);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(session.getName(), created.getName());
        assertEquals(session.getDescription(), created.getDescription());
        assertEquals(session.getTeacher().getId(), created.getTeacher().getId());
    }

    /**
     * Tests the behavior of the {@code SessionService#delete} method
     */
    @Test
    void testDeleteSession() {
        Session savedSession = sessionRepository.save(session);

        sessionService.delete(savedSession.getId());

        Optional<Session> found = sessionRepository.findById(savedSession.getId());
        assertFalse(found.isPresent());
    }

    /**
     * Tests the behavior of the {@code SessionService#findAll} method
     */
    @Test
    void testFindAllSessions() {
        sessionRepository.save(session);

        List<Session> sessions = sessionService.findAll();

        assertEquals(1, sessions.size());
        assertEquals(session.getName(), sessions.get(0).getName());
    }

    /**
     * Tests the behavior of the {@code SessionService#getById} method when a session is found
     */
    @Test
    void testGetByIdFound() {
        Session savedSession = sessionRepository.save(session);

        Session found = sessionService.getById(savedSession.getId());

        assertNotNull(found);
        assertEquals(savedSession.getId(), found.getId());
        assertEquals(savedSession.getName(), found.getName());
    }

    /**
     * Tests the behavior of the {@code SessionService#getById} method when a session is not found
     */
    @Test
    void testGetByIdNotFound() {
        Session found = sessionService.getById(999L);

        assertNull(found);
    }

    /**
     * Tests the behavior of the {@code SessionService#update} method
     */
    @Test
    void testUpdateSession() {
        Session savedSession = sessionRepository.save(session);

        Session updated = new Session();
        updated.setName("Updated Name");
        updated.setDescription("Updated Description");
        updated.setDate(new Date());
        updated.setTeacher(teacher);
        updated.setUsers(new ArrayList<>());

        Session result = sessionService.update(savedSession.getId(), updated);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(savedSession.getId(), result.getId());
    }

    /**
     * Tests the behavior of the participate method when both session and user are found
     *
     * @throws NotFoundException if the session or user is not found
     * @throws BadRequestException if the user has already participated in this session
     */
    @Test
    void testParticipateOk() {
        Session savedSession = sessionRepository.save(session);

        sessionService.participate(savedSession.getId(), user.getId());

        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElse(null);
        assertNotNull(updatedSession);
        assertTrue(updatedSession.getUsers().contains(user));
    }

    /**
     * Tests that the participate method throws a BadRequestException when a user has already participated in this session
     */
    @Test
    void testParticipateAlreadyParticipatingThrows() {
        session.getUsers().add(user);
        Session savedSession = sessionRepository.save(session);

        assertThrows(BadRequestException.class, () ->
                sessionService.participate(savedSession.getId(), user.getId()));
    }

    /**
     * Tests that the participate method throws a NotFoundException when either session or user is not found
     *
     * @throws NotFoundException if the session or user is not found
     */
    @Test
    void testParticipateSessionOrUserNotFoundThrows() {
        assertThrows(NotFoundException.class, () ->
                sessionService.participate(999L, user.getId()));
    }

    /**
     * Tests the behavior of the noLongerParticipate method when the user has participated in the session
     *
     * @throws NotFoundException if the session is not found
     */
    @Test
    void testNoLongerParticipateOk() {
        session.getUsers().add(user);
        Session savedSession = sessionRepository.save(session);

        sessionService.noLongerParticipate(savedSession.getId(), user.getId());

        Session updatedSession = sessionRepository.findById(savedSession.getId()).orElse(null);
        assertNotNull(updatedSession);
        assertFalse(updatedSession.getUsers().contains(user));
    }

    /**
     * Tests that the noLongerParticipate method throws a BadRequestException
     * when the user has not participated in this session
     */
    @Test
    void testNoLongerParticipateNotParticipatingThrows() {
        Session savedSession = sessionRepository.save(session);

        assertThrows(BadRequestException.class, () ->
                sessionService.noLongerParticipate(savedSession.getId(), user.getId()));
    }

    /**
     * Tests that the noLongerParticipate method throws a NotFoundException when the session is not found
     */
    @Test
    void testNoLongerParticipateSessionNotFoundThrows() {
        assertThrows(NotFoundException.class, () ->
                sessionService.noLongerParticipate(999L, user.getId()));
    }
}
