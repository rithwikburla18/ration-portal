package com.rationportal.controller;

import com.rationportal.model.*;
import com.rationportal.repository.*;
import com.rationportal.service.FpsDistributionService;
import com.rationportal.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/fps-distribution")
public class FpsDistributionController {
    private static final ZoneId IST=ZoneId.of("Asia/Kolkata");
    private static final SecureRandom RANDOM=new SecureRandom();
    private final RationCardRepository cards;
    private final FamilyMemberRepository members;
    private final DistributionOtpRepository otps;
    private final FpsDealerApplicationRepository dealers;
    private final DistributionLogRepository logs;
    private final PasswordEncoder encoder;
    private final NotificationService notifications;
    private final FpsDistributionService distribution;
    private final JdbcTemplate jdbc;
    private final int cooldownSeconds;
    private final java.math.BigDecimal riceKg;

    public FpsDistributionController(RationCardRepository cards,FamilyMemberRepository members,
            DistributionOtpRepository otps,FpsDealerApplicationRepository dealers,DistributionLogRepository logs,
            PasswordEncoder encoder,NotificationService notifications,FpsDistributionService distribution,
            JdbcTemplate jdbc,@Value("${ration.distribution.otp-cooldown-seconds:60}") int cooldownSeconds,
            @Value("${ration.entitlement.kg-per-member:6}") java.math.BigDecimal riceKg) {
        this.cards=cards;this.members=members;this.otps=otps;this.dealers=dealers;this.logs=logs;
        this.encoder=encoder;this.notifications=notifications;this.distribution=distribution;this.jdbc=jdbc;
        this.cooldownSeconds=cooldownSeconds;this.riceKg=riceKg;
    }

    @PostMapping("/otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String,String> body,Authentication auth) {
        String number=value(body,"rationCardNumber");if(number.isBlank())return ResponseEntity.badRequest().body(Map.of("message","Enter the ration card number."));
        RationCard card=cards.findByRationCardNumber(number).orElse(null);
        if(card==null||!"ACTIVE".equalsIgnoreCase(card.getStatus()))
            return ResponseEntity.status(404).body(Map.of("message","This ration number does not exist. Please go and apply for a new ration card."));
        FpsDealerApplication dealer=approvedDealer(auth);if(dealer==null)return ResponseEntity.status(403).body(Map.of("message","Approved ration-shop dealer access is required."));
        if(card.getOwnerEmail()==null||card.getOwnerEmail().isBlank())return ResponseEntity.status(409).body(Map.of("message","No verified email is registered on this ration card. Please contact the administrator."));
        LocalDateTime now=LocalDateTime.now(IST);
        Optional<DistributionOtp> latest=otps.findTopByRationCardNumberAndDealerEmailOrderByCreatedAtDesc(number,dealer.getEmail());
        if(latest.isPresent()&&latest.get().getCreatedAt()!=null&&latest.get().getCreatedAt().plusSeconds(cooldownSeconds).isAfter(now))
            return ResponseEntity.status(429).body(Map.of("message","Please wait before requesting another OTP."));
        String otp=String.format(Locale.ROOT,"%06d",RANDOM.nextInt(1_000_000));
        DistributionOtp record=new DistributionOtp();record.setRationCardNumber(number);record.setDealerEmail(dealer.getEmail());
        record.setOtpHash(encoder.encode(otp));record.setCreatedAt(now);record.setExpiresAt(now.plusMinutes(5));record.setAttempts(0);record.setUsed(false);
        otps.save(record);
        try {
            notifications.sendEmail(card.getOwnerEmail(),"Ration Portal - Rice Distribution Verification",
                "<p>A ration-shop dealer has requested verification for your ration card ending "+escape(number.substring(Math.max(0,number.length()-4)))+".</p><p>Your one-time verification code is <b>"+otp+"</b>. It expires in 5 minutes and can be used once. Do not share it with anyone except to confirm this distribution.</p>");
        } catch(Exception ex) {
            record.setUsed(true);otps.save(record);
            return ResponseEntity.status(503).body(Map.of("message","The verification email could not be sent right now. Please try again shortly."));
        }
        try{jdbc.update("INSERT INTO audit_logs(event_type,actor_email,target_reference,outcome,details) VALUES (?,?,?,?,?)","DISTRIBUTION_OTP_REQUEST",dealer.getEmail(),mask(number),"SUCCESS","OTP request recorded; code intentionally excluded.");}catch(Exception ignored){}
        return ResponseEntity.ok(Map.of("message","A verification OTP was sent to the email registered on this ration card.","maskedEmail",maskEmail(card.getOwnerEmail()),"expiresInSeconds",300));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String,String> body,Authentication auth) {
        String number=value(body,"rationCardNumber"), code=value(body,"otp");
        FpsDealerApplication dealer=approvedDealer(auth);if(dealer==null)return ResponseEntity.status(403).body(Map.of("message","Approved ration-shop dealer access is required."));
        DistributionOtp otp=otps.findTopByRationCardNumberAndDealerEmailOrderByCreatedAtDesc(number,dealer.getEmail()).orElse(null);
        LocalDateTime now=LocalDateTime.now(IST);
        if(otp==null||Boolean.TRUE.equals(otp.getUsed())||otp.getExpiresAt()==null||otp.getExpiresAt().isBefore(now))
            return ResponseEntity.badRequest().body(Map.of("message","This OTP has expired or has already been used. Request a new OTP."));
        if(otp.getAttempts()==null||otp.getAttempts()>=3){otp.setUsed(true);otps.save(otp);return ResponseEntity.status(429).body(Map.of("message","Maximum OTP attempts reached. Request a new OTP."));}
        if(code.length()!=6||!code.chars().allMatch(Character::isDigit)||!encoder.matches(code,otp.getOtpHash())){
            otp.setAttempts(otp.getAttempts()+1);if(otp.getAttempts()>=3)otp.setUsed(true);otps.save(otp);
            try{jdbc.update("INSERT INTO audit_logs(event_type,actor_email,target_reference,outcome,details) VALUES (?,?,?,?,?)","DISTRIBUTION_OTP_VERIFY",dealer.getEmail(),mask(number),"FAILED","Invalid OTP attempt; code intentionally excluded.");}catch(Exception ignored){}
            return ResponseEntity.badRequest().body(Map.of("message",otp.getAttempts()>=3?"Maximum OTP attempts reached. Request a new OTP.":"The OTP is incorrect. Please check the email and try again.","attemptsRemaining",Math.max(0,3-otp.getAttempts())));
        }
        RationCard card=cards.findByRationCardNumber(number).orElse(null);
        if(card==null||!"ACTIVE".equalsIgnoreCase(card.getStatus()))return ResponseEntity.status(404).body(Map.of("message","This ration number does not exist or is not active."));
        List<FamilyMember> family=members.findByRationCardId(card.getId());
        if(family.isEmpty())return ResponseEntity.status(409).body(Map.of("message","No registered family members were found for this ration card."));
        String verificationToken=UUID.randomUUID().toString()+UUID.randomUUID();
        otp.setUsed(true);otp.setVerifiedAt(now);otp.setVerificationTokenHash(sha256(verificationToken));otp.setVerificationExpiresAt(now.plusMinutes(5));otps.save(otp);
        try{jdbc.update("INSERT INTO audit_logs(event_type,actor_email,target_reference,outcome,details) VALUES (?,?,?,?,?)","DISTRIBUTION_OTP_VERIFY",dealer.getEmail(),mask(number),"SUCCESS","OTP verified; code intentionally excluded.");}catch(Exception ignored){}
        List<Map<String,Object>> items=new ArrayList<>();
        for(FamilyMember m:family){Map<String,Object> row=new LinkedHashMap<>();row.put("name",m.getName());row.put("age",m.getAge());row.put("gender",m.getGender());row.put("relationship",m.getRelationship());row.put("riceKg",riceKg);items.add(row);}
        return ResponseEntity.ok(Map.of("message","Identity verified. Review the family quantity before submitting.","verificationToken",verificationToken,"rationCardNumber",number,"cardType",card.getCardType(),"headOfFamily",card.getHeadOfFamily(),"familyMembers",items,"familyMemberCount",family.size(),"riceKgPerMember",riceKg,"totalRiceKg",distribution.calculateFamilyQuota(family.size()),"submitNotice","THIS MONTH'S DISTRIBUTION WILL BE COMPLETED ON CLICKING SUBMIT"));
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody Map<String,String> body,Authentication auth) {
        String number=value(body,"rationCardNumber"), token=value(body,"verificationToken");
        FpsDealerApplication dealer=approvedDealer(auth);if(dealer==null)return ResponseEntity.status(403).body(Map.of("message","Approved ration-shop dealer access is required."));
        if(number.isBlank()||token.isBlank())return ResponseEntity.badRequest().body(Map.of("message","Verified ration card and OTP verification token are required."));
        try {
            DistributionLog log=distribution.submit(number,token,dealer.getEmail(),dealer);
            RationCard card=cards.findByRationCardNumber(number).orElseThrow();
            String date=log.getDistributionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String time=log.getDistributionTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            try{notifications.sendEmail(card.getOwnerEmail(),"Ration Portal - Monthly Ration Collected","<p>Your ration has been successfully distributed.</p><p><b>Date:</b> "+date+"<br><b>Time:</b> "+time+" IST<br><b>Quantity:</b> "+log.getQuantityKg()+" kg<br><b>FPS:</b> "+escape(dealer.getShopName())+" ("+escape(dealer.getFpsId())+")<br><b>Receipt ID:</b> "+log.getReceiptId()+"</p>");}
            catch(Exception ex){System.err.println("DISTRIBUTION_SUCCESS_EMAIL_FAILED receipt="+log.getReceiptId()+" errorType="+ex.getClass().getSimpleName());}
            Map<String,Object> result=new LinkedHashMap<>();result.put("message","Rice distribution completed successfully.");result.put("rationCardNumber",number);result.put("date",date);result.put("time",time+" IST");result.put("quantityKg",log.getQuantityKg());result.put("fpsId",log.getFpsId());result.put("shopName",dealer.getShopName());result.put("receiptId",log.getReceiptId());result.put("transactionId",log.getTransactionId());
            return ResponseEntity.ok(result);
        } catch(FpsDistributionService.AlreadyDistributedException ex){return ResponseEntity.status(409).body(Map.of("message",ex.getMessage()));}
        catch(DataIntegrityViolationException ex){return ResponseEntity.status(409).body(Map.of("message","This ration card has already received its monthly distribution."));}
        catch(IllegalArgumentException ex){return ResponseEntity.badRequest().body(Map.of("message",ex.getMessage()));}
        catch(IllegalStateException ex){return ResponseEntity.status(409).body(Map.of("message",ex.getMessage()));}
    }

    @GetMapping("/records")
    public ResponseEntity<?> records(@RequestParam(required=false) String search,@RequestParam(required=false) String month,
            @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size,Authentication auth) {
        FpsDealerApplication dealer=approvedDealer(auth);if(dealer==null)return ResponseEntity.status(403).body(Map.of("message","Approved ration-shop dealer access is required."));
        if(page<0||size<1||size>50)return ResponseEntity.badRequest().body(Map.of("message","Page must be zero or greater and page size must be between 1 and 50."));
        if(search!=null&&search.length()>50)return ResponseEntity.badRequest().body(Map.of("message","Search must be 50 characters or fewer."));
        if(month!=null&&!month.isBlank()&&!month.matches("^\\d{4}-\\d{2}$"))return ResponseEntity.badRequest().body(Map.of("message","Month must use YYYY-MM format."));
        var data=logs.searchForDealer(dealer.getEmail(),month==null||month.isBlank()?null:month,search==null||search.isBlank()?null:search.trim(),org.springframework.data.domain.PageRequest.of(page,size));
        List<Map<String,Object>> items=new ArrayList<>();
        for(DistributionLog log:data.getContent()){
            Map<String,Object> row=new LinkedHashMap<>();row.put("rationCardNumber",log.getRationCard()==null?"—":log.getRationCard().getRationCardNumber());
            row.put("date",log.getDistributionDate());row.put("time",log.getDistributionTime());row.put("quantityKg",log.getQuantityKg());
            row.put("receiptId",log.getReceiptId());row.put("transactionId",log.getTransactionId());row.put("fpsId",log.getFpsId());items.add(row);
        }
        return ResponseEntity.ok(Map.of("records",items,"page",data.getNumber(),"size",data.getSize(),"totalElements",data.getTotalElements(),"totalPages",data.getTotalPages()));
    }

    private FpsDealerApplication approvedDealer(Authentication auth){
        if(auth==null||!auth.isAuthenticated()||auth.getName()==null)return null;
        return dealers.findByEmailIgnoreCase(auth.getName()).filter(d->"APPROVED".equalsIgnoreCase(d.getStatus())).orElse(null);
    }
    private String value(Map<String,String> body,String key){String v=body.get(key);return v==null?"":v.trim();}
    private String maskEmail(String email){int at=email.indexOf('@');if(at<2)return "***"+(at>=0?email.substring(at):"");return email.substring(0,1)+"*****"+email.substring(at-1);}
    private String mask(String number){return number==null||number.length()<4?"****":"********"+number.substring(number.length()-4);}
    private String escape(String s){return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");}
    private String sha256(String value){try{byte[] bytes=java.security.MessageDigest.getInstance("SHA-256").digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));StringBuilder out=new StringBuilder();for(byte b:bytes)out.append(String.format("%02x",b));return out.toString();}catch(Exception ex){throw new IllegalStateException("Secure token hashing is unavailable.",ex);}}
}
