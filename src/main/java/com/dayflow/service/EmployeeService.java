package com.dayflow.service;

import com.dayflow.model.Employee;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service for Employee CRUD operations with role-based field protection.
 */
@Service
@Transactional
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

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with Employee ID: " + employeeId));
    }

    @Transactional(readOnly = true)
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

    /**
     * Deletes the employee with the given ID.
     * @throws IllegalArgumentException if no employee found with that ID.
     */
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found with ID: " + id);
        }
        employeeRepository.deleteById(id);
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
