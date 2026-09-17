package com.rationportal.config;

import com.rationportal.security.JwtAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) { this.jwtAuthenticationFilter = jwtAuthenticationFilter; }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception { return configuration.getAuthenticationManager(); }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> {}).csrf(csrf -> csrf.disable()).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(auth -> auth
            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/ration-cards/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/transparency/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/grievances").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/grievances/*").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/applications/ration-card").permitAll()
            .requestMatchers("/api/import/**").hasRole("ADMIN")
            .requestMatchers("/api/distribution-logs/**").hasRole("ADMIN")
            .requestMatchers("/api/family-members/**").hasAnyRole("CITIZEN", "ADMIN")
            .requestMatchers("/api/applications/ration-card/**").hasAnyRole("CITIZEN", "ADMIN")
            .requestMatchers("/api/ration-cards/**").hasRole("ADMIN")
            .requestMatchers("/api/grievances/**").hasRole("ADMIN")
            .requestMatchers("/api/dashboard/**").hasAnyRole("CITIZEN", "ADMIN")
            .anyRequest().authenticated()
        ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}