package com.rationportal.controller;

import com.rationportal.service.RationCardImportService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class RationCardImportController {
    private final RationCardImportService service;

    public RationCardImportController(RationCardImportService service) {
        this.service = service;
    }

    @PostMapping("/ration-cards")
    public ResponseEntity<?> importRationCards(@RequestParam("file") MultipartFile file) {
        try {
            int count = service.importCsv(file);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "imported", count,
                    "message", count + " ration card record(s) imported."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", ex.getMessage() == null ? "CSV validation failed." : ex.getMessage()));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "error",
                    "message", "At least one record conflicts with existing data. No rows were imported."));
        } catch (Exception ex) {
            System.err.println("RATION_CARD_CSV_IMPORT_FAILED errorType=" + ex.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "CSV import failed. No records were imported; review the file and server logs."));
        }
    }
}
