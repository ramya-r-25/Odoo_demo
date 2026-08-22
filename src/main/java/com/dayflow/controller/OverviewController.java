package com.dayflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller serving initial menu endpoints for Dayflow HRMS.
 */
@RestController
@RequestMapping("/api")
public class OverviewController {

    @GetMapping("/overview")
    public ResponseEntity<Map<String, String>> getOverview() {
        Map<String, String> response = new HashMap<>();
        response.put("module", "Dayflow HRMS");
        response.put("status", "Active");
        response.put("message", "Welcome to Dayflow HRMS Overview");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfiguration() {
        Map<String, String> response = new HashMap<>();
        response.put("section", "Configuration");
        response.put("access", "HR / Administrator");
        return ResponseEntity.ok(response);
    }
}
