package dev.ivan.reviewverso_back.auth.dto;

import java.util.Set;

public record LoginResponseDTO(
    String token,
    String type,
    Long idUser,
    String userName,
    String email,
    Set<String> roles
) {
    // Constructor convenience para tipo Bearer
    public LoginResponseDTO(String token, Long idUser, String userName, String email, Set<String> roles) {
        this(token, "Bearer", idUser, userName, email, roles);
    }
}