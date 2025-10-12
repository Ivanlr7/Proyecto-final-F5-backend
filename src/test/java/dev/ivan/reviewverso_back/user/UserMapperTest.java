package dev.ivan.reviewverso_back.user;

import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.role.RoleRepository;
import dev.ivan.reviewverso_back.user.dtos.UserMapper;
import dev.ivan.reviewverso_back.user.dtos.UserRequestDTO;
import dev.ivan.reviewverso_back.user.dtos.UserResponseDTO;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private RoleRepository roleRepository;

    private UserMapper userMapper;

    private RoleEntity userRole;
    private RoleEntity adminRole;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(roleRepository);
        
        userRole = RoleEntity.builder()
                .idRole(1L)
                .name("USER")
                .build();

        adminRole = RoleEntity.builder()
                .idRole(2L)
                .name("ADMIN")
                .build();
    }

    @Test
    void userRequestDtoToUserEntity_ShouldConvertUserRequestDTOToUserEntity() {
        // Given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "testUser",
                "test@email.com",
                "password123",
                "profile-image.jpg",
                Set.of("USER")
        );

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        // When
        UserEntity result = userMapper.userRequestDtoToUserEntity(userRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("testUser");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
        assertThat(result.getPassword()).isEqualTo("password123");
        assertThat(result.getRoles()).hasSize(1);
        assertThat(result.getRoles().iterator().next().getName()).isEqualTo("USER");
        assertThat(result.getProfile()).isNotNull();
        assertThat(result.getProfile().getProfileImage()).isEqualTo("profile-image.jpg");
    }

    @Test
    void userRequestDtoToUserEntity_WithoutProfileImage_ShouldNotCreateProfile() {
        // Given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "testUser",
                "test@email.com",
                "password123",
                null,
                Set.of("USER")
        );

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        // When
        UserEntity result = userMapper.userRequestDtoToUserEntity(userRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("testUser");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
        assertThat(result.getPassword()).isEqualTo("password123");
        assertThat(result.getRoles()).hasSize(1);
        assertThat(result.getProfile()).isNull();
    }

    @Test
    void userRequestDtoToUserEntity_WithInvalidRole_ShouldThrowException() {
        // Given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "testUser",
                "test@email.com",
                "password123",
                null,
                Set.of("INVALID_ROLE")
        );

        when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userMapper.userRequestDtoToUserEntity(userRequestDTO))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Rol no encontrado: INVALID_ROLE");
    }

    @Test
    void userEntityToUserResponseDto_ShouldConvertUserEntityToResponseDTO() {
        // Given
        ProfileEntity profile = ProfileEntity.builder()
                .idProfile(1L)
                .profileImage("profile-image.jpg")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .idUser(1L)
                .userName("testUser")
                .email("test@email.com")
                .password("encodedPassword")
                .roles(Set.of(userRole, adminRole))
                .profile(profile)
                .build();

        // When
        UserResponseDTO result = userMapper.userEntityToUserResponseDto(userEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.idUser()).isEqualTo(1L);
        assertThat(result.userName()).isEqualTo("testUser");
        assertThat(result.email()).isEqualTo("test@email.com");
        assertThat(result.profileImage()).isEqualTo("profile-image.jpg");
        assertThat(result.roles()).containsExactlyInAnyOrder("USER", "ADMIN");
    }

    @Test
    void userEntityToUserResponseDto_WithoutProfile_ShouldReturnNullProfileImage() {
        // Given
        UserEntity userEntity = UserEntity.builder()
                .idUser(1L)
                .userName("testUser")
                .email("test@email.com")
                .password("encodedPassword")
                .roles(Set.of(userRole))
                .build();

        // When
        UserResponseDTO result = userMapper.userEntityToUserResponseDto(userEntity);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.profileImage()).isNull();
        assertThat(result.roles()).containsExactly("USER");
    }
}