package com.musement.backend.services;

import com.musement.backend.dto.UserUpdateDTO;
import com.musement.backend.exceptions.UserAlreadyExistsException;
import com.musement.backend.models.User;
import com.musement.backend.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        userService = new UserService(userRepository, passwordEncoder);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("currentUser", "password"));
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
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


    /**
     * Test that the updateUser() method updates the user's email successfully.
     */
    @Test
    public void testUpdateUserSuccess() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("currentUser"); // matches the current authenticated user
        existingUser.setEmail("old@example.com");

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(userId, updateDTO);

        assertEquals("new@example.com", updatedUser.getEmail());
        verify(userRepository).save(existingUser);
    }

    /**
     * Test that the updateUser() method throws an AccessDeniedException
     * when the user tries to update another user's data.
     */
    @Test
    public void testUpdateUserAccessDenied() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("otherUser"); // does not match the current authenticated user
        existingUser.setEmail("old@example.com");

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, updateDTO));
    }
}
