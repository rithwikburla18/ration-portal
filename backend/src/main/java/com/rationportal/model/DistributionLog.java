package com.rationportal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="distribution_logs", indexes={
    @Index(name="ix_distribution_dealer_month",columnList="dealer_email,distribution_month")
})
public class DistributionLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ration_card_id") private RationCard rationCard;
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="member_id") private FamilyMember member;
    @Column(name="quantity_kg",nullable=false) private BigDecimal quantityKg;
    @Column(name="distribution_date",nullable=false) private LocalDate distributionDate;
    @Column(name="distribution_time") private LocalTime distributionTime;
    @Column(name="fps_id") private String fpsId;
    @Column(name="transaction_status",nullable=false) private String transactionStatus;
    @Column(name="distribution_month",length=7) private String distributionMonth;
    @Column(name="transaction_id",unique=true,length=50) private String transactionId;
    @Column(name="receipt_id",length=50) private String receiptId;
    @Column(name="dealer_email",length=150) private String dealerEmail;
    public Long getId(){return id;}
    public RationCard getRationCard(){return rationCard;} public void setRationCard(RationCard v){rationCard=v;}
    public FamilyMember getMember(){return member;} public void setMember(FamilyMember v){member=v;}
    public BigDecimal getQuantityKg(){return quantityKg;} public void setQuantityKg(BigDecimal v){quantityKg=v;}
    public LocalDate getDistributionDate(){return distributionDate;} public void setDistributionDate(LocalDate v){distributionDate=v;}
    public LocalTime getDistributionTime(){return distributionTime;} public void setDistributionTime(LocalTime v){distributionTime=v;}
    public String getFpsId(){return fpsId;} public void setFpsId(String v){fpsId=v;}
    public String getTransactionStatus(){return transactionStatus;} public void setTransactionStatus(String v){transactionStatus=v;}
    public String getDistributionMonth(){return distributionMonth;} public void setDistributionMonth(String v){distributionMonth=v;}
    public String getTransactionId(){return transactionId;} public void setTransactionId(String v){transactionId=v;}
    public String getReceiptId(){return receiptId;} public void setReceiptId(String v){receiptId=v;}
    public String getDealerEmail(){return dealerEmail;} public void setDealerEmail(String v){dealerEmail=v;}
}
