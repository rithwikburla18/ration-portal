package com.rationportal.controller;

import com.rationportal.model.DistributionLog;
import com.rationportal.model.FamilyMember;
import com.rationportal.model.RationCard;
import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.DistributionLogRepository;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.RationCardApplicationRepository;
import com.rationportal.repository.RationCardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RationCardRepository rationCardRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final DistributionLogRepository distributionLogRepository;
    private final RationCardApplicationRepository applicationRepository;

    public DashboardController(
            RationCardRepository rationCardRepository,
            FamilyMemberRepository familyMemberRepository,
            DistributionLogRepository distributionLogRepository,
            RationCardApplicationRepository applicationRepository) {
        this.rationCardRepository = rationCardRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.distributionLogRepository = distributionLogRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> metrics(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required."));
        }

        List<RationCard> cards = rationCardRepository.findAll();
        List<DistributionLog> distributions = distributionLogRepository.findAll();

        BigDecimal totalRice = BigDecimal.ZERO;
        for (DistributionLog distribution : distributions) {
            if (distribution == null) {
                continue;
            }
            BigDecimal quantity = distribution.getQuantityKg();
            if (quantity != null) {
                totalRice = totalRice.add(quantity);
            }
        }

        List<String> fpsIds = new ArrayList<>();
        for (DistributionLog distribution : distributions) {
            if (distribution == null) {
                continue;
            }
            String fpsId = distribution.getFpsId();
            if (fpsId == null || fpsId.isBlank()) {
                continue;
            }
            String trimmedFpsId = fpsId.trim();
            if (!fpsIds.contains(trimmedFpsId)) {
                fpsIds.add(trimmedFpsId);
            }
        }

        long activeFps = fpsIds.size();

        long approvedCards = 0;
        for (RationCard card : cards) {
            if (isApproved(card)) {
                approvedCards++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("authenticated", true);
        result.put("role", isAdmin(authentication) ? "ADMIN" : "CITIZEN");
        result.put("totalRationCards", cards.size());
        result.put("approvedRationCards", approvedCards);
        result.put("riceDistributedKg", totalRice);
        result.put("activeFps", activeFps);

        if (!isAdmin(authentication)) {
            String email = authentication.getName().trim().toLowerCase();

            List<RationCard> ownerCards =
                    rationCardRepository.findByOwnerEmailIgnoreCase(email);

            RationCard card = null;
            for (RationCard ownerCard : ownerCards) {
                if (isApproved(ownerCard)) {
                    card = ownerCard;
                    break;
                }
            }

            RationCard latestCard =
                    ownerCards.isEmpty() ? null : ownerCards.get(ownerCards.size() - 1);

            List<FamilyMember> family = card == null
                    ? List.of()
                    : familyMemberRepository.findByRationCardId(card.getId());

            BigDecimal familyRice = BigDecimal.ZERO;
            long adultCount = 0;
            long childCount = 0;

            for (FamilyMember member : family) {
                if (member == null) {
                    continue;
                }

                BigDecimal riceQuota = member.getRiceQuotaKg();
                if (riceQuota != null) {
                    familyRice = familyRice.add(riceQuota);
                }

                Integer age = member.getAge();
                if (age != null) {
                    if (age >= 18) {
                        adultCount++;
                    } else if (age >= 0) {
                        childCount++;
                    }
                }
            }

            List<RationCardApplication> applications =
                    applicationRepository.findAllByEmailOrderByCreatedAtDesc(email);

            RationCardApplication latestApplication =
                    applications.isEmpty() ? null : applications.get(0);

            Map<String, Object> ownCard = new LinkedHashMap<>();
            ownCard.put("number",
                    card == null ? null : card.getRationCardNumber());
            ownCard.put("type",
                    card == null ? null : card.getCardType());
            ownCard.put("status",
                    latestCard == null ? "NOT_ISSUED" : latestCard.getStatus());
            ownCard.put("approvalStatus",
                    latestCard == null ? "NOT_ISSUED" : latestCard.getApprovalStatus());

            result.put("rationCard", ownCard);
            result.put("familyMembers", family.size());
            result.put("adultCount", adultCount);
            result.put("childCount", childCount);
            result.put("familyRiceQuotaKg", familyRice);
            result.put("applicationStatus",
                    latestApplication == null
                            ? "NONE"
                            : latestApplication.getStatus());

            List<Map<String, Object>> recentDistribution = new ArrayList<>();

            if (card != null) {
                List<DistributionLog> cardDistributions =
                        distributionLogRepository.findByRationCardId(card.getId());

                cardDistributions.sort((first, second) -> {
                    if (first == null) {
                        return 1;
                    }
                    if (second == null) {
                        return -1;
                    }
                    if (first.getDistributionDate() == null
                            && second.getDistributionDate() == null) {
                        return 0;
                    }
                    if (first.getDistributionDate() == null) {
                        return 1;
                    }
                    if (second.getDistributionDate() == null) {
                        return -1;
                    }

                    return second.getDistributionDate()
                            .compareTo(first.getDistributionDate());
                });

                int limit = Math.min(5, cardDistributions.size());

                for (int i = 0; i < limit; i++) {
                    DistributionLog distribution = cardDistributions.get(i);

                    if (distribution == null) {
                        continue;
                    }

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", distribution.getDistributionDate());
                    item.put("fpsId", distribution.getFpsId());
                    item.put("quantityKg", distribution.getQuantityKg());
                    item.put("status", distribution.getTransactionStatus());

                    recentDistribution.add(item);
                }
            }

            result.put("recentDistribution", recentDistribution);
        }

        return ResponseEntity.ok(result);
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

        if (authentication.getAuthorities() == null) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority == null) {
                continue;
            }

            String authorityName = authority.getAuthority();

            if ("ROLE_ADMIN".equals(authorityName)) {
                return true;
            }
        }

        return false;
    }

    private boolean isApproved(RationCard card) {
        if (card == null) {
            return false;
        }

        String status = card.getStatus();

        if (status == null) {
            return false;
        }

        String normalized = status.trim().toUpperCase();

        return "APPROVED".equals(normalized)
                || "ACTIVE".equals(normalized);
    }
}