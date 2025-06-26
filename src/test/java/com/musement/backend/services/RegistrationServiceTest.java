package com.musement.backend.services;

import com.musement.backend.dto.RegistrationRequestDTO;
import com.musement.backend.dto.RegistrationResponseDTO;
import com.musement.backend.exceptions.UserAlreadyExistsException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RegistrationService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new RegistrationService(userRepository, passwordEncoder);
    }

    @Test
    void register_NewEmail_ShouldSaveAndReturnDto() {
        var dto = new RegistrationRequestDTO("poopsen", "poopsen@example.com", "pwd");
        when(userRepository.findUserByEmail("poopsen@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("pwd")).thenReturn("ENC(pwd)");

        RegistrationResponseDTO resp = service.register(dto);
        ArgumentCaptor<User> cap = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(cap.capture());
        User saved = cap.getValue();
        assertThat(saved.getUsername()).isEqualTo("poopsen");
        assertThat(saved.getEmail()).isEqualTo("poopsen@example.com");
        assertThat(saved.getPassword()).isEqualTo("ENC(pwd)");

        assertThat(resp.getUsername()).isEqualTo("poopsen");
        assertThat(resp.getEmail()).isEqualTo("poopsen@example.com");
        assertThat(resp.getId()).isNull();
    }

    @Test
    void register_ExistingEmail_ShouldThrow() {
        var dto = new RegistrationRequestDTO("voopsen", "voopsen@example.com", "pw");
        when(userRepository.findUserByEmail("voopsen@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> service.register(dto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepository, never()).save(any());
    }
}
