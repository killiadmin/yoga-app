package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the TeacherController class
 */
public class TeacherControllerTest {

    private MockMvc mockMvc;

    private TeacherService teacherService;
    private TeacherMapper teacherMapper;

    private static Teacher teacherMock;
    private static TeacherDto teacherDtoMock;

    /**
     * Initializes the teacher mock objects before each test
     */
    @BeforeAll
    static void beforeAll() {
        LocalDateTime now = LocalDateTime.now();

        teacherMock = new Teacher(1L, "Teacher", "Test", now, now);

        teacherDtoMock = new TeacherDto();
        teacherDtoMock.setId(1L);
        teacherDtoMock.setFirstName("Test");
        teacherDtoMock.setLastName("Teacher");
        teacherDtoMock.setCreatedAt(now);
        teacherDtoMock.setUpdatedAt(now);
    }

    /**
     * Initializes the teacher service and mapper mock objects before each test
     */
    @BeforeEach
    void setUp() {
        teacherService = mock(TeacherService.class);
        teacherMapper = mock(TeacherMapper.class);

        TeacherController teacherController = new TeacherController(teacherService, teacherMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
    }

    /**
     * Tests the functionality of the findTeacherById method
     *
     * @throws Exception if any error occurs during testing
     */
    @Test
    public void testFindTeacherById() throws Exception {
        when(teacherService.findById(1L)).thenReturn(teacherMock);
        when(teacherMapper.toDto(teacherMock)).thenReturn(teacherDtoMock);

        mockMvc.perform(get("/api/teacher/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("Teacher"));
    }

    /**
     * Tests the functionality of the findTeacherById method when a teacher with the given ID is not found
     *
     * @throws Exception if any error occurs during testing
     */
    @Test
    public void testFindTeacherByIdNotFound() throws Exception {
        when(teacherService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests the functionality of the findTeacherById method when the ID format is incorrect
     *
     * @throws Exception if any error occurs during testing
     */
    @Test
    public void testFindTeacherByIdFormatIncorrect() throws Exception {
        mockMvc.perform(get("/api/teacher/badrequest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the functionality of the find all teachers method
     *
     * @throws Exception if any error occurs during testing
     */
    @Test
    public void testFindAllTeachers() throws Exception {
        when(teacherService.findAll()).thenReturn(List.of(teacherMock));
        when(teacherMapper.toDto(List.of(teacherMock))).thenReturn(List.of(teacherDtoMock));

        mockMvc.perform(get("/api/teacher").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Test"))
                .andExpect(jsonPath("$[0].lastName").value("Teacher"));
    }
}
