package com.musement.backend.services;

import com.musement.backend.exceptions.UserAlreadyExistsException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    /**
     * Test that the createUser method saves the user when the username and email are unique.
     */
    @Test
    void createUser_ShouldSaveUser_WhenUsernameAndEmailAreUnique() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("example@gmail.com");
        user.setPassword("password");

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user, createdUser);

        // Verify that the save method was called once
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Test that the createUser() throws a UserAlreadyExistsException when the username already exists.
     */
    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("example@gmail.com");
        user.setPassword("password");
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
    }

    /**
     * Test that the createUser() throws a UserAlreadyExistsException when the email already exists.
     */
    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("example@gmail.com");
        user.setPassword("password");
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
    }

    /**
     * Test that getUserByUsername() returns the user when the user exists.
     */
    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("example@gmail.com");
        user.setPassword("password");
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername(user.getUsername());
        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
    }

    /**
     * Test that getUserByUsername() returns an empty optional when the user doesn't exist.
     */
    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserDoesNotExists() {
        when(userRepository.findUserByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserByUsername("nonexistent");
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("example@gmail.com");
        user.setPassword("password");
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}
