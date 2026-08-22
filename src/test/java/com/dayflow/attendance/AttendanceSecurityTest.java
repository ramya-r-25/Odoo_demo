package com.dayflow.attendance;

import com.dayflow.attendance.repository.AttendanceRepository;
import com.dayflow.attendance.repository.EmployeeRepository;
import com.dayflow.leave.repository.LeaveRequestRepository;
import com.dayflow.model.Employee;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security integration tests for Attendance module.
 *
 * Note: mock users use emails that match seeded User records so
 * getEmployeeIdByUsername() resolves correctly via DB.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AttendanceSecurityTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setUp() {
        // Delete in FK-safe order: leaves → attendance → employees → users
        leaveRequestRepository.deleteAll();
        attendanceRepository.deleteAll();
        employeeRepository.deleteAll();
        userRepository.deleteAll();

        // Create Employee 1 (linked by employeeCode = User.employeeId)
        employee1 = employeeRepository.save(
                new Employee("EMP-T01", "Alex Morgan", "alex@test.com", "Engineering"));
        // Create Employee 2
        employee2 = employeeRepository.save(
                new Employee("EMP-T02", "Sarah Connor", "sarah@test.com", "HR"));

        // Create matching User records so getEmployeeIdByUsername() works
        userRepository.save(new User("EMP-T01", "Alex Morgan", "alex@test.com",
                passwordEncoder.encode("pass"), Role.EMPLOYEE));
        userRepository.save(new User("EMP-T02", "Sarah Connor", "sarah@test.com",
                passwordEncoder.encode("pass"), Role.HR_ADMIN));
    }

    // 1. Employee can access own attendance — using email username matching DB
    @Test
    @WithMockUser(username = "alex@test.com", roles = {"EMPLOYEE"})
    public void testEmployeeCanAccessOwnAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/" + employee1.getId()))
                .andExpect(status().isOk());
    }

    // 2. Employee CANNOT access another employee's attendance (403 Forbidden)
    @Test
    @WithMockUser(username = "alex@test.com", roles = {"EMPLOYEE"})
    public void testEmployeeCannotAccessOtherEmployeeAttendance() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/" + employee2.getId()))
                .andExpect(status().isForbidden());
    }

    // 3. HR_ADMIN can access all attendance
    @Test
    @WithMockUser(username = "sarah@test.com", roles = {"HR_ADMIN"})
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
    @WithMockUser(username = "alex@test.com", roles = {"EMPLOYEE"})
    public void testEmployeeCannotPerformAdminOperations() throws Exception {
        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isForbidden());
    }

    // 6. Employee can check-in and check-out using their linked employee profile
    @Test
    @WithMockUser(username = "alex@test.com", roles = {"EMPLOYEE"})
    public void testCheckInAndCheckOutIntegration() throws Exception {
        mockMvc.perform(post("/api/attendance/check-in"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/attendance/check-out"))
                .andExpect(status().isOk());
    }
}
