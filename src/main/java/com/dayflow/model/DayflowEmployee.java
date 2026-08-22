package com.dayflow.model;

import java.util.ArrayList;
import java.util.List;

public class DayflowEmployee {
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String jobPosition;
    private String department;
    private String salaryStructure;
    private String profilePicture;
    private List<String> documents;

    public DayflowEmployee() {
        this.documents = new ArrayList<>();
    }

    public DayflowEmployee(String employeeId, String name, String email, String phone, 
                           String address, String jobPosition, String department, 
                           String salaryStructure, String profilePicture, List<String> documents) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.jobPosition = jobPosition;
        this.department = department;
        this.salaryStructure = salaryStructure;
        this.profilePicture = profilePicture;
        this.documents = (documents != null) ? documents : new ArrayList<>();
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
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

    public String getSalaryStructure() {
        return salaryStructure;
    }

    public void setSalaryStructure(String salaryStructure) {
        this.salaryStructure = salaryStructure;
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
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "DayflowEmployee{" +
                "employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", jobPosition='" + jobPosition + '\'' +
                ", department='" + department + '\'' +
                ", salaryStructure='" + salaryStructure + '\'' +
                ", profilePicture='" + (profilePicture != null ? "[Attached]" : "None") + '\'' +
                ", documents=" + documents +
                '}';
    }
}
