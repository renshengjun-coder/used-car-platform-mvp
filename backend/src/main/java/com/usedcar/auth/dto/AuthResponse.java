package com.usedcar.auth.dto;

public record AuthResponse(Long userId, String email, String token) {}
