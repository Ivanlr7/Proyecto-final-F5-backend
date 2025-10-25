package dev.ivan.reviewverso_back.user.service;

import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.user.dtos.UserRequestDTO;
import dev.ivan.reviewverso_back.user.dtos.UserResponseDTO;
import dev.ivan.reviewverso_back.user.dtos.UserMapper;
import dev.ivan.reviewverso_back.user.exceptions.UserNotFoundException;
import dev.ivan.reviewverso_back.role.RoleRepository;
import dev.ivan.reviewverso_back.role.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getEntities retorna lista de usuarios")
    void getEntities_returnsList() {
    UserEntity user = UserEntity.builder().idUser(1L).userName("u").roles(new java.util.HashSet<>()).build();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(mock(RoleEntity.class)));
        var result = userService.getEntities();
        assertThat(result, hasSize(1));
    }

    @Test
    @DisplayName("getByID retorna usuario o lanza excepción si no existe")
    void getByID_worksAndThrows() {
    UserEntity user = UserEntity.builder().idUser(1L).userName("u").roles(new java.util.HashSet<>()).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(mock(RoleEntity.class)));
        var dto = userService.getByID(1L);
        assertThat(dto, notNullValue());
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        try {
            userService.getByID(2L);
        } catch (UserNotFoundException ex) {
            assertThat(ex.getMessage(), containsString("Usuario no encontrado"));
            return;
        }
        throw new AssertionError("Se esperaba UserNotFoundException");
    }

    @Test
    @DisplayName("updateEntity actualiza campos y roles, lanza excepción si no existe")
    void updateEntity_worksAndThrows() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("u").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(mock(RoleEntity.class)));
        when(userRepository.save(any())).thenReturn(user);
        UserRequestDTO dto = mock(UserRequestDTO.class);
        when(dto.userName()).thenReturn("nuevo");
        when(dto.email()).thenReturn("e@e.com");
        when(dto.password()).thenReturn("123");
        when(dto.profileImage()).thenReturn(null);
        when(dto.roles()).thenReturn(Set.of("USER"));
        var resp = userService.updateEntity(1L, dto);
        assertThat(resp, notNullValue());
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        try {
            userService.updateEntity(2L, dto);
        } catch (UserNotFoundException ex) {
            assertThat(ex.getMessage(), containsString("Usuario no encontrado"));
            return;
        }
        throw new AssertionError("Se esperaba UserNotFoundException");
    }

    @Test
    @DisplayName("deleteEntity borra usuario o lanza excepción si no existe")
    void deleteEntity_worksAndThrows() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteEntity(1L);
        when(userRepository.existsById(2L)).thenReturn(false);
        try {
            userService.deleteEntity(2L);
        } catch (UserNotFoundException ex) {
            assertThat(ex.getMessage(), containsString("Usuario no encontrado"));
            return;
        }
        throw new AssertionError("Se esperaba UserNotFoundException");
    }

    @Test
    @DisplayName("findByEmail y findByUserName retornan Optional")
    void findByEmailAndUserName() {
    UserEntity user = UserEntity.builder().idUser(1L).userName("u").email("e@e.com").roles(new java.util.HashSet<>()).build();
        when(userRepository.findByEmail("e@e.com")).thenReturn(Optional.of(user));
        when(userRepository.findByUserName("u")).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(mock(RoleEntity.class)));
        var emailOpt = userService.findByEmail("e@e.com");
        var userNameOpt = userService.findByUserName("u");
        assertThat(emailOpt.isPresent(), is(true));
        assertThat(userNameOpt.isPresent(), is(true));
        when(userRepository.findByEmail("no@no.com")).thenReturn(Optional.empty());
        when(userRepository.findByUserName("no")).thenReturn(Optional.empty());
        assertThat(userService.findByEmail("no@no.com").isEmpty(), is(true));
        assertThat(userService.findByUserName("no").isEmpty(), is(true));
    }
}
