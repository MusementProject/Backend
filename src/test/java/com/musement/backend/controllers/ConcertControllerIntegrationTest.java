package com.musement.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musement.backend.models.Concert;
import com.musement.backend.services.ConcertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConcertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConcertService concertService;

    private List<Concert> defaultConcerts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Concert concert1 = new Concert();
        concert1.setId(1L);
        concert1.setLocation("St. Petersburg");
        concert1.setDate(LocalDateTime.now().plusDays(2));
        defaultConcerts.add(concert1);

        Concert concert2 = new Concert();
        concert2.setId(2L);
        concert2.setLocation("Moscow");
        concert2.setDate(LocalDateTime.now().plusDays(3));
        defaultConcerts.add(concert2);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "ADMIN")
    void getAllConcerts() throws Exception {
        when(concertService.getAllConcerts()).thenReturn(defaultConcerts);

        mockMvc.perform(get("/api/concerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].location").value("St. Petersburg"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].location").value("Moscow"));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "ADMIN")
    void getConcertById() throws Exception {
        when(concertService.getConcertById(1L)).thenReturn(defaultConcerts.get(0));

        mockMvc.perform(get("/api/concerts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.location").value("St. Petersburg"));
    }

    @Test
    void getAttendees() {

    }

    @Test
    void isUserAttendingConcert() {
    }

    @Test
    void getConcertsByAttendee() {
    }

    @Test
    void searchConcerts() {
    }

    @Test
    void createConcert() {
    }

    @Test
    void addAttendeeToConcert() {
    }

    @Test
    void updateConcert() {
    }

    @Test
    void removeAttendeeFromConcert() {
    }
}