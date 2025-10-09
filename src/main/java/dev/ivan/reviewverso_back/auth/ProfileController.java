package dev.ivan.reviewverso_back.auth;

import dev.ivan.reviewverso_back.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api-endpoint}/auth")
public class ProfileController {

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("Usuario no autenticado");
        }

        Map<String, Object> profile = Map.of(
            "id", userDetails.getId(),
            "username", userDetails.getDisplayName(),
            "email", userDetails.getUsername(),
            "roles", userDetails.getAuthorities()
        );

        return ResponseEntity.ok(profile);
    }
}