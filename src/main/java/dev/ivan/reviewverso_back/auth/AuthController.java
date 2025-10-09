package dev.ivan.reviewverso_back.auth;

import dev.ivan.reviewverso_back.auth.dto.LoginRequestDTO;
import dev.ivan.reviewverso_back.auth.dto.LoginResponseDTO;
import dev.ivan.reviewverso_back.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("${api-endpoint}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Autenticar las credenciales
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password()
                )
            );

            // Generar JWT token
            String token = tokenService.generateToken(authentication);

            // Obtener detalles del usuario autenticado
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // Crear respuesta
            LoginResponseDTO loginResponse = new LoginResponseDTO(
                token,
                userDetails.getId(),
                userDetails.getDisplayName(),
                userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toSet())
            );

            return ResponseEntity.ok(loginResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest()
                .body("Credenciales inválidas: " + e.getMessage());
        }
    }
}
