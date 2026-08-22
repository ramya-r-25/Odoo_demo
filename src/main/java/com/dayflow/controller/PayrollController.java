package com.dayflow.controller;

import com.dayflow.model.Payroll;
import com.dayflow.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Payroll management.
 * Only HR_ADMIN can manage payroll.
 * EMPLOYEE can view their own payroll by ID.
 */
@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    // ----------------------------------------------------------------
    // LIST — HR_ADMIN only
    // ----------------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<List<Payroll>> getAll() {
        return ResponseEntity.ok(payrollService.findAll());
    }

    // ----------------------------------------------------------------
    // GET by ID — authenticated users
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Payroll> getById(@PathVariable Long id) {
        return payrollService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // GET by Employee — EMPLOYEE can view own; HR_ADMIN can view all
    // ----------------------------------------------------------------
    @GetMapping("/by-employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'EMPLOYEE', 'ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Payroll> getByEmployee(@PathVariable Long employeeId) {
        return payrollService.findByEmployeeId(employeeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // CREATE — HR_ADMIN only
    // ----------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Payroll> create(@Valid @RequestBody Payroll payroll) {
        Payroll saved = payrollService.save(payroll);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ----------------------------------------------------------------
    // UPDATE — HR_ADMIN only
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
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
    // DELETE — HR_ADMIN only
    // ----------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (payrollService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        payrollService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
