package com.rationportal.controller;

import com.rationportal.model.Grievance;
import com.rationportal.model.RationCard;
import com.rationportal.repository.GrievanceRepository;
import com.rationportal.repository.RationCardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GrievanceControllerTests {
    private final GrievanceRepository grievances = mock(GrievanceRepository.class);
    private final RationCardRepository cards = mock(RationCardRepository.class);
    private final GrievanceController controller = new GrievanceController(grievances, cards);

    @Test
    void citizenCannotChooseGrievanceReferenceOrInitialStatus() {
        RationCard card = new RationCard();
        card.setRationCardNumber("123456789012");
        card.setOwnerEmail("citizen@example.com");
        when(cards.findByRationCardNumberAndOwnerEmailIgnoreCase(
                "123456789012", "citizen@example.com")).thenReturn(Optional.of(card));
        when(grievances.save(any(Grievance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Grievance request = validGrievance();
        request.setGrievanceNumber("FAKE-REFERENCE");
        request.setStatus("RESOLVED");
        ResponseEntity<?> response = controller.submit(request, citizen());

        assertEquals(201, response.getStatusCode().value());
        Grievance saved = (Grievance) response.getBody();
        assertNotEquals("FAKE-REFERENCE", saved.getGrievanceNumber());
        assertTrue(saved.getGrievanceNumber().startsWith("GRV"));
        assertEquals("SUBMITTED", saved.getStatus());
        assertEquals("citizen@example.com", saved.getSubmittedByEmail());
    }

    @Test
    void citizenListingQueriesOnlyThatCitizensGrievances() {
        Grievance own = validGrievance();
        own.setGrievanceNumber("GRV123456789ABC");
        own.setSubmittedByEmail("citizen@example.com");
        when(grievances.findBySubmittedByEmailIgnoreCaseOrderByCreatedAtDesc("citizen@example.com"))
                .thenReturn(List.of(own));

        ResponseEntity<?> response = controller.getAll(citizen());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(own), response.getBody());
        verify(grievances, never()).findAll();
    }

    @Test
    void onlyAdminCanUpdateGrievanceStatus() {
        Grievance existing = validGrievance();
        existing.setGrievanceNumber("GRV123456789ABC");
        existing.setStatus("PROCESSING");
        when(grievances.findByGrievanceNumber("GRV123456789ABC")).thenReturn(Optional.of(existing));
        when(grievances.save(any(Grievance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> denied = controller.updateStatus(
                "GRV123456789ABC", Map.of("status", "RESOLVED"), citizen());
        assertEquals(403, denied.getStatusCode().value());
        verify(grievances, never()).save(any(Grievance.class));

        ResponseEntity<?> approved = controller.updateStatus(
                "GRV123456789ABC", Map.of("status", "RESOLVED"), admin());
        assertEquals(200, approved.getStatusCode().value());
        assertEquals("RESOLVED", existing.getStatus());
    }

    @Test
    void finalGrievanceStatusCannotBeChangedAfterClosure() {
        Grievance existing = validGrievance();
        existing.setGrievanceNumber("GRV123456789ABC");
        existing.setStatus("RESOLVED");
        when(grievances.findByGrievanceNumber("GRV123456789ABC")).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = controller.updateStatus(
                "GRV123456789ABC", Map.of("status", "PROCESSING"), admin());

        assertEquals(409, response.getStatusCode().value());
    }

    private Grievance validGrievance() {
        Grievance grievance = new Grievance();
        grievance.setApplicantName("Test Citizen");
        grievance.setRationCardNumber("123456789012");
        grievance.setCategory("Distribution");
        grievance.setContactNumber("9876543210");
        grievance.setDescription("Test grievance details");
        return grievance;
    }

    private UsernamePasswordAuthenticationToken citizen() {
        return new UsernamePasswordAuthenticationToken(
                "citizen@example.com", "test",
                List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));
    }

    private UsernamePasswordAuthenticationToken admin() {
        return new UsernamePasswordAuthenticationToken(
                "admin@example.com", "test",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}