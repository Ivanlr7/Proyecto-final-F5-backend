package dev.ivan.reviewverso_back.user.service;

import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.user.dtos.UserRequestDTO;
import dev.ivan.reviewverso_back.user.dtos.UserResponseDTO;
import dev.ivan.reviewverso_back.user.dtos.UserMapper;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import dev.ivan.reviewverso_back.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public List<UserResponseDTO> getEntities() {
        return userRepository.findAll().stream()
                .map(user -> new UserMapper(roleRepository).userEntityToUserResponseDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getByID(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        return new UserMapper(roleRepository).userEntityToUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateEntity(Long id, UserRequestDTO dto) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));

        if (dto.userName() != null) user.setUserName(dto.userName());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.password() != null) user.setPassword(dto.password());

        if (dto.profileImage() != null) {
            if (user.getProfile() != null) {
                user.getProfile().setProfileImage(dto.profileImage());
            } else {
                var profile = new dev.ivan.reviewverso_back.profile.ProfileEntity();
                profile.setProfileImage(dto.profileImage());
                profile.setUser(user);
                user.setProfile(profile);
            }
        }

        if (dto.roles() != null && !dto.roles().isEmpty()) {
            var roles = dto.roles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new UserNotFoundException("Rol no encontrado: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        userRepository.save(user);
        return new UserMapper(roleRepository).userEntityToUserResponseDto(user);
    }

    @Override
    @Transactional
    public void deleteEntity(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public Optional<UserResponseDTO> findByEmail(String email) {
        UserMapper userMapper = new UserMapper(roleRepository);
        return userRepository.findByEmail(email)
                .map(userMapper::userEntityToUserResponseDto);
    }

    public Optional<UserResponseDTO> findByUserName(String userName) {
        UserMapper userMapper = new UserMapper(roleRepository);
        return userRepository.findByUserName(userName)
                .map(userMapper::userEntityToUserResponseDto);
    }


}
