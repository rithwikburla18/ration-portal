package com.rationportal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.RationCardApplicationRepository;
import com.rationportal.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping({
        "/api/applications/ration-card",
        "/api/applications"
})
public class RationCardApplicationController {

    private final RationCardApplicationRepository repository;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "SUBMITTED",
            "PENDING",
            "VERIFIED",
            "APPROVED",
            "REJECTED"
    );

    public RationCardApplicationController(
            RationCardApplicationRepository repository,
            ObjectMapper objectMapper,
            NotificationService notificationService) {

        this.repository = repository;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<?> submitApplication(
            @RequestBody @NonNull Map<String, Object> request,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        try {
            String headOfFamily = getString(request, "headOfFamily");
            String mobile = getString(request, "mobile");
            String email = authentication.getName();
            String cardType = getString(request, "cardType");
            String address = getString(request, "address");
            String district = getString(request, "district");
            String state = getString(request, "state");
            String pincode = getString(request, "pincode");

            Object familyMembersObject = request.get("familyMembers");

            if (headOfFamily.isBlank()
                    || mobile.isBlank()
                    || email.isBlank()
                    || cardType.isBlank()
                    || address.isBlank()
                    || district.isBlank()
                    || state.isBlank()
                    || pincode.isBlank()
                    || familyMembersObject == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "All required application details must be provided."
                        ));
            }

            if (!(familyMembersObject instanceof List<?> familyMembers)) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Family members data must be a valid list."
                        ));
            }

            if (familyMembers.isEmpty()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "At least one family member is required."
                        ));
            }

            RationCardApplication application =
                    new RationCardApplication();

            application.setApplicationNumber(
                    "RCAPP-" + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase()
            );

            application.setApplicantName(headOfFamily);
            application.setMobile(mobile);
            application.setEmail(email);
            application.setCardType(cardType);
            application.setFamilyMembers(familyMembers.size());

            application.setFamilyMembersData(
                    objectMapper.writeValueAsString(familyMembers)
            );

            application.setAddress(address);
            application.setDistrict(district);
            application.setState(state);
            application.setPincode(pincode);
            application.setStatus("SUBMITTED");

            RationCardApplication savedApplication =
                    repository.save(application);

            try {
                notificationService.sendApplicationSubmittedEmail(
                        savedApplication.getEmail(),
                        savedApplication.getApplicantName(),
                        savedApplication.getApplicationNumber()
                );
            } catch (Exception notificationException) {
                System.err.println(
                        "APPLICATION SUBMITTED EMAIL NOTIFICATION FAILED: "
                                + notificationException.getMessage()
                );
            }

            try {
                notificationService.sendApplicationSubmittedSms(
                        savedApplication.getMobile(),
                        savedApplication.getApplicationNumber()
                );
            } catch (Exception notificationException) {
                System.err.println(
                        "APPLICATION SUBMITTED SMS NOTIFICATION FAILED: "
                                + notificationException.getMessage()
                );
            }

            return ResponseEntity.ok(savedApplication);

        } catch (JsonProcessingException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Unable to process family member information."
                    ));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "message",
                            "Unable to submit the ration card application."
                    ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getApplications(
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        boolean isAdmin = isAdmin(authentication);

        List<RationCardApplication> applications;

        if (isAdmin) {
            applications = repository.findAll();
        } else {
            applications =
                    repository.findAllByEmailOrderByCreatedAtDesc(
                            authentication.getName()
                    );
        }

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{applicationNumber}")
    public ResponseEntity<?> getApplicationByNumber(
            @PathVariable @NonNull String applicationNumber,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        RationCardApplication application =
                repository.findByApplicationNumber(applicationNumber)
                        .orElse(null);

        if (application == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "Application not found."
                    ));
        }

        boolean isAdmin = isAdmin(authentication);

        if (!isAdmin
                && !authentication.getName()
                .equalsIgnoreCase(application.getEmail())) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "Application not found."
                    ));
        }

        return ResponseEntity.ok(application);
    }

    @PutMapping("/{applicationNumber}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable @NonNull String applicationNumber,
            @RequestBody @NonNull Map<String, Object> request,
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        if (!isAdmin(authentication)) {

            return ResponseEntity.status(403)
                    .body(Map.of(
                            "message",
                            "Only administrators can update application status."
                    ));
        }

        String requestedStatus = getString(request, "status");

        if (requestedStatus.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Application status is required."
                    ));
        }

        String normalizedStatus =
                requestedStatus.trim().toUpperCase();

        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Invalid application status.",
                            "allowedStatuses",
                            ALLOWED_STATUSES
                    ));
        }

        RationCardApplication application =
                repository.findByApplicationNumber(applicationNumber)
                        .orElse(null);

        if (application == null) {

            return ResponseEntity.status(404)
                    .body(Map.of(
                            "message",
                            "Application not found."
                    ));
        }

        String previousStatus = application.getStatus();

        application.setStatus(normalizedStatus);

        RationCardApplication savedApplication =
                repository.save(application);

        try {
            notificationService.sendApplicationStatusEmail(
                    savedApplication.getEmail(),
                    savedApplication.getApplicantName(),
                    savedApplication.getApplicationNumber(),
                    savedApplication.getStatus()
            );
        } catch (Exception notificationException) {
            System.err.println(
                    "APPLICATION STATUS EMAIL NOTIFICATION FAILED: "
                            + notificationException.getMessage()
            );
        }

        try {
            notificationService.sendApplicationStatusSms(
                    savedApplication.getMobile(),
                    savedApplication.getApplicationNumber(),
                    savedApplication.getStatus()
            );
        } catch (Exception notificationException) {
            System.err.println(
                    "APPLICATION STATUS SMS NOTIFICATION FAILED: "
                            + notificationException.getMessage()
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Application status updated successfully.",
                        "applicationNumber",
                        savedApplication.getApplicationNumber(),
                        "previousStatus",
                        previousStatus == null ? "" : previousStatus,
                        "status",
                        savedApplication.getStatus(),
                        "emailNotification",
                        "ATTEMPTED",
                        "smsNotification",
                        "ATTEMPTED",
                        "application",
                        savedApplication
                )
        );
    }

    private boolean isAuthenticated(Authentication authentication) {

        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getName() != null
                && !authentication.getName().isBlank();
    }

    private boolean isAdmin(Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority())
                );
    }

    private String getString(
            Map<String, Object> request,
            String fieldName) {

        Object value = request.get(fieldName);

        if (value == null) {
            return "";
        }

        return value.toString().trim();
    }
}