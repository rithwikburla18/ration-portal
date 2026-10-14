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
import org.springframework.lang.NonNull;

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
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath == null) contextPath = "";
        if (path == null || !path.startsWith(contextPath + "/api/")) {
            return true;
        }
        String method = request.getMethod();
        if (method != null && HttpMethod.OPTIONS.matches(method)) return true;
        if (path.startsWith(contextPath + "/api/auth/")
                || path.equals(contextPath + "/api/health")
                || path.equals(contextPath + "/api/ration-cards/my")
                || path.startsWith(contextPath + "/api/ration-cards/verify/")
                || path.equals(contextPath + "/api/fair-price-shops")
                || path.startsWith(contextPath + "/api/fair-price-shops/")
                || path.equals(contextPath + "/api/applications")
                || path.startsWith(contextPath + "/api/applications/")
                || path.equals(contextPath + "/api/grievances")
                || path.startsWith(contextPath + "/api/grievances/")
                || path.startsWith(contextPath + "/api/dashboard/")
                || path.startsWith(contextPath + "/api/transparency/")) {
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

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
