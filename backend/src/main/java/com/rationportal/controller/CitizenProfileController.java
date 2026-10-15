package com.rationportal.controller;

import com.rationportal.model.FamilyMember;
import com.rationportal.model.RationCard;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.RationCardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Returns the signed-in citizen's own issued ration card and family records.
 * Ownership is derived exclusively from the authenticated JWT principal.
 */
@RestController
@RequestMapping("/api/citizen")
public class CitizenProfileController {
    private final RationCardRepository rationCards;
    private final FamilyMemberRepository familyMembers;

    public CitizenProfileController(RationCardRepository rationCards, FamilyMemberRepository familyMembers) {
        this.rationCards = rationCards;
        this.familyMembers = familyMembers;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required."));
        }

        String email = authentication.getName().trim().toLowerCase(Locale.ROOT);
        List<RationCard> owned = rationCards.findByOwnerEmailIgnoreCaseOrderByCreatedAtDesc(email);
        RationCard card = owned.stream()
                .filter(this::isApproved)
                .findFirst()
                .orElse(null);

        Map<String, Object> result = new LinkedHashMap<>();
        if (card == null) {
            result.put("rationCard", null);
            result.put("familyMembers", List.of());
            result.put("applicationStatus", owned.isEmpty() ? "NONE" : owned.get(0).getStatus());
            result.put("message", "No active ration card is currently linked to this account.");
            return ResponseEntity.ok(result);
        }

        Map<String, Object> cardData = new LinkedHashMap<>();
        cardData.put("id", card.getId());
        cardData.put("number", card.getRationCardNumber());
        cardData.put("rationCardNumber", card.getRationCardNumber());
        cardData.put("type", card.getCardType());
        cardData.put("cardType", card.getCardType());
        cardData.put("headOfFamily", card.getHeadOfFamily());
        cardData.put("address", card.getAddress());
        cardData.put("district", card.getDistrict());
        cardData.put("state", card.getState());
        cardData.put("status", card.getStatus());
        cardData.put("approvalStatus", card.getApprovalStatus());
        cardData.put("issuedAt", card.getIssuedAt());
        result.put("rationCard", cardData);

        List<Map<String, Object>> members = new ArrayList<>();
        for (FamilyMember member : familyMembers.findByRationCardId(card.getId())) {
            if (member == null) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("memberId", member.getMemberId());
            row.put("name", member.getName());
            row.put("relationship", member.getRelationship());
            row.put("age", member.getAge());
            row.put("gender", member.getGender());
            row.put("riceQuotaKg", member.getRiceQuotaKg());
            members.add(row);
        }
        result.put("familyMembers", members);
        result.put("applicationStatus", "APPROVED");
        return ResponseEntity.ok(result);
    }

    private boolean isApproved(RationCard card) {
        if (card == null) return false;
        String status = normalize(card.getStatus());
        return "ACTIVE".equals(status) || "APPROVED".equals(status);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}