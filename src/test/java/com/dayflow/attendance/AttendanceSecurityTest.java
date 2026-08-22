package com.dayflow.attendance;

import com.dayflow.attendance.repository.AttendanceRepository;
import com.dayflow.attendance.repository.EmployeeRepository;
import com.dayflow.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AttendanceSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setUp() {
        attendanceRepository.deleteAll();
        employeeRepository.deleteAll();

        employee1 = employeeRepository.save(new Employee("EMP-001", "Alex Morgan", "alex@dayflow.com", "Engineering"));
        employee2 = employeeRepository.save(new Employee("EMP-002", "Sarah Connor", "sarah@dayflow.com", "Human Resources"));
    }

    // 1. Employee can view own attendance
    @Test
    @WithMockUser(username = "alex", roles = {"EMPLOYEE"})
    public void testEmployeeCanViewOwnAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/" + employee1.getId()))
                .andExpect(status().isOk());
    }

    // 2. Unauthorized employee cross-access is blocked with 403 Forbidden
    @Test
    @WithMockUser(username = "alex", roles = {"EMPLOYEE"})
    public void testEmployeeCannotViewOtherEmployeeAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/" + employee2.getId()))
                .andExpect(status().isForbidden());
    }

    // 3. HR_ADMIN can view any employee's attendance
    @Test
    @WithMockUser(username = "sarah", roles = {"HR_ADMIN"})
    public void testHrAdminCanViewAnyEmployeeAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/" + employee1.getId()))
                .andExpect(status().isOk());
    }

    // 4. Check-in and check-out operation test under authenticated context
    @Test
    @WithMockUser(username = "alex", roles = {"EMPLOYEE"})
    public void testCheckInAndCheckOutIntegration() throws Exception {
        mockMvc.perform(post("/api/attendance/check-in").param("employeeId", employee1.getId().toString()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/attendance/check-out").param("employeeId", employee1.getId().toString()))
                .andExpect(status().isOk());
    }
}
