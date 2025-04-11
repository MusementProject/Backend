package com.musement.backend.services;

import com.musement.backend.dto.UserUpdateDTO;
import com.musement.backend.exceptions.UserAlreadyExistsException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    public UserUpdateDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found."));

        // TODO: add this check (cur.user == user) everywhere

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("You are not allowed to update this user");
        }

        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getTelegram() != null) user.setTelegram(dto.getTelegram());

        User updatedUser = userRepository.save(user);
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername(updatedUser.getUsername());
        userUpdateDTO.setNickname(updatedUser.getNickname());
        userUpdateDTO.setBio(updatedUser.getBio());
        userUpdateDTO.setTelegram(updatedUser.getTelegram());
        return userUpdateDTO;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Returns a list of users whose username contains the given username.
     *
     * @param username part of Username to search for
     * @return List of users
     */
    public List<User> searchByUsername(String username) {
        return userRepository.findUserByUsernameContainingIgnoreCase(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
