package dev.ivan.reviewverso_back.user.dtos;

import java.util.Set;

public record UserRequestDTO(
    String userName,
    String email,
    String password,
    String profileImage,
    Set<String> roles
    
) {}