package com.rationportal.repository;

import com.rationportal.model.DistributionOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DistributionOtpRepository extends JpaRepository<DistributionOtp,Long> {
    Optional<DistributionOtp> findTopByRationCardNumberAndDealerEmailOrderByCreatedAtDesc(String rationCardNumber,String dealerEmail);
    Optional<DistributionOtp> findByVerificationTokenHashAndDealerEmailAndRationCardNumber(String tokenHash,String dealerEmail,String rationCardNumber);
}
