package dev.ivan.reviewverso_back.auth;

import dev.ivan.reviewverso_back.auth.dto.AuthResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private TokenService tokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("login devuelve token si autenticación es exitosa")
    void login_success() {
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest("user", "pass");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn("jwt.token");

        ResponseEntity<?> response = authController.login(loginRequest);
    assertThat(response.getStatusCode().value(), is(200));
        assertThat(response.getBody(), instanceOf(AuthResponseDTO.class));
        assertThat(((AuthResponseDTO) response.getBody()).token(), is("jwt.token"));
    }

    @Test
    @DisplayName("login devuelve 401 si autenticación falla")
    void login_failure() {
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest("user", "badpass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        ResponseEntity<?> response = authController.login(loginRequest);
    assertThat(response.getStatusCode().value(), is(401));
        assertThat(response.getBody(), instanceOf(String.class));
        assertThat(((String) response.getBody()), containsString("Credenciales inválidas"));
    }

    @Test
    @DisplayName("logout devuelve mensaje de éxito")
    void logout_success() {
        ResponseEntity<?> response = authController.logout();
    assertThat(response.getStatusCode().value(), is(200));
        assertThat(response.getBody(), is("Sesión cerrada con éxito"));
    }
}
