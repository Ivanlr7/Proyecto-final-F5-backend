package dev.ivan.reviewverso_back.security;

import dev.ivan.reviewverso_back.role.RoleEntity;
import dev.ivan.reviewverso_back.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SecurityUserTest {
    @Test
    @DisplayName("getAuthorities retorna roles como GrantedAuthority")
    void getAuthorities_returnsRoles() {
        RoleEntity role1 = RoleEntity.builder().idRole(1L).name("ROLE_USER").build();
        RoleEntity role2 = RoleEntity.builder().idRole(2L).name("ROLE_ADMIN").build();
        UserEntity user = UserEntity.builder()
                .idUser(10L)
                .userName("testuser")
                .email("test@example.com")
                .password("secret")
                .roles(Set.of(role1, role2))
                .build();
        SecurityUser securityUser = new SecurityUser(user);
        assertThat(securityUser.getAuthorities(), hasSize(2));
        assertThat(securityUser.getAuthorities(), hasItem(hasProperty("authority", is("ROLE_USER"))));
        assertThat(securityUser.getAuthorities(), hasItem(hasProperty("authority", is("ROLE_ADMIN"))));
    }

    @Test
    @DisplayName("getUsername, getPassword, getUserId, getEmail funcionan")
    void getters_work() {
        UserEntity user = UserEntity.builder()
                .idUser(42L)
                .userName("usuario")
                .email("correo@x.com")
                .password("clave")
                .roles(Set.of())
                .build();
        SecurityUser securityUser = new SecurityUser(user);
        assertThat(securityUser.getUsername(), is("usuario"));
        assertThat(securityUser.getPassword(), is("clave"));
        assertThat(securityUser.getUserId(), is(42L));
        assertThat(securityUser.getEmail(), is("correo@x.com"));
        assertThat(securityUser.getUser(), is(user));
    }

    @Test
    @DisplayName("isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled retornan true")
    void account_flags_are_true() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("u").email("e").password("p").roles(Set.of()).build();
        SecurityUser securityUser = new SecurityUser(user);
        assertThat(securityUser.isAccountNonExpired(), is(true));
        assertThat(securityUser.isAccountNonLocked(), is(true));
        assertThat(securityUser.isCredentialsNonExpired(), is(true));
        assertThat(securityUser.isEnabled(), is(true));
    }
}
