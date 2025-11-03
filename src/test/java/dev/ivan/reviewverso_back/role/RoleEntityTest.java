package dev.ivan.reviewverso_back.role;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Set;
import java.util.HashSet;
import dev.ivan.reviewverso_back.user.UserEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("RoleEntity Tests")
class RoleEntityTest {

    @Test
    void testRoleEntityBuilder() {
        // Given
        Long expectedId = 1L;
        String expectedName = "ROLE_USER";
        Set<UserEntity> expectedUsers = new HashSet<>();

        // When
        RoleEntity role = RoleEntity.builder()
            .idRole(expectedId)
            .name(expectedName)
            .users(expectedUsers)
            .build();

        // Then
        assertThat(role.getIdRole(), is(expectedId));
        assertThat(role.getName(), is(expectedName));
        assertThat(role.getUsers(), is(empty()));
    }

    @Test
    void testRoleEntityNoArgsConstructor() {
        // When
        RoleEntity role = new RoleEntity();

        // Then
        assertThat(role.getIdRole(), is(nullValue()));
        assertThat(role.getName(), is(nullValue()));
        assertThat(role.getUsers(), is(nullValue()));
    }

    @Test
    void testRoleEntityAllArgsConstructor() {
        // Given
        Long expectedId = 2L;
        String expectedName = "ROLE_ADMIN";
        Set<UserEntity> expectedUsers = new HashSet<>();

        // When
        RoleEntity role = new RoleEntity(expectedId, expectedName, expectedUsers);

        // Then
        assertThat(role.getIdRole(), is(expectedId));
        assertThat(role.getName(), is(expectedName));
        assertThat(role.getUsers(), is(notNullValue()));
        assertThat(role.getUsers(), is(empty()));
    }

    @Test
    void testRoleEntitySettersAndGetters() {
        // Given
        RoleEntity role = new RoleEntity();
        String expectedName = "ROLE_MODERATOR";
        Long expectedId = 3L;

        // When
        role.setName(expectedName);
        role.setIdRole(expectedId);

        // Then
        assertThat(role.getName(), is(expectedName));
        assertThat(role.getIdRole(), is(expectedId));
    }

    @Test
    void testRoleEntityWithMultipleUsers() {
        // Given
        UserEntity user1 = UserEntity.builder()
            .idUser(1L)
            .userName("user1")
            .email("user1@example.com")
            .build();

        UserEntity user2 = UserEntity.builder()
            .idUser(2L)
            .userName("user2")
            .email("user2@example.com")
            .build();

        Set<UserEntity> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        // When
        RoleEntity role = RoleEntity.builder()
            .idRole(1L)
            .name("ROLE_USER")
            .users(users)
            .build();

        // Then
        assertThat(role.getUsers(), hasSize(2));
        assertThat(role.getUsers(), containsInAnyOrder(user1, user2));
    }

    @Test
    void testCreateUserRole() {
        // When
        RoleEntity role = RoleEntity.builder()
            .name("ROLE_USER")
            .build();

        // Then
        assertThat(role.getName(), is("ROLE_USER"));
    }

    @Test
    void testCreateAdminRole() {
        // When
        RoleEntity role = RoleEntity.builder()
            .name("ROLE_ADMIN")
            .build();

        // Then
        assertThat(role.getName(), is("ROLE_ADMIN"));
    }

    @Test
    void testRoleEntityWithEmptyUsers() {
        // When
        RoleEntity role = RoleEntity.builder()
            .idRole(1L)
            .name("ROLE_USER")
            .users(Set.of())
            .build();

        // Then
        assertThat(role.getUsers(), is(empty()));
    }

    @Test
    void testUpdateRoleUsers() {
        // Given
        RoleEntity role = RoleEntity.builder()
            .idRole(1L)
            .name("ROLE_USER")
            .users(new HashSet<>())
            .build();

        UserEntity newUser = UserEntity.builder()
            .idUser(1L)
            .userName("newUser")
            .email("newuser@example.com")
            .build();

        // When
        role.getUsers().add(newUser);

        // Then
        assertThat(role.getUsers(), hasSize(1));
        assertThat(role.getUsers(), contains(newUser));
    }
}
