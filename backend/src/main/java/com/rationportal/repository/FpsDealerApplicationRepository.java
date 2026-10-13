package com.rationportal.repository;

import com.rationportal.model.FpsDealerApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FpsDealerApplicationRepository extends JpaRepository<FpsDealerApplication, Long> {
    Optional<FpsDealerApplication> findByEmailIgnoreCase(String email);
    Optional<FpsDealerApplication> findByLicenseNumberIgnoreCase(String licenseNumber);
    List<FpsDealerApplication> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);
    List<FpsDealerApplication> findByStatusIgnoreCase(String status);
}
