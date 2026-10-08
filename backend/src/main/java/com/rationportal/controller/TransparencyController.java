package com.rationportal.controller;

import com.rationportal.repository.DistributionLogRepository;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.GrievanceRepository;
import com.rationportal.repository.RationCardApplicationRepository;
import com.rationportal.repository.RationCardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transparency")
public class TransparencyController {

    private final RationCardRepository rationCardRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final DistributionLogRepository distributionLogRepository;
    private final RationCardApplicationRepository applicationRepository;
    private final GrievanceRepository grievanceRepository;

    public TransparencyController(
            RationCardRepository rationCardRepository,
            FamilyMemberRepository familyMemberRepository,
            DistributionLogRepository distributionLogRepository,
            RationCardApplicationRepository applicationRepository,
            GrievanceRepository grievanceRepository) {

        this.rationCardRepository = rationCardRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.distributionLogRepository = distributionLogRepository;
        this.applicationRepository = applicationRepository;
        this.grievanceRepository = grievanceRepository;
    }

    private boolean isAuthenticated(
            Authentication authentication) {

        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getName() != null
                && !authentication.getName().isBlank();
    }

    private boolean isAdmin(
            Authentication authentication) {

        return isAuthenticated(authentication)
                && authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication required."
                    ));
        }

        Map<String, Object> result =
                new LinkedHashMap<>();

        /*
         * These are aggregate transparency figures only.
         *
         * No citizen-specific:
         * - email
         * - mobile number
         * - address
         * - ration card owner
         * - family member details
         * - grievance details
         * are exposed here.
         */
        result.put(
                "totalRationCards",
                rationCardRepository.count()
        );

        result.put(
                "totalFamilyMembers",
                familyMemberRepository.count()
        );

        result.put(
                "totalDistributionRecords",
                distributionLogRepository.count()
        );

        result.put(
                "totalApplications",
                applicationRepository.count()
        );

        result.put(
                "totalGrievances",
                grievanceRepository.count()
        );

        /*
         * Identify the authenticated authority.
         * This is generated server-side.
         */
        result.put(
                "accessLevel",
                isAdmin(authentication)
                        ? "ADMIN"
                        : "CITIZEN"
        );

        return ResponseEntity.ok(result);
    }
}