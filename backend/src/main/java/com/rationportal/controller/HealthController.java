package com.rationportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(
                Map.of(
                        "application", "Ration Portal",
                        "status", "UP",
                        "message", "Ration Portal Backend is running"
                )
        );
    }

    @GetMapping("/api/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(
                Map.of(
                        "status", "UP",
                        "service", "ration-portal-backend"
                )
        );
    }
}