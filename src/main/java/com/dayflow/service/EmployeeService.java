package com.dayflow.service;

import com.dayflow.model.Employee;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with Email: " + email));
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

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee updateEmployee(Long id, Employee inputData, SecurityGroup userRole) {
        Employee existing = getEmployeeById(id);

        if (userRole == SecurityGroup.HR_ADMIN) {
            // HR_ADMIN can update all fields
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
        } else if (userRole == SecurityGroup.EMPLOYEE) {
            // Normal EMPLOYEE can update ONLY: phone, address, profilePicture
            // Salary, fullName, email, employeeId, jobPosition, department, documents are IMMUTABLE for EMPLOYEE
            existing.setPhone(inputData.getPhone());
            existing.setAddress(inputData.getAddress());
            if (inputData.getProfilePicture() != null && !inputData.getProfilePicture().trim().isEmpty()) {
                existing.setProfilePicture(inputData.getProfilePicture());
            }
        } else {
            throw new SecurityException("Unauthorized role for employee update");
        }

        return employeeRepository.save(existing);
    }

    public boolean deleteEmployee(Long id) {
        return employeeRepository.deleteById(id);
    }

    private void validateRequiredFields(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee object cannot be null");
        }
        if (employee.getEmployeeId() == null || employee.getEmployeeId().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: employeeId is required");
        }
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Name is required");
        }
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Validation Error: Email is required");
        }
        if (!EMAIL_PATTERN.matcher(employee.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Validation Error: Invalid email format '" + employee.getEmail() + "'");
        }
    }
}
