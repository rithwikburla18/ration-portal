package com.rationportal.repository;

import com.rationportal.model.RationCardApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RationCardApplicationRepository
        extends JpaRepository<RationCardApplication, Long> {

    Optional<RationCardApplication> findByApplicationNumber(
            String applicationNumber
    );

    List<RationCardApplication> findAllByEmailOrderByCreatedAtDesc(
            String email
    );
}