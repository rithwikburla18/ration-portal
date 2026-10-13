package com.rationportal.controller;

import com.rationportal.repository.DistributionLogRepository;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.RationCardApplicationRepository;
import com.rationportal.repository.RationCardRepository;
import com.rationportal.repository.FpsDealerApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardControllerTests {
    @Test
    void citizenMetricsUseDatabaseAggregatesInsteadOfLoadingAllCitizensData() {
        RationCardRepository cards = mock(RationCardRepository.class);
        FamilyMemberRepository members = mock(FamilyMemberRepository.class);
        DistributionLogRepository distributions = mock(DistributionLogRepository.class);
        RationCardApplicationRepository applications = mock(RationCardApplicationRepository.class);
        FpsDealerApplicationRepository fps = mock(FpsDealerApplicationRepository.class);

        when(cards.count()).thenReturn(10L);
        when(cards.countByStatusIgnoreCase("ACTIVE")).thenReturn(4L);
        when(cards.countByStatusIgnoreCase("APPROVED")).thenReturn(1L);
        when(cards.findByOwnerEmailIgnoreCaseOrderByCreatedAtDesc("citizen@example.com")).thenReturn(List.of());
        when(distributions.sumQuantityKg()).thenReturn(new BigDecimal("50.00"));
        when(fps.countByStatusIgnoreCase("APPROVED")).thenReturn(2L);
        when(applications.findAllByEmailOrderByCreatedAtDesc("citizen@example.com")).thenReturn(List.of());

        DashboardController controller = new DashboardController(cards, members, distributions, applications, fps);
        var authentication = new UsernamePasswordAuthenticationToken(
                "citizen@example.com",
                "test",
                List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));

        ResponseEntity<?> response = controller.metrics(authentication);
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10L, body.get("totalRationCards"));
        assertEquals(5L, body.get("approvedRationCards"));
        assertEquals(new BigDecimal("50.00"), body.get("riceDistributedKg"));
        assertEquals(2L, body.get("activeFps"));
        verify(cards, never()).findAll();
        verify(distributions, never()).findAll();
    }
}