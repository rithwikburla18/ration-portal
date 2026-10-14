package com.rationportal.service;

import com.rationportal.model.DistributionLog;
import com.rationportal.model.DistributionOtp;
import com.rationportal.model.FamilyMember;
import com.rationportal.model.FpsDealerApplication;
import com.rationportal.model.RationCard;
import com.rationportal.repository.DistributionLogRepository;
import com.rationportal.repository.DistributionOtpRepository;
import com.rationportal.repository.FamilyMemberRepository;
import com.rationportal.repository.RationCardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
class FpsDistributionServiceTests {
    @Test
    void approvedCardCanCompleteOtpVerifiedMonthlyDistribution() throws Exception {
        RationCardRepository cards = mock(RationCardRepository.class);
        FamilyMemberRepository members = mock(FamilyMemberRepository.class);
        DistributionLogRepository logs = mock(DistributionLogRepository.class);
        DistributionOtpRepository otps = mock(DistributionOtpRepository.class);
        JdbcTemplate jdbc = mock(JdbcTemplate.class);

        FpsDistributionService service = new FpsDistributionService(
                cards, members, logs, otps, jdbc, new BigDecimal("6"), 1, 31);

        String number = "123456789012";
        String token = "valid-short-lived-token";
        String dealerEmail = "dealer@example.com";
        RationCard card = new RationCard();
        card.setRationCardNumber(number);
        card.setStatus("APPROVED");
        when(cards.findByRationCardNumber(number)).thenReturn(Optional.of(card));
        String month = YearMonth.now(ZoneId.of("Asia/Kolkata")).toString();
        when(logs.existsByRationCardIdAndDistributionMonth(card.getId(), month)).thenReturn(false);

        FamilyMember member = new FamilyMember();
        member.setName("Test Member");
        member.setAge(30);
        member.setRiceQuotaKg(new BigDecimal("6"));
        when(members.findByRationCardId(card.getId())).thenReturn(List.of(member));

        String tokenHash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8)));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        DistributionOtp otp = new DistributionOtp();
        otp.setRationCardNumber(number);
        otp.setDealerEmail(dealerEmail);
        otp.setCreatedAt(now.minusMinutes(1));
        otp.setExpiresAt(now.plusMinutes(4));
        otp.setVerifiedAt(now.minusSeconds(5));
        otp.setVerificationTokenHash(tokenHash);
        otp.setVerificationExpiresAt(now.plusMinutes(4));
        otp.setUsed(true);
        when(otps.findByVerificationTokenHashAndDealerEmailAndRationCardNumber(tokenHash, dealerEmail, number))
                .thenReturn(Optional.of(otp));
        when(logs.saveAndFlush(any(DistributionLog.class))).thenAnswer(invocation -> java.util.Objects.requireNonNull(invocation.getArgument(0, DistributionLog.class), "Expected saved distribution log"));

        FpsDealerApplication dealer = new FpsDealerApplication();
        dealer.setFpsId("FPS-TEST-123");
        dealer.setEmail(dealerEmail);
        DistributionLog result = service.submit(number, token, dealerEmail, dealer);

        assertEquals("COMPLETED", result.getTransactionStatus());
        assertEquals(new BigDecimal("6"), result.getQuantityKg());
        assertEquals("FPS-TEST-123", result.getFpsId());
        assertEquals(month, result.getDistributionMonth());
    }
}