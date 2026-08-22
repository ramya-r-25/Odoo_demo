package com.dayflow.leave;

import com.dayflow.controller.EmployeeController;
import com.dayflow.leave.controller.LeaveController;
import com.dayflow.leave.controller.PayrollController;
import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.model.LeaveType;
import com.dayflow.leave.service.LeaveService;
import com.dayflow.model.Employee;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class LeaveAndPayrollTest {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveController leaveController;

    @Autowired
    private PayrollController payrollController;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setup() {
        employeeRepository.clear();

        employee1 = new Employee(
                null, "EMP-101", "Alex Morgan", "alex@dayflow.com", "+1-555-0101",
                "100 Tech Lane", "Software Engineer", "Engineering", 90000.0,
                "alex.png", List.of("resume.pdf")
        );
        employee1 = employeeRepository.save(employee1);

        employee2 = new Employee(
                null, "EMP-102", "Sarah Connor", "sarah@dayflow.com", "+1-555-0102",
                "200 HR St", "HR Specialist", "Human Resources", 75000.0,
                "sarah.png", List.of("id.pdf")
        );
        employee2 = employeeRepository.save(employee2);
    }

    // ==========================================
    // LEAVE MANAGEMENT TESTS
    // ==========================================

    @Test
    @WithMockUser(username = "alex@dayflow.com", roles = {"EMPLOYEE"})
    public void testEmployeeCanSubmitLeave() {
        LeaveRequestDTO dto = new LeaveRequestDTO(
                LeaveType.PAID,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                "Vacation trip"
        );
        
        Model model = new ConcurrentModel();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "leaveRequest");
        
        String view = leaveController.submitLeave(dto, bindingResult, model, redirectAttributes);
        assertEquals("redirect:/leave", view);
        
        List<LeaveRequest> leaves = leaveService.getLeavesByEmployee(employee1.getId());
        assertEquals(1, leaves.size());
        assertEquals(LeaveType.PAID, leaves.get(0).getLeaveType());
        assertEquals(LeaveStatus.PENDING, leaves.get(0).getStatus());
    }

    @Test
    @WithMockUser(username = "alex@dayflow.com", roles = {"EMPLOYEE"})
    public void testEmployeeCanViewOwnLeave() {
        LeaveRequestDTO dto = new LeaveRequestDTO(
                LeaveType.SICK,
                LocalDate.now(),
                LocalDate.now(),
                "Fever"
        );
        leaveService.applyLeave(employee1, dto);

        Model model = new ConcurrentModel();
        String view = leaveController.myLeaves(model);
        
        assertEquals("leave/my-leaves", view);
        List<?> leaves = (List<?>) model.getAttribute("leaves");
        assertNotNull(leaves);
        assertEquals(1, leaves.size());
    }

    @Test
    @WithMockUser(username = "sarah@dayflow.com", roles = {"EMPLOYEE"})
    public void testEmployeeCannotViewAnotherEmployeesLeave() {
        // employee1 has 1 leave
        LeaveRequestDTO dto = new LeaveRequestDTO(
                LeaveType.SICK,
                LocalDate.now(),
                LocalDate.now(),
                "Fever"
        );
        leaveService.applyLeave(employee1, dto);

        Model model = new ConcurrentModel();
        leaveController.myLeaves(model);
        
        List<?> leaves = (List<?>) model.getAttribute("leaves");
        assertNotNull(leaves);
        assertEquals(0, leaves.size()); // employee2 should see 0 leaves
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void testHrAdminCanViewAllLeave() {
        leaveService.applyLeave(employee1, new LeaveRequestDTO(LeaveType.PAID, LocalDate.now(), LocalDate.now(), ""));
        leaveService.applyLeave(employee2, new LeaveRequestDTO(LeaveType.SICK, LocalDate.now(), LocalDate.now(), ""));

        Model model = new ConcurrentModel();
        String view = leaveController.adminLeaves(model);
        
        assertEquals("leave/admin-leaves", view);
        List<?> leaves = (List<?>) model.getAttribute("leaves");
        assertNotNull(leaves);
        assertEquals(2, leaves.size());
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void testHrAdminCanApproveLeave() {
        LeaveRequest leave = leaveService.applyLeave(employee1, new LeaveRequestDTO(LeaveType.PAID, LocalDate.now(), LocalDate.now(), ""));
        
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = leaveController.approveLeave(leave.getId(), "Approved by HR", redirectAttributes);
        
        assertEquals("redirect:/leave/approvals", view);
        LeaveRequest updated = leaveService.getLeaveById(leave.getId()).orElseThrow();
        assertEquals(LeaveStatus.APPROVED, updated.getStatus());
        assertEquals("Approved by HR", updated.getHrComment());
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void testHrAdminCanRejectLeave() {
        LeaveRequest leave = leaveService.applyLeave(employee1, new LeaveRequestDTO(LeaveType.PAID, LocalDate.now(), LocalDate.now(), ""));
        
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = leaveController.rejectLeave(leave.getId(), "Rejected due to workload", redirectAttributes);
        
        assertEquals("redirect:/leave/approvals", view);
        LeaveRequest updated = leaveService.getLeaveById(leave.getId()).orElseThrow();
        assertEquals(LeaveStatus.REJECTED, updated.getStatus());
        assertEquals("Rejected due to workload", updated.getHrComment());
    }

    @Test
    @WithMockUser(username = "alex@dayflow.com", roles = {"EMPLOYEE"})
    public void testEmployeeCannotApproveOrRejectLeave() {
        LeaveRequest leave = leaveService.applyLeave(employee1, new LeaveRequestDTO(LeaveType.PAID, LocalDate.now(), LocalDate.now(), ""));
        
        assertThrows(SecurityException.class, () -> {
            leaveController.approveLeave(leave.getId(), "Hack", new RedirectAttributesModelMap());
        });

        assertThrows(SecurityException.class, () -> {
            leaveController.rejectLeave(leave.getId(), "Hack", new RedirectAttributesModelMap());
        });
    }

    // ==========================================
    // PAYROLL / SALARY VISIBILITY TESTS
    // ==========================================

    @Test
    @WithMockUser(username = "alex@dayflow.com", roles = {"EMPLOYEE"})
    public void testEmployeeCanViewOwnSalary() {
        Model model = new ConcurrentModel();
        String view = payrollController.salaryPage(model);
        
        assertEquals("payroll/salary", view);
        Employee modelEmployee = (Employee) model.getAttribute("employee");
        assertNotNull(modelEmployee);
        assertEquals("Alex Morgan", modelEmployee.getFullName());
        assertEquals(90000.0, modelEmployee.getSalary());
    }

    @Test
    @WithMockUser(username = "alex@dayflow.com", roles = {"EMPLOYEE"})
    public void testEmployeeCannotUpdateSalary() {
        assertThrows(SecurityException.class, () -> {
            payrollController.updateSalary(employee1.getId(), 150000.0, new RedirectAttributesModelMap());
        });
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void testHrAdminCanViewAllSalaries() {
        Model model = new ConcurrentModel();
        String view = payrollController.salaryPage(model);
        
        assertEquals("payroll/salary", view);
        List<?> employees = (List<?>) model.getAttribute("employees");
        assertNotNull(employees);
        assertEquals(2, employees.size());
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void testHrAdminCanUpdateSalary() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        String view = payrollController.updateSalary(employee1.getId(), 110000.0, redirectAttributes);
        
        assertEquals("redirect:/payroll", view);
        Employee updated = employeeRepository.findById(employee1.getId()).orElseThrow();
        assertEquals(110000.0, updated.getSalary());
    }
}
