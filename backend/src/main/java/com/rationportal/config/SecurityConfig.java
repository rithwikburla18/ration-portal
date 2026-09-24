package com.rationportal.config;

import com.rationportal.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

                // Public backend endpoints
                .requestMatchers(
                        "/",
                        "/api/health",
                        "/error"
                )
                    .permitAll()

                // Authentication endpoints
                .requestMatchers("/api/auth/**")
                    .permitAll()

                // Ration cards
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/ration-cards/**"
                )
                    .hasAnyRole("CITIZEN", "ADMIN")

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/ration-cards/**"
                )
                    .hasRole("ADMIN")

                // Family members
                .requestMatchers(
                        HttpMethod.GET,
                        "/api/family-members/**"
                )
                    .hasAnyRole("CITIZEN", "ADMIN")

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/family-members/**"
                )
                    .hasRole("ADMIN")

                // Applications
                .requestMatchers("/api/applications/**")
                    .hasAnyRole("CITIZEN", "ADMIN")

                // Import
                .requestMatchers("/api/import/**")
                    .hasRole("ADMIN")

                // Distribution logs
                .requestMatchers("/api/distribution-logs/**")
                    .hasRole("ADMIN")

                // Grievances
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/grievances"
                )
                    .hasAnyRole("CITIZEN", "ADMIN")

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/grievances/{number}"
                )
                    .hasAnyRole("CITIZEN", "ADMIN")

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/grievances"
                )
                    .hasRole("ADMIN")

                // Transparency
                .requestMatchers("/api/transparency/**")
                    .hasAnyRole("CITIZEN", "ADMIN")

                // Dashboard
                .requestMatchers("/api/dashboard/**")
                    .hasAnyRole("CITIZEN", "ADMIN")

                // Everything else requires authentication
                .anyRequest()
                    .authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}