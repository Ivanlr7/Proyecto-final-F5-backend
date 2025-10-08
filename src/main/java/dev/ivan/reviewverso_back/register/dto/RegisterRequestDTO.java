package dev.ivan.reviewverso_back.register.dto;

import java.util.Set;

public record RegisterRequestDTO(
    String userName,
    String email,
    String password,
    String profileImage,
    Set<String> roles


) {}