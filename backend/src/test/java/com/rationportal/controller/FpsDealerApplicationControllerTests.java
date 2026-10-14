
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FpsDealerApplicationControllerTests {

    private final FpsDealerApplicationRepository applications =
            mock(FpsDealerApplicationRepository.class);

    private final UserRepository users = mock(UserRepository.class);

    private final PasswordEncoder encoder = mock(PasswordEncoder.class);

    private final NotificationService notifications =
            mock(NotificationService.class);

    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);

    private FpsDealerApplicationController controller() {
        return new FpsDealerApplicationController(
                applications,
                users,
                encoder,
                notifications,
                jdbc);
    }

    @Test
    void registrationRequiresAcceptedDeclaration() {
        Map<String, Object> body = validRegistration();
        body.put("termsAccepted", "false");

        ResponseEntity<?> response = controller().register(body);

        assertEquals(400, response.getStatusCode().value());

        verifyNoApplicationWasSaved();
        verifyNoUserWasSaved();
    }

    @Test
    void registrationRejectsNonFiniteCoordinates() {
        FpsDealerApplicationController endpoint = controller();

        Map<String, Object> nanLatitude = validRegistration();
        nanLatitude.put("latitude", "NaN");
        assertEquals(400, endpoint.register(nanLatitude).getStatusCode().value());

        Map<String, Object> nanLongitude = validRegistration();
        nanLongitude.put("longitude", "NaN");
        assertEquals(400, endpoint.register(nanLongitude).getStatusCode().value());

        Map<String, Object> infiniteLatitude = validRegistration();
        infiniteLatitude.put("latitude", "Infinity");
        assertEquals(400, endpoint.register(infiniteLatitude).getStatusCode().value());

        Map<String, Object> infiniteLongitude = validRegistration();
        infiniteLongitude.put("longitude", "-Infinity");
        assertEquals(400, endpoint.register(infiniteLongitude).getStatusCode().value());

        verifyNoApplicationWasSaved();
        verifyNoUserWasSaved();
    }

    @Test
    void registrationCreatesPendingDealerAndDisabledLoginAccount() {
        when(users.findByEmail("dealer@example.com"))
                .thenReturn(Optional.empty());

        when(applications.findByLicenseNumberIgnoreCase("FPS-LIC-001"))
                .thenReturn(Optional.empty());

        when(encoder.encode("StrongPassword123"))
                .thenReturn("encoded-password");

        ResponseEntity<?> response = controller().register(validRegistration());

        assertEquals(201, response.getStatusCode().value());

        verifyApplicationWasSaved();
        verifyDealerUserWasSaved();
        verify(encoder).encode("StrongPassword123");
    }

    @Test
    void nonAdministratorCannotDecideDealerApplication() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "dealer@example.com",
                "test",
                List.of(new SimpleGrantedAuthority("ROLE_FPS_DEALER")));

        ResponseEntity<?> response = controller().decide(
                42L,
                Map.of("status", "APPROVED"),
                authentication);

        assertEquals(403, response.getStatusCode().value());

        verify(applications, never()).findById(42L);
        verifyNoUserWasSaved();

        verify(
                notifications,
                never()).sendEmail(
                        anyString(),
                        anyString(),
                        anyString());
    }

    /*
     * These helpers isolate the null-safety suppression to Mockito matcher
     * calls. Mockito matchers can appear nullable to Eclipse's null analysis,
     * even though they are used to describe arguments during verification.
     */

    @SuppressWarnings("null")
    private void verifyNoApplicationWasSaved() {
        verify(applications, never())
                .save(any(FpsDealerApplication.class));
    }

    @SuppressWarnings("null")
    private void verifyNoUserWasSaved() {
        verify(users, never()).save(any(User.class));
    }

    @SuppressWarnings("null")
    private void verifyApplicationWasSaved() {
        verify(applications).save(any(FpsDealerApplication.class));
    }

    @SuppressWarnings("null")
    private void verifyDealerUserWasSaved() {
        verify(users).save(org.mockito.ArgumentMatchers.argThat(user ->
                "dealer@example.com".equals(user.getEmail())
                        && "FPS_DEALER".equals(user.getRole())
                        && "PENDING".equals(user.getStatus())
                        && "encoded-password".equals(user.getPasswordHash())));
    }

    private Map<String, Object> validRegistration() {
        return new HashMap<>(Map.ofEntries(
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
