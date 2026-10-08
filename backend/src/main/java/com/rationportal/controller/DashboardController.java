package com.rationportal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getName() != null
                && !authentication.getName().isBlank();
    }

    private boolean isAdmin(Authentication authentication) {
        return isAuthenticated(authentication)
                && authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> metrics(
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        /*
         * Dashboard access is available to authenticated citizens
         * and administrators.
         *
         * The role is returned from the authenticated server identity,
         * never from browser-supplied data.
         */
        return ResponseEntity.ok(
                Map.of(
                        "status",
                        "Backend is working!",
                        "message",
                        "Dashboard API is connected successfully.",
                        "authenticated",
                        true,
                        "role",
                        isAdmin(authentication)
                                ? "ADMIN"
                                : "CITIZEN"
                )
        );
    }
}