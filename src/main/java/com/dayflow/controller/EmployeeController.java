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

    // Endpoint: View employee profile (Server-side URL Tamper Protection)
    public Employee getEmployeeProfile(Long targetEmployeeId, SecurityGroup currentUserRole, Long loggedInEmployeeId) {
        if (currentUserRole == SecurityGroup.EMPLOYEE) {
            if (loggedInEmployeeId == null || !targetEmployeeId.equals(loggedInEmployeeId)) {
                throw new SecurityException("Access Denied: Employees can view only their own profile");
            }
        }
        return employeeService.getEmployeeById(targetEmployeeId);
    }

    // Endpoint: Create employee (HR_ADMIN only)
    public Employee createEmployee(Employee employee, SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can create new employees");
        }
        return employeeService.createEmployee(employee);
    }

    // Endpoint: Update employee profile (Server-side Security & URL Tamper Protection)
    public Employee updateEmployee(Long targetEmployeeId, Employee updatedData, SecurityGroup currentUserRole, Long loggedInEmployeeId) {
        if (currentUserRole == SecurityGroup.EMPLOYEE) {
            if (loggedInEmployeeId == null || !targetEmployeeId.equals(loggedInEmployeeId)) {
                throw new SecurityException("Access Denied: Employees can edit only their own profile");
            }
        }
        return employeeService.updateEmployee(targetEmployeeId, updatedData, currentUserRole);
    }

    // Endpoint: Delete employee (HR_ADMIN only)
    public boolean deleteEmployee(Long targetEmployeeId, SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can delete employees");
        }
        return employeeService.deleteEmployee(targetEmployeeId);
    }
}
