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

    // 1. Employee can access own attendance
    @Test
    @WithMockUser(username = "alex", roles = {"EMPLOYEE"})
    public void testEmployeeCanAccessOwnAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/" + employee1.getId()))
                .andExpect(status().isOk());
    }

    // 2. Employee CANNOT access another employee's attendance (403 Forbidden)
    @Test
    @WithMockUser(username = "alex", roles = {"EMPLOYEE"})
    public void testEmployeeCannotAccessOtherEmployeeAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/" + employee2.getId()))
                .andExpect(status().isForbidden());
    }

    // 3. HR_ADMIN can access all attendance
    @Test
    @WithMockUser(username = "sarah", roles = {"HR_ADMIN"})
    public void testHrAdminCanAccessAllAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/attendance/employee/" + employee1.getId()))
                .andExpect(status().isOk());
    }

    // 4. Unauthenticated user cannot access attendance API (401 Unauthorized)
    @Test
    public void testUnauthenticatedUserCannotAccessAttendanceApi() throws Exception {
        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isUnauthorized());
    }

    // 5. Employee cannot perform admin attendance operations (403 Forbidden)
    @Test
    @WithMockUser(username = "alex", roles = {"EMPLOYEE"})
    public void testEmployeeCannotPerformAdminOperations() throws Exception {
        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isForbidden());
    }

    // 6. Check-in and check-out operation test under authenticated context
    @Test
    @WithMockUser(username = "alex", roles = {"EMPLOYEE"})
    public void testCheckInAndCheckOutIntegration() throws Exception {
        mockMvc.perform(post("/api/attendance/check-in"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/attendance/check-out"))
                .andExpect(status().isOk());
    }
}
