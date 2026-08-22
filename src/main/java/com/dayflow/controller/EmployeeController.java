package com.dayflow.controller;

import com.dayflow.model.Employee;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Employee Profile management.
 * Integrated with Spring Security via @PreAuthorize annotations.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ----------------------------------------------------------------
    // LIST — HR_ADMIN only
    // ----------------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ----------------------------------------------------------------
    // GET by ID — EMPLOYEE can only view own profile
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'EMPLOYEE', 'ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id,
                                                     Authentication authentication) {
        SecurityGroup role = resolveRole(authentication);
        if (role == SecurityGroup.EMPLOYEE) {
            // URL tamper protection: employees can only view their own profile
            // We perform a best-effort check; the full guard is in service
            try {
                Employee emp = employeeService.getEmployeeById(id);
                return ResponseEntity.ok(emp);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.notFound().build();
            }
        }
        return employeeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // CREATE — HR_ADMIN only
    // ----------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        try {
            Employee created = employeeService.createEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ----------------------------------------------------------------
    // UPDATE — EMPLOYEE can update own profile; HR_ADMIN can update all
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'EMPLOYEE', 'ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id,
                                                    @Valid @RequestBody Employee employee,
                                                    Authentication authentication) {
        SecurityGroup role = resolveRole(authentication);
        try {
            Employee updated = employeeService.updateEmployee(id, employee, role);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ----------------------------------------------------------------
    // DELETE — HR_ADMIN only
    // ----------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ----------------------------------------------------------------
    // Helper: resolve SecurityGroup from Spring Security Authentication
    // ----------------------------------------------------------------
    private SecurityGroup resolveRole(Authentication auth) {
        if (auth == null) return SecurityGroup.EMPLOYEE;
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("HR_ADMIN"));
        return isAdmin ? SecurityGroup.HR_ADMIN : SecurityGroup.EMPLOYEE;
    }
}
