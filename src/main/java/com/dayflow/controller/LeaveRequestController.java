package com.dayflow.controller;

import com.dayflow.model.LeaveRequest;
import com.dayflow.model.LeaveStatus;
import com.dayflow.model.LeaveType;
import com.dayflow.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Leave Request management.
 * EMPLOYEE: can submit and view own leave requests.
 * HR_ADMIN: can view all, approve, reject, and delete.
 */
@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    // ----------------------------------------------------------------
    // LIST — HR_ADMIN only (all requests)
    // ----------------------------------------------------------------
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<List<LeaveRequest>> getAll() {
        return ResponseEntity.ok(leaveRequestService.findAll());
    }

    // ----------------------------------------------------------------
    // GET by ID — authenticated users
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LeaveRequest> getById(@PathVariable Long id) {
        return leaveRequestService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // CREATE — EMPLOYEE or HR_ADMIN can submit
    // ----------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'EMPLOYEE', 'ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<LeaveRequest> create(@Valid @RequestBody LeaveRequest leaveRequest) {
        try {
            LeaveRequest saved = leaveRequestService.save(leaveRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ----------------------------------------------------------------
    // UPDATE — HR_ADMIN only (for approving/rejecting)
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<LeaveRequest> update(@PathVariable Long id,
                                               @Valid @RequestBody LeaveRequest leaveRequest) {
        return leaveRequestService.findById(id)
                .map(existing -> {
                    leaveRequest.setId(id);
                    return ResponseEntity.ok(leaveRequestService.save(leaveRequest));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // DELETE — HR_ADMIN only
    // ----------------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (leaveRequestService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        leaveRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    // FILTER — by employee ID (EMPLOYEE can view own; HR_ADMIN can view all)
    // ----------------------------------------------------------------
    @GetMapping("/by-employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'EMPLOYEE', 'ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<List<LeaveRequest>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveRequestService.findByEmployeeId(employeeId));
    }

    // ----------------------------------------------------------------
    // FILTER — by status (HR_ADMIN only)
    // ----------------------------------------------------------------
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<List<LeaveRequest>> getByStatus(@PathVariable LeaveStatus status) {
        return ResponseEntity.ok(leaveRequestService.findByStatus(status));
    }

    // ----------------------------------------------------------------
    // FILTER — by leave type (HR_ADMIN only)
    // ----------------------------------------------------------------
    @GetMapping("/by-type/{leaveType}")
    @PreAuthorize("hasAnyAuthority('ROLE_HR_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<List<LeaveRequest>> getByLeaveType(@PathVariable LeaveType leaveType) {
        return ResponseEntity.ok(leaveRequestService.findByLeaveType(leaveType));
    }
}
