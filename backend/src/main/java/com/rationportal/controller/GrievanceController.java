package com.rationportal.controller;

import com.rationportal.model.Grievance;
import com.rationportal.repository.GrievanceRepository;
import com.rationportal.repository.RationCardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceRepository repository;
    private final RationCardRepository rationCardRepository;

    public GrievanceController(GrievanceRepository repository, RationCardRepository rationCardRepository) {
        this.repository = repository;
        this.rationCardRepository = rationCardRepository;
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getName() != null
                && !authentication.getName().isBlank();
    }

    private boolean isAdmin(Authentication authentication) {
        if (!isAuthenticated(authentication)) return false;
        for (var authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) return true;
        }
        return false;
    }

    private boolean isOwner(
            Grievance grievance,
            Authentication authentication) {

        return isAuthenticated(authentication)
                && grievance != null
                && grievance.getSubmittedByEmail() != null
                && grievance.getSubmittedByEmail()
                .equalsIgnoreCase(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<?> submit(
            @RequestBody Grievance grievance,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        if (grievance == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Grievance details are required."
                    ));
        }

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

        /*
         * NEVER trust submittedByEmail from the browser.
         * The authenticated server identity is authoritative.
         */
        if (!isAdmin(authentication)) {
            String cardNumber = grievance.getRationCardNumber();
            if (cardNumber == null || cardNumber.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Ration card number is required."
                ));
            }

            if (rationCardRepository.findByRationCardNumberAndOwnerEmailIgnoreCase(
                    cardNumber.trim(), authentication.getName().trim().toLowerCase()
            ).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "You are not authorized to submit a grievance for this ration card."
                ));
            }
        }

        grievance.setSubmittedByEmail(
                authentication.getName()
                        .trim()
                        .toLowerCase()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(grievance));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        /*
         * Administrator can view every grievance.
         */
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(repository.findAll());
        }

        /*
         * Citizen can view ONLY their own grievances.
         */
        List<Grievance> own = new ArrayList<>();
        for (Grievance grievance : repository.findAll()) {
            if (isOwner(grievance, authentication)) own.add(grievance);
        }

        return ResponseEntity.ok(own);
    }

    @GetMapping("/{number}")
    public ResponseEntity<?> getByNumber(
            @PathVariable String number,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        if (number == null || number.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Grievance number is required."
                    ));
        }

        return repository.findByGrievanceNumber(number)
                .map(grievance -> {

                    /*
                     * Admin can view any grievance.
                     */
                    if (isAdmin(authentication)) {
                        return ResponseEntity.ok(grievance);
                    }

                    /*
                     * Citizen can view only their own grievance.
                     */
                    if (isOwner(grievance, authentication)) {
                        return ResponseEntity.ok(grievance);
                    }

                    /*
                     * Do not reveal another citizen's grievance.
                     */
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of(
                                    "message",
                                    "You are not authorized to view this grievance."
                            ));
                })
                .orElse(
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "message",
                                        "Grievance not found."
                                ))
                );
    }

    /*
     * Citizens are deliberately not given a PUT/PATCH/DELETE endpoint.
     *
     * Grievance status and administrative actions must be handled
     * through a dedicated administrator-only workflow.
     */
}