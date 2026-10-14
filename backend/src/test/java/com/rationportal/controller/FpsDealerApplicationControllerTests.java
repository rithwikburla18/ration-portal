package com.rationportal.controller;

import com.rationportal.model.FpsDealerApplication;
import com.rationportal.model.User;
import com.rationportal.repository.FpsDealerApplicationRepository;
import com.rationportal.repository.UserRepository;
import com.rationportal.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FpsDealerApplicationControllerTests {
    private final FpsDealerApplicationRepository applications = mock(FpsDealerApplicationRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final NotificationService notifications = mock(NotificationService.class);
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);

    private FpsDealerApplicationController controller() {
        return new FpsDealerApplicationController(applications, users, encoder, notifications, jdbc);
    }

    @Test
    void registrationRequiresAcceptedDeclaration() {
        Map<String, Object> body = validRegistration();
        body.put("termsAccepted", "false");

        ResponseEntity<?> response = controller().register(body);

        assertEquals(400, response.getStatusCode().value());
        verify(applications, never()).save(any(FpsDealerApplication.class));
        verify(users, never()).save(any(User.class));
    }

    @Test
    void registrationCreatesPendingDealerAndDisabledLoginAccount() {
        when(users.findByEmail("dealer@example.com")).thenReturn(Optional.empty());
        when(applications.findByLicenseNumberIgnoreCase("FPS-LIC-001")).thenReturn(Optional.empty());
        when(encoder.encode("StrongPassword123")).thenReturn("encoded-password");

        ResponseEntity<?> response = controller().register(validRegistration());

        assertEquals(201, response.getStatusCode().value());
        verify(applications).save(any(FpsDealerApplication.class));
        verify(users).save(org.mockito.ArgumentMatchers.argThat(user ->
                "dealer@example.com".equals(user.getEmail())
                        && "FPS_DEALER".equals(user.getRole())
                        && "PENDING".equals(user.getStatus())
                        && "encoded-password".equals(user.getPasswordHash())));
        verify(encoder).encode("StrongPassword123");
    }

    @Test
    void nonAdministratorCannotDecideDealerApplication() {
        var auth = new UsernamePasswordAuthenticationToken(
                "dealer@example.com", "test",
                List.of(new SimpleGrantedAuthority("ROLE_FPS_DEALER")));

        ResponseEntity<?> response = controller().decide(
                42L, Map.of("status", "APPROVED"), auth);

        assertEquals(403, response.getStatusCode().value());
        verify(applications, never()).findById(42L);
        verify(users, never()).save(any(User.class));
        verify(notifications, never()).sendEmail(anyString(), anyString(), anyString());
    }

    private Map<String, Object> validRegistration() {
        return new java.util.HashMap<>(Map.ofEntries(
                Map.entry("dealerName", "Test Dealer"),
                Map.entry("shopName", "Test Fair Price Shop"),
                Map.entry("licenseNumber", "FPS-LIC-001"),
                Map.entry("mobile", "9876543210"),
                Map.entry("email", "dealer@example.com"),
                Map.entry("password", "StrongPassword123"),
                Map.entry("district", "Warangal"),
                Map.entry("mandalVillage", "Hanamkonda"),
                Map.entry("shopAddress", "Test address"),
                Map.entry("latitude", 17.99),
                Map.entry("longitude", 79.59),
                Map.entry("termsAccepted", "true")));
    }
}
