package com.dayflow;

import com.dayflow.attendance.model.Attendance;
import com.dayflow.attendance.model.AttendanceStatus;
import com.dayflow.attendance.repository.AttendanceRepository;
import com.dayflow.attendance.repository.EmployeeRepository;
import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.model.LeaveType;
import com.dayflow.leave.repository.LeaveRequestRepository;
import com.dayflow.model.Employee;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Main Entry Point for Dayflow HRMS Spring Boot Application.
 *
 * Seed data strategy:
 *  - User accounts are seeded in SecurityConfig (EMP001 / HR001)
 *  - Employee entities are seeded here, linked by employeeCode matching User.employeeId
 *  - Demo attendance records are created for today
 *  - Demo leave requests are created to test the approval workflow
 */
@SpringBootApplication
public class DayflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(DayflowApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDemoData(
            EmployeeRepository employeeRepository,
            AttendanceRepository attendanceRepository,
            LeaveRequestRepository leaveRequestRepository) {
        return args -> {
            // Only seed if no employees exist
            if (employeeRepository.count() == 0) {
                // Employee 1: matches User with employeeId = "EMP001" and email = "employee@dayflow.com"
                Employee emp1 = new Employee();
                emp1.setEmployeeCode("EMP001");  // matches User.employeeId
                emp1.setName("John Employee");
                emp1.setEmail("employee@dayflow.com"); // matches User.email
                emp1.setDepartment("Engineering");
                emp1.setJobPosition("Software Engineer");
                emp1 = employeeRepository.save(emp1);

                // Employee 2: matches User with employeeId = "HR001" and email = "admin@dayflow.com"
                Employee emp2 = new Employee();
                emp2.setEmployeeCode("HR001");   // matches User.employeeId
                emp2.setName("Sarah HR Admin");
                emp2.setEmail("admin@dayflow.com"); // matches User.email
                emp2.setDepartment("Human Resources");
                emp2.setJobPosition("HR Manager");
                emp2 = employeeRepository.save(emp2);

                // Seed attendance for today
                LocalDate today = LocalDate.now();
                // emp1: checked in today (open attendance – no checkout yet)
                attendanceRepository.save(new Attendance(
                        emp1, today,
                        LocalDateTime.now().withHour(9).withMinute(0).withSecond(0),
                        null,
                        AttendanceStatus.PRESENT));

                // emp2: full day yesterday
                attendanceRepository.save(new Attendance(
                        emp2, today.minusDays(1),
                        LocalDateTime.now().minusDays(1).withHour(9).withMinute(0).withSecond(0),
                        LocalDateTime.now().minusDays(1).withHour(18).withMinute(0).withSecond(0),
                        AttendanceStatus.PRESENT));

                // Seed demo leave requests
                // Pending leave for emp1 (future)
                LeaveRequest pending = new LeaveRequest(
                        emp1, LeaveType.ANNUAL,
                        today.plusDays(5), today.plusDays(7),
                        "Planned vacation");
                leaveRequestRepository.save(pending);

                // Already approved leave for emp1 in the past
                LeaveRequest approved = new LeaveRequest(
                        emp1, LeaveType.SICK,
                        today.minusDays(10), today.minusDays(10),
                        "Fever");
                approved.approve("admin@dayflow.com");
                leaveRequestRepository.save(approved);

                // Rejected leave for emp2
                LeaveRequest rejected = new LeaveRequest(
                        emp2, LeaveType.CASUAL,
                        today.plusDays(2), today.plusDays(3),
                        "Personal work");
                rejected.reject("admin@dayflow.com", "Insufficient staffing during this period");
                leaveRequestRepository.save(rejected);
            }
        };
    }
}
