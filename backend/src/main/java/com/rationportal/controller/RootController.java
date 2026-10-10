package com.rationportal.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
@GetMapping("/")
public ResponseEntity<Map<String, Object>> root() {
Map<String, Object> response = new LinkedHashMap<>();
response.put("status", "UP");
response.put("service", "Ration Portal Backend");
response.put("message", "Backend is running. Use the /api endpoints for application services.");
return ResponseEntity.ok(response);
}
}