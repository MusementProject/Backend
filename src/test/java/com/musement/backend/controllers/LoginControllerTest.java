package com.musement.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musement.backend.config.GoogleTokenAuthenticationFilter;
import com.musement.backend.config.JwtTokenProvider;
import com.musement.backend.dto.LoginRequestDTO;
import com.musement.backend.dto.LoginResponseDTO;
import com.musement.backend.exceptions.WrongPasswordException;
import com.musement.backend.repositories.UserRepository;
import com.musement.backend.services.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private JwtTokenProvider jwtProvider;

    @MockitoBean
    private GoogleTokenAuthenticationFilter googleTokenAuthenticationFilter;
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("POST /api/login — success")
    void login_Success() throws Exception {
        var req = new LoginRequestDTO();
        req.setUsername("user");
        req.setPassword("secret");

        var svcResp = new LoginResponseDTO();
        svcResp.setId(123L);
        svcResp.setUsername("user");
        svcResp.setEmail("user@example.com");
        when(loginService.login(any(LoginRequestDTO.class))).thenReturn(svcResp);
        when(jwtProvider.generateToken("user")).thenReturn("jwt-token-xyz");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.token").value("jwt-token-xyz"));
    }

    @Test
    @DisplayName("POST /api/login — user not found → 500")
    void login_UserNotFound() throws Exception {
        var req = new LoginRequestDTO("nope", "pw");
        when(loginService.login(any())).thenThrow(new UsernameNotFoundException("not found"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        // убедимся, что JWT не генерится
        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("POST /api/login — wrong password → 500")
    void login_WrongPassword() throws Exception {
        var req = new LoginRequestDTO("user", "wrong");
        when(loginService.login(any())).thenThrow(new WrongPasswordException("Wrong password"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());

        // убедимся, что JWT не генерится
        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("POST /api/login — validation error → 400 + поле username & password")
    void login_InvalidRequest() throws Exception {
        // оба поля пустые
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.password").exists());
    }
}
