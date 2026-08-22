package com.dayflow.attendance;

import com.dayflow.attendance.model.Attendance;
import com.dayflow.attendance.model.AttendanceStatus;
import com.dayflow.attendance.model.Employee;
import com.dayflow.attendance.repository.AttendanceRepository;
import com.dayflow.attendance.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class DayflowAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DayflowAttendanceApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository) {
        return args -> {
            // Seed initial sample data for validation
            Employee emp1 = employeeRepository.save(new Employee("EMP-001", "Alex Morgan", "alex@dayflow.com", "Engineering"));
            Employee emp2 = employeeRepository.save(new Employee("EMP-002", "Sarah Connor", "sarah@dayflow.com", "Human Resources"));

            LocalDate today = LocalDate.now();

            Attendance att1 = new Attendance(emp1, today, LocalDateTime.now().minusHours(8), LocalDateTime.now(), AttendanceStatus.PRESENT);
            attendanceRepository.save(att1);

            Attendance att2 = new Attendance(emp2, today, LocalDateTime.now().minusHours(4), LocalDateTime.now(), AttendanceStatus.HALF_DAY);
            attendanceRepository.save(att2);
        };
    }
}
