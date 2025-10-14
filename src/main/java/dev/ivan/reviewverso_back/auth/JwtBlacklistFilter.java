package dev.ivan.reviewverso_back.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        log.debug("JwtBlacklistFilter - Authorization header: {}", authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("JwtBlacklistFilter - Extracted token: {}", token.substring(0, Math.min(20, token.length())) + "...");
            
            // Verificar si el token está en la blacklist
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                log.warn("JwtBlacklistFilter - Token is blacklisted: {}", token.substring(0, Math.min(20, token.length())) + "...");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token invalidado");
                return;
            } else {
                log.debug("JwtBlacklistFilter - Token is not blacklisted, continuing...");
            }
        }
        
        filterChain.doFilter(request, response);
    }
}