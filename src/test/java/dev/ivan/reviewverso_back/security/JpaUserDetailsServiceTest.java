package dev.ivan.reviewverso_back.security;

import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class JpaUserDetailsServiceTest {
    @Test
    @DisplayName("loadUserByUsername busca por email y retorna UserDetails")
    void loadUserByUsername_email() {
        UserEntity user = UserEntity.builder().idUser(1L).userName("u").email("e@x.com").password("p").roles(Set.of()).build();
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByEmail("e@x.com")).thenReturn(Optional.of(user));
        JpaUserDetailsService service = new JpaUserDetailsService(repo);
        UserDetails details = service.loadUserByUsername("e@x.com");
        assertThat(details.getUsername(), is("u"));
        assertThat(details.getPassword(), is("p"));
    }

    @Test
    @DisplayName("loadUserByUsername busca por userName si no encuentra email")
    void loadUserByUsername_userName() {
        UserEntity user = UserEntity.builder().idUser(2L).userName("usuario").email("correo@x.com").password("clave").roles(Set.of()).build();
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByEmail("usuario")).thenReturn(Optional.empty());
        when(repo.findByUserName("usuario")).thenReturn(Optional.of(user));
        JpaUserDetailsService service = new JpaUserDetailsService(repo);
        UserDetails details = service.loadUserByUsername("usuario");
        assertThat(details.getUsername(), is("usuario"));
        assertThat(details.getPassword(), is("clave"));
    }

    @Test
    @DisplayName("lanza UsernameNotFoundException si no existe usuario")
    void loadUserByUsername_notFound() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByEmail("nope")).thenReturn(Optional.empty());
        when(repo.findByUserName("nope")).thenReturn(Optional.empty());
        JpaUserDetailsService service = new JpaUserDetailsService(repo);
        try {
            service.loadUserByUsername("nope");
        } catch (UsernameNotFoundException ex) {
            assertThat(ex.getMessage(), containsString("nope"));
            return;
        }
        throw new AssertionError("Se esperaba UsernameNotFoundException");
    }
}
