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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FpsDealerDecisionFlowTests {
    private final FpsDealerApplicationRepository applications = mock(FpsDealerApplicationRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final NotificationService notifications = mock(NotificationService.class);
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final FpsDealerApplicationController controller =
            new FpsDealerApplicationController(applications, users, encoder, notifications, jdbc);

    @Test
    void adminApprovalActivatesDealerLoginImmediately() {
        FpsDealerApplication app = pendingApplication();
        User user = pendingUser();
        when(applications.findById(42L)).thenReturn(Optional.of(app));
        when(users.findByEmail("dealer@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.decide(42L, Map.of("status", "APPROVED"), admin());

        assertEquals(200, response.getStatusCode().value());
        assertEquals("ACTIVE", user.getStatus());
        verify(app).setStatus("APPROVED");
        verify(app).setDecidedAt(any());
        verify(applications).save(app);
        verify(users).save(user);
        verify(notifications).sendEmail(eq("dealer@example.com"), contains("APPROVED"), anyString());
    }

    @Test
    void adminRejectionStoresRequiredReasonAndDisablesDealerLogin() {
        FpsDealerApplication app = pendingApplication();
        User user = pendingUser();
        when(applications.findById(42L)).thenReturn(Optional.of(app));
        when(users.findByEmail("dealer@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.decide(42L,
                Map.of("status", "REJECTED", "rejectionReason", "Missing documentation"), admin());

        assertEquals(200, response.getStatusCode().value());
        assertEquals("REJECTED", user.getStatus());
        verify(app).setStatus("REJECTED");
        verify(app).setRejectionReason("Missing documentation");
        verify(applications).save(app);
        verify(users).save(user);
        verify(notifications).sendEmail(eq("dealer@example.com"), contains("REJECTED"), contains("Missing documentation"));
    }

    @Test
    void rejectionWithoutReasonIsRejectedBeforeLoadingApplication() {
        ResponseEntity<?> response = controller.decide(42L,
                Map.of("status", "REJECTED", "rejectionReason", " "), admin());

        assertEquals(400, response.getStatusCode().value());
        verify(applications, never()).findById(42L);
        verifyNoInteractions(users);
    }

    private FpsDealerApplication pendingApplication() {
        FpsDealerApplication app = mock(FpsDealerApplication.class);
        when(app.getStatus()).thenReturn("PENDING");
        when(app.getEmail()).thenReturn("dealer@example.com");
        when(app.getDealerName()).thenReturn("Test Dealer");
        when(app.getShopName()).thenReturn("Test FPS");
        return app;
    }

    private User pendingUser() {
        User user = new User();
        user.setEmail("dealer@example.com");
        user.setFullName("Test Dealer");
        user.setRole("FPS_DEALER");
        user.setStatus("PENDING");
        return user;
    }

    private UsernamePasswordAuthenticationToken admin() {
        return new UsernamePasswordAuthenticationToken("admin@example.com", "test",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}