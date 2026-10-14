package com.rationportal.security;

import com.rationportal.repository.RationCardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ApprovedCitizenAccessFilterTests {
    private final ApprovedCitizenAccessFilter filter =
            new ApprovedCitizenAccessFilter(mock(RationCardRepository.class));

    @Test
    void publicFairPriceShopSearchIsNotBlockedByCitizenApprovalFilter() {
        assertTrue(shouldNotFilter("GET", "/api/fair-price-shops"));
        assertTrue(shouldNotFilter("GET", "/api/fair-price-shops/search"));
    }

    @Test
    void publicRationCardVerificationIsNotBlockedByCitizenApprovalFilter() {
        assertTrue(shouldNotFilter("GET", "/api/ration-cards/verify/123456789012"));
    }

    @Test
    void protectedEndpointsRemainSubjectToApprovalFilter() {
        assertFalse(shouldNotFilter("GET", "/api/distribution-logs/ration-card/1"));
        assertFalse(shouldNotFilter("POST", "/api/fps-distribution/otp"));
    }

    @Test
    void dashboardTransparencyAndGrievancesRemainAvailableBeforeCardApproval() {
        assertTrue(shouldNotFilter("GET", "/api/dashboard/metrics"));
        assertTrue(shouldNotFilter("GET", "/api/transparency/summary"));
        assertTrue(shouldNotFilter("GET", "/api/grievances"));
        assertTrue(shouldNotFilter("POST", "/api/grievances"));
    }

    @Test
    void portabilityAndFamilyServicesStillRequireCardApproval() {
        assertFalse(shouldNotFilter("POST", "/api/onorc/portability"));
        assertFalse(shouldNotFilter("GET", "/api/family-members/ration-card/1"));
    }

    @Test
    void applicationWorkflowRemainsAvailableBeforeCardApproval() {
        assertTrue(shouldNotFilter("POST", "/api/applications"));
        assertTrue(shouldNotFilter("GET", "/api/applications/RCAPP-123"));
    }

    private boolean shouldNotFilter(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        return filter.shouldNotFilter(request);
    }
}