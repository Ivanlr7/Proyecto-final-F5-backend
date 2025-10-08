package dev.ivan.reviewverso_back.register;

import dev.ivan.reviewverso_back.register.dto.RegisterRequestDTO;
import dev.ivan.reviewverso_back.register.dto.RegisterResponseDTO;
import dev.ivan.reviewverso_back.register.dto.RegisterMapper;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class RegisterMapperTest {
    @Test
    void toRegisterResponseDTO_mapsFieldsCorrectly() {
        ProfileEntity profile = ProfileEntity.builder()
                .profileImage("img.png")
                .build();
        RoleEntity role = RoleEntity.builder().name("ROLE_USER").build();
        UserEntity user = UserEntity.builder()
                .idUser(1L)
                .userName("usuario")
                .email("correo@ejemplo.com")
                .profile(profile)
                .roles(Set.of(role))
                .build();

        RegisterResponseDTO dto = RegisterMapper.toRegisterResponseDTO(user);
        assertThat(dto.idUser(), is(1L));
        assertThat(dto.userName(), is("usuario"));
        assertThat(dto.email(), is("correo@ejemplo.com"));
        assertThat(dto.profileImage(), is("img.png"));
        assertThat(dto.roles(), contains("ROLE_USER"));
    }

    @Test
    void toRegisterResponseDTO_handlesNullProfileImage() {
        UserEntity user = UserEntity.builder()
                .idUser(2L)
                .userName("otro")
                .email("otro@ejemplo.com")
                .profile(ProfileEntity.builder().profileImage(null).build())
                .roles(Set.of(RoleEntity.builder().name("ROLE_ADMIN").build()))
                .build();
        RegisterResponseDTO dto = RegisterMapper.toRegisterResponseDTO(user);
        assertThat(dto.profileImage(), nullValue());
        assertThat(dto.roles(), contains("ROLE_ADMIN"));
    }

    @Test
    void toUserEntity_mapsFieldsCorrectly() {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", "img.png", Set.of("ROLE_USER"));
        ProfileEntity profile = ProfileEntity.builder().profileImage("img.png").build();
        UserEntity user = RegisterMapper.toUserEntity(dto, "hashedpass", profile);
        assertThat(user.getUserName(), is("usuario"));
        assertThat(user.getEmail(), is("correo@ejemplo.com"));
        assertThat(user.getPassword(), is("hashedpass"));
        assertThat(user.getProfile(), is(profile));
    }

    @Test
    void toProfileEntity_mapsFieldsCorrectly() {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", "img.png", Set.of("ROLE_USER"));
        ProfileEntity profile = RegisterMapper.toProfileEntity(dto);
        assertThat(profile.getProfileImage(), is("img.png"));
    }
}
