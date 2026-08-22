package com.dayflow.attendance.controller;

import com.dayflow.attendance.dto.AttendanceDTO;
import com.dayflow.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // 1. Create Attendance Record (Basic Form View submit)
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

    // 3. Get Attendance Detail (Basic Form View read)
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(@PathVariable Long id) {
        AttendanceDTO dto = attendanceService.getAttendanceById(id);
        return ResponseEntity.ok(dto);
    }

    // 4. Get All Attendance Records (Basic List View / HR Admin View)
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

    // 7. Employee-Specific Attendance View
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceDTO>> getEmployeeAttendance(@PathVariable Long employeeId) {
        List<AttendanceDTO> list = attendanceService.getEmployeeAttendance(employeeId);
        return ResponseEntity.ok(list);
    }

    // 8. Employee Check-In Endpoint
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceDTO> checkIn(
            @RequestParam Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkInTime) {
        AttendanceDTO dto = attendanceService.employeeCheckIn(employeeId, checkInTime);
        return ResponseEntity.ok(dto);
    }

    // 9. Employee Check-Out Endpoint
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceDTO> checkOut(
            @RequestParam Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOutTime) {
        AttendanceDTO dto = attendanceService.employeeCheckOut(employeeId, checkOutTime);
        return ResponseEntity.ok(dto);
    }
}
