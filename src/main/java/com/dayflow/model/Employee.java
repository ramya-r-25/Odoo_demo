package com.dayflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String employeeCode;

    @Column
    private String employeeId;

    @Column(nullable = false)
    private String name;

    @Column
    private String fullName;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String address;

    @Column
    private String jobPosition;

    @Column
    private String department;

    @Column
    private Double salary;

    @Column
    private String profilePicture;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> documents = new ArrayList<>();

    public Employee() {
    }

    public Employee(Long id, String employeeCode, String name, String email, String department) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.employeeId = employeeCode;
        this.name = name;
        this.fullName = name;
        this.email = email;
        this.department = department;
    }

    public Employee(String employeeCode, String name, String email, String department) {
        this.employeeCode = employeeCode;
        this.employeeId = employeeCode;
        this.name = name;
        this.fullName = name;
        this.email = email;
        this.department = department;
    }

    public Employee(Long id, String employeeId, String fullName, String email, String phone, 
                    String address, String jobPosition, String department, 
                    Double salary, String profilePicture, List<String> documents) {
        this.id = id;
        this.employeeCode = employeeId;
        this.employeeId = employeeId;
        this.name = fullName;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.jobPosition = jobPosition;
        this.department = department;
        this.salary = salary;
        this.profilePicture = profilePicture;
        this.documents = (documents != null) ? new ArrayList<>(documents) : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode != null ? employeeCode : employeeId;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
        this.employeeId = employeeCode;
    }

    public String getEmployeeId() {
        return employeeId != null ? employeeId : employeeCode;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
        this.employeeCode = employeeId;
    }

    public String getName() {
        return name != null ? name : fullName;
    }

    public void setName(String name) {
        this.name = name;
        this.fullName = name;
    }

    public String getFullName() {
        return fullName != null ? fullName : name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.name = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = (documents != null) ? new ArrayList<>(documents) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(getEmployeeCode(), employee.getEmployeeCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getEmployeeCode());
    }
}
