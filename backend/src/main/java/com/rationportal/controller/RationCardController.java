package com.rationportal.controller;

import com.rationportal.model.RationCard;
import com.rationportal.service.RationCardService;
import com.rationportal.service.RationCardIssuanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import java.nio.charset.StandardCharsets;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/ration-cards")
public class RationCardController {

    private final RationCardService service;
    private final RationCardIssuanceService issuanceService;

    public RationCardController(RationCardService service, RationCardIssuanceService issuanceService) {
        this.service = service;
        this.issuanceService = issuanceService;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    @GetMapping
    public ResponseEntity<?> getAll(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        if (isAdmin(authentication)) {
            return ResponseEntity.ok(service.getAll());
        }

        return ResponseEntity.ok(
                service.getApprovedCardsForOwner(authentication.getName())
        );
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyApprovedCard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        if (isAdmin(authentication)) {
            return ResponseEntity.ok(
                    Map.of("approved", true, "role", "ADMIN")
            );
        }

        RationCard card =
                service.getApprovedCardForOwner(authentication.getName());

        if (card == null) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "approved", false,
                            "message",
                            "No approved ration card is linked to this account yet."
                    ));
        }

        return ResponseEntity.ok(card);
    }

    @GetMapping("/{number}/pdf")
    public ResponseEntity<?> downloadPdf(@PathVariable String number, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Authentication required."));
        }
        try {
            byte[] pdf = issuanceService.downloadPdf(number, authentication.getName(), isAdmin(authentication));
            String filename = "ration-card-" + number + ".pdf";
            return ResponseEntity.ok()
                    .contentType(Objects.requireNonNull(MediaType.APPLICATION_PDF))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString())
                    .body(pdf);
        } catch (SecurityException ex) {
            return ResponseEntity.status(403).body(Map.of("message", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(Map.of("message", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.internalServerError().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/verify/{number}")
    public ResponseEntity<?> verifyCard(@PathVariable String number) {
        RationCard card = service.getByNumber(number);
        if (card == null || card.getStatus() == null
                || !("ACTIVE".equalsIgnoreCase(card.getStatus())
                || "APPROVED".equalsIgnoreCase(card.getStatus()))) {
            return ResponseEntity.status(404).body(Map.of(
                    "verified", false,
                    "message", "No active ration card was found for this number."
            ));
        }
        Map<String, Object> verification = new java.util.LinkedHashMap<>();
        verification.put("verified", true);
        verification.put("rationCardNumber", card.getRationCardNumber());
        verification.put("cardType", card.getCardType());
        verification.put("district", card.getDistrict());
        verification.put("state", card.getState());
        verification.put("status", card.getStatus());
        verification.put("issuedAt", card.getIssuedAt());
        verification.put("message", "This ration card is active in the Ration Portal.");
        return ResponseEntity.ok(verification);
    }

    @GetMapping("/{number}")
    public ResponseEntity<?> getByNumber(
            @PathVariable String number,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        if (isAdmin(authentication)) {
            RationCard card = service.getByNumber(number);

            return card == null
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(card);
        }

        RationCard card =
                service.getApprovedCardForOwner(
                        number,
                        authentication.getName()
                );

        if (card == null) {
            return ResponseEntity.status(403)
                    .body(Map.of(
                            "message",
                            "This ration card is not approved for your account."
                    ));
        }

        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody @NonNull RationCard card,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403).body(Map.of(
                    "message", "Citizens must submit a ration-card application. Only administrators can create a manual ration-card record."));
        }

        String validation = validateManualCard(card);
        if (validation != null) {
            return ResponseEntity.badRequest().body(Map.of("message", validation));
        }

        card.setRationCardNumber(card.getRationCardNumber().trim());
        card.setCardType(card.getCardType().trim());
        card.setHeadOfFamily(card.getHeadOfFamily().trim());
        card.setAddress(card.getAddress().trim());
        card.setDistrict(card.getDistrict().trim());
        card.setState(card.getState().trim());
        card.setOwnerEmail(card.getOwnerEmail().trim().toLowerCase(java.util.Locale.ROOT));
        if (card.getMobile() != null) card.setMobile(card.getMobile().trim());
        if (card.getPincode() != null) card.setPincode(card.getPincode().trim());
        card.setStatus("PENDING");
        card.setApprovalStatus("PENDING");
        return ResponseEntity.status(201).body(service.save(card));
    }

    private String validateManualCard(RationCard card) {
        String number = card.getRationCardNumber();
        if (number == null || !number.trim().matches("^[0-9]{12}$")) {
            return "Ration-card number must contain exactly 12 digits.";
        }
        if (card.getCardType() == null || card.getCardType().isBlank() || card.getCardType().trim().length() > 20) {
            return "Card type is required and must not exceed 20 characters.";
        }
        if (card.getHeadOfFamily() == null || card.getHeadOfFamily().isBlank() || card.getHeadOfFamily().trim().length() > 120) {
            return "Head-of-family name is required and must not exceed 120 characters.";
        }
        if (card.getAddress() == null || card.getAddress().isBlank() || card.getAddress().trim().length() > 2000) {
            return "Address is required and must not exceed 2000 characters.";
        }
        if (card.getDistrict() == null || card.getDistrict().isBlank() || card.getDistrict().trim().length() > 100
                || card.getState() == null || card.getState().isBlank() || card.getState().trim().length() > 100) {
            return "District and state are required and must not exceed 100 characters each.";
        }
        String email = card.getOwnerEmail();
        if (email == null || email.isBlank() || email.trim().length() > 150
                || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return "A valid owner email address is required.";
        }
        if (card.getMobile() != null && !card.getMobile().isBlank()
                && !card.getMobile().trim().matches("^(?:\\+91)?[6-9][0-9]{9}$")) {
            return "Enter a valid Indian mobile number.";
        }
        if (card.getPincode() != null && !card.getPincode().isBlank()
                && !card.getPincode().trim().matches("^[0-9]{6}$")) {
            return "Pincode must contain exactly six digits.";
        }
        return null;
    }


    @GetMapping("/admin/pending")
    public ResponseEntity<?> getPending(Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of(
                            "message",
                            "Administrator access required."
                    ));
        }

        return ResponseEntity.ok(service.getPending());
    }

    @PutMapping("/admin/{number}/approve")
    public ResponseEntity<?> approve(
            @PathVariable String number,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of(
                            "message",
                            "Only the administrator can approve ration cards."
                    ));
        }

        RationCard card = service.getByNumber(number);
        if (card == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(409).body(Map.of(
                "message", "Direct ration-card approval is disabled. Use the application-management workflow so issuance, family members, and the digital card are processed together.",
                "applicationNumber", card.getApplicationNumber() == null ? "" : card.getApplicationNumber()
        ));
    }

    @PutMapping("/admin/{number}/reject")
    public ResponseEntity<?> reject(
            @PathVariable String number,
            Authentication authentication) {

        if (!isAdmin(authentication)) {
            return ResponseEntity.status(403)
                    .body(Map.of(
                            "message",
                            "Only the administrator can reject ration cards."
                    ));
        }

        RationCard card = service.getByNumber(number);
        if (card == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(409).body(Map.of(
                "message", "Direct ration-card rejection is disabled. Reject the linked application through the application-management workflow.",
                "applicationNumber", card.getApplicationNumber() == null ? "" : card.getApplicationNumber()
        ));
    }
}
