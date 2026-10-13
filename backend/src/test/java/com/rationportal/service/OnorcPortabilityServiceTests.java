package com.rationportal.service;

import com.rationportal.model.OnorcPortability;
import com.rationportal.model.RationCard;
import com.rationportal.repository.OnorcPortabilityRepository;
import com.rationportal.repository.RationCardRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OnorcPortabilityServiceTests {
    @Test
    void serverAssignsReferenceAndAuthoritativeHomeAndStatusValues() {
        OnorcPortabilityRepository requests = mock(OnorcPortabilityRepository.class);
        RationCardRepository cards = mock(RationCardRepository.class);
        OnorcPortabilityService service = new OnorcPortabilityService(requests, cards);
        RationCard card = new RationCard();
        card.setRationCardNumber("123456789012");
        card.setOwnerEmail("citizen@example.com");
        card.setStatus("ACTIVE");
        card.setState("Telangana");
        card.setDistrict("Warangal");
        when(cards.findByRationCardNumberAndOwnerEmailIgnoreCase("123456789012", "citizen@example.com"))
                .thenReturn(Optional.of(card));
        when(requests.save(any(OnorcPortability.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OnorcPortability request = new OnorcPortability();
        request.setRationCardNumber("123456789012");
        request.setDestinationState(" Maharashtra ");
        request.setDestinationDistrict(" Pune ");
        request.setReferenceNumber("CLIENT-CHOSEN");
        request.setStatus("COMPLETED");

        OnorcPortability result = service.create(request, "CITIZEN@example.com", false);

        assertNotEquals("CLIENT-CHOSEN", result.getReferenceNumber());
        assertEquals(true, result.getReferenceNumber().startsWith("ONORC-"));
        assertEquals("REQUESTED", result.getStatus());
        assertEquals("citizen@example.com", result.getRequestedBy());
        assertEquals("Telangana", result.getHomeState());
        assertEquals("Warangal", result.getHomeDistrict());
        assertEquals("Maharashtra", result.getDestinationState());
        assertEquals("Pune", result.getDestinationDistrict());
    }

    @Test
    void rejectsMissingDestinationBeforeSaving() {
        OnorcPortabilityRepository requests = mock(OnorcPortabilityRepository.class);
        RationCardRepository cards = mock(RationCardRepository.class);
        OnorcPortabilityService service = new OnorcPortabilityService(requests, cards);
        RationCard card = new RationCard();
        card.setRationCardNumber("123456789012");
        card.setOwnerEmail("citizen@example.com");
        card.setStatus("ACTIVE");
        when(cards.findByRationCardNumberAndOwnerEmailIgnoreCase("123456789012", "citizen@example.com"))
                .thenReturn(Optional.of(card));
        OnorcPortability request = new OnorcPortability();
        request.setRationCardNumber("123456789012");
        assertThrows(IllegalArgumentException.class, () -> service.create(request, "citizen@example.com", false));
    }

    @Test
    void statusCanMoveFromRequestToProcessingButRejectedIsTerminal() {
        OnorcPortabilityRepository requests = mock(OnorcPortabilityRepository.class);
        RationCardRepository cards = mock(RationCardRepository.class);
        OnorcPortabilityService service = new OnorcPortabilityService(requests, cards);
        OnorcPortability request = new OnorcPortability();
        request.setReferenceNumber("ONORC-TEST1234567890");
        request.setStatus("REQUESTED");
        when(requests.findByReferenceNumber("ONORC-TEST1234567890")).thenReturn(Optional.of(request));
        when(requests.save(any(OnorcPortability.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("PROCESSING", service.updateStatus("ONORC-TEST1234567890", "processing").getStatus());
        request.setStatus("REJECTED");
        assertThrows(IllegalStateException.class,
                () -> service.updateStatus("ONORC-TEST1234567890", "APPROVED"));
    }
}