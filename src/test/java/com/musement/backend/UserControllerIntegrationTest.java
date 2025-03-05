package com.musement.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musement.backend.dto.UserUpdateDTO;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        user = new User();
        user.setUsername("user");
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("pass"));
    }


    @Test
    public void testCreateUser() throws Exception {
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    public void testGetAllUsersWithAuth() throws Exception {
        userRepository.save(user);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(user2);

        mockMvc.perform(get("/api/users")
                        .with(httpBasic("user", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].email").value("user@example.com"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }

    @Test
    public void testGetAllUsersUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetUser() throws Exception {
        userRepository.save(user);
        Long id = userRepository.findUserByUsername("user").get().getId();

        mockMvc.perform(get("/api/users/username/user")
                        .with(httpBasic("user", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        mockMvc.perform(get("/api/users/email/user@example.com")
                        .with(httpBasic("user", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        mockMvc.perform(get("/api/users/id/" + id)
                        .with(httpBasic("user", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));

    }

    @Test
    public void testUpdateUser() throws Exception {
        userRepository.save(user);
        Long id = userRepository.findUserByUsername("user").get().getId();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail("new_email@example.com");
        String userUpdateJson = objectMapper.writeValueAsString(userUpdateDTO);

        mockMvc.perform(patch("/api/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateJson)
                        .with(httpBasic("user", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new_email@example.com"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        userRepository.save(user);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(user2);

        Long id = userRepository.findUserByUsername("user").get().getId();

        mockMvc.perform(delete("/api/users/id/" + id)
                        .with(httpBasic("user", "pass")))
                .andExpect(status().isNoContent());

        // check that the user was deleted
        mockMvc.perform(get("/api/users/id/" + id)
                        .with(httpBasic("user2", "pass")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchByUsername() throws Exception {
        userRepository.save(user);

        List<String> usernames = List.of("hey_user", "hihi_user3", "nonono");
        usernames.forEach(username -> {
            User user = new User();
            user.setUsername(username);
            user.setEmail(username + "@example.com");
            user.setPassword(passwordEncoder.encode("pass"));
            userRepository.save(user);
        });
        mockMvc.perform(get("/api/users/searchByUsername?username=user")
                        .with(httpBasic("user", "pass")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[1].username").value("hey_user"))
                .andExpect(jsonPath("$[2].username").value("hihi_user3"))
                .andExpect(jsonPath("$[3]").doesNotExist());
    }
}
