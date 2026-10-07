package com.rationportal.controller;

import com.rationportal.model.Grievance;
import com.rationportal.repository.GrievanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceRepository repository;

    public GrievanceController(GrievanceRepository repository) {
        this.repository = repository;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority != null
                                && "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private String authenticatedEmail(Authentication authentication) {
        return authentication.getName()
                .trim()
                .toLowerCase(java.util.Locale.ROOT);
    }

    @PostMapping
    public ResponseEntity<?> submit(
            @RequestBody Grievance grievance,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        if (grievance.getApplicantName() == null || grievance.getApplicantName().isBlank()
                || grievance.getRationCardNumber() == null || grievance.getRationCardNumber().isBlank()
                || grievance.getCategory() == null || grievance.getCategory().isBlank()
                || grievance.getContactNumber() == null || grievance.getContactNumber().isBlank()
                || grievance.getDescription() == null || grievance.getDescription().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Please provide your name, ration card number, category, contact number, and description."
                    ));
        }

        grievance.setSubmittedByEmail(authenticatedEmail(authentication));

        if (grievance.getGrievanceNumber() == null || grievance.getGrievanceNumber().isBlank()) {
            grievance.setGrievanceNumber(
                    "GRV" + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 8)
                            .toUpperCase(java.util.Locale.ROOT)
            );
        }

        if (grievance.getStatus() == null || grievance.getStatus().isBlank()) {
            grievance.setStatus("SUBMITTED");
        }

        return ResponseEntity.ok(repository.save(grievance));
    }

    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin access required."));
        }

        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{number}")
    public ResponseEntity<?> getByNumber(
            @PathVariable String number,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        String email = authenticatedEmail(authentication);

        return repository.findByGrievanceNumber(number)
                .map(grievance -> {
                    if (isAdmin(authentication)
                            || email.equalsIgnoreCase(grievance.getSubmittedByEmail())) {
                        return ResponseEntity.ok(grievance);
                    }
                    return ResponseEntity.status(403)
                            .body(Map.of(
                                    "message",
                                    "You are not authorized to view this grievance."
                            ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
