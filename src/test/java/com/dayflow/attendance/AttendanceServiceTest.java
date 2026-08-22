package com.dayflow.attendance;

import com.dayflow.attendance.dto.AttendanceDTO;
import com.dayflow.attendance.model.AttendanceStatus;
import com.dayflow.model.Employee;
import com.dayflow.attendance.repository.AttendanceRepository;
import com.dayflow.attendance.repository.EmployeeRepository;
import com.dayflow.attendance.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AttendanceServiceTest {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private Employee testEmployee;

    @BeforeEach
    public void setUp() {
        attendanceRepository.deleteAll();
        employeeRepository.deleteAll();

        testEmployee = employeeRepository.save(new Employee("EMP-TEST", "Test Employee", "test@dayflow.com", "QA"));
    }

    // Test 1: Check-in & Duration Calculation
    @Test
    public void testCheckInAndDurationCalculation() {
        LocalDateTime checkInTime = LocalDateTime.now().minusHours(8);
        AttendanceDTO checkedIn = attendanceService.employeeCheckIn(testEmployee.getId(), checkInTime);

        assertNotNull(checkedIn.getId());
        assertNotNull(checkedIn.getCheckIn());
        assertNull(checkedOutOrCheckIn(checkedIn));

        LocalDateTime checkOutTime = LocalDateTime.now();
        AttendanceDTO checkedOut = attendanceService.employeeCheckOut(testEmployee.getId(), checkOutTime);

        assertNotNull(checkedOut.getCheckOut());
        assertEquals(8.0, checkedOut.getWorkingDuration());
        assertEquals(AttendanceStatus.PRESENT, checkedOut.getStatus());
    }

    private LocalDateTime checkedOutOrCheckIn(AttendanceDTO dto) {
        return dto.getCheckOut();
    }

    // Test 2: Prevent Duplicate Check-in
    @Test
    public void testDuplicateCheckInPrevention() {
        LocalDateTime now = LocalDateTime.now();
        attendanceService.employeeCheckIn(testEmployee.getId(), now);

        // Attempt second check-in on the same date for the same employee
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            attendanceService.employeeCheckIn(testEmployee.getId(), now);
        });

        assertTrue(exception.getMessage().contains("already checked in"));
    }

    // Test 3: Prevent Check-out without Check-in
    @Test
    public void testCheckOutWithoutCheckInPrevention() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            attendanceService.employeeCheckOut(testEmployee.getId(), LocalDateTime.now());
        });

        assertTrue(exception.getMessage().contains("No active check-in record found"));
    }

    // Test 4: Employee-Specific Attendance Retrieval
    @Test
    public void testGetEmployeeAttendance() {
        attendanceService.employeeCheckIn(testEmployee.getId(), LocalDateTime.now().minusHours(4));
        attendanceService.employeeCheckOut(testEmployee.getId(), LocalDateTime.now());

        List<AttendanceDTO> employeeRecords = attendanceService.getEmployeeAttendance(testEmployee.getId());
        assertEquals(1, employeeRecords.size());
        assertEquals(testEmployee.getId(), employeeRecords.get(0).getEmployeeId());
    }

    // Test 5: Daily and Weekly Attendance Queries
    @Test
    public void testDailyAndWeeklyQueries() {
        LocalDate today = LocalDate.now();
        attendanceService.employeeCheckIn(testEmployee.getId(), LocalDateTime.now().minusHours(8));
        attendanceService.employeeCheckOut(testEmployee.getId(), LocalDateTime.now());

        List<AttendanceDTO> daily = attendanceService.getDailyAttendance(today);
        assertEquals(1, daily.size());

        List<AttendanceDTO> weekly = attendanceService.getWeeklyAttendance(today);
        assertFalse(weekly.isEmpty());
    }
}
