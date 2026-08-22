package com.dayflow.model;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private Long id;
    private String employeeId;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String jobPosition;
    private String department;
    private Double salary;
    private String profilePicture;
    private List<String> documents;

    public Employee() {
        this.documents = new ArrayList<>();
    }

    public Employee(Long id, String employeeId, String fullName, String email, String phone, 
                    String address, String jobPosition, String department, 
                    Double salary, String profilePicture, List<String> documents) {
        this.id = id;
        this.employeeId = employeeId;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeId='" + employeeId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", jobPosition='" + jobPosition + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", profilePicture='" + profilePicture + '\'' +
                ", documents=" + documents +
                '}';
    }
}
