package com.rationportal.controller;

import com.rationportal.model.OnorcPortability;
import com.rationportal.service.OnorcPortabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onorc")
public class OnorcController {

    private final OnorcPortabilityService service;

    public OnorcController(OnorcPortabilityService service) {
        this.service = service;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null &&
            authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @PostMapping("/portability")
    public ResponseEntity<?> create(
        @RequestBody OnorcPortability request,
        Authentication authentication
    ) {
        return ResponseEntity.ok(
            service.create(request, authentication.getName(), isAdmin(authentication))
        );
    }

    @GetMapping("/portability/{reference}")
    public ResponseEntity<?> get(
        @PathVariable String reference,
        Authentication authentication
    ) {
        return ResponseEntity.ok(
            service.getByReference(reference, authentication.getName(), isAdmin(authentication))
        );
    }

    @GetMapping("/my-requests")
    public List<OnorcPortability> myRequests(Authentication authentication) {
        return service.getMyRequests(authentication.getName());
    }
}
