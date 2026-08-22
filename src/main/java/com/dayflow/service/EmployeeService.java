package com.dayflow.service;

import com.dayflow.model.Employee;
import com.dayflow.model.Role;
import com.dayflow.model.User;

import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;

import java.util.List;
import java.util.regex.Pattern;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(Employee employee) {
        validateRequiredFields(employee);
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
    }

    public Employee getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with Employee ID: " + employeeId));
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found for email: " + email));
    }

    // Resolve employee associated with authenticated User
    public Employee getEmployeeByAuthenticatedUser(User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getEmail() == null) {
            throw new SecurityException("Authentication Error: User principal is not logged in");
        }
        if (authenticatedUser.getEmployeeId() != null && !authenticatedUser.getEmployeeId().isEmpty()) {
            return getEmployeeByEmployeeId(authenticatedUser.getEmployeeId());
        }
        return getEmployeeByEmail(authenticatedUser.getEmail());
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Update profile with role-based field restrictions enforced server-side
    public Employee updateEmployee(Long id, Employee inputData, Role userRole) {
        Employee existing = getEmployeeById(id);

        if (userRole == Role.HR_ADMIN) {
            // HR_ADMIN can update all fields (salary, job position, department, documents, employeeId, name, email)
            validateRequiredFields(inputData);
            existing.setEmployeeId(inputData.getEmployeeId());
            existing.setFullName(inputData.getFullName());
            existing.setEmail(inputData.getEmail());
            existing.setJobPosition(inputData.getJobPosition());
            existing.setDepartment(inputData.getDepartment());
            existing.setSalary(inputData.getSalary());
            existing.setPhone(inputData.getPhone());
            existing.setAddress(inputData.getAddress());
            existing.setProfilePicture(inputData.getProfilePicture());
            if (inputData.getDocuments() != null) {
                existing.setDocuments(inputData.getDocuments());
            }
        } else if (userRole == Role.EMPLOYEE) {
            // Normal EMPLOYEE can update ONLY: phone, address, profilePicture
            // Salary, fullName, email, employeeId, jobPosition, department, documents MUST NOT be edited
            existing.setPhone(inputData.getPhone());
            existing.setAddress(inputData.getAddress());
            if (inputData.getProfilePicture() != null && !inputData.getProfilePicture().trim().isEmpty()) {
                existing.setProfilePicture(inputData.getProfilePicture());
            }
        } else {
            throw new SecurityException("Unauthorized role for employee profile update");
        }

        return employeeRepository.save(existing);
    }

    // Overload for SecurityGroup compatibility
    public Employee updateEmployee(Long id, Employee inputData, SecurityGroup userRole) {
        Role mappedRole = (userRole == SecurityGroup.HR_ADMIN) ? Role.HR_ADMIN : Role.EMPLOYEE;
        return updateEmployee(id, inputData, mappedRole);
    }

    public boolean deleteEmployee(Long id) {
        return employeeRepository.deleteById(id);
    }

    private void validateRequiredFields(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee object cannot be null");
        }
        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Employee ID is required");
        }
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Full Name is required");
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Email is required");
        }
        if (!EMAIL_PATTERN.matcher(employee.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Validation Error: Invalid email format '" + employee.getEmail() + "'");
        }
    }
}
