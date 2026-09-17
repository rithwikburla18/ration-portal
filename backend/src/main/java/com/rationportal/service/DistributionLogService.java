package com.rationportal.service; import com.rationportal.model.DistributionLog; import com.rationportal.repository.DistributionLogRepository; import org.springframework.stereotype.Service; import org.springframework.lang.NonNull; import java.util.List; @Service public class DistributionLogService { private final DistributionLogRepository repository; public DistributionLogService(DistributionLogRepository repository){this.repository=repository;} public List<DistributionLog> getByRationCard(Long rationCardId){return repository.findByRationCardId(rationCardId);} public DistributionLog save(@NonNull DistributionLog log){return repository.save(log);}  }




