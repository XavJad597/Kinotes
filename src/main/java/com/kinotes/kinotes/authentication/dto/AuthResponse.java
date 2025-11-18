package com.kinotes.kinotes.authentication.dto;

public record AuthResponse(
        String token,
        String username,
        String role ) {
}
