package com.musement.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musement.backend.models.Artist;
import com.musement.backend.repositories.UserRepository;
import com.musement.backend.services.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistService artistService;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private com.musement.backend.config.JwtTokenProvider jwtTokenProvider;

    private Artist beatles;

    @BeforeEach
    void setUp() {
        beatles = new Artist();
        beatles.setId(1L);
        beatles.setName("The Beatles");
    }

    @Test
    void getAllArtists_ReturnsJsonArray() throws Exception {
        when(artistService.getAllArtists()).thenReturn(List.of(beatles));

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("The Beatles"));

        verify(artistService).getAllArtists();
    }

    @Test
    void getArtistById_Found_ReturnsArtist() throws Exception {
        when(artistService.getArtistById(1L)).thenReturn(Optional.of(beatles));

        mockMvc.perform(get("/api/artists/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("The Beatles"));

        verify(artistService).getArtistById(1L);
    }

    @Test
    void getArtistById_NotFound_Returns404() throws Exception {
        when(artistService.getArtistById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/artists/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(artistService).getArtistById(99L);
    }

    @Test
    void createArtist_ReturnsCreatedArtist() throws Exception {
        Artist toCreate = new Artist();
        toCreate.setName("Queen");
        Artist created = new Artist();
        created.setId(2L);
        created.setName("Queen");

        when(artistService.createArtist(any(Artist.class))).thenReturn(created);

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Queen"));

        ArgumentCaptor<Artist> captor = ArgumentCaptor.forClass(Artist.class);
        verify(artistService).createArtist(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Queen");
    }
}
