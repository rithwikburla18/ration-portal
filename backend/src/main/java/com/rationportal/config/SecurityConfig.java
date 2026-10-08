package com.rationportal.config;

import com.rationportal.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String allowedOrigins;

    public SecurityConfig(
            @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${app.cors.allowed-origins:http://localhost:5500,http://127.0.0.1:5500,https://ration-portal-frontend.onrender.com}") String allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/api/health", "/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/ration-cards/admin/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/ration-cards").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/ration-cards/**").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/family-members/**").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/family-members/**").hasRole("ADMIN")
                        .requestMatchers("/api/applications/**").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers("/api/import/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/distribution-logs/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/distribution-logs/**").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/grievances").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/grievances/{number}").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/grievances").hasRole("ADMIN")
                        .requestMatchers("/api/transparency/**").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers("/api/dashboard/**").hasAnyRole("CITIZEN", "ADMIN")
                        .requestMatchers("/api/onorc/**").hasAnyRole("CITIZEN", "ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = new ArrayList<>();
        String configuredOrigins = allowedOrigins == null ? "" : allowedOrigins;
        String[] originValues = configuredOrigins.split(",");
        for (String origin : originValues) {
            if (origin == null) {
                continue;
            }
            String trimmedOrigin = origin.trim();
            if (!trimmedOrigin.isBlank()) {
                origins.add(trimmedOrigin);
            }
        }
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Location"));
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}