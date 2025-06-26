package com.musement.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musement.backend.dto.LoginRequestDTO;
import com.musement.backend.dto.LoginResponseDTO;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",

                "cloudinary.cloud_name=test",
                "cloudinary.api_key=test",
                "cloudinary.api_secret=test",
                "jwt.secret=testsecret",
                "jwt.expiration-ms=3600000",
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User u = new User();
        u.setUsername("poopsen");
        u.setEmail("poopsen@example.com");
        u.setPassword(passwordEncoder.encode("secret"));
        userRepository.save(u);
    }

    @Test
    void login_Success() throws Exception {
        var req = new LoginRequestDTO("poopsen", "secret");
        String jsonReq = om.writeValueAsString(req);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("poopsen"))
                .andExpect(jsonPath("$.email").value("poopsen@example.com"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andDo(mvcResult -> {
                    var resp = om.readValue(mvcResult.getResponse().getContentAsString(), LoginResponseDTO.class);
                    assertThat(resp.getToken()).startsWith("ey"); // JWT token should start with "ey"
                });
    }

    @Test
    void login_WrongPassword_ShouldReturn500() throws Exception {
        var req = new LoginRequestDTO("poopsen", "badpass");
        String jsonReq = om.writeValueAsString(req);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_UserNotFound_ShouldReturn500() throws Exception {
        var req = new LoginRequestDTO("nobody", "whatever");
        String jsonReq = om.writeValueAsString(req);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonReq))
                .andExpect(status().isInternalServerError());
    }
}
