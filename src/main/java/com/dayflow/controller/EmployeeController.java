package com.dayflow.controller;

import com.dayflow.model.Employee;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;

import java.util.List;

public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Endpoint: View authenticated employee's OWN profile (Principal-based, ignores URL manipulation)
    public Employee getMyProfile(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new SecurityException("Access Denied: Unauthenticated user");
        }
        return employeeService.getEmployeeByAuthenticatedUser(authenticatedUser);
    }

    // Endpoint: Employee list (HR_ADMIN only)
    public List<Employee> getEmployeeList(User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getRole() != Role.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can access the employee list");
        }
        return employeeService.getAllEmployees();
    }

    // Compatibility overload for SecurityGroup
    public List<Employee> getEmployeeList(SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can access the employee list");
        }
        return employeeService.getAllEmployees();
    }

    // Endpoint: View individual employee profile with server-side URL tamper protection
    public Employee getEmployeeProfile(Long targetEmployeeId, User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new SecurityException("Access Denied: Unauthenticated user");
        }

        Employee targetEmployee = employeeService.getEmployeeById(targetEmployeeId);

        if (authenticatedUser.getRole() == Role.EMPLOYEE) {
            // Identify employee associated with authenticated user
            Employee ownEmployee = employeeService.getEmployeeByAuthenticatedUser(authenticatedUser);
            // Verify ownership: direct URL manipulation attempt on another profile is BLOCKED server-side
            if (!targetEmployee.getId().equals(ownEmployee.getId())) {
                throw new SecurityException("Access Denied: Direct URL manipulation detected. You cannot view another employee's profile.");
            }
        }

        return targetEmployee;
    }

    // Compatibility overload for SecurityGroup
    public Employee getEmployeeProfile(Long targetEmployeeId, SecurityGroup currentUserRole, Long loggedInEmployeeId) {
        Employee targetEmployee = employeeService.getEmployeeById(targetEmployeeId);
        if (currentUserRole == SecurityGroup.EMPLOYEE) {
            if (loggedInEmployeeId == null || !targetEmployeeId.equals(loggedInEmployeeId)) {
                throw new SecurityException("Access Denied: Direct URL manipulation detected. You cannot view another employee's profile.");
            }
        }
        return targetEmployee;
    }

    // Endpoint: Create employee (HR_ADMIN only)
    public Employee createEmployee(Employee employee, User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getRole() != Role.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can create new employees");
        }
        return employeeService.createEmployee(employee);
    }

    // Compatibility overload for SecurityGroup
    public Employee createEmployee(Employee employee, SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can create new employees");
        }
        return employeeService.createEmployee(employee);
    }

    // Endpoint: Update employee profile with server-side URL tamper protection and role restrictions
    public Employee updateEmployee(Long targetEmployeeId, Employee updatedData, User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new SecurityException("Access Denied: Unauthenticated user");
        }

        Employee targetEmployee = employeeService.getEmployeeById(targetEmployeeId);

        if (authenticatedUser.getRole() == Role.EMPLOYEE) {
            Employee ownEmployee = employeeService.getEmployeeByAuthenticatedUser(authenticatedUser);
            if (!targetEmployee.getId().equals(ownEmployee.getId())) {
                throw new SecurityException("Access Denied: Direct URL manipulation detected. You cannot edit another employee's profile.");
            }
        }

        return employeeService.updateEmployee(targetEmployeeId, updatedData, authenticatedUser.getRole());
    }

    // Compatibility overload for SecurityGroup
    public Employee updateEmployee(Long targetEmployeeId, Employee updatedData, SecurityGroup currentUserRole, Long loggedInEmployeeId) {
        if (currentUserRole == SecurityGroup.EMPLOYEE) {
            if (loggedInEmployeeId == null || !targetEmployeeId.equals(loggedInEmployeeId)) {
                throw new SecurityException("Access Denied: Direct URL manipulation detected. You cannot edit another employee's profile.");
            }
        }
        return employeeService.updateEmployee(targetEmployeeId, updatedData, currentUserRole);
    }

    // Endpoint: Delete employee (HR_ADMIN only)
    public boolean deleteEmployee(Long targetEmployeeId, User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getRole() != Role.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can delete employees");
        }
        return employeeService.deleteEmployee(targetEmployeeId);
    }

    // Compatibility overload for SecurityGroup
    public boolean deleteEmployee(Long targetEmployeeId, SecurityGroup currentUserRole) {
        if (currentUserRole != SecurityGroup.HR_ADMIN) {
            throw new SecurityException("Access Denied: Only HR_ADMIN can delete employees");
        }
        return employeeService.deleteEmployee(targetEmployeeId);
    }
}
