package com.musement.backend.services;

import com.musement.backend.dto.LoginGoogleRequestDTO;
import com.musement.backend.dto.LoginRequestDTO;
import com.musement.backend.dto.LoginResponseDTO;
import com.musement.backend.dto.RegistrationResponseDTO;
import com.musement.backend.exceptions.WrongPasswordException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO request)
            throws UsernameNotFoundException, WrongPasswordException {
        User user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password");
        }
        // TODO generate JWT token

        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }

    public LoginResponseDTO loginGoogleUser(String googleUserId, String googleUserEmail, String googleUserName){
        User user = userRepository.findUserByEmail(googleUserEmail).orElse(new User());
        if (user.getEmail() == null){
            user.setUsername(googleUserName);
            user.setEmail(googleUserEmail);
            user.setGoogleId(googleUserId);
            user.setPassword("pass");
            userRepository.save(user);
        }

        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
}
