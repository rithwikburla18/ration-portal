package com.rationportal.service;

import com.rationportal.model.RationCard;
import com.rationportal.repository.RationCardRepository;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;
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
        return repository.findByRationCardNumber(number).orElse(null);
    }

    public RationCard save(@NonNull RationCard card) {
        if (card.getStatus() == null || card.getStatus().isBlank()) {
            card.setStatus("PENDING");
        }
        return repository.save(card);
    }

    public RationCard approve(String number) {
        RationCard card = getByNumber(number);
        if (card == null) {
            return null;
        }
        card.setStatus("APPROVED");
        return repository.save(card);
    }

    public RationCard reject(String number) {
        RationCard card = getByNumber(number);
        if (card == null) {
            return null;
        }
        card.setStatus("REJECTED");
        return repository.save(card);
    }
}
