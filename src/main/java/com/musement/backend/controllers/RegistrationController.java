package com.musement.backend.controllers;

import com.musement.backend.dto.RegistrationRequestDTO;
import com.musement.backend.dto.RegistrationResponseDTO;
import com.musement.backend.services.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody RegistrationRequestDTO request) {
        RegistrationResponseDTO response = registrationService.register(request);
        return ResponseEntity.ok(response);
    }
}
