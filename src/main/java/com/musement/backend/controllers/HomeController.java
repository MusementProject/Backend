package com.musement.backend.controllers;

import com.musement.backend.models.User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
//@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {})
public class HomeController {
    @GetMapping
    public String home() {
        return "Musement server is running";
    }
}
