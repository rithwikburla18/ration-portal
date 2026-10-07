package com.rationportal.repository;

import com.rationportal.model.RationCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RationCardRepository extends JpaRepository<RationCard, Long> {
    Optional<RationCard> findByRationCardNumber(String rationCardNumber);
    List<RationCard> findByStatusIgnoreCase(String status);
}
