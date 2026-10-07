package com.rationportal.repository;

import com.rationportal.model.RationCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RationCardRepository extends JpaRepository<RationCard, Long> {

    Optional<RationCard> findByRationCardNumber(String rationCardNumber);

    List<RationCard> findByStatusIgnoreCase(String status);

    Optional<RationCard> findByRationCardNumberAndOwnerEmailIgnoreCase(
        String rationCardNumber,
        String ownerEmail
    );

    List<RationCard> findByOwnerEmailIgnoreCase(String ownerEmail);
}