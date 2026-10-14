
package com.rationportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.RationCardApplicationRepository;
import com.rationportal.service.NotificationService;
import com.rationportal.service.RationCardIssuanceService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RationCardApplicationStatusTransitionTests {

    private static final String APPLICATION_NUMBER = "RCAPP-TEST123";

    private final RationCardApplicationRepository repository =
            mock(RationCardApplicationRepository.class);

    private final NotificationService notifications =
            mock(NotificationService.class);

    private final RationCardIssuanceService issuanceService =
            mock(RationCardIssuanceService.class);

    private final RationCardApplicationController controller =
            new RationCardApplicationController(
                    repository,
                    new ObjectMapper(),
                    notifications,
                    issuanceService);

    @Test
    void approvedApplicationCannotReturnToPending() {
        RationCardApplication application = application("APPROVED");

        when(repository.findByApplicationNumber(APPLICATION_NUMBER))
                .thenReturn(Optional.of(application));

        ResponseEntity<?> response = updateStatus("PENDING");

        assertEquals(409, response.getStatusCode().value());
        assertEquals("APPROVED", application.getStatus());

        verifyApplicationWasNotSaved();

        verify(issuanceService, never())
                .processApplicationStatus(any(RationCardApplication.class));
    }

    @Test
    void rejectedApplicationCannotBeApprovedAgain() {
        RationCardApplication application = application("REJECTED");

        when(repository.findByApplicationNumber(APPLICATION_NUMBER))
                .thenReturn(Optional.of(application));

        ResponseEntity<?> response = updateStatus("APPROVED");

        assertEquals(409, response.getStatusCode().value());
        assertEquals("REJECTED", application.getStatus());

        verifyApplicationWasNotSaved();

        verify(issuanceService, never())
                .processApplicationStatus(any(RationCardApplication.class));
    }

    @Test
    void pendingApplicationCanMoveToVerified() {
        RationCardApplication application = application("PENDING");

        when(repository.findByApplicationNumber(APPLICATION_NUMBER))
                .thenReturn(Optional.of(application));

        stubSaveReturningArgument();

        ResponseEntity<?> response = updateStatus("VERIFIED");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("VERIFIED", application.getStatus());

        verifyApplicationWasSavedTwice(application);
        verify(issuanceService).processApplicationStatus(application);
    }

    /*
     * Keep null-analysis suppressions limited to helpers using Mockito
     * matchers or framework APIs whose nullness annotations are broader
     * than the values supplied by these tests.
     */

    @SuppressWarnings("null")
    private ResponseEntity<?> updateStatus(String status) {
        return controller.updateApplicationStatus(
                APPLICATION_NUMBER,
                Map.of("status", status),
                admin());
    }

    @SuppressWarnings("null")
    private void verifyApplicationWasNotSaved() {
        verify(repository, never())
                .save(any(RationCardApplication.class));
    }

    @SuppressWarnings("null")
    private void stubSaveReturningArgument() {
        when(repository.save(any(RationCardApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }


        @SuppressWarnings("null")
        private void verifyApplicationWasSavedTwice(
        RationCardApplication application) {
        verify(repository, times(2)).save(application);
        }


    private static RationCardApplication application(String status) {
        RationCardApplication application = new RationCardApplication();
        application.setApplicationNumber(APPLICATION_NUMBER);
        application.setStatus(status);
        application.setApplicantName("Test Applicant");
        application.setEmail("citizen-test@example.com");
        application.setMobile("9000000000");
        return application;
    }

    private static Authentication admin() {
        return new UsernamePasswordAuthenticationToken(
                "admin-test@example.com",
                "test-only",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
