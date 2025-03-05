package com.musement.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musement.backend.models.User;
import com.musement.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    private User makeUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    @Test
    @WithMockUser
    void getAllUsers() throws Exception {
        User user1 = makeUser(1L, "user1", "user1@example.com");
        User user2 = makeUser(2L, "user2", "user2@exmaple.com");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(user1, user2))));
    }

    @Test
    @WithMockUser
    void getUserByUsername() throws Exception {
        User user1 = makeUser(1L, "anna", "anna@example.com");
        when(userService.getUserByUsername("anna")).thenReturn(java.util.Optional.of(user1));

        mockMvc.perform(get("/api/users/username/anna"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    @WithMockUser
    void getUserById() throws Exception {
        User user1 = makeUser(10L, "anna", "anna@example.com");
        when(userService.getUserById(10L)).thenReturn(java.util.Optional.of(user1));

        mockMvc.perform(get("/api/users/id/10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    @WithMockUser
    void getUserByEmail() throws Exception {
        User user1 = makeUser(1L, "anna", "anna@example.com");
        when(userService.getUserByEmail("anna@example.com")).thenReturn(java.util.Optional.of(user1));

        mockMvc.perform(get("/api/users/email/anna@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    void createUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void searchUsersByUsername() {
    }
}