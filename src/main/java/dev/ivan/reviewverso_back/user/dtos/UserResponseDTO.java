package dev.ivan.reviewverso_back.user.dtos;

import java.util.Set;

public record UserResponseDTO (
    Long idUser,
    String userName,
    String email,
    String profileImage,
    Set<String> roles
) {}
