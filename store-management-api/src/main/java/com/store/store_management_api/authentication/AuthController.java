package com.store.store_management_api.authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Received registration request for username: {}", request.getUsername());
        String responseMessage = authService.register(request);
        logger.info("User {} registered successfully via API.", request.getUsername());
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        logger.info("Received login request for username: {}", request.getUsername());
        AuthResponse authResponse = authService.login(request);
        logger.info("User {} logged in successfully via API.", request.getUsername());
        return ResponseEntity.ok(authResponse);
    }
}