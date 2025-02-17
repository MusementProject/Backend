package com.musement.backend.controllers;

import com.musement.backend.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class HomeController {
    @GetMapping
    public User home() {
        return new User("Nastia");
    }
}
