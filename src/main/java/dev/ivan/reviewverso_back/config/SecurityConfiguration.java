package dev.ivan.reviewverso_back.config;

import java.util.Arrays;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${jwt.key}")
    private String key;

    @Value("${api-endpoint}")
    private String endpoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfiguration()))
                .csrf(csfr -> csfr
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable())
                .headers(header -> header
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, endpoint + "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, endpoint + "/auth/token").permitAll()
                        .requestMatchers(HttpMethod.POST, endpoint + "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, endpoint + "/auth/logout").permitAll()
                        
                        .requestMatchers(HttpMethod.GET, endpoint + "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, endpoint + "/users/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, endpoint + "/users/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT, endpoint + "/users/**").hasAnyRole("ADMIN","USER")
                
                        .requestMatchers(HttpMethod.GET, endpoint + "/files/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, endpoint + "/files/images/**").hasAnyRole("ADMIN", "USER")
                
                        .requestMatchers(HttpMethod.GET, endpoint + "/reviews").permitAll()
                        .requestMatchers(HttpMethod.GET, endpoint + "/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.POST, endpoint + "/reviews").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.POST, endpoint + "/reviews/*/like").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, endpoint + "/reviews/*/like").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT, endpoint + "/reviews/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, endpoint + "/reviews/**").hasAnyRole("ADMIN","USER")
                     
                        
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ))
                .httpBasic(withDefaults());

        http.headers(header -> header.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(key.getBytes()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] bytes = key.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(bytes, 0, bytes.length, "HmacSHA512");
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(
            org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    /**
     * Convierte los claims del JWT en authorities de Spring Security
     */
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
        org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter converter = 
            new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        
        // Converter personalizado para authorities
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String scope = jwt.getClaimAsString("scope");
            if (scope != null && !scope.isEmpty()) {
                return java.util.Arrays.stream(scope.split(" "))
                    .map(role -> {
                        // Convertir SCOPE_ROLE_USER a ROLE_USER para compatibilidad con hasRole()
                        if (role.startsWith("ROLE_")) {
                            return new org.springframework.security.core.authority.SimpleGrantedAuthority(role);
                        }
                        return new org.springframework.security.core.authority.SimpleGrantedAuthority(role);
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            return java.util.List.of();
        });
        
        return converter;
    }

    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "multipart/form-data"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}