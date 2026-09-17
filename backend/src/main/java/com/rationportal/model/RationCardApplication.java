package com.rationportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="ration_card_applications")
public class RationCardApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String applicantName;

    @Column(nullable=false)
    private Integer familyMembers;

    @Column(nullable=false)
    private String district;

    @Column(nullable=false)
    private String address;

    @Column(nullable=false)
    private String status;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "SUBMITTED";
    }

    public Long getId() { return id; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public Integer getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(Integer familyMembers) { this.familyMembers = familyMembers; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
