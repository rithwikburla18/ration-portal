package com.rationportal.controller;

import com.rationportal.model.Grievance;
import com.rationportal.repository.GrievanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceRepository repository;

    public GrievanceController(GrievanceRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<?> submit(
            @RequestBody Grievance grievance,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        String authenticatedEmail = authentication.getName()
                .trim()
                .toLowerCase();

        grievance.setSubmittedByEmail(authenticatedEmail);

        if (grievance.getGrievanceNumber() == null
                || grievance.getGrievanceNumber().isBlank()) {

            grievance.setGrievanceNumber(
                    "GRV"
                            + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 8)
                            .toUpperCase()
            );
        }

        return ResponseEntity.ok(repository.save(grievance));
    }

    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        if (!isAdmin) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin access required."));
        }

        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{number}")
    public ResponseEntity<?> getByNumber(
            @PathVariable String number,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        String authenticatedEmail = authentication.getName()
                .trim()
                .toLowerCase();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        return repository.findByGrievanceNumber(number)
                .map(grievance -> {

                    if (isAdmin) {
                        return ResponseEntity.ok(grievance);
                    }

                    if (!authenticatedEmail.equals(
                            grievance.getSubmittedByEmail())) {

                        return ResponseEntity.status(403)
                                .body(Map.of(
                                        "message",
                                        "You are not authorized to view this grievance."
                                ));
                    }

                    return ResponseEntity.ok(grievance);
                })
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }
}