package com.dayflow.attendance;

import com.dayflow.attendance.dto.AttendanceDTO;
import com.dayflow.attendance.model.AttendanceStatus;
import com.dayflow.attendance.model.Employee;
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

    @Test
    public void testCreateAttendanceAndDurationCalculation() {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setEmployeeId(testEmployee.getId());
        dto.setDate(LocalDate.now());
        dto.setCheckIn(LocalDateTime.now().minusHours(8));
        dto.setCheckOut(LocalDateTime.now());
        dto.setStatus(AttendanceStatus.PRESENT);

        AttendanceDTO created = attendanceService.createAttendance(dto);

        assertNotNull(created.getId());
        assertEquals(8.0, created.getWorkingDuration());
        assertEquals(AttendanceStatus.PRESENT, created.getStatus());
    }

    @Test
    public void testEmployeeCheckInAndCheckOutFlow() {
        LocalDateTime checkInTime = LocalDateTime.now().minusHours(4);
        AttendanceDTO checkedIn = attendanceService.employeeCheckIn(testEmployee.getId(), checkInTime);

        assertNotNull(checkedIn.getId());
        assertNotNull(checkedIn.getCheckIn());
        assertNull(checkedIn.getCheckOut());

        LocalDateTime checkOutTime = LocalDateTime.now();
        AttendanceDTO checkedOut = attendanceService.employeeCheckOut(testEmployee.getId(), checkOutTime);

        assertNotNull(checkedOut.getCheckOut());
        assertEquals(4.0, checkedOut.getWorkingDuration());
        assertEquals(AttendanceStatus.HALF_DAY, checkedOut.getStatus());
    }

    @Test
    public void testDailyAndWeeklyViews() {
        LocalDate today = LocalDate.now();
        attendanceService.employeeCheckIn(testEmployee.getId(), LocalDateTime.now().minusHours(8));
        attendanceService.employeeCheckOut(testEmployee.getId(), LocalDateTime.now());

        List<AttendanceDTO> daily = attendanceService.getDailyAttendance(today);
        assertEquals(1, daily.size());

        List<AttendanceDTO> weekly = attendanceService.getWeeklyAttendance(today.minusDays(1));
        assertFalse(weekly.isEmpty());
    }
}
