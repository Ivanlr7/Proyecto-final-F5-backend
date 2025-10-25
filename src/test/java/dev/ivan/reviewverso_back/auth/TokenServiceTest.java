package dev.ivan.reviewverso_back.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TokenServiceTest {
    private JwtEncoder jwtEncoder;
    private JwtDecoder jwtDecoder;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        jwtEncoder = mock(JwtEncoder.class);
        jwtDecoder = mock(JwtDecoder.class);
        tokenService = new TokenService(jwtEncoder, jwtDecoder);
    }

    @Test
    @DisplayName("generateToken incluye claims básicos y roles")
    void generateToken_includesClaimsAndRoles() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
    Collection<GrantedAuthority> authorities = new java.util.HashSet<>();
    authorities.add((GrantedAuthority) () -> "ROLE_USER");
    authorities.add((GrantedAuthority) () -> "ROLE_ADMIN");
    when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
    when(authentication.getPrincipal()).thenReturn(new FakeSecurityUser(42L, "test@example.com"));

    Jwt jwt = mock(Jwt.class);
    when(jwt.getTokenValue()).thenReturn("mocked.jwt.token");
    when(jwtEncoder.encode(any())).thenReturn(jwt);

    String token = tokenService.generateToken(authentication);
    assertThat(token, is("mocked.jwt.token"));
    }

    @Test
    @DisplayName("getTokenExpiration decodifica y retorna la expiración")
    void getTokenExpiration_returnsExpiration() {
        Instant exp = Instant.now().plusSeconds(3600);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getExpiresAt()).thenReturn(exp);
        when(jwtDecoder.decode("token123")).thenReturn(jwt);
        Instant result = tokenService.getTokenExpiration("token123");
        assertThat(result, is(exp));
    }

    @Test
    @DisplayName("isTokenValid true si no ha expirado, false si ha expirado o es inválido")
    void isTokenValid_checksExpiration() {
        Instant future = Instant.now().plusSeconds(3600);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getExpiresAt()).thenReturn(future);
        when(jwtDecoder.decode("validtoken")).thenReturn(jwt);
        assertThat(tokenService.isTokenValid("validtoken"), is(true));

        Instant past = Instant.now().minusSeconds(10);
        Jwt jwt2 = mock(Jwt.class);
        when(jwt2.getExpiresAt()).thenReturn(past);
        when(jwtDecoder.decode("expiredtoken")).thenReturn(jwt2);
        assertThat(tokenService.isTokenValid("expiredtoken"), is(false));

        when(jwtDecoder.decode("badtoken")).thenThrow(new org.springframework.security.oauth2.jwt.JwtException("bad"));
        assertThat(tokenService.isTokenValid("badtoken"), is(false));
    }


    static class FakeSecurityUser extends dev.ivan.reviewverso_back.security.SecurityUser {
        private final Long userId;
        private final String email;
        public FakeSecurityUser(Long userId, String email) {
            super(new dev.ivan.reviewverso_back.user.UserEntity());
            this.userId = userId;
            this.email = email;
        }
        @Override public Long getUserId() { return userId; }
        @Override public String getEmail() { return email; }
        @Override public String getPassword() { return null; }
        @Override public String getUsername() { return null; }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return true; }
        @Override public List<? extends GrantedAuthority> getAuthorities() { return List.of(); }
    }
}
