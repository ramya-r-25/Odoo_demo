package com.dayflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "payroll",
       uniqueConstraints = @UniqueConstraint(
           columnNames = "employee_id",
           name = "uk_payroll_employee"
       ))
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull(message = "Salary structure is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_structure_id", nullable = false)
    private SalaryStructure salaryStructure;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public Payroll() {}

    public Payroll(Employee employee, SalaryStructure salaryStructure) {
        this.employee = employee;
        this.salaryStructure = salaryStructure;
    }

    // ----------------------------------------------------------------
    // Convenience getters (delegated to salary structure)
    // ----------------------------------------------------------------
    @Transient
    public Double getBasicSalary() {
        return (salaryStructure != null) ? salaryStructure.getBasicSalary() : 0.0;
    }

    @Transient
    public Double getGrossSalary() {
        return (salaryStructure != null) ? salaryStructure.getGrossSalary() : 0.0;
    }

    @Transient
    public Double getNetSalary() {
        return (salaryStructure != null) ? salaryStructure.getNetSalary() : 0.0;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public SalaryStructure getSalaryStructure() { return salaryStructure; }
    public void setSalaryStructure(SalaryStructure salaryStructure) { this.salaryStructure = salaryStructure; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
