package com.rationportal.controller;

import com.rationportal.model.FpsDealerApplication;
import com.rationportal.model.User;
import com.rationportal.repository.FpsDealerApplicationRepository;
import com.rationportal.repository.UserRepository;
import com.rationportal.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FpsDealerRegistrationStatusTests {
    private final FpsDealerApplicationRepository applications = mock(FpsDealerApplicationRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final NotificationService notifications = mock(NotificationService.class);
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);

    private FpsDealerApplicationController controller() {
        return new FpsDealerApplicationController(applications, users, encoder, notifications, jdbc);
    }

    @Test
    void duplicatePendingApplicationExplainsThatApprovalIsStillPending() {
        FpsDealerApplication existing = mock(FpsDealerApplication.class);
        when(existing.getStatus()).thenReturn("PENDING");
        when(applications.findByEmailIgnoreCase("dealer@example.com")).thenReturn(Optional.of(existing));
        when(applications.findByLicenseNumberIgnoreCase("FPS-LIC-001")).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = controller().register(validRegistration());

        assertEquals(409, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("DEALER_APPLICATION_PENDING", body.get("code"));
        assertEquals("PENDING", body.get("status"));
        verify(applications, never()).save(any(FpsDealerApplication.class));
        verify(users, never()).save(any(User.class));
    }

    @Test
    void duplicateApprovedApplicationDirectsDealerToSignIn() {
        FpsDealerApplication existing = mock(FpsDealerApplication.class);
        when(existing.getStatus()).thenReturn("APPROVED");
        when(applications.findByEmailIgnoreCase("dealer@example.com")).thenReturn(Optional.of(existing));
        when(applications.findByLicenseNumberIgnoreCase("FPS-LIC-001")).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = controller().register(validRegistration());

        assertEquals(409, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("DEALER_ALREADY_APPROVED", body.get("code"));
        verify(applications, never()).save(any(FpsDealerApplication.class));
    }

    @Test
    void rejectedApplicationCanBeResubmittedWithSameEmailAndLicence() {
        FpsDealerApplication existing = mock(FpsDealerApplication.class);
        when(existing.getId()).thenReturn(27L);
        when(existing.getStatus()).thenReturn("REJECTED");
        when(applications.findByEmailIgnoreCase("dealer@example.com")).thenReturn(Optional.of(existing));
        when(applications.findByLicenseNumberIgnoreCase("FPS-LIC-001")).thenReturn(Optional.of(existing));
        User user = new User();
        user.setEmail("dealer@example.com");
        user.setRole("FPS_DEALER");
        user.setStatus("REJECTED");
        when(users.findByEmail("dealer@example.com")).thenReturn(Optional.of(user));
        when(encoder.encode("StrongPassword123")).thenReturn("encoded-password");

        ResponseEntity<?> response = controller().register(validRegistration());

        assertEquals(200, response.getStatusCode().value());
        verify(existing).setStatus("PENDING");
        verify(existing).setRejectionReason(null);
        verify(existing).setDecidedAt(null);
        verify(applications).save(existing);
        assertEquals("PENDING", user.getStatus());
        verify(users).save(user);
    }

    private Map<String, Object> validRegistration() {
        Map<String, Object> body = new HashMap<>();
        body.put("dealerName", "Test Dealer");
        body.put("shopName", "Test Fair Price Shop");
        body.put("licenseNumber", "FPS-LIC-001");
        body.put("mobile", "9876543210");
        body.put("email", "dealer@example.com");
        body.put("password", "StrongPassword123");
        body.put("district", "Warangal");
        body.put("mandalVillage", "Hanamkonda");
        body.put("shopAddress", "Test shop address");
        body.put("latitude", "17.9689");
        body.put("longitude", "79.5941");
        body.put("termsAccepted", "true");
        return body;
    }
}