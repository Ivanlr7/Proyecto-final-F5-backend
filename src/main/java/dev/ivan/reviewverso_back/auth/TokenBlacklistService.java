package dev.ivan.reviewverso_back.auth;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.util.Map;

@Service
public class TokenBlacklistService {
    
    // Almacena tokens invalidados con su tiempo de expiración
    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
    
     //Invalidar un token agregándolo a la blacklist

    public void invalidateToken(String token, Instant expiration) {
        blacklistedTokens.put(token, expiration);
        cleanExpiredTokens(); // Limpia tokens ya expirados
    }
    
    
     
    //Verifica si un token está en la blacklist

    public boolean isTokenBlacklisted(String token) {
        cleanExpiredTokens();
        return blacklistedTokens.containsKey(token);
    }
    
    
     //Limpia tokens expirados de la blacklist para optimizar memoria
    
    private void cleanExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> 
            entry.getValue().isBefore(now));
    }
    
    
     //Obtiene el número de tokens en blacklist (para debugging)
     
    public int getBlacklistedTokenCount() {
        cleanExpiredTokens();
        return blacklistedTokens.size();
    }
}