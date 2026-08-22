package com.dayflow.controller;

import com.dayflow.model.LeaveRequest;
import com.dayflow.model.LeaveStatus;
import com.dayflow.model.LeaveType;
import com.dayflow.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    // ----------------------------------------------------------------
    // LIST
    // ----------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAll() {
        return ResponseEntity.ok(leaveRequestService.findAll());
    }

    // ----------------------------------------------------------------
    // GET by ID
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getById(@PathVariable Long id) {
        return leaveRequestService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    @PostMapping
    public ResponseEntity<LeaveRequest> create(@Valid @RequestBody LeaveRequest leaveRequest) {
        LeaveRequest saved = leaveRequestService.save(leaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @PutMapping("/{id}")
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
    // DELETE
    // ----------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (leaveRequestService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        leaveRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    // FILTER endpoints
    // ----------------------------------------------------------------
    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveRequestService.findByEmployeeId(employeeId));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<LeaveRequest>> getByStatus(@PathVariable LeaveStatus status) {
        return ResponseEntity.ok(leaveRequestService.findByStatus(status));
    }

    @GetMapping("/by-type/{leaveType}")
    public ResponseEntity<List<LeaveRequest>> getByLeaveType(@PathVariable LeaveType leaveType) {
        return ResponseEntity.ok(leaveRequestService.findByLeaveType(leaveType));
    }
}
