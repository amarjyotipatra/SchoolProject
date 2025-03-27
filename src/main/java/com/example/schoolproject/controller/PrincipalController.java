package com.example.schoolproject.controller;

import com.example.schoolproject.dto.PrincipalDTO;
import com.example.schoolproject.service.PrincipalService;
import com.example.schoolproject.service.ScoreService; // Import corrected service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException; // Import for potential exception
import org.springframework.http.HttpStatus; // Import for HttpStatus

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/principals")
public class PrincipalController {

    @Autowired
    private PrincipalService principalService;

    @Autowired
    private ScoreService scoreService; // Corrected injection type

    @GetMapping("/username/{userName}")
    @PreAuthorize("hasRole('PRINCIPAL')") // Added PreAuthorize based on other controllers
    public ResponseEntity<PrincipalDTO> getPrincipalByUserName(@PathVariable String userName) {
        Optional<PrincipalDTO> principalDTO = principalService.findByUserName(userName);
        return principalDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('PRINCIPAL')") // Added PreAuthorize for consistency
    public ResponseEntity<?> createPrincipal(@RequestBody PrincipalDTO principalDTO) {
        // Password should be encoded in the service layer before saving
        try {
            PrincipalDTO savedPrincipal = principalService.savePrincipal(principalDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrincipal); // Use 201 Created
        } catch (DataIntegrityViolationException e) {
             return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
        } catch (IllegalArgumentException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Log the exception e.g., using SLF4j logger
            // log.error("Error creating principal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred during principal creation.");
        }
    }

    @GetMapping("/chart/cumulative-average")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Map<String, Object>> getCumulativeAverageChart() {
        Map<String, Object> chartData = scoreService.getCumulativeAverageChartData();
        return ResponseEntity.ok(chartData);
    }
}