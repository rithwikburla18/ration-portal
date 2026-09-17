package com.rationportal.controller;

import com.rationportal.model.RationCardApplication;
import com.rationportal.repository.RationCardApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications/ration-card")
public class RationCardApplicationController {

    private final RationCardApplicationRepository repository;

    public RationCardApplicationController(RationCardApplicationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public RationCardApplication submitApplication(@RequestBody @NonNull RationCardApplication application) {
        return repository.save(application);
    }

    @GetMapping
    public List<RationCardApplication> getApplications() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RationCardApplication> getApplicationById(@PathVariable @NonNull Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
