package com.dayflow.controller;

import com.dayflow.model.SalaryStructure;
import com.dayflow.service.SalaryStructureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Salary Structure management.
 * Only HR_ADMIN can create/update/delete salary structures.
 * Authenticated users can view active structures.
 */
@RestController
@RequestMapping("/api/salary-structures")
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    public SalaryStructureController(SalaryStructureService salaryStructureService) {
        this.salaryStructureService = salaryStructureService;
    }

    // ----------------------------------------------------------------
    // LIST — HR_ADMIN only
    // ----------------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<List<SalaryStructure>> getAll() {
        return ResponseEntity.ok(salaryStructureService.findAll());
    }

    // ----------------------------------------------------------------
    // LIST active — authenticated users (employees can see their structure)
    // ----------------------------------------------------------------
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SalaryStructure>> getAllActive() {
        return ResponseEntity.ok(salaryStructureService.findAllActive());
    }

    // ----------------------------------------------------------------
    // GET by ID — authenticated users
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SalaryStructure> getById(@PathVariable Long id) {
        return salaryStructureService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // CREATE — HR_ADMIN only
    // ----------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<SalaryStructure> create(@Valid @RequestBody SalaryStructure salaryStructure) {
        SalaryStructure saved = salaryStructureService.save(salaryStructure);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ----------------------------------------------------------------
    // UPDATE — HR_ADMIN only
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<SalaryStructure> update(@PathVariable Long id,
                                                  @Valid @RequestBody SalaryStructure salaryStructure) {
        return salaryStructureService.findById(id)
                .map(existing -> {
                    salaryStructure.setId(id);
                    return ResponseEntity.ok(salaryStructureService.save(salaryStructure));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // DELETE — HR_ADMIN only
    // ----------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (salaryStructureService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        salaryStructureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
