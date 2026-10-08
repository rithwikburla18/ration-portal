package com.rationportal.service;

import com.rationportal.model.RationCard;
import com.rationportal.repository.RationCardRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class RationCardService {
    private final RationCardRepository repository;
    public RationCardService(RationCardRepository repository) { this.repository = repository; }
    public List<RationCard> getAll() { return repository.findAll(); }
    public List<RationCard> getPending() { return repository.findByStatusIgnoreCase("PENDING"); }
    public RationCard getByNumber(String number) {
        if (number == null || number.isBlank()) return null;
        return repository.findByRationCardNumber(number.trim()).orElse(null);
    }
    public List<RationCard> getApprovedCardsForOwner(String ownerEmail) {
        if (ownerEmail == null || ownerEmail.isBlank()) return List.of();
        List<RationCard> cards = repository.findByOwnerEmailIgnoreCase(ownerEmail.trim());
        List<RationCard> approved = new ArrayList<>();
        for (RationCard card : cards) {
            if (isApproved(card)) approved.add(card);
        }
        return approved;
    }
    public RationCard getApprovedCardForOwner(String ownerEmail) {
        List<RationCard> cards = getApprovedCardsForOwner(ownerEmail);
        return cards.isEmpty() ? null : cards.get(0);
    }
    public RationCard getApprovedCardForOwner(String rationCardNumber, String ownerEmail) {
        if (rationCardNumber == null || rationCardNumber.isBlank() || ownerEmail == null || ownerEmail.isBlank()) return null;
        RationCard card = repository.findByRationCardNumberAndOwnerEmailIgnoreCase(rationCardNumber.trim(), ownerEmail.trim()).orElse(null);
        return isApproved(card) ? card : null;
    }
    public boolean isApproved(RationCard card) {
        if (card == null || card.getStatus() == null) return false;
        String status = card.getStatus().trim().toUpperCase(Locale.ROOT);
        return "APPROVED".equals(status) || "ACTIVE".equals(status);
    }
    public RationCard save(@NonNull RationCard card) {
        String status = card.getStatus();
        if (status == null || status.isBlank()) status = "PENDING";
        status = status.trim().toUpperCase(Locale.ROOT);
        if (!Set.of("PENDING", "APPROVED", "ACTIVE", "REJECTED").contains(status)) status = "PENDING";
        card.setStatus(status);
        card.setApprovalStatus(status);
        if (card.getOwnerEmail() != null) card.setOwnerEmail(card.getOwnerEmail().trim().toLowerCase(Locale.ROOT));
        return repository.save(card);
    }
    public RationCard approve(String number) {
        RationCard card = getByNumber(number);
        if (card == null) return null;
        card.setStatus("APPROVED");
        card.setApprovalStatus("APPROVED");
        return repository.save(card);
    }
    public RationCard reject(String number) {
        RationCard card = getByNumber(number);
        if (card == null) return null;
        card.setStatus("REJECTED");
        card.setApprovalStatus("REJECTED");
        return repository.save(card);
    }
}