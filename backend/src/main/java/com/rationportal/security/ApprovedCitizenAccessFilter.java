package com.rationportal.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationportal.model.RationCard;
import com.rationportal.repository.RationCardRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Enforces ration-card approval at the API boundary for citizen services.
 * Application submission/status and the current-card status endpoint stay
 * available so a citizen can obtain approval and see the decision.
 */
public class ApprovedCitizenAccessFilter extends OncePerRequestFilter {

    private final RationCardRepository rationCardRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApprovedCitizenAccessFilter(RationCardRepository rationCardRepository) {
        this.rationCardRepository = rationCardRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null || !path.startsWith(request.getContextPath() + "/api/")) {
            return true;
        }
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;
        if (path.startsWith(request.getContextPath() + "/api/auth/")
                || path.equals(request.getContextPath() + "/api/health")
                || path.equals(request.getContextPath() + "/api/ration-cards/my")
                || path.equals(request.getContextPath() + "/api/applications")
                || path.startsWith(request.getContextPath() + "/api/applications/")) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream()
                .noneMatch(authority -> "ROLE_CITIZEN".equals(authority.getAuthority()))) {
            filterChain.doFilter(request, response);
            return;
        }

        List<RationCard> cards = rationCardRepository
                .findByOwnerEmailIgnoreCase(authentication.getName());

        boolean approved = cards != null && cards.stream().anyMatch(card -> {
            if (card == null || card.getStatus() == null) return false;
            String status = card.getStatus().trim().toUpperCase(Locale.ROOT);
            return "APPROVED".equals(status) || "ACTIVE".equals(status);
        });

        if (!approved) {
            response.setStatus(423);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "code", "RATION_CARD_APPROVAL_REQUIRED",
                    "message", "Administrator approval is required before you can use this service."
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
