package dev.ivan.reviewverso_back.register.dto;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import java.util.Collections;
import java.util.stream.Collectors;

public class RegisterMapper {
    public static UserEntity toUserEntity(RegisterRequestDTO dto, String encodedPassword, ProfileEntity profile) {
        return UserEntity.builder()
                .userName(dto.userName())
                .email(dto.email())
                .password(encodedPassword)
                .profile(profile)
                .build();
    }

    public static ProfileEntity toProfileEntity(RegisterRequestDTO dto) {
        return ProfileEntity.builder()
                .profileImage(dto.profileImage())
                .build();
    }

    public static RegisterResponseDTO toRegisterResponseDTO(UserEntity user) {
        return new RegisterResponseDTO(
                user.getIdUser(),
                user.getUserName(),
                user.getEmail(),
                user.getProfile() != null ? user.getProfile().getProfileImage() : null,
                user.getRoles() != null ? user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()) : Collections.emptySet()
        );
    }
}