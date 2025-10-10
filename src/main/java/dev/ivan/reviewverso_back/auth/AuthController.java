package dev.ivan.reviewverso_back.auth;

import dev.ivan.reviewverso_back.auth.dto.AuthResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

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
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // Extraer token del header Authorization
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // Obtener fecha de expiración del token
                var expiration = tokenService.getTokenExpiration(token);
                
                // Invalidar token agregándolo a la blacklist
                tokenBlacklistService.invalidateToken(token, expiration);
                
                return ResponseEntity.ok().body("Sesión cerrada con éxito- Token invalidado");
            } catch (Exception e) {
                return ResponseEntity.ok().body("Sesión cerrada con éxito");
            }
        }
        
        return ResponseEntity.ok().body("Sesión cerrada con éxito");
    }

    // Record para el request de login
    public record LoginRequest(String identifier, String password) {}
}