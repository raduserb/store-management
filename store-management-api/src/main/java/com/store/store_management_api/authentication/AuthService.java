package com.store.store_management_api.authentication;

import com.store.store_management_api.exception.UserAlreadyExistsException;
import com.store.store_management_api.security.JwtUtil;
import com.store.store_management_api.user.Role;
import com.store.store_management_api.user.User;
import com.store.store_management_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public String register(RegisterRequest request) {
        log.info("Attempting to register user with username: {}", request.getUsername());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("User registration failed: Username {} is already taken.", request.getUsername());
            throw new UserAlreadyExistsException("Username is already taken: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role assignedRole = (request.getRole() != null) ? request.getRole() : Role.USER;
        user.setRole(assignedRole);
        log.debug("Assigning role {} to user {}", assignedRole, request.getUsername());

        userRepository.save(user);
        log.info("User {} registered successfully.", request.getUsername());

        return "User registered successfully.";
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Attempting to log in user with username: {}", request.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        log.debug("User {} authenticated successfully.", request.getUsername());

        String token = jwtUtil.generateToken(request.getUsername());
        log.info("User {} logged in successfully. Token generated.", request.getUsername());
        return new AuthResponse(token);
    }
}