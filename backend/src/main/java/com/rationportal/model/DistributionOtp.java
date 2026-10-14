package com.rationportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="distribution_otps", indexes={
    @Index(name="ix_distribution_otp_lookup", columnList="ration_card_number,dealer_email,created_at")
})
public class DistributionOtp {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="ration_card_number",nullable=false,length=20) private String rationCardNumber;
    @Column(name="dealer_email",nullable=false,length=150) private String dealerEmail;
    @Column(name="otp_hash",nullable=false,length=100) private String otpHash;
    @Column(name="created_at",nullable=false) private LocalDateTime createdAt;
    @Column(name="expires_at",nullable=false) private LocalDateTime expiresAt;
    @Column(nullable=false) private Integer attempts=0;
    @Column(nullable=false) private Boolean used=false;
    @Column(name="verified_at") private LocalDateTime verifiedAt;
    @Column(name="verification_token_hash",length=64) private String verificationTokenHash;
    @Column(name="verification_expires_at") private LocalDateTime verificationExpiresAt;
    @Column(name="submitted_at") private LocalDateTime submittedAt;
    @PrePersist protected void onCreate(){if(createdAt==null)createdAt=LocalDateTime.now();if(attempts==null)attempts=0;if(used==null)used=false;}
    public Long getId(){return id;}
    public String getRationCardNumber(){return rationCardNumber;} public void setRationCardNumber(String v){rationCardNumber=v;}
    public String getDealerEmail(){return dealerEmail;} public void setDealerEmail(String v){dealerEmail=v;}
    public String getOtpHash(){return otpHash;} public void setOtpHash(String v){otpHash=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime v){expiresAt=v;}
    public Integer getAttempts(){return attempts;} public void setAttempts(Integer v){attempts=v;}
    public Boolean getUsed(){return used;} public void setUsed(Boolean v){used=v;}
    public LocalDateTime getVerifiedAt(){return verifiedAt;} public void setVerifiedAt(LocalDateTime v){verifiedAt=v;}
    public String getVerificationTokenHash(){return verificationTokenHash;} public void setVerificationTokenHash(String v){verificationTokenHash=v;}
    public LocalDateTime getVerificationExpiresAt(){return verificationExpiresAt;} public void setVerificationExpiresAt(LocalDateTime v){verificationExpiresAt=v;}
    public LocalDateTime getSubmittedAt(){return submittedAt;} public void setSubmittedAt(LocalDateTime v){submittedAt=v;}
}
