package com.rationportal.service;

import com.rationportal.model.*;
import com.rationportal.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FpsDistributionService {
    private static final ZoneId IST=ZoneId.of("Asia/Kolkata");
    private final RationCardRepository cards;
    private final FamilyMemberRepository members;
    private final DistributionLogRepository logs;
    private final DistributionOtpRepository otps;
    private final JdbcTemplate jdbc;
    private final BigDecimal riceKg;
    private final int startDay,endDay;

    public FpsDistributionService(RationCardRepository cards,FamilyMemberRepository members,
            DistributionLogRepository logs,DistributionOtpRepository otps,JdbcTemplate jdbc,
            @Value("${ration.entitlement.kg-per-member:6}") BigDecimal riceKg,
            @Value("${ration.distribution.start-day:1}") int startDay,
            @Value("${ration.distribution.end-day:15}") int endDay) {
        this.cards=cards;this.members=members;this.logs=logs;this.otps=otps;this.jdbc=jdbc;
        this.riceKg=riceKg;this.startDay=startDay;this.endDay=endDay;
    }

    @Transactional
    public DistributionLog submit(String number,String token,String dealerEmail,FpsDealerApplication dealer) {
        LocalDateTime now=LocalDateTime.now(IST);
        int day=now.getDayOfMonth();
        if(day<startDay||day>endDay)throw new IllegalStateException("Rice distribution is allowed only from the "+startDay+"st through the "+endDay+"th of each month.");
        RationCard card=cards.findByRationCardNumber(number).orElse(null);
        if(card==null||!"ACTIVE".equalsIgnoreCase(card.getStatus()))
            throw new IllegalArgumentException("This ration number does not exist or is not active. Please go and apply for a new ration card.");
        String month=YearMonth.from(now).toString();
        if(logs.existsByRationCardIdAndDistributionMonth(card.getId(),month)) {
            DistributionLog old=logs.findByRationCardId(card.getId()).stream()
                .filter(x->month.equals(x.getDistributionMonth())).findFirst().orElse(null);
            String date=old==null? "earlier this month":old.getDistributionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            throw new AlreadyDistributedException("Already distributed this month on "+date+".");
        }
        String tokenHash=sha256(token);
        DistributionOtp otp=otps.findByVerificationTokenHashAndDealerEmailAndRationCardNumber(tokenHash,dealerEmail,number).orElse(null);
        if(otp==null||otp.getSubmittedAt()!=null||otp.getVerifiedAt()==null||otp.getVerificationExpiresAt()==null||otp.getVerificationExpiresAt().isBefore(now))
            throw new IllegalArgumentException("OTP verification has expired. Request a new OTP and verify again.");
        List<FamilyMember> family=members.findByRationCardId(card.getId());
        if(family.isEmpty())throw new IllegalStateException("No registered family members were found for this ration card.");
        BigDecimal quantity=riceKg.multiply(BigDecimal.valueOf(family.size()));
        String transaction="RCP-"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase(Locale.ROOT);
        DistributionLog log=new DistributionLog();
        log.setRationCard(card);log.setQuantityKg(quantity);log.setDistributionDate(now.toLocalDate());
        log.setDistributionTime(now.toLocalTime().withNano(0));log.setFpsId(dealer.getFpsId());
        log.setTransactionStatus("COMPLETED");log.setDistributionMonth(month);log.setTransactionId(transaction);
        log.setReceiptId(transaction);log.setDealerEmail(dealerEmail);
        DistributionLog saved=logs.saveAndFlush(log);
        otp.setSubmittedAt(now);otps.save(otp);
        jdbc.update("INSERT INTO audit_logs(event_type,actor_email,target_reference,outcome,details) VALUES (?,?,?,?,?)",
                "DISTRIBUTION_SUBMIT",dealerEmail,mask(number),"SUCCESS","Monthly family distribution recorded.");
        return saved;
    }

    public BigDecimal calculateFamilyQuota(int membersCount){return riceKg.multiply(BigDecimal.valueOf(Math.max(0,membersCount)));}
    private String sha256(String value){
        try{byte[] bytes=MessageDigest.getInstance("SHA-256").digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder out=new StringBuilder();for(byte b:bytes)out.append(String.format("%02x",b));return out.toString();
        }catch(Exception ex){throw new IllegalStateException("Secure token hashing is unavailable.",ex);}
    }
    private String mask(String number){if(number==null||number.length()<4)return "****";return "********"+number.substring(number.length()-4);}
    public static class AlreadyDistributedException extends RuntimeException { public AlreadyDistributedException(String message){super(message);} }
}
