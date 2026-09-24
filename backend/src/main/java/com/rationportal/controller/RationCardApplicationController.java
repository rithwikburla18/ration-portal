package com.rationportal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.RationCardApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({
        "/api/applications/ration-card",
        "/api/applications"
})
public class RationCardApplicationController {

    private final RationCardApplicationRepository repository;
    private final ObjectMapper objectMapper;

    public RationCardApplicationController(
            RationCardApplicationRepository repository,
            ObjectMapper objectMapper) {

        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> submitApplication(
            @RequestBody @NonNull Map<String, Object> request,
            Authentication authentication) {

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
    public ResponseEntity<List<RationCardApplication>> getApplications(
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority())
                );

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

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority())
                );

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