package com.rationportal.controller;

import com.rationportal.model.DistributionLog;
import com.rationportal.model.RationCard;
import com.rationportal.repository.RationCardRepository;
import com.rationportal.service.DistributionLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/distribution-logs")
public class DistributionLogController {

    private final DistributionLogService service;
    private final RationCardRepository rationCardRepository;

    public DistributionLogController(
        DistributionLogService service,
        RationCardRepository rationCardRepository
    ) {
        this.service = service;
        this.rationCardRepository = rationCardRepository;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null &&
            authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private void verifyAccess(
        @NonNull Long rationCardId,
        Authentication authentication
    ) {
        RationCard card = rationCardRepository.findById(rationCardId)
            .orElseThrow(() -> new RuntimeException("Ration card not found"));

        if (isAdmin(authentication)) {
            return;
        }

        if (card.getOwnerEmail() == null ||
            !card.getOwnerEmail().equalsIgnoreCase(authentication.getName())) {
            throw new RuntimeException(
                "You are not authorized to access this ration card"
            );
        }

        String status = card.getStatus();

        if (status == null ||
            (!"APPROVED".equalsIgnoreCase(status) &&
             !"ACTIVE".equalsIgnoreCase(status))) {
            throw new RuntimeException("Ration card is not approved");
        }
    }

    @GetMapping("/ration-card/{rationCardId}")
    public ResponseEntity<?> getByRationCard(
        @PathVariable @NonNull Long rationCardId,
        Authentication authentication
    ) {
        try {
            verifyAccess(rationCardId, authentication);
            return ResponseEntity.ok(
                service.getByRationCard(rationCardId)
            );
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(
        @RequestBody @NonNull DistributionLog log,
        Authentication authentication
    ) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                    "message",
                    "Only ADMIN can create distribution records"
                ));
        }

        DistributionLog saved = Objects.requireNonNull(
            service.save(log),
            "Distribution log could not be saved"
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(saved);
    }
}