package com.musement.backend.controllers;

import com.musement.backend.dto.UserDTO;
import com.musement.backend.models.User;
import com.musement.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchByUsername")
    public List<User> searchUsersByUsername(@RequestParam String username) {
        return userService.searchByUsername(username);
    }


    // current user
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrent(Principal principal) {
        if (principal == null) {
            // if principal is null, return 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // extract user from principal
        User domainUser = userService
                .getUserByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO dto = new UserDTO(
                domainUser.getUsername(),
                domainUser.getEmail(),
                domainUser.getNickname(),
                domainUser.getBio(),
                domainUser.getProfilePicture()
        );
        return ResponseEntity.ok(dto);
    }

    // current user, with @AuthenticationPrincipal, by id
    @PreAuthorize("#id == principal.id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }


    @PreAuthorize("#id == principal.id")
    @PatchMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable Long id,
            @RequestBody UserDTO dto
    ) {
        System.out.println("Update user, controller: " + dto);
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }
}
