package com.kinotes.kinotes.authentication.dto;

import java.util.UUID;

public record AuthResponse(
        String token,
        String username,
        String role,
        UUID userId ) {
}
