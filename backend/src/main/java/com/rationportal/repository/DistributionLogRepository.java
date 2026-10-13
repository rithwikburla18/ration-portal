package com.rationportal.repository;

import com.rationportal.model.DistributionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DistributionLogRepository extends JpaRepository<DistributionLog, Long> {
    List<DistributionLog> findByRationCardId(Long rationCardId);
    List<DistributionLog> findTop5ByRationCardIdOrderByDistributionDateDescDistributionTimeDesc(Long rationCardId);
    boolean existsByRationCardIdAndDistributionMonth(Long rationCardId,String distributionMonth);

    @Query("select sum(d.quantityKg) from DistributionLog d")
    java.math.BigDecimal sumQuantityKg();

    @Query("select count(distinct trim(d.fpsId)) from DistributionLog d where d.fpsId is not null and trim(d.fpsId) <> ''")
    long countDistinctFpsIds();
    @Query("select d from DistributionLog d join d.rationCard c where d.dealerEmail=:email and (:month is null or d.distributionMonth=:month) and (:search is null or lower(c.rationCardNumber) like lower(concat('%',:search,'%')) or lower(coalesce(d.receiptId, '')) like lower(concat('%',:search,'%')) or lower(coalesce(d.transactionId, '')) like lower(concat('%',:search,'%')) or lower(coalesce(d.fpsId, '')) like lower(concat('%',:search,'%'))) order by d.distributionDate desc, d.distributionTime desc")
    Page<DistributionLog> searchForDealer(@Param("email") String email,@Param("month") String month,@Param("search") String search,Pageable pageable);
}
