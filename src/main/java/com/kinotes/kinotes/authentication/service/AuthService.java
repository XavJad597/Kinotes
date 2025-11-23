package com.kinotes.kinotes.authentication.service;

import com.kinotes.kinotes.authentication.dto.AuthResponse;
import com.kinotes.kinotes.authentication.dto.LoginRequest;
import com.kinotes.kinotes.authentication.dto.RegisterRequest;
import com.kinotes.kinotes.entity.User;
import com.kinotes.kinotes.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for handling authentication operations including registration and login.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(
            UserRepository userRepository,
            CustomPasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Registers a new user.
     *
     * @param request the registration request containing username, email, and password
     * @return authentication response with JWT token
     * @throws IllegalArgumentException if username or email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user with default role "owner" (since you're the only user)
        // You can change this to "user" if you want different default behavior
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.username())
                .email(request.email())
                .fullName(request.username()) // Default full name to username
                .passwordHash(passwordEncoder.encode(request.password()))
                .role("owner") // Set as owner by default
                .build();

        userRepository.save(user);

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        
        // Get user from database to retrieve role
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
    }
}
