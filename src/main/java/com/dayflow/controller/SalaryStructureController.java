package com.dayflow.controller;

import com.dayflow.model.SalaryStructure;
import com.dayflow.service.SalaryStructureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary-structures")
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    public SalaryStructureController(SalaryStructureService salaryStructureService) {
        this.salaryStructureService = salaryStructureService;
    }

    // ----------------------------------------------------------------
    // LIST
    // ----------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<SalaryStructure>> getAll() {
        return ResponseEntity.ok(salaryStructureService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SalaryStructure>> getAllActive() {
        return ResponseEntity.ok(salaryStructureService.findAllActive());
    }

    // ----------------------------------------------------------------
    // GET by ID
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<SalaryStructure> getById(@PathVariable Long id) {
        return salaryStructureService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    @PostMapping
    public ResponseEntity<SalaryStructure> create(@Valid @RequestBody SalaryStructure salaryStructure) {
        SalaryStructure saved = salaryStructureService.save(salaryStructure);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
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
    // DELETE
    // ----------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (salaryStructureService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        salaryStructureService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
