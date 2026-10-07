package com.rationportal.controller;

import com.rationportal.model.RationCard;
import com.rationportal.service.RationCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ration-cards")
public class RationCardController {

    private final RationCardService service;

    public RationCardController(RationCardService service) {
        this.service = service;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null &&
            authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @GetMapping
    public List<RationCard> getAll(Authentication authentication) {
        if (isAdmin(authentication)) {
            return service.getAll();
        }
        return service.getApprovedCardsForOwner(authentication.getName());
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyCard(Authentication authentication) {
        if (isAdmin(authentication)) {
            Map<String, Object> result = new HashMap<>();
            result.put("approved", true);
            result.put("role", "ADMIN");
            return ResponseEntity.ok(result);
        }

        try {
            return ResponseEntity.ok(service.getApprovedCardForOwner(authentication.getName()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("approved", false, "message", ex.getMessage()));
        }
    }

    @GetMapping("/{number}")
    public RationCard getByNumber(
        @PathVariable String number,
        Authentication authentication
    ) {
        if (isAdmin(authentication)) {
            return service.getByNumber(number);
        }
        return service.getApprovedCardForOwner(number, authentication.getName());
    }

    @PostMapping
    public ResponseEntity<?> create(
        @RequestBody RationCard card,
        Authentication authentication
    ) {
        if (!isAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Only ADMIN can create ration cards"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(card));
    }

    @GetMapping("/admin/pending")
    public List<RationCard> getPending() {
        return service.getPending();
    }

    @PutMapping("/admin/{number}/approve")
    public RationCard approve(@PathVariable String number) {
        return service.approve(number);
    }

    @PutMapping("/admin/{number}/reject")
    public RationCard reject(@PathVariable String number) {
        return service.reject(number);
    }
}