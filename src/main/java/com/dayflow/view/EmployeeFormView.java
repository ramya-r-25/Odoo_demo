package com.dayflow.view;

import com.dayflow.model.DayflowEmployee;

public class EmployeeFormView {

    public void renderFormView(DayflowEmployee employee) {
        System.out.println("=================================================");
        System.out.println("            DAYFLOW EMPLOYEE FORM VIEW           ");
        System.out.println("=================================================");
        if (employee == null) {
            System.out.println("[New Employee Form]");
            System.out.println("Fields: Employee ID, Name, Email, Phone, Address, Job Position, Department, Salary Structure, Profile Picture, Documents");
        } else {
            System.out.println("Employee ID      : " + employee.getEmployeeId());
            System.out.println("Name             : " + employee.getName());
            System.out.println("Email            : " + employee.getEmail());
            System.out.println("Phone            : " + employee.getPhone());
            System.out.println("Address          : " + employee.getAddress());
            System.out.println("Job Position     : " + employee.getJobPosition());
            System.out.println("Department       : " + employee.getDepartment());
            System.out.println("Salary Structure : " + employee.getSalaryStructure());
            System.out.println("Profile Picture  : " + (employee.getProfilePicture() != null ? "[Image Attached]" : "None"));
            System.out.println("Documents        : " + employee.getDocuments());
        }
        System.out.println("=================================================");
    }
}
