package com.dayflow.attendance.controller;

import com.dayflow.attendance.dto.AttendanceDTO;
import com.dayflow.attendance.service.AttendanceService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Thymeleaf View Controller for /attendance.
 * Resolves the authenticated user's employee ID via the service layer (no hardcoding).
 * HR_ADMIN can see all records; EMPLOYEE sees only their own.
 */
@Controller
public class AttendanceViewController {

    private final AttendanceService attendanceService;

    public AttendanceViewController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/attendance")
    public String renderAttendancePage(
            @RequestParam(required = false) String view,
            @RequestParam(required = false) Long filterEmployeeId,
            Authentication authentication,
            Model model) {

        String username = authentication != null ? authentication.getName() : "";
        boolean isHrAdmin = authentication != null
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"));

        // Resolve current employee ID — null safe (will be null if no employee profile linked)
        Long currentEmployeeId = attendanceService.getEmployeeIdByUsername(username);

        List<AttendanceDTO> history;
        if (isHrAdmin && filterEmployeeId != null) {
            history = attendanceService.getEmployeeAttendance(filterEmployeeId);
        } else if (isHrAdmin && "all".equalsIgnoreCase(view)) {
            history = attendanceService.getAllAttendances();
        } else if ("daily".equalsIgnoreCase(view)) {
            history = attendanceService.getDailyAttendance(LocalDate.now());
        } else if ("weekly".equalsIgnoreCase(view)) {
            history = attendanceService.getWeeklyAttendance(LocalDate.now());
        } else if (isHrAdmin) {
            history = attendanceService.getAllAttendances();
        } else if (currentEmployeeId != null) {
            history = attendanceService.getEmployeeAttendance(currentEmployeeId);
        } else {
            history = List.of(); // no linked employee profile
        }

        // Today's record for check-in/check-out button state
        AttendanceDTO todayRecord = null;
        if (currentEmployeeId != null) {
            todayRecord = attendanceService.getEmployeeAttendance(currentEmployeeId).stream()
                    .filter(a -> LocalDate.now().equals(a.getDate()))
                    .findFirst()
                    .orElse(null);
        }

        model.addAttribute("username", username);
        model.addAttribute("employeeId", currentEmployeeId);
        model.addAttribute("isHrAdmin", isHrAdmin);
        model.addAttribute("todayRecord", todayRecord);
        model.addAttribute("history", history);
        model.addAttribute("currentView", view != null ? view : (isHrAdmin ? "all" : "my"));

        return "attendance";
    }
}
