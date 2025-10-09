package dev.ivan.reviewverso_back.auth;

import dev.ivan.reviewverso_back.register.dto.RegisterRequestDTO;

import org.springframework.security.core.Authentication;
import dev.ivan.reviewverso_back.user.UserEntity;
import dev.ivan.reviewverso_back.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "${api-endpoint}/auth")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(Authentication authentication) {
        try {
            String token = tokenService.generateToken(authentication);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("No autenticado: credenciales inválidas o faltantes");
        }
    }

}