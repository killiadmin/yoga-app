package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the TeacherService class
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeacherServiceTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherService teacherService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sets up the test data before each test
     */
    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM sessions").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM teachers").executeUpdate();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        entityManager.flush();
    }

    /**
     * Tests the behavior of the {@code TeacherService#findAll} method.
     * Checks that when calling the findAll method on the TeacherService, it returns a list containing all teachers.
     */
    @Test
    void testFindAll_ReturnsListOfTeachers() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher1 = new Teacher();
        teacher1.setLastName("Teacher1");
        teacher1.setFirstName("First1");
        teacher1.setCreatedAt(now);
        teacher1.setUpdatedAt(now);

        Teacher teacher2 = new Teacher();
        teacher2.setLastName("Teacher2");
        teacher2.setFirstName("First2");
        teacher2.setCreatedAt(now);
        teacher2.setUpdatedAt(now);

        teacherRepository.save(teacher1);
        teacherRepository.save(teacher2);

        // Act
        List<Teacher> result = teacherService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Teacher1", result.getFirst().getLastName());
    }

    /**
     * Checks that when a teacher is found by ID, the TeacherService returns the expected teacher.
     */
    @Test
    void testFindById_ReturnsTeacher_WhenFound() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Teacher teacher = new Teacher();
        teacher.setLastName("Teacher1");
        teacher.setFirstName("First1");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);

        Teacher savedTeacher = teacherRepository.save(teacher);

        // Act
        Teacher result = teacherService.findById(savedTeacher.getId());

        // Assert
        assertNotNull(result);
        assertEquals("Teacher1", result.getLastName());
    }

    /**
     * Tests the behavior of the {@code TeacherService#findById} method when a teacher is not found.
     * The method should return null in this case.
     */
    @Test
    void testFindById_ReturnsNull_WhenNotFound() {
        // Act
        Teacher result = teacherService.findById(999L);

        // Assert
        assertNull(result);
    }
}
