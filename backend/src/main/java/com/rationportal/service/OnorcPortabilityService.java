package com.rationportal.service;

import com.rationportal.model.OnorcPortability;
import com.rationportal.model.RationCard;
import com.rationportal.repository.OnorcPortabilityRepository;
import com.rationportal.repository.RationCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OnorcPortabilityService {

    private final OnorcPortabilityRepository repository;
    private final RationCardRepository rationCardRepository;

    public OnorcPortabilityService(
        OnorcPortabilityRepository repository,
        RationCardRepository rationCardRepository
    ) {
        this.repository = repository;
        this.rationCardRepository = rationCardRepository;
    }

    public OnorcPortability create(OnorcPortability request, String email, boolean admin) {
        String cardNumber = request.getRationCardNumber();

        if (cardNumber == null || cardNumber.isBlank()) {
            throw new RuntimeException("Ration card number is required");
        }

        RationCard card;

        if (admin) {
            card = rationCardRepository.findByRationCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Ration card not found"));
        } else {
            card = rationCardRepository.findByRationCardNumberAndOwnerEmailIgnoreCase(cardNumber, email)
                .orElseThrow(() -> new RuntimeException("Ration card not found or not owned by this account"));
        }

        if (!isApproved(card)) {
            throw new RuntimeException("Ration card is not approved");
        }

        request.setHomeState(card.getState());
        request.setHomeDistrict(card.getDistrict());
        request.setRequestedBy(email);
        request.setStatus("REQUESTED");

        if (request.getReferenceNumber() == null || request.getReferenceNumber().isBlank()) {
            request.setReferenceNumber("ONORC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        }

        return repository.save(request);
    }

    public OnorcPortability getByReference(String reference, String email, boolean admin) {
        OnorcPortability request = repository.findByReferenceNumber(reference)
            .orElseThrow(() -> new RuntimeException("ONORC request not found"));

        if (!admin && !email.equalsIgnoreCase(request.getRequestedBy())) {
            throw new RuntimeException("You are not authorized to view this ONORC request");
        }

        return request;
    }

    public List<OnorcPortability> getMyRequests(String email) {
        return repository.findByRequestedByOrderByCreatedAtDesc(email);
    }

    private boolean isApproved(RationCard card) {
        String status = card.getStatus();
        return status != null &&
            ("APPROVED".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status));
    }
}