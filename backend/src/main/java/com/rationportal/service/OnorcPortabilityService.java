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
        if (request == null) {
            throw new IllegalArgumentException("ONORC portability request is required.");
        }
        String cardNumber = request.getRationCardNumber() == null ? "" : request.getRationCardNumber().trim();
        if (cardNumber.isBlank() || cardNumber.length() > 50) {
            throw new IllegalArgumentException("A valid ration card number is required.");
        }

        String destinationState = request.getDestinationState() == null ? "" : request.getDestinationState().trim();
        String destinationDistrict = request.getDestinationDistrict() == null ? "" : request.getDestinationDistrict().trim();
        if (destinationState.isBlank() || destinationState.length() > 100) {
            throw new IllegalArgumentException("Destination state is required and must not exceed 100 characters.");
        }
        if (destinationDistrict.isBlank() || destinationDistrict.length() > 100) {
            throw new IllegalArgumentException("Destination district is required and must not exceed 100 characters.");
        }

        RationCard card;
        if (admin) {
            card = rationCardRepository.findByRationCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("Ration card not found."));
        } else {
            card = rationCardRepository.findByRationCardNumberAndOwnerEmailIgnoreCase(cardNumber, email)
                .orElseThrow(() -> new IllegalArgumentException("Ration card not found or not owned by this account."));
        }
        if (!isApproved(card)) {
            throw new IllegalArgumentException("Ration card is not approved.");
        }

        request.setRationCardNumber(cardNumber);
        request.setHomeState(card.getState());
        request.setHomeDistrict(card.getDistrict());
        request.setDestinationState(destinationState);
        request.setDestinationDistrict(destinationDistrict);
        request.setFpsName(clean(request.getFpsName()));
        request.setFpsCode(clean(request.getFpsCode()));
        request.setRequestedBy(email.trim().toLowerCase(java.util.Locale.ROOT));
        request.setStatus("REQUESTED");
        // The reference is generated server-side; a caller cannot choose or reuse it.
        request.setReferenceNumber("ONORC-" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 16).toUpperCase(java.util.Locale.ROOT));
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

    public OnorcPortability updateStatus(String reference, String requestedStatus) {
        OnorcPortability request = repository.findByReferenceNumber(reference)
                .orElse(null);
        if (request == null) return null;

        String status = requestedStatus == null ? "" : requestedStatus.trim().toUpperCase(java.util.Locale.ROOT);
        if (!List.of("PROCESSING", "APPROVED", "REJECTED", "COMPLETED").contains(status)) {
            throw new IllegalArgumentException("Status must be PROCESSING, APPROVED, REJECTED, or COMPLETED.");
        }

        String current = request.getStatus() == null ? "" : request.getStatus().trim().toUpperCase(java.util.Locale.ROOT);
        if (current.equals(status)) return request;
        if ("REJECTED".equals(current) || "COMPLETED".equals(current)) {
            throw new IllegalStateException("A rejected or completed ONORC request cannot be changed.");
        }
        if ("REQUESTED".equals(current) && "COMPLETED".equals(status)) {
            throw new IllegalStateException("An ONORC request must be approved before it can be completed.");
        }

        request.setStatus(status);
        return repository.save(request);
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
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