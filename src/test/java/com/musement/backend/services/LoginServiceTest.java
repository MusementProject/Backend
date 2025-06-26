package com.musement.backend.services;

import com.musement.backend.dto.LoginRequestDTO;
import com.musement.backend.dto.LoginResponseDTO;
import com.musement.backend.exceptions.WrongPasswordException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setUsername("anya");
        req.setPassword("pass123");

        User user = new User();
        user.setId(42L);
        user.setUsername("anya");
        user.setEmail("anya@example.com");
        user.setPassword("encoded");

        when(userRepository.findUserByUsername("anya")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encoded")).thenReturn(true);

        LoginResponseDTO resp = loginService.login(req);

        assertEquals(42L, resp.getId());
        assertEquals("anya", resp.getUsername());
        assertEquals("anya@example.com", resp.getEmail());
    }

    @Test
    void login_UserNotFound_Throws() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setUsername("nobody");
        req.setPassword("x");
        when(userRepository.findUserByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                loginService.login(req)
        );
    }

    @Test
    void login_WrongPassword_Throws() {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setUsername("anya");
        req.setPassword("wrong");

        User user = new User();
        user.setPassword("enc");
        when(userRepository.findUserByUsername("anya")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "enc")).thenReturn(false);

        assertThrows(WrongPasswordException.class, () ->
                loginService.login(req)
        );
    }

    @Test
    void loginGoogleUser_NewAndSave() {
        String googleId = "gid123";
        String email = "new@user.com";
        String name = "NewUser";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        ArgumentCaptor<User> userCap = ArgumentCaptor.forClass(User.class);

        LoginResponseDTO dto = loginService.loginGoogleUser(googleId, email, name);

        verify(userRepository).save(userCap.capture());
        User saved = userCap.getValue();
        assertEquals(googleId, saved.getGoogleId());
        assertEquals(email, saved.getEmail());
        assertEquals(name, saved.getUsername());
        assertEquals(email, dto.getEmail());
        assertEquals(name, dto.getUsername());
    }

    @Test
    void loginGoogleUser_Existing_NoSave() {
        User existing = new User();
        existing.setId(7L);
        existing.setEmail("exists@u.com");
        existing.setUsername("ExistsUser");

        when(userRepository.findUserByEmail("exists@u.com"))
                .thenReturn(Optional.of(existing));

        LoginResponseDTO dto = loginService.loginGoogleUser(
                "ignoredGoogleId",
                "exists@u.com",
                "IgnoredName"
        );

        verify(userRepository, never()).save(any());
        assertEquals(7L, dto.getId());
        assertEquals("ExistsUser", dto.getUsername());
        assertEquals("exists@u.com", dto.getEmail());
    }
}
