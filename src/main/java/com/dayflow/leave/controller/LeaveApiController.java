package com.dayflow.leave.controller;

import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveType;
import com.dayflow.leave.service.LeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Leave Management.
 * - EMPLOYEE: Apply leave, view own leaves.
 * - HR_ADMIN: View all, approve, reject.
 */
@RestController
@RequestMapping("/api/leave")
public class LeaveApiController {

    private final LeaveService leaveService;

    public LeaveApiController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    private boolean isHrAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"));
    }

    /** Apply for leave (Employee only). */
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(
            @RequestParam String leaveType,
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam(required = false) String reason,
            Authentication authentication) {

        Long employeeId = leaveService.resolveEmployeeIdFromUsername(authentication.getName());
        if (employeeId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No employee profile linked to your account. Please contact HR."));
        }

        LeaveType type;
        try {
            type = LeaveType.valueOf(leaveType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid leave type: " + leaveType));
        }

        LeaveRequestDTO dto = leaveService.applyLeave(employeeId, type, fromDate, toDate, reason);
        return ResponseEntity.ok(dto);
    }

    /** Get own leave requests (Employee). */
    @GetMapping("/my")
    public ResponseEntity<List<LeaveRequestDTO>> getMyLeaves(Authentication authentication) {
        Long employeeId = leaveService.resolveEmployeeIdFromUsername(authentication.getName());
        if (employeeId == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(leaveService.getMyLeaves(employeeId));
    }

    /** Get all leave requests (HR Admin only). */
    @GetMapping("/all")
    public ResponseEntity<List<LeaveRequestDTO>> getAllLeaves(Authentication authentication) {
        if (!isHrAdmin(authentication)) {
            throw new AccessDeniedException("Only HR Admin can view all leave requests.");
        }
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    /** Get pending leave requests (HR Admin only). */
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaves(Authentication authentication) {
        if (!isHrAdmin(authentication)) {
            throw new AccessDeniedException("Only HR Admin can view pending leave requests.");
        }
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }

    /** Approve leave (HR Admin only). */
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id, Authentication authentication) {
        if (!isHrAdmin(authentication)) {
            throw new AccessDeniedException("Only HR Admin can approve leave requests.");
        }
        LeaveRequestDTO dto = leaveService.approveLeave(id, authentication.getName());
        return ResponseEntity.ok(dto);
    }

    /** Reject leave (HR Admin only). */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        if (!isHrAdmin(authentication)) {
            throw new AccessDeniedException("Only HR Admin can reject leave requests.");
        }
        LeaveRequestDTO dto = leaveService.rejectLeave(id, authentication.getName(), reason);
        return ResponseEntity.ok(dto);
    }

    /** Get leave by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getLeaveById(@PathVariable Long id, Authentication authentication) {
        LeaveRequestDTO dto = leaveService.getLeaveById(id);
        // Employees can only see their own
        if (!isHrAdmin(authentication)) {
            Long myEmpId = leaveService.resolveEmployeeIdFromUsername(authentication.getName());
            if (myEmpId == null || !myEmpId.equals(dto.getEmployeeId())) {
                throw new AccessDeniedException("You are not allowed to view this leave request.");
            }
        }
        return ResponseEntity.ok(dto);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleLeaveError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
