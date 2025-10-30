package dev.ivan.reviewverso_back.user;

import dev.ivan.reviewverso_back.profile.ProfileEntity;
import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        // Crear rol USER si no existe
        userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });
    }

    @Test
    @DisplayName("Guardar y recuperar usuario por ID")
    void saveAndFindById() {
        // Given
        UserEntity user = UserEntity.builder()
                .userName("testuser")
                .email("test@example.com")
                .password("encodedpassword")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();

        // When
        UserEntity savedUser = userRepository.save(user);
        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getIdUser());

        // Then
        assertThat(foundUser.isPresent(), is(true));
        assertThat(foundUser.get().getUserName(), is("testuser"));
        assertThat(foundUser.get().getEmail(), is("test@example.com"));
        assertThat(foundUser.get().getRoles(), hasSize(1));
    }

    @Test
    @DisplayName("Buscar usuario por email")
    void findByEmail() {
        // Given
        UserEntity user = UserEntity.builder()
                .userName("john")
                .email("john@example.com")
                .password("pass123")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        userRepository.save(user);

        // When
        Optional<UserEntity> found = userRepository.findByEmail("john@example.com");
        Optional<UserEntity> notFound = userRepository.findByEmail("notexist@example.com");

        // Then
        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getUserName(), is("john"));
        assertThat(notFound.isEmpty(), is(true));
    }

    @Test
    @DisplayName("Buscar usuario por userName")
    void findByUserName() {
        // Given
        UserEntity user = UserEntity.builder()
                .userName("johndoe")
                .email("johndoe@example.com")
                .password("pass123")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        userRepository.save(user);

        // When
        Optional<UserEntity> found = userRepository.findByUserName("johndoe");
        Optional<UserEntity> notFound = userRepository.findByUserName("notexist");

        // Then
        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getEmail(), is("johndoe@example.com"));
        assertThat(notFound.isEmpty(), is(true));
    }

    @Test
    @DisplayName("Guardar usuario con perfil y profileImage")
    void saveUserWithProfile() {
        // Given
        ProfileEntity profile = ProfileEntity.builder()
                .profileImage("avatar.png")
                .build();

        UserEntity user = UserEntity.builder()
                .userName("userprofile")
                .email("userprofile@example.com")
                .password("pass123")
                .roles(new HashSet<>(Set.of(userRole)))
                .profile(profile)
                .build();

        profile.setUser(user);

        // When
        UserEntity savedUser = userRepository.save(user);
        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getIdUser());

        // Then
        assertThat(foundUser.isPresent(), is(true));
        assertThat(foundUser.get().getProfile(), notNullValue());
        assertThat(foundUser.get().getProfile().getProfileImage(), is("avatar.png"));
    }

    @Test
    @DisplayName("Eliminar usuario por ID")
    void deleteById() {
        // Given
        UserEntity user = UserEntity.builder()
                .userName("todelete")
                .email("todelete@example.com")
                .password("pass123")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        UserEntity savedUser = userRepository.save(user);
        Long userId = savedUser.getIdUser();

        // When
        userRepository.deleteById(userId);
        Optional<UserEntity> foundUser = userRepository.findById(userId);

        // Then
        assertThat(foundUser.isEmpty(), is(true));
    }

    @Test
    @DisplayName("Actualizar usuario existente")
    void updateUser() {
        // Given
        UserEntity user = UserEntity.builder()
                .userName("oldname")
                .email("old@example.com")
                .password("pass123")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        UserEntity savedUser = userRepository.save(user);

        // When
        savedUser.setUserName("newname");
        savedUser.setEmail("new@example.com");
        userRepository.save(savedUser);
        Optional<UserEntity> updatedUser = userRepository.findById(savedUser.getIdUser());

        // Then
        assertThat(updatedUser.isPresent(), is(true));
        assertThat(updatedUser.get().getUserName(), is("newname"));
        assertThat(updatedUser.get().getEmail(), is("new@example.com"));
    }

    @Test
    @DisplayName("Verificar que email es único (constraint)")
    void emailShouldBeUnique() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .userName("user1")
                .email("duplicate@example.com")
                .password("pass123")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
        userRepository.save(user1);

        UserEntity user2 = UserEntity.builder()
                .userName("user2")
                .email("duplicate@example.com")
                .password("pass456")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();

  
        Optional<UserEntity> existingUser = userRepository.findByEmail("duplicate@example.com");
        assertThat(existingUser.isPresent(), is(true));
        assertThat(existingUser.get().getUserName(), is("user1"));
    }

    @Test
    @DisplayName("Listar todos los usuarios")
    void findAll() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .userName("user1")
                .email("user1@example.com")
                .password("pass123")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();

        UserEntity user2 = UserEntity.builder()
                .userName("user2")
                .email("user2@example.com")
                .password("pass456")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // When
        var users = userRepository.findAll();

        // Then
        assertThat(users, hasSize(greaterThanOrEqualTo(2)));
    }
}
