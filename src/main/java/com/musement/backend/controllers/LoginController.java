package com.musement.backend.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.musement.backend.config.GoogleConfig;
import com.musement.backend.config.JwtTokenProvider;
import com.musement.backend.dto.LoginGoogleRequestDTO;
import com.musement.backend.dto.LoginRequestDTO;
import com.musement.backend.dto.LoginResponseDTO;
import com.musement.backend.services.LoginService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    private final LoginService loginService;
    private static final GoogleConfig googleConfig = new GoogleConfig();
    private final JwtTokenProvider jwtProvider;

    public LoginController(LoginService loginService,
                           JwtTokenProvider jwtProvider) {
        this.loginService = loginService;
        this.jwtProvider = jwtProvider;
    }

    /**
     * обычный логин по паролю
     */
    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = loginService.login(request);
        String jwt = jwtProvider.generateToken(response.getUsername());
        response.setToken(jwt);
        System.out.println(jwt);
        return ResponseEntity.ok(response);
    }

    /**
     * гугловскйи логин
     */
    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> loginWithGoogle(@Valid @RequestBody LoginGoogleRequestDTO request) {
        String token = request.getToken();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), jsonFactory)
                .setAudience(Collections.singletonList(googleConfig.getClientId()))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            GoogleIdToken.Payload tokenPayload = idToken.getPayload();
            String googleUserId = tokenPayload.getSubject();
            String email = tokenPayload.getEmail();
            String name = tokenPayload.get("name").toString();

            LoginResponseDTO response = loginService.loginGoogleUser(googleUserId, email, name);
            String jwt = jwtProvider.generateToken(response.getUsername());
            response.setToken(jwt);  // теперь генерится свой токен
//            response.setToken(token); // было
            System.out.println(googleConfig.getClientId());
            return ResponseEntity.ok(response);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponseDTO());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
