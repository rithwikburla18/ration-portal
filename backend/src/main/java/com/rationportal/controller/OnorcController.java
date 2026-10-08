package com.rationportal.controller;

import com.rationportal.model.OnorcPortability;
import com.rationportal.service.OnorcPortabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/onorc")
public class OnorcController {

    private final OnorcPortabilityService service;

    public OnorcController(OnorcPortabilityService service) {
        this.service = service;
    }

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

    @PostMapping("/portability")
    public ResponseEntity<?> create(
            @RequestBody OnorcPortability request,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        if (request == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "ONORC portability request is required."
                    ));
        }

        try {
            return ResponseEntity.ok(
                    service.create(
                            request,
                            authentication.getName().trim().toLowerCase(),
                            isAdmin(authentication)
                    )
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            ex.getMessage()
                    ));
        }
    }

    @GetMapping("/portability/{reference}")
    public ResponseEntity<?> get(
            @PathVariable String reference,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        if (reference == null || reference.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "ONORC reference number is required."
                    ));
        }

        try {
            return ResponseEntity.ok(
                    service.getByReference(
                            reference.trim(),
                            authentication.getName().trim().toLowerCase(),
                            isAdmin(authentication)
                    )
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            ex.getMessage()
                    ));
        }
    }

    @GetMapping("/my-requests")
    public ResponseEntity<?> myRequests(
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        return ResponseEntity.ok(
                service.getMyRequests(
                        authentication.getName().trim().toLowerCase()
                )
        );
    }
}