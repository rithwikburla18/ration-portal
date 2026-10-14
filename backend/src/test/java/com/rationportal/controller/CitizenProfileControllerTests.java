package com.rationportal.controller;

import com.rationportal.model.FamilyMember;
import com.rationportal.model.RationCard;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.RationCardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CitizenProfileControllerTests {
    private final RationCardRepository cards = mock(RationCardRepository.class);
    private final FamilyMemberRepository members = mock(FamilyMemberRepository.class);
    private final CitizenProfileController controller = new CitizenProfileController(cards, members);

    @Test
    void profileRequiresAuthenticatedPrincipal() {
        ResponseEntity<?> response = controller.profile(null);
        assertEquals(401, response.getStatusCode().value());
        verifyNoInteractions(cards, members);
    }

    @Test
    void profileReturnsOnlyAuthenticatedOwnersApprovedCardAndFamily() {
        RationCard card = new RationCard();
        ReflectionTestUtils.setField(card, "id", 99L);
        card.setRationCardNumber("535976550000");
        card.setCardType("PHH");
        card.setHeadOfFamily("Example Citizen");
        card.setOwnerEmail("citizen@example.com");
        card.setStatus("ACTIVE");
        card.setApprovalStatus("APPROVED");
        when(cards.findByOwnerEmailIgnoreCaseOrderByCreatedAtDesc("citizen@example.com"))
                .thenReturn(List.of(card));
        FamilyMember adult = new FamilyMember();
        adult.setMemberId("M-1");
        adult.setName("Example Citizen");
        adult.setRelationship("Head");
        adult.setAge(35);
        adult.setGender("Female");
        adult.setRiceQuotaKg(new BigDecimal("6.00"));
        when(members.findByRationCardId(card.getId())).thenReturn(List.of(adult));

        ResponseEntity<?> response = controller.profile(citizen(" CITIZEN@EXAMPLE.COM "));

        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        @SuppressWarnings("unchecked")
        Map<String, Object> cardData = (Map<String, Object>) body.get("rationCard");
        assertEquals("535976550000", cardData.get("number"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) body.get("familyMembers");
        assertEquals(1, rows.size());
        assertEquals("Example Citizen", rows.get(0).get("name"));
        verify(cards).findByOwnerEmailIgnoreCaseOrderByCreatedAtDesc("citizen@example.com");
        verify(members).findByRationCardId(card.getId());
    }

    @Test
    void profileReturnsClearEmptyStateWhenNoIssuedCardExists() {
        when(cards.findByOwnerEmailIgnoreCaseOrderByCreatedAtDesc("citizen@example.com"))
                .thenReturn(List.of());
        ResponseEntity<?> response = controller.profile(citizen("citizen@example.com"));
        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertNull(body.get("rationCard"));
        assertEquals(List.of(), body.get("familyMembers"));
        verifyNoInteractions(members);
    }

    private UsernamePasswordAuthenticationToken citizen(String email) {
        return new UsernamePasswordAuthenticationToken(email, "test",
                List.of(new SimpleGrantedAuthority("ROLE_CITIZEN")));
    }
}