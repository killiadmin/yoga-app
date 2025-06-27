package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TeacherService class
 */
class TeacherServiceTest {

    private TeacherRepository teacherRepository;
    private TeacherService teacherService;

    /**
     * Sets up the test by creating a mock of the TeacherRepository
     */
    @BeforeEach
    void setUp() {
        teacherRepository = mock(TeacherRepository.class);
        teacherService = new TeacherService(teacherRepository);
    }

    /**
     * Tests the behavior of the {@code TeacherService#findAll} method.
     * Checks that when calling the findAll method on the TeacherService, it returns a list containing all teachers.
     */
    @Test
    void testFindAll_ReturnsListOfTeachers() {
        // Arrange
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setLastName("Teacher1");

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setLastName("Teacher2");

        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher1, teacher2));

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
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Teacher1");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act
        Teacher result = teacherService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Teacher1", result.getLastName());
    }

    /**
     * Tests the behavior of the {@code TeacherService#findById} method when a teacher is not found.
     * The method should return null in this case.*/
    @Test
    void testFindById_ReturnsNull_WhenNotFound() {
        // Arrange
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Teacher result = teacherService.findById(99L);

        // Assert
        assertNull(result);
    }
}
