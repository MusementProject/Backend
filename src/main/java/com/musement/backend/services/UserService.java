package com.musement.backend.services;

import com.musement.backend.dto.UserUpdateDTO;
import com.musement.backend.exceptions.UserAlreadyExistsException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public User createUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists.");
        }
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found."));

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
