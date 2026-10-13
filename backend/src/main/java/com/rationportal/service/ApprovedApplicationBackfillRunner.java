package com.rationportal.service;

import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.RationCardApplicationRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApprovedApplicationBackfillRunner implements ApplicationRunner {
    private final RationCardApplicationRepository applications;
    private final RationCardIssuanceService issuance;

    public ApprovedApplicationBackfillRunner(RationCardApplicationRepository applications,
                                              RationCardIssuanceService issuance) {
        this.applications = applications;
        this.issuance = issuance;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (RationCardApplication application : applications.findByStatusIgnoreCase("APPROVED")) {
            try {
                issuance.processApplicationStatus(application);
            } catch (Exception ex) {
                // Continue repairing other records without exposing citizen data in logs.
                System.err.println("APPROVED_APPLICATION_BACKFILL_FAILED application="
                        + application.getApplicationNumber()
                        + " errorType=" + ex.getClass().getSimpleName());
            }
        }
    }
}
