package com.rationportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fps_dealer_applications",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_fps_dealer_email", columnNames = "email"),
           @UniqueConstraint(name = "uk_fps_license_number", columnNames = "license_number")
       })
public class FpsDealerApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="fps_id", unique=true, length=40)
    private String fpsId;
    @Column(name="dealer_name", nullable=false, length=120)
    private String dealerName;
    @Column(name="shop_name", nullable=false, length=150)
    private String shopName;
    @Column(name="license_number", nullable=false, unique=true, length=80)
    private String licenseNumber;
    @Column(nullable=false, unique=true, length=150)
    private String email;
    @Column(nullable=false, length=15)
    private String mobile;
    @Column(nullable=false, length=100)
    private String district;
    @Column(name="mandal_village", nullable=false, length=150)
    private String mandalVillage;
    @Column(name="shop_address", nullable=false, columnDefinition="TEXT")
    private String shopAddress;
    @Column(nullable=false)
    private Double latitude;
    @Column(nullable=false)
    private Double longitude;
    @Column(nullable=false, length=30)
    private String status = "PENDING";
    @Column(name="rejection_reason", columnDefinition="TEXT")
    private String rejectionReason;
    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;
    @Column(name="decided_at")
    private LocalDateTime decidedAt;

    @PrePersist protected void onCreate(){ if(createdAt==null)createdAt=LocalDateTime.now(); if(status==null||status.isBlank())status="PENDING"; }
    public Long getId(){return id;}
    public String getFpsId(){return fpsId;} public void setFpsId(String v){fpsId=v;}
    public String getDealerName(){return dealerName;} public void setDealerName(String v){dealerName=v;}
    public String getShopName(){return shopName;} public void setShopName(String v){shopName=v;}
    public String getLicenseNumber(){return licenseNumber;} public void setLicenseNumber(String v){licenseNumber=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
    public String getDistrict(){return district;} public void setDistrict(String v){district=v;}
    public String getMandalVillage(){return mandalVillage;} public void setMandalVillage(String v){mandalVillage=v;}
    public String getShopAddress(){return shopAddress;} public void setShopAddress(String v){shopAddress=v;}
    public Double getLatitude(){return latitude;} public void setLatitude(Double v){latitude=v;}
    public Double getLongitude(){return longitude;} public void setLongitude(Double v){longitude=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getRejectionReason(){return rejectionReason;} public void setRejectionReason(String v){rejectionReason=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getDecidedAt(){return decidedAt;} public void setDecidedAt(LocalDateTime v){decidedAt=v;}
}
