package com.dayflow.controller;

import com.dayflow.model.Employee;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;

import java.util.List;

public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Endpoint: Employee list (HR_ADMIN only)
    public List<Employee> getEmployeeList(SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can access the employee list");
        }
        return employeeService.getAllEmployees();
    }

    // Endpoint: View employee profile
    public Employee getEmployeeProfile(Long id, SecurityGroup currentUserRole, Long loggedInUserId) {
        if (currentUserRole == SecurityGroup.EMPLOYEE && !id.equals(loggedInUserId)) {
            throw new SecurityException("Access Denied: Employees can view only their own profile");
        }
        return employeeService.getEmployeeById(id);
    }

    // Endpoint: Create employee (HR_ADMIN only)
    public Employee createEmployee(Employee employee, SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can create new employees");
        }
        return employeeService.createEmployee(employee);
    }

    // Endpoint: Edit employee profile
    public Employee updateEmployee(Long id, Employee updatedData, SecurityGroup currentUserRole, Long loggedInUserId) {
        if (currentUserRole == SecurityGroup.EMPLOYEE && !id.equals(loggedInUserId)) {
            throw new SecurityException("Access Denied: Employees can edit only their own profile");
        }
        return employeeService.updateEmployee(id, updatedData, currentUserRole);
    }

    // Endpoint: Delete employee (HR_ADMIN only)
    public boolean deleteEmployee(Long id, SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can delete employees");
        }
        return employeeService.deleteEmployee(id);
    }
}
