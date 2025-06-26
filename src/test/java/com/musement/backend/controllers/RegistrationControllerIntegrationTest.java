package com.musement.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musement.backend.dto.RegistrationRequestDTO;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "cloudinary.cloud_name=test",
                "cloudinary.api_key=test",
                "cloudinary.api_secret=test",
                "jwt.secret=testsecret",
                "jwt.expiration-ms=3600000"
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_Success_ShouldReturn200AndUserData() throws Exception {
        var req = new RegistrationRequestDTO("poopsen", "poopsen@example.com", "password123");
        String jsonReq = om.writeValueAsString(req);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("poopsen"))
                .andExpect(jsonPath("$.email").value("poopsen@example.com"))
                .andDo(mvc -> {
                    var users = userRepository.findAll();
                    assertThat(users).hasSize(1);
                    User saved = users.get(0);
                    assertThat(saved.getUsername()).isEqualTo("poopsen");
                    assertThat(saved.getEmail()).isEqualTo("poopsen@example.com");
                    assertThat(saved.getPassword()).isNotEqualTo("password123");
                });
    }

    @Test
    void register_MissingFields_ShouldReturn400WithErrors() throws Exception {
        String incomplete = "{ \"email\": \"bob@example.com\" }";

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incomplete))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void register_DuplicateEmail_ShouldReturn400() throws Exception {
        User u = new User();
        u.setUsername("charlie");
        u.setEmail("charlie@example.com");
        u.setPassword("irrelevant");
        userRepository.save(u);

        var req = new RegistrationRequestDTO("other", "charlie@example.com", "pass");
        String jsonReq = om.writeValueAsString(req);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }
}

