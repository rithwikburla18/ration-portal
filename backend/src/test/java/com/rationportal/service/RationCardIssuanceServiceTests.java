package com.rationportal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rationportal.model.FamilyMember;
import com.rationportal.model.RationCard;
import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.RationCardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
class RationCardIssuanceServiceTests {

    @Test
    void repeatedApprovalReusesCardAndMembersAndRetriesFailedEmail() throws Exception {
        RationCardRepository cards = mock(RationCardRepository.class);
        FamilyMemberRepository members = mock(FamilyMemberRepository.class);
        NotificationService notifications = mock(NotificationService.class);

        RationCardIssuanceService service = new RationCardIssuanceService(
                cards, members, new ObjectMapper(), notifications,
                new BigDecimal("6"), "https://ration-portal.example");

        RationCardApplication app = new RationCardApplication();
        app.setApplicationNumber("APP-TEST-001");
        app.setApplicantName("Test Citizen");
        app.setEmail("citizen@example.com");
        app.setCardType("PHH");
        app.setAddress("Test address");
        app.setDistrict("Warangal");
        app.setState("Telangana");
        app.setMobile("9876543210");
        app.setPincode("506001");
        app.setStatus("APPROVED");
        app.setFamilyMembersData(
                "[{\"name\":\"Test Citizen\",\"age\":35,\"gender\":\"Other\",\"relationship\":\"Head\"}]");

        AtomicReference<RationCard> storedCard = new AtomicReference<>();
        AtomicInteger lookups = new AtomicInteger();

        when(cards.findByApplicationNumber("APP-TEST-001"))
                .thenAnswer(invocation -> {
                    if (lookups.getAndIncrement() == 0) {
                        return Optional.empty();
                    }
                    return Optional.ofNullable(storedCard.get());
                });

        when(cards.findByRationCardNumber("RC-APP-TEST-001"))
                .thenReturn(Optional.empty());

        when(cards.saveAndFlush(any(RationCard.class)))
                .thenAnswer(invocation -> {
                    RationCard card = java.util.Objects.requireNonNull(invocation.getArgument(0, RationCard.class), "Expected saved ration card");
                    ReflectionTestUtils.setField(card, "id", 77L);
                    storedCard.set(card);
                    return card;
                });

        FamilyMember existingMember = new FamilyMember();
        existingMember.setName("Test Citizen");
        existingMember.setAge(35);
        existingMember.setGender("Other");
        existingMember.setRelationship("Head");
        existingMember.setRiceQuotaKg(new BigDecimal("6"));

        when(members.findByRationCardId(77L))
                .thenReturn(List.of())
                .thenReturn(List.of(existingMember))
                .thenReturn(List.of(existingMember));

        doThrow(new IllegalStateException("Simulated email outage"))
                .doNothing()
                .when(notifications)
                .sendEmailWithAttachment(
                        anyString(), anyString(), anyString(), any(byte[].class), anyString());

        service.processApplicationStatus(app);

        RationCard card = storedCard.get();
        assertNotNull(card);
        assertEquals("ACTIVE", card.getStatus());
        assertEquals("COMPLETED", app.getCompletionStatus());
        assertEquals(card.getRationCardNumber(), app.getIssuedRationCardNumber());
        assertNull(card.getDocumentEmailedAt(),
                "Failed email delivery must not be recorded as successful.");

        service.processApplicationStatus(app);

        assertSame(card, storedCard.get(),
                "A repeated approval should reuse the existing ration card.");
        assertEquals("ACTIVE", card.getStatus());
        assertEquals("COMPLETED", app.getCompletionStatus());
        assertNotNull(card.getDocumentEmailedAt(),
                "Successful retry should record the email delivery timestamp.");

        verify(cards, times(2)).saveAndFlush(any(RationCard.class));
        verify(members, times(1)).saveAll(any());
        verify(notifications, times(2)).sendEmailWithAttachment(
                anyString(), anyString(), anyString(), any(byte[].class), anyString());
    }

    @Test
    void pdfDownloadProducesValidPdfForOwnerAndRejectsAnotherCitizen() {
        RationCardRepository cards = mock(RationCardRepository.class);
        FamilyMemberRepository members = mock(FamilyMemberRepository.class);
        NotificationService notifications = mock(NotificationService.class);

        RationCardIssuanceService service = new RationCardIssuanceService(
                cards, members, new ObjectMapper(), notifications,
                new BigDecimal("6"), "https://ration-portal.example");

        RationCard card = new RationCard();
        ReflectionTestUtils.setField(card, "id", 88L);
        card.setRationCardNumber("123456789012");
        card.setCardType("PHH");
        card.setHeadOfFamily("Test Citizen");
        card.setAddress("Test address");
        card.setDistrict("Warangal");
        card.setState("Telangana");
        card.setOwnerEmail("citizen@example.com");
        card.setMobile("9876543210");
        card.setPincode("506001");
        card.setStatus("ACTIVE");
        card.setApprovalStatus("ACTIVE");

        when(cards.findByRationCardNumber("123456789012"))
                .thenReturn(Optional.of(card));
        when(members.findByRationCardId(88L)).thenReturn(List.of());

        byte[] pdf = service.downloadPdf(
                "123456789012", "citizen@example.com", false);

        assertNotNull(pdf);
        assertTrue(pdf.length > 100, "Generated PDF should not be empty.");
        assertEquals("%PDF-", new String(pdf, 0, 5, StandardCharsets.US_ASCII));

        assertThrows(SecurityException.class,
                () -> service.downloadPdf(
                        "123456789012", "other@example.com", false),
                "A different citizen must not download this card.");

        assertThrows(IllegalArgumentException.class,
                () -> service.downloadPdf(
                        "999999999999", "citizen@example.com", false),
                "An unknown ration card must not produce a PDF.");
    }
}