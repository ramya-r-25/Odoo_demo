package com.dayflow.attendance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee code is required")
    @Column(unique = true, nullable = false)
    private String employeeCode;

    @NotBlank(message = "Employee name is required")
    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column
    private String department;

    public Employee() {
    }

    public Employee(Long id, String employeeCode, String name, String email, String department) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public Employee(String employeeCode, String name, String email, String department) {
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(employeeCode, employee.employeeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeCode);
    }
}
