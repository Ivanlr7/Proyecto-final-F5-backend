package dev.ivan.reviewverso_back.user.dtos;

import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.role.RoleRepository;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    private final RoleRepository roleRepository;

    public UserMapper(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public UserEntity userRequestDtoToUserEntity(UserRequestDTO userRequestDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userRequestDTO.userName());
        userEntity.setEmail(userRequestDTO.email());
        userEntity.setPassword(userRequestDTO.password());

        if (userRequestDTO.profileImage() != null && !userRequestDTO.profileImage().isEmpty()) {
            ProfileEntity profile = new ProfileEntity();
            profile.setProfileImage(userRequestDTO.profileImage());
            profile.setUser(userEntity);
            userEntity.setProfile(profile);
        }

        Set<RoleEntity> roles = userRequestDTO.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new UserNotFoundException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());
        userEntity.setRoles(roles);

        return userEntity;
    }


    public UserResponseDTO userEntityToUserResponseDto(UserEntity userEntity) {
        Set<String> roles = userEntity.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());

        String profileImage = userEntity.getProfile() != null ? 
                userEntity.getProfile().getProfileImage() : null;

        return new UserResponseDTO(
                userEntity.getIdUser(),
                userEntity.getUserName(),
                userEntity.getEmail(),
                profileImage,
                roles
        );
    }
}