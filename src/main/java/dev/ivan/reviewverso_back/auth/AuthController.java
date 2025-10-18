package dev.ivan.reviewverso_back.auth;

import dev.ivan.reviewverso_back.auth.dto.AuthResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/token")
    public ResponseEntity<?> token(Authentication authentication) {
        try {
            String token = tokenService.generateToken(authentication);
            return ResponseEntity.ok(new AuthResponseDTO(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("No autenticado: credenciales inválidas o faltantes");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.identifier(), 
                    loginRequest.password()
                )
            );

     
            String token = tokenService.generateToken(authentication);
            
            return ResponseEntity.ok(new AuthResponseDTO(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
  
        return ResponseEntity.ok().body("Sesión cerrada con éxito");
    }

    // Record para el request de login
    public record LoginRequest(String identifier, String password) {}
}