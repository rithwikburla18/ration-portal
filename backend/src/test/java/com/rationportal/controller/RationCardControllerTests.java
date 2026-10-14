package com.rationportal.controller;

import com.rationportal.model.RationCard;
import com.rationportal.service.RationCardIssuanceService;
import com.rationportal.service.RationCardService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
class RationCardControllerTests {
    private final RationCardService service = mock(RationCardService.class);
    private final RationCardIssuanceService issuance = mock(RationCardIssuanceService.class);
    private final RationCardController controller = new RationCardController(service, issuance);

    @Test
    void citizenCannotBypassApplicationWorkflowWithManualCardCreation() {
        ResponseEntity<?> response = controller.create(validCard(), citizen());
        assertEquals(403, response.getStatusCode().value());
        verify(service, never()).save(any(RationCard.class));
    }

    @Test
    void adminCanCreateValidatedManualCardAsPending() {
        RationCard card = validCard();
        when(service.save(any(RationCard.class))).thenAnswer(invocation -> java.util.Objects.requireNonNull(invocation.getArgument(0, RationCard.class), "Expected saved ration card"));

        ResponseEntity<?> response = controller.create(card, admin());

        assertEquals(201, response.getStatusCode().value());
        assertEquals("PENDING", card.getStatus());
        assertEquals("PENDING", card.getApprovalStatus());
        assertEquals("owner@example.com", card.getOwnerEmail());
        verify(service).save(card);
    }

    @Test
    void manualCardCreationRejectsInvalidNumberAndPincode() {
        RationCard card = validCard();
        card.setRationCardNumber("RC-123");
        ResponseEntity<?> badNumber = controller.create(card, admin());
        assertEquals(400, badNumber.getStatusCode().value());

        card = validCard();
        card.setPincode("12345");
        ResponseEntity<?> badPincode = controller.create(card, admin());
        assertEquals(400, badPincode.getStatusCode().value());
        verify(service, never()).save(any(RationCard.class));
    }

    private RationCard validCard() {
        RationCard card = new RationCard();
        card.setRationCardNumber("123456789012");
        card.setCardType("BPL");
        card.setHeadOfFamily("Test Citizen");
        card.setAddress("1 Test Road");
        card.setDistrict("Warangal");
        card.setState("Telangana");
        card.setOwnerEmail(" Owner@Example.com ");
        card.setMobile("9876543210");
        card.setPincode("506001");
        return card;
    }

    private UsernamePasswordAuthenticationToken citizen() {
        return new UsernamePasswordAuthenticationToken("citizen@example.com", "test",
                List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));
    }

    private UsernamePasswordAuthenticationToken admin() {
        return new UsernamePasswordAuthenticationToken("admin@example.com", "test",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
