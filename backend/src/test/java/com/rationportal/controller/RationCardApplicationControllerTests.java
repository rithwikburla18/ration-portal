package com.rationportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.RationCardApplicationRepository;
import com.rationportal.service.NotificationService;
import com.rationportal.service.RationCardIssuanceService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RationCardApplicationControllerTests {
    private final RationCardApplicationRepository repository = mock(RationCardApplicationRepository.class);
    private final NotificationService notifications = mock(NotificationService.class);
    private final RationCardIssuanceService issuance = mock(RationCardIssuanceService.class);
    private final RationCardApplicationController controller =
            new RationCardApplicationController(repository, new ObjectMapper(), notifications, issuance);

    @Test
    void rejectsInvalidPincodeWithoutSavingApplication() {
        Map<String, Object> request = validRequest();
        request.put("pincode", "50600");
        ResponseEntity<?> response = controller.submitApplication(request, citizen());
        assertEquals(400, response.getStatusCode().value());
        verify(repository, never()).save(any(RationCardApplication.class));
    }

    @Test
    void rejectsInvalidFamilyAgeWithoutSavingApplication() {
        Map<String, Object> request = validRequest();
        Map<String, Object> member = new LinkedHashMap<>();
        member.put("name", "Test Person");
        member.put("age", 150);
        member.put("gender", "Other");
        member.put("relationship", "Self");
        request.put("familyMembers", List.of(member));
        ResponseEntity<?> response = controller.submitApplication(request, citizen());
        assertEquals(400, response.getStatusCode().value());
        verify(repository, never()).save(any(RationCardApplication.class));
    }

    @Test
    void acceptsValidApplicationAndUsesAuthenticatedEmail() {
        Map<String, Object> request = validRequest();
        request.put("email", "attacker@example.test");
        when(repository.save(any(RationCardApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.submitApplication(request, citizen());

        assertEquals(200, response.getStatusCode().value());
        RationCardApplication saved = (RationCardApplication) response.getBody();
        assertEquals("citizen@example.com", saved.getEmail());
        assertEquals("SUBMITTED", saved.getStatus());
        assertEquals(1, saved.getFamilyMembers());
    }

    @Test
    void rejectsIllegalTransitionFromRejectedToApproved() {
        RationCardApplication existing = new RationCardApplication();
        existing.setApplicationNumber("RCAPP-TEST123");
        existing.setEmail("citizen@example.com");
        existing.setStatus("REJECTED");
        when(repository.findByApplicationNumber("RCAPP-TEST123")).thenReturn(Optional.of(existing));

        UsernamePasswordAuthenticationToken admin = new UsernamePasswordAuthenticationToken(
                "admin@example.com", "test", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        ResponseEntity<?> response = controller.updateApplicationStatus(
                "RCAPP-TEST123", Map.of("status", "APPROVED"), admin);

        assertEquals(409, response.getStatusCode().value());
        verify(repository, never()).save(any(RationCardApplication.class));
    }

    private Map<String, Object> validRequest() {
        Map<String, Object> member = new LinkedHashMap<>();
        member.put("name", "Test Person");
        member.put("age", 30);
        member.put("gender", "Other");
        member.put("relationship", "Self");
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("headOfFamily", "Test Person");
        request.put("mobile", "9876543210");
        request.put("email", "ignored@example.test");
        request.put("cardType", "BPL");
        request.put("address", "1 Test Road");
        request.put("district", "Warangal");
        request.put("state", "Telangana");
        request.put("pincode", "506001");
        request.put("familyMembers", new ArrayList<>(List.of(member)));
        return request;
    }

    private UsernamePasswordAuthenticationToken citizen() {
        return new UsernamePasswordAuthenticationToken(
                "citizen@example.com", "test",
                List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));
    }
}