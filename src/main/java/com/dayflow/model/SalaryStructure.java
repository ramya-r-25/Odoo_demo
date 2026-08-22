package com.dayflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "salary_structures")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Structure name is required")
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Double basicSalary = 0.0;

    private Double hra = 0.0;              // House Rent Allowance
    private Double da = 0.0;               // Dearness Allowance
    private Double otherAllowance = 0.0;

    private Double pfDeduction = 0.0;      // Provident Fund
    private Double esiDeduction = 0.0;     // ESI
    private Double taxDeduction = 0.0;
    private Double otherDeduction = 0.0;

    private boolean active = true;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public SalaryStructure() {}

    public SalaryStructure(String name, Double basicSalary) {
        this.name = name;
        this.basicSalary = basicSalary;
    }

    // ----------------------------------------------------------------
    // Computed helpers (mirror Odoo computed fields)
    // ----------------------------------------------------------------
    @Transient
    public Double getGrossSalary() {
        return basicSalary + hra + da + otherAllowance;
    }

    @Transient
    public Double getTotalDeductions() {
        return pfDeduction + esiDeduction + taxDeduction + otherDeduction;
    }

    @Transient
    public Double getNetSalary() {
        return getGrossSalary() - getTotalDeductions();
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }

    public Double getHra() { return hra; }
    public void setHra(Double hra) { this.hra = hra; }

    public Double getDa() { return da; }
    public void setDa(Double da) { this.da = da; }

    public Double getOtherAllowance() { return otherAllowance; }
    public void setOtherAllowance(Double otherAllowance) { this.otherAllowance = otherAllowance; }

    public Double getPfDeduction() { return pfDeduction; }
    public void setPfDeduction(Double pfDeduction) { this.pfDeduction = pfDeduction; }

    public Double getEsiDeduction() { return esiDeduction; }
    public void setEsiDeduction(Double esiDeduction) { this.esiDeduction = esiDeduction; }

    public Double getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(Double taxDeduction) { this.taxDeduction = taxDeduction; }

    public Double getOtherDeduction() { return otherDeduction; }
    public void setOtherDeduction(Double otherDeduction) { this.otherDeduction = otherDeduction; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
