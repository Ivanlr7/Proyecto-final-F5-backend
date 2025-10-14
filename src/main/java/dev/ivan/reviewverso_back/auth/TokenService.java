package dev.ivan.reviewverso_back.auth;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(Authentication authentication) {

        Instant now = Instant.now();

        // Recopilar roles del usuario autenticado
        String roles = collectRoles(authentication);
        
        // Obtener información adicional del usuario si es SecurityUser
        Long userId = extractUserId(authentication);
        String email = extractEmail(authentication);

        System.out.println("<-------------- roles: " + roles);
        System.out.println("<-------------- userId: " + userId);
        System.out.println("<-------------- email: " + email);

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(authentication.getName())
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claim("scope", roles);
        
        // Agregar claims adicionales si están disponibles
        if (userId != null) {
            claimsBuilder.claim("userId", userId);
        }
        if (email != null && !email.isEmpty()) {
            claimsBuilder.claim("email", email);
        }

        JwtClaimsSet claims = claimsBuilder.build();

        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
        return this.jwtEncoder.encode(encoderParameters).getTokenValue();
    }

    /**
     * Recopila los roles del usuario autenticado
     */
    private String collectRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.joining(" "));
    }

    /**
     * Extrae el ID del usuario si el principal es SecurityUser
     */
    private Long extractUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof dev.ivan.reviewverso_back.security.SecurityUser securityUser) {
            return securityUser.getUserId();
        }
        return null;
    }

    /**
     * Extrae el email del usuario si el principal es SecurityUser
     */
    private String extractEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof dev.ivan.reviewverso_back.security.SecurityUser securityUser) {
            return securityUser.getEmail();
        }
        return null;
    }

    /**
     * Extrae la fecha de expiración de un token JWT
     */
    public Instant getTokenExpiration(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getExpiresAt();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Token inválido", e);
        }
    }

    /**
     * Verifica si un token es válido
     */
    public boolean isTokenValid(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getExpiresAt().isAfter(Instant.now());
        } catch (JwtException e) {
            return false;
        }
    }

}