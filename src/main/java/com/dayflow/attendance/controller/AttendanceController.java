package com.dayflow.attendance.controller;

import com.dayflow.attendance.dto.AttendanceDTO;
import com.dayflow.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // Helper: Determine target employee ID based on authentication & role
    private Long resolveTargetEmployeeId(Long requestEmployeeId, Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Unauthorized: Authentication context is missing.");
        }
        boolean isHrAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"));
        Long authenticatedEmployeeId = attendanceService.getEmployeeIdByUsername(authentication.getName());

        if (!isHrAdmin) {
            // For EMPLOYEE role, ALWAYS use authenticated employee identity to prevent parameter tampering
            if (authenticatedEmployeeId == null) {
                throw new IllegalStateException("No employee profile linked to your account. Please contact HR Admin.");
            }
            return authenticatedEmployeeId;
        } else {
            // For HR_ADMIN, allow specifying employeeId or default to authenticated user
            return requestEmployeeId != null ? requestEmployeeId : authenticatedEmployeeId;
        }
    }

    // Helper: Validate cross-employee ownership access
    private void validateOwnershipOrAdmin(Long targetEmployeeId, Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Unauthorized: Authentication context is missing.");
        }
        boolean isHrAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"));
        if (!isHrAdmin) {
            Long authenticatedEmployeeId = attendanceService.getEmployeeIdByUsername(authentication.getName());
            if (authenticatedEmployeeId == null) {
                throw new AccessDeniedException("Forbidden: No employee profile linked to your account.");
            }
            if (!authenticatedEmployeeId.equals(targetEmployeeId)) {
                throw new AccessDeniedException("Forbidden: Employees are not allowed to access another employee's attendance.");
            }
        }
    }

    // 1. Create Attendance Record
    @PostMapping
    public ResponseEntity<AttendanceDTO> createAttendance(@Valid @RequestBody AttendanceDTO dto, Authentication authentication) {
        if (authentication != null && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"))) {
            throw new AccessDeniedException("Forbidden: Employees cannot create raw attendance records.");
        }
        AttendanceDTO created = attendanceService.createAttendance(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 2. Update Attendance Record
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(@PathVariable Long id, @RequestBody AttendanceDTO dto, Authentication authentication) {
        if (authentication != null && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"))) {
            throw new AccessDeniedException("Forbidden: Employees cannot update attendance records.");
        }
        AttendanceDTO updated = attendanceService.updateAttendance(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 3. Get Attendance Detail
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(@PathVariable Long id, Authentication authentication) {
        AttendanceDTO dto = attendanceService.getAttendanceById(id);
        validateOwnershipOrAdmin(dto.getEmployeeId(), authentication);
        return ResponseEntity.ok(dto);
    }

    // 4. Get All Attendance Records (HR_ADMIN ONLY)
    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendances(Authentication authentication) {
        if (authentication != null && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"))) {
            throw new AccessDeniedException("Forbidden: Employee role cannot access all employee attendance records.");
        }
        List<AttendanceDTO> list = attendanceService.getAllAttendances();
        return ResponseEntity.ok(list);
    }

    // 5. Daily Attendance View
    @GetMapping("/daily")
    public ResponseEntity<List<AttendanceDTO>> getDailyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDTO> list = attendanceService.getDailyAttendance(date);
        return ResponseEntity.ok(list);
    }

    // 6. Weekly Attendance View
    @GetMapping("/weekly")
    public ResponseEntity<List<AttendanceDTO>> getWeeklyAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        List<AttendanceDTO> list = attendanceService.getWeeklyAttendance(startDate);
        return ResponseEntity.ok(list);
    }

    // 7. Employee-Specific Attendance View with Strict Security Guard
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceDTO>> getEmployeeAttendance(@PathVariable Long employeeId, Authentication authentication) {
        validateOwnershipOrAdmin(employeeId, authentication);
        List<AttendanceDTO> list = attendanceService.getEmployeeAttendance(employeeId);
        return ResponseEntity.ok(list);
    }

    // 8. Authenticated Employee Self Attendance View
    @GetMapping("/my-attendance")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendance(Authentication authentication) {
        Long employeeId = resolveTargetEmployeeId(null, authentication);
        List<AttendanceDTO> list = attendanceService.getEmployeeAttendance(employeeId);
        return ResponseEntity.ok(list);
    }

    // 9. Employee Check-In Endpoint (Automatically resolves authenticated user identity)
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceDTO> checkIn(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkInTime,
            Authentication authentication) {
        Long targetEmployeeId = resolveTargetEmployeeId(employeeId, authentication);
        AttendanceDTO dto = attendanceService.employeeCheckIn(targetEmployeeId, checkInTime);
        return ResponseEntity.ok(dto);
    }

    // 10. Employee Check-Out Endpoint (Automatically resolves authenticated user identity)
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceDTO> checkOut(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOutTime,
            Authentication authentication) {
        Long targetEmployeeId = resolveTargetEmployeeId(employeeId, authentication);
        AttendanceDTO dto = attendanceService.employeeCheckOut(targetEmployeeId, checkOutTime);
        return ResponseEntity.ok(dto);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleAttendanceError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
