package dev.ivan.reviewverso_back.register.dto;

import java.util.Set;

public record RegisterResponseDTO(
    Long idUser,
    String userName,
    String email,
    String profileImage,
    Set<String> roles
)