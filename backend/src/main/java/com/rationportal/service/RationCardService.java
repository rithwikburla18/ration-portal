package com.rationportal.service;

import com.rationportal.model.RationCard;
import com.rationportal.repository.RationCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RationCardService {

    private final RationCardRepository repository;

    public RationCardService(RationCardRepository repository) {
        this.repository = repository;
    }

    public List<RationCard> getAll() {
        return repository.findAll();
    }

    public List<RationCard> getPending() {
        return repository.findByStatusIgnoreCase("PENDING");
    }

    public RationCard getByNumber(String number) {
        return repository.findByRationCardNumber(number)
            .orElseThrow(() -> new RuntimeException("Ration card not found"));
    }

    public List<RationCard> getApprovedCardsForOwner(String email) {
        return repository.findByOwnerEmailIgnoreCase(email)
            .stream()
            .filter(this::isApproved)
            .toList();
    }

    public RationCard getApprovedCardForOwner(String email) {
        return getApprovedCardsForOwner(email)
            .stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No approved ration card found for this account"));
    }

    public RationCard getApprovedCardForOwner(String number, String email) {
        RationCard card = repository.findByRationCardNumberAndOwnerEmailIgnoreCase(number, email)
            .orElseThrow(() -> new RuntimeException("Ration card not found or not owned by this account"));

        if (!isApproved(card)) {
            throw new RuntimeException("Ration card is not approved");
        }

        return card;
    }

    public boolean isApproved(RationCard card) {
        String status = card.getStatus();
        return status != null &&
            ("APPROVED".equalsIgnoreCase(status) || "ACTIVE".equalsIgnoreCase(status));
    }

    public RationCard save(RationCard card) {
        if (card.getStatus() == null || card.getStatus().isBlank()) {
            card.setStatus("PENDING");
        }
        return repository.save(card);
    }

    public RationCard approve(String number) {
        RationCard card = getByNumber(number);
        card.setStatus("APPROVED");
        return repository.save(card);
    }

    public RationCard reject(String number) {
        RationCard card = getByNumber(number);
        card.setStatus("REJECTED");
        return repository.save(card);
    }
}