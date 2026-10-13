package com.rationportal.controller;

import com.rationportal.model.DistributionLog;
import com.rationportal.model.DistributionOtp;
import com.rationportal.model.FamilyMember;
import com.rationportal.model.FpsDealerApplication;
import com.rationportal.model.RationCard;
import com.rationportal.repository.DistributionLogRepository;
import com.rationportal.repository.DistributionOtpRepository;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.FpsDealerApplicationRepository;
import com.rationportal.repository.RationCardRepository;
import com.rationportal.service.FpsDistributionService;
import com.rationportal.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FpsDistributionControllerTests {
    private final RationCardRepository cards = mock(RationCardRepository.class);
    private final FamilyMemberRepository members = mock(FamilyMemberRepository.class);
    private final DistributionOtpRepository otps = mock(DistributionOtpRepository.class);
    private final FpsDealerApplicationRepository dealers = mock(FpsDealerApplicationRepository.class);
    private final DistributionLogRepository logs = mock(DistributionLogRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final NotificationService notifications = mock(NotificationService.class);
    private final FpsDistributionService distribution = mock(FpsDistributionService.class);
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);

    private FpsDistributionController controller() {
        return new FpsDistributionController(cards, members, otps, dealers, logs, encoder,
                notifications, distribution, jdbc, 60, new BigDecimal("6"));
    }

    @Test
    void pendingDealerCannotProbeRationCardExistenceWhenRequestingOtp() {
        var auth = dealerAuthentication("pending@example.com");
        when(dealers.findByEmailIgnoreCase("pending@example.com")).thenReturn(Optional.empty());

        var response = controller().requestOtp(Map.of("rationCardNumber", "123456789012"), auth);

        assertEquals(403, response.getStatusCode().value());
        verify(cards, never()).findByRationCardNumber(anyString());
    }

    @Test
    void thirdIncorrectOtpAttemptLocksCodeAndReturnsTooManyRequests() {
        String number = "123456789012";
        String email = "dealer@example.com";
        FpsDealerApplication dealer = new FpsDealerApplication();
        dealer.setEmail(email);
        dealer.setFpsId("FPS-TEST");
        dealer.setStatus("APPROVED");
        when(dealers.findByEmailIgnoreCase(email)).thenReturn(Optional.of(dealer));

        DistributionOtp otp = new DistributionOtp();
        otp.setRationCardNumber(number);
        otp.setDealerEmail(email);
        otp.setOtpHash("stored-hash");
        otp.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(4));
        otp.setAttempts(2);
        otp.setUsed(false);
        when(otps.findTopByRationCardNumberAndDealerEmailOrderByCreatedAtDesc(number, email))
                .thenReturn(Optional.of(otp));
        when(encoder.matches("123456", "stored-hash")).thenReturn(false);

        var response = controller().verifyOtp(
                Map.of("rationCardNumber", number, "otp", "123456"),
                dealerAuthentication(email));

        assertEquals(429, response.getStatusCode().value());
        assertEquals(3, otp.getAttempts());
        assertEquals(true, otp.getUsed());
        verify(otps).save(otp);
    }

    @Test
    void approvedDealerCanRequestOtpForActiveCardWithoutReceivingOtpInResponse() {
        String number = "123456789012";
        String email = "dealer@example.com";
        FpsDealerApplication dealer = new FpsDealerApplication();
        dealer.setEmail(email);
        dealer.setFpsId("FPS-TEST");
        dealer.setStatus("APPROVED");
        when(dealers.findByEmailIgnoreCase(email)).thenReturn(Optional.of(dealer));

        RationCard card = new RationCard();
        card.setRationCardNumber(number);
        card.setStatus("ACTIVE");
        card.setOwnerEmail("citizen@example.com");
        when(cards.findByRationCardNumber(number)).thenReturn(Optional.of(card));
        when(otps.findTopByRationCardNumberAndDealerEmailOrderByCreatedAtDesc(number, email))
                .thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("hashed-otp");

        var response = controller().requestOtp(
                Map.of("rationCardNumber", number),
                dealerAuthentication(email));

        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        org.junit.jupiter.api.Assertions.assertFalse(body.containsKey("otp"));
        verify(notifications).sendEmail(anyString(), anyString(), anyString());
    }

    private UsernamePasswordAuthenticationToken dealerAuthentication(String email) {
        return new UsernamePasswordAuthenticationToken(email, "test",
                List.of(new SimpleGrantedAuthority("ROLE_FPS_DEALER")));
    }
}
