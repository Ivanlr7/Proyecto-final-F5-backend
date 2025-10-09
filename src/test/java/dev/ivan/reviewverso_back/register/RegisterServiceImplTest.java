package dev.ivan.reviewverso_back.register;

import dev.ivan.reviewverso_back.register.dto.RegisterRequestDTO;
import dev.ivan.reviewverso_back.register.service.RegisterServiceImpl;
import dev.ivan.reviewverso_back.register.dto.RegisterResponseDTO;
import dev.ivan.reviewverso_back.register.exceptions.RegisterIllegalArgumentException;
import dev.ivan.reviewverso_back.user.UserRepository;
import dev.ivan.reviewverso_back.role.RoleRepository;
import dev.ivan.reviewverso_back.file.FileStorageService;
import dev.ivan.reviewverso_back.profile.ProfileEntity;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.role.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegisterServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private FileStorageService fileStorageService;
    @InjectMocks
    private RegisterServiceImpl registerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_throwsException_whenEmailExists() {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", null, Set.of("ROLE_USER"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(UserEntity.class)));
        assertThrows(RegisterIllegalArgumentException.class, () -> registerService.register(dto, null));
    }

    @Test
    void register_throwsException_whenUserNameExists() {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", null, Set.of("ROLE_USER"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(mock(UserEntity.class)));
        assertThrows(RegisterIllegalArgumentException.class, () -> registerService.register(dto, null));
    }

    @Test
    void register_throwsException_whenRoleNotFound() {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", null, Set.of("ROLE_USER"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThrows(RegisterIllegalArgumentException.class, () -> registerService.register(dto, null));
    }

    @Test
    void register_throwsException_whenImageFails() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", null, Set.of("ROLE_USER"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(mock(RoleEntity.class)));
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileStorageService.storeFile(any())).thenThrow(new java.io.IOException("fail"));
        assertThrows(RegisterIllegalArgumentException.class, () -> registerService.register(dto, file));
    }

    @Test
    void register_success_withImage() throws Exception {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", null, Set.of("ROLE_USER"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        RoleEntity role = new RoleEntity();
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileStorageService.storeFile(any())).thenReturn("test.png");

        RegisterResponseDTO response = registerService.register(dto, file);
        assertThat(response, notNullValue());
        assertThat(response.profileImage(), is("test.png"));
    }

    @Test
    void register_success_withoutImage() {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario", "correo@ejemplo.com", "1234", null, Set.of("ROLE_USER"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        RoleEntity role = new RoleEntity();
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RegisterResponseDTO response = registerService.register(dto, null);
        assertThat(response, notNullValue());
        assertThat(response.profileImage(), nullValue());
    }
}
