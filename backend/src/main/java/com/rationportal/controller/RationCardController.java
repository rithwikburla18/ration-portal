package com.rationportal.controller;

import com.rationportal.model.RationCard;
import com.rationportal.service.RationCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ration-cards")
public class RationCardController {

    private final RationCardService service;

    public RationCardController(RationCardService service) {
        this.service = service;
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

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required."));
        }

        if (isAdmin(authentication)) {

            if (card.getOwnerEmail() == null
                    || card.getOwnerEmail().isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Owner email is required when an administrator creates a ration card."
                        ));
            }

            card.setOwnerEmail(
                    card.getOwnerEmail().trim().toLowerCase()
            );

            card.setStatus("PENDING");

            return ResponseEntity.status(201)
                    .body(service.save(card));
        }

        card.setOwnerEmail(
                authentication.getName().trim().toLowerCase()
        );

        card.setStatus("PENDING");

        return ResponseEntity.status(201)
                .body(service.save(card));
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

        RationCard card = service.approve(number);

        return card == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(card);
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

        RationCard card = service.reject(number);

        return card == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(card);
    }
}
