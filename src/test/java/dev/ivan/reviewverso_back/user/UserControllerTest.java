package dev.ivan.reviewverso_back.user;

import dev.ivan.reviewverso_back.user.dtos.UserRequestDTO;
import dev.ivan.reviewverso_back.user.dtos.UserResponseDTO;
import dev.ivan.reviewverso_back.user.exceptions.UserAccessDeniedException;
import dev.ivan.reviewverso_back.implementations.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private IUserService<UserResponseDTO, UserRequestDTO> userService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Authentication mockAuth(Long userId, String userName, boolean isAdmin) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(userName);
        when(auth.getPrincipal()).thenReturn(userName); 
        java.util.Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (isAdmin) {
            authorities.add((GrantedAuthority) () -> "ROLE_ADMIN");
        } else {
            authorities.add((GrantedAuthority) () -> "ROLE_USER");
        }
    when(auth.getAuthorities()).thenAnswer(invocation -> authorities);
        return auth;
    }

    @Test
    @DisplayName("getAllUsers retorna lista de usuarios")
    void getAllUsers_returnsList() {
        List<UserResponseDTO> users = List.of(mock(UserResponseDTO.class));
        when(userService.getEntities()).thenReturn(users);
        ResponseEntity<List<UserResponseDTO>> response = userController.getAllUsers();
        assertThat(response.getStatusCode().value(), is(200));
        assertThat(response.getBody(), is(users));
    }

    @Test
    @DisplayName("getUserById permite admin o self, deniega otros")
    void getUserById_accessControl() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("u").build();
        UserEntity admin = UserEntity.builder().idUser(99L).userName("admin").build();
        UserEntity other = UserEntity.builder().idUser(2L).userName("other").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUserName("u")).thenReturn(Optional.of(user));
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findByUserName("other")).thenReturn(Optional.of(other));
        when(userService.getByID(1L)).thenReturn(mock(UserResponseDTO.class));
      
        Authentication adminAuth = mockAuth(99L, "admin", true);
        ResponseEntity<UserResponseDTO> adminResp = userController.getUserById(1L, adminAuth);
        assertThat(adminResp.getStatusCode().value(), is(200));

        Authentication selfAuth = mockAuth(1L, "u", false);
        ResponseEntity<UserResponseDTO> selfResp = userController.getUserById(1L, selfAuth);
        assertThat(selfResp.getStatusCode().value(), is(200));
        Authentication otherAuth = mockAuth(2L, "other", false);
        try {
            userController.getUserById(1L, otherAuth);
        } catch (UserAccessDeniedException ex) {
            assertThat(ex.getMessage(), containsString("No puedes acceder"));
            return;
        }
        throw new AssertionError("Se esperaba UserAccessDeniedException");
    }

    @Test
    @DisplayName("updateUser permite admin o self, deniega otros")
    void updateUser_accessControl() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("u").build();
        UserEntity admin = UserEntity.builder().idUser(99L).userName("admin").build();
        UserEntity other = UserEntity.builder().idUser(2L).userName("other").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUserName("u")).thenReturn(Optional.of(user));
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findByUserName("other")).thenReturn(Optional.of(other));
        when(userService.updateEntity(eq(1L), any(), any())).thenReturn(mock(UserResponseDTO.class));
        UserRequestDTO dto = mock(UserRequestDTO.class);
        // Admin puede actualizar cualquier usuario
        Authentication adminAuth = mockAuth(99L, "admin", true);
        ResponseEntity<UserResponseDTO> adminResp = userController.updateUser(1L, dto, null, adminAuth);
        assertThat(adminResp.getStatusCode().value(), is(200));
        // Self puede actualizarse
        Authentication selfAuth = mockAuth(1L, "u", false);
        ResponseEntity<UserResponseDTO> selfResp = userController.updateUser(1L, dto, null, selfAuth);
        assertThat(selfResp.getStatusCode().value(), is(200));
        // Otro usuario no puede actualizar
        Authentication otherAuth = mockAuth(2L, "other", false);
        try {
            userController.updateUser(1L, dto, null, otherAuth);
        } catch (UserAccessDeniedException ex) {
            assertThat(ex.getMessage(), containsString("No puedes editar"));
            return;
        }
        throw new AssertionError("Se esperaba UserAccessDeniedException");
    }

    @Test
    @DisplayName("deleteUser permite admin o self, deniega otros")
    void deleteUser_accessControl() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("u").build();
        UserEntity admin = UserEntity.builder().idUser(99L).userName("admin").build();
        UserEntity other = UserEntity.builder().idUser(2L).userName("other").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUserName("u")).thenReturn(Optional.of(user));
        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(admin));
        when(userRepository.findByUserName("other")).thenReturn(Optional.of(other));
        // Admin puede borrar cualquier usuario
        Authentication adminAuth = mockAuth(99L, "admin", true);
        ResponseEntity<Void> adminResp = userController.deleteUser(1L, adminAuth);
        assertThat(adminResp.getStatusCode().value(), is(204));
        // Self puede borrarse
        Authentication selfAuth = mockAuth(1L, "u", false);
        ResponseEntity<Void> selfResp = userController.deleteUser(1L, selfAuth);
        assertThat(selfResp.getStatusCode().value(), is(204));
        // Otro usuario no puede borrar
        Authentication otherAuth = mockAuth(2L, "other", false);
        try {
            userController.deleteUser(1L, otherAuth);
        } catch (UserAccessDeniedException ex) {
            assertThat(ex.getMessage(), containsString("No puedes borrar"));
            return;
        }
        throw new AssertionError("Se esperaba UserAccessDeniedException");
    }

    @Test
    @DisplayName("getCurrentUser retorna el usuario actual")
    void getCurrentUser_works() {
    UserEntity user = UserEntity.builder().idUser(1L).userName("u").build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.findByUserName("u")).thenReturn(Optional.of(user));
    when(userService.getByID(1L)).thenReturn(mock(UserResponseDTO.class));
    Authentication auth = mockAuth(1L, "u", false);
    ResponseEntity<UserResponseDTO> resp = userController.getCurrentUser(auth);
    assertThat(resp.getStatusCode().value(), is(200));
    }
}
