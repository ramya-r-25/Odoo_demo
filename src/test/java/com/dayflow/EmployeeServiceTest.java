package com.dayflow;

import com.dayflow.controller.EmployeeController;
import com.dayflow.model.Employee;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EmployeeServiceTest {

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeController controller;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testCreateEmployeeValid() {
        Employee emp1 = new Employee(
                null, "EMP-101", "Alex Morgan", "alex@dayflow.com", "+1-555-0101",
                "100 Tech Lane", "Software Engineer", "Engineering", 90000.0,
                "alex.png", Arrays.asList("resume.pdf")
        );
        Employee saved1 = service.createEmployee(emp1);
        assertNotNull(saved1.getId(), "ID should be generated");
        assertEquals("EMP-101", saved1.getEmployeeId(), "Employee ID mismatch");
    }

    @Test
    public void testCreateEmployeeInvalidEmail() {
        Employee invalidEmp = new Employee(null, "EMP-999", "John Doe", "invalid-email-format", null, null, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> service.createEmployee(invalidEmp));
    }

    @Test
    public void testEmployeeEditingRestrictions() {
        Employee emp = new Employee(
                null, "EMP-101", "Alex Morgan", "alex@dayflow.com", "+1-555-0101",
                "100 Tech Lane", "Software Engineer", "Engineering", 90000.0,
                "alex.png", Arrays.asList("resume.pdf")
        );
        Employee original = service.createEmployee(emp);

        Employee editAttempt = new Employee(
                original.getId(), "EMP-HACKED", "Hacked Name", "hacked@dayflow.com",
                "+1-555-NEW-PHONE", "555 New Address", "Hacked Position", "Hacked Dept",
                999999.0, "new_pic.png", null
        );

        Employee result = service.updateEmployee(original.getId(), editAttempt, SecurityGroup.EMPLOYEE);

        assertEquals("+1-555-NEW-PHONE", result.getPhone());
        assertEquals("555 New Address", result.getAddress());
        assertEquals("new_pic.png", result.getProfilePicture());
        assertEquals(90000.0, result.getSalary());
        assertEquals("Engineering", result.getDepartment());
        assertEquals("Alex Morgan", result.getFullName());
    }

    @Test
    public void testHrAdminFullEditAccess() {
        Employee emp = new Employee(
                null, "EMP-101", "Alex Morgan", "alex@dayflow.com", "+1-555-0101",
                "100 Tech Lane", "Software Engineer", "Engineering", 90000.0,
                "alex.png", Arrays.asList("resume.pdf")
        );
        Employee original = service.createEmployee(emp);

        Employee hrEdit = new Employee(
                original.getId(), "EMP-101", "Alex Morgan", "alex@dayflow.com",
                original.getPhone(), original.getAddress(), "Lead Engineer", "Core Engineering",
                120000.0, original.getProfilePicture(), original.getDocuments()
        );

        Employee result = service.updateEmployee(original.getId(), hrEdit, SecurityGroup.HR_ADMIN);
        assertEquals(120000.0, result.getSalary());
        assertEquals("Lead Engineer", result.getJobPosition());
    }
}
