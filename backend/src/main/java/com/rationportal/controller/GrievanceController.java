package com.rationportal.controller;

import com.rationportal.model.Grievance;
import com.rationportal.repository.GrievanceRepository;
import com.rationportal.repository.RationCardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
                    .body(Map.of("message", "Authentication required."));
        }
        if (grievance == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Grievance details are required."));
        }

        grievance.setApplicantName(clean(grievance.getApplicantName()));
        grievance.setRationCardNumber(clean(grievance.getRationCardNumber()));
        grievance.setCategory(clean(grievance.getCategory()));
        grievance.setContactNumber(clean(grievance.getContactNumber()));
        grievance.setDescription(clean(grievance.getDescription()));

        String validationMessage = validateSubmission(grievance);
        if (validationMessage != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validationMessage));
        }

        /*
         * The API caller cannot choose the reference, initial status, creation
         * timestamp, or submitting identity. These are set by the server.
         */
        grievance.setGrievanceNumber(
                "GRV" + UUID.randomUUID().toString().replace("-", "")
                        .substring(0, 12).toUpperCase(Locale.ROOT));
        grievance.setStatus("SUBMITTED");

        if (!isAdmin(authentication)) {
            String email = authentication.getName().trim().toLowerCase(Locale.ROOT);
            if (rationCardRepository.findByRationCardNumberAndOwnerEmailIgnoreCase(
                    grievance.getRationCardNumber(), email).isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "You are not authorized to submit a grievance for this ration card."));
            }
            grievance.setSubmittedByEmail(email);
        } else {
            grievance.setSubmittedByEmail(authentication.getName().trim().toLowerCase(Locale.ROOT));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(grievance));
    }

    private String validateSubmission(Grievance grievance) {
        if (grievance.getApplicantName().isBlank() || grievance.getApplicantName().length() > 120) {
            return "Applicant name is required and must not exceed 120 characters.";
        }
        if (grievance.getRationCardNumber().isBlank() || grievance.getRationCardNumber().length() > 50) {
            return "A valid ration card number is required.";
        }
        if (grievance.getCategory().isBlank() || grievance.getCategory().length() > 100) {
            return "Grievance category is required and must not exceed 100 characters.";
        }
        if (grievance.getDescription().isBlank() || grievance.getDescription().length() > 5000) {
            return "Grievance details must contain 1 to 5000 characters.";
        }
        String contact = grievance.getContactNumber();
        String digits = contact.replaceAll("\\D", "");
        if (contact.length() > 30 || !contact.matches("[+0-9() .-]{10,30}")
                || digits.length() < 10 || digits.length() > 15) {
            return "Enter a valid contact number.";
        }
        return null;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required."));
        }
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(repository.findAll());
        }
        String email = authentication.getName().trim().toLowerCase(Locale.ROOT);
        return ResponseEntity.ok(repository.findBySubmittedByEmailIgnoreCaseOrderByCreatedAtDesc(email));
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

        return repository.findByGrievanceNumber(number.trim())
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
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "Grievance not found."));
                })
                .orElse(
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of(
                                        "message",
                                        "Grievance not found."
                                ))
                );
    }

    private static final Set<String> ALLOWED_ADMIN_STATUSES =
            Set.of("PENDING", "PROCESSING", "RESOLVED", "REJECTED");

    @PutMapping("/{number}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String number,
            @RequestBody Map<String, String> body,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required."));
        }
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only administrators can update grievance status."));
        }
        String requested = body == null ? "" : body.getOrDefault("status", "");
        String status = requested.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_ADMIN_STATUSES.contains(status)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Status must be PENDING, PROCESSING, RESOLVED, or REJECTED.",
                    "allowedStatuses", ALLOWED_ADMIN_STATUSES));
        }
        Grievance grievance = repository.findByGrievanceNumber(number == null ? "" : number.trim()).orElse(null);
        if (grievance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Grievance not found."));
        }

        String previous = clean(grievance.getStatus()).toUpperCase(Locale.ROOT);
        if (previous.equals(status)) {
            return ResponseEntity.ok(Map.of(
                    "message", "Grievance already has the requested status.",
                    "grievanceNumber", grievance.getGrievanceNumber(),
                    "previousStatus", previous,
                    "status", previous));
        }
        if ("RESOLVED".equals(previous) || "REJECTED".equals(previous)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "A resolved or rejected grievance cannot be changed to another status."));
        }

        grievance.setStatus(status);
        repository.save(grievance);
        return ResponseEntity.ok(Map.of(
                "message", "Grievance status updated successfully.",
                "grievanceNumber", grievance.getGrievanceNumber(),
                "previousStatus", previous,
                "status", status));
    }

    /*
     * Citizens are deliberately not given a PUT/PATCH/DELETE endpoint.
     *
     * Grievance status and administrative actions must be handled
     * through a dedicated administrator-only workflow.
     */
}