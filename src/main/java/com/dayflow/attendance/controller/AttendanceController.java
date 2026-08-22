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

    // Helper method: Validate if the logged-in user can access employeeId
    private void validateOwnershipOrAdmin(Long employeeId, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        boolean isHrAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"));
        if (!isHrAdmin) {
            Long currentEmployeeId = attendanceService.getEmployeeIdByUsername(authentication.getName());
            if (!currentEmployeeId.equals(employeeId)) {
                throw new AccessDeniedException("Unauthorized: Employees can view only their own attendance records.");
            }
        }
    }

    // 1. Create Attendance Record
    @PostMapping
    public ResponseEntity<AttendanceDTO> createAttendance(@Valid @RequestBody AttendanceDTO dto) {
        AttendanceDTO created = attendanceService.createAttendance(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 2. Update Attendance Record
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(@PathVariable Long id, @RequestBody AttendanceDTO dto) {
        AttendanceDTO updated = attendanceService.updateAttendance(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 3. Get Attendance Detail (Basic Form View)
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(@PathVariable Long id) {
        AttendanceDTO dto = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(dto);
    }

    // 4. Get All Attendance Records (HR_ADMIN View)
    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendances() {
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

    // 7. Employee-Specific Attendance View with Ownership Validation Guard
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceDTO>> getEmployeeAttendance(@PathVariable Long employeeId, Authentication authentication) {
        validateOwnershipOrAdmin(employeeId, authentication);
        List<AttendanceDTO> list = attendanceService.getEmployeeAttendance(employeeId);
        return ResponseEntity.ok(list);
    }

    // 8. Authenticated Employee Self Attendance View
    @GetMapping("/my-attendance")
    public ResponseEntity<List<AttendanceDTO>> getMyAttendance(Principal principal) {
        String username = principal != null ? principal.getName() : "alex";
        Long employeeId = attendanceService.getEmployeeIdByUsername(username);
        List<AttendanceDTO> list = attendanceService.getEmployeeAttendance(employeeId);
        return ResponseEntity.ok(list);
    }

    // 9. Employee Check-In Endpoint
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceDTO> checkIn(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkInTime,
            Principal principal) {
        Long targetEmployeeId = employeeId != null ? employeeId : attendanceService.getEmployeeIdByUsername(principal != null ? principal.getName() : "alex");
        AttendanceDTO dto = attendanceService.employeeCheckIn(targetEmployeeId, checkInTime);
        return ResponseEntity.ok(dto);
    }

    // 10. Employee Check-Out Endpoint
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceDTO> checkOut(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOutTime,
            Principal principal) {
        Long targetEmployeeId = employeeId != null ? employeeId : attendanceService.getEmployeeIdByUsername(principal != null ? principal.getName() : "alex");
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
