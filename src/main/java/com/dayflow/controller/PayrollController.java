package com.dayflow.controller;

import com.dayflow.model.Payroll;
import com.dayflow.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    // ----------------------------------------------------------------
    // LIST
    // ----------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<Payroll>> getAll() {
        return ResponseEntity.ok(payrollService.findAll());
    }

    // ----------------------------------------------------------------
    // GET by ID
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Payroll> getById(@PathVariable Long id) {
        return payrollService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // GET by Employee
    // ----------------------------------------------------------------
    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<Payroll> getByEmployee(@PathVariable Long employeeId) {
        return payrollService.findByEmployeeId(employeeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Payroll> create(@Valid @RequestBody Payroll payroll) {
        Payroll saved = payrollService.save(payroll);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Payroll> update(@PathVariable Long id,
                                          @Valid @RequestBody Payroll payroll) {
        return payrollService.findById(id)
                .map(existing -> {
                    payroll.setId(id);
                    return ResponseEntity.ok(payrollService.save(payroll));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (payrollService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        payrollService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
