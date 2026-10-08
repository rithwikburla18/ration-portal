package com.rationportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ration_cards")
public class RationCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ration_card_number", unique = true, nullable = false)
    private String rationCardNumber;
    @Column(name = "card_type", nullable = false)
    private String cardType;
    @Column(name = "head_of_family")
    private String headOfFamily;
    private String address;
    private String district;
    private String state;
    @Column(name = "owner_email")
    private String ownerEmail;
    @Column(nullable = false)
    private String status;
    @Column(name = "approval_status")
    private String approvalStatus;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null || status.isBlank()) status = "PENDING";
        if (approvalStatus == null || approvalStatus.isBlank()) approvalStatus = status;
    }

    public Long getId() { return id; }
    public String getRationCardNumber() { return rationCardNumber; }
    public void setRationCardNumber(String value) { this.rationCardNumber = value; }
    public String getCardType() { return cardType; }
    public void setCardType(String value) { this.cardType = value; }
    public String getHeadOfFamily() { return headOfFamily; }
    public void setHeadOfFamily(String value) { this.headOfFamily = value; }
    public String getAddress() { return address; }
    public void setAddress(String value) { this.address = value; }
    public String getDistrict() { return district; }
    public void setDistrict(String value) { this.district = value; }
    public String getState() { return state; }
    public void setState(String value) { this.state = value; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String value) { this.ownerEmail = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String value) { this.approvalStatus = value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime value) { this.createdAt = value; }
}