package com.rationportal.controller;

import com.rationportal.model.Grievance;
import com.rationportal.repository.GrievanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceRepository repository;

    public GrievanceController(GrievanceRepository repository) {
        this.repository = repository;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null &&
            authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @PostMapping
    public ResponseEntity<?> submit(
        @RequestBody Grievance grievance,
        Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Authentication required."));
        }

        if (grievance.getGrievanceNumber() == null ||
            grievance.getGrievanceNumber().isBlank()) {
            grievance.setGrievanceNumber(
                "GRV" + UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 8).toUpperCase()
            );
        }

        grievance.setSubmittedByEmail(authentication.getName().toLowerCase());
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(grievance));
    }

    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(repository.findAll());
        }

        List<Grievance> own = repository.findAll().stream()
            .filter(g -> g.getSubmittedByEmail() != null &&
                g.getSubmittedByEmail().equalsIgnoreCase(authentication.getName()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(own);
    }

    @GetMapping("/{number}")
    public ResponseEntity<?> getByNumber(
        @PathVariable String number,
        Authentication authentication
    ) {
        return repository.findByGrievanceNumber(number)
            .map(grievance -> {
                if (isAdmin(authentication) ||
                    (grievance.getSubmittedByEmail() != null &&
                     grievance.getSubmittedByEmail().equalsIgnoreCase(authentication.getName()))) {
                    return ResponseEntity.ok(grievance);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You are not authorized to view this grievance."));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}