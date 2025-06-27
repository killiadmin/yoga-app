package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Unit tests for the SessionController class
 */
@SpringBootTest
public class SessionControllerTest {

    @Autowired
    private SessionController sessionController;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private MockMvc mockMvc;
    private static ObjectMapper objectMapper;
    private static SessionDto sessionDto;
    private static Session session;

    /**
     * Initializes the necessary objects for testing
     */
    @BeforeAll
    static void init() {
        objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

        LocalDateTime now = LocalDateTime.now();
        sessionDto = new SessionDto(
                1L,
                "Name Test",
                new Date(),
                1L,
                "Description Test",
                List.of(1L),
                now,
                now
        );

        session = new Session();
        session.setId(1L);
        session.setName("Name Test");
        session.setDescription("Description Test");
        session.setId(1L);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
    }

    /**
     * Initializes the necessary objects for testing.
     */
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
    }

    /**
     * Find by ID, where a valid session is found
     */
    @Test
    void testFindByIdSuccess() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    /**
     * Find by ID, where a valid session is not found.
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testFindByIdNotFound() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Find by ID, where a request is sent with an invalid ID
     * The expected status code is 400 (Bad Request)
     */
    @Test
    void testFindByIdBadRequest() throws Exception {
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Find all, where a valid session is found
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testFindAll() throws Exception {
        when(sessionService.findAll()).thenReturn(List.of(session));
        when(sessionMapper.toDto(List.of(session))).thenReturn(List.of(sessionDto));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    /**
     * Create method
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testCreate() throws Exception {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    /**
     * Update method, where a valid session is updated
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testUpdateSuccess() throws Exception {
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.update(1L, session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(put("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    /**
     * Update method, where a request with bad request is sent
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testUpdateBadRequest() throws Exception {
        mockMvc.perform(put("/api/session/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Delete method with a valid session ID
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testDeleteSuccess() throws Exception {
        when(sessionService.getById(1L)).thenReturn(session);
        doNothing().when(sessionService).delete(1L);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isOk());
    }

    /**
     * Delete method, where a session with ID 999 is not found
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testDeleteNotFound() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Delete method with a request that is not valid
     * The expected status code is 400 (Bad Request)
     */
    @Test
    void testDeleteBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Participate method with a valid request
     * The expected status code is 200 (OK)
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testParticipateSuccess() throws Exception {
        doNothing().when(sessionService).participate(1L, 2L);

        mockMvc.perform(post("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }

    /**
     * Tests the participate method with a bad request. The expected status code is 400 (Bad Request)
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testParticipateBadRequest() throws Exception {
        mockMvc.perform(post("/api/session/a/participate/b"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests the no longer participate method with a valid request
     * The expected status code is 200 (OK)
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testNoLongerParticipateSuccess() throws Exception {
        doNothing().when(sessionService).noLongerParticipate(1L, 2L);

        mockMvc.perform(delete("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }

    /**
     * Tests the no longer participate method with a bad request
     * The expected status code is 400 (Bad Request)
     *
     * @throws Exception if any error occurs during the test
     */
    @Test
    void testNoLongerParticipateBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/a/participate/b"))
                .andExpect(status().isBadRequest());
    }
}
