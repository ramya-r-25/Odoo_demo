package com.dayflow.menu;

import com.dayflow.model.DayflowEmployee;
import com.dayflow.security.SecurityGroup;
import com.dayflow.view.EmployeeFormView;
import com.dayflow.view.EmployeeListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeProfileMenu {
    private final List<DayflowEmployee> employeeStore;
    private final EmployeeFormView formView;
    private final EmployeeListView listView;

    public EmployeeProfileMenu() {
        this.employeeStore = new ArrayList<>();
        this.formView = new EmployeeFormView();
        this.listView = new EmployeeListView();
        seedInitialData();
    }

    private void seedInitialData() {
        DayflowEmployee emp1 = new DayflowEmployee(
                "EMP-001",
                "Alex Morgan",
                "alex.morgan@dayflow.com",
                "+1-555-0192",
                "123 Tech Blvd, Suite 400",
                "Software Engineer",
                "Engineering",
                "Full-time Fixed",
                "avatar_alex.png",
                Arrays.asList("resume.pdf", "contract.pdf")
        );
        DayflowEmployee emp2 = new DayflowEmployee(
                "EMP-002",
                "Sarah Jenkins",
                "sarah.jenkins@dayflow.com",
                "+1-555-0193",
                "456 HR Way, Floor 2",
                "HR Specialist",
                "Human Resources",
                "Full-time Fixed",
                "avatar_sarah.png",
                Arrays.asList("id_proof.pdf")
        );
        employeeStore.add(emp1);
        employeeStore.add(emp2);
    }

    public void displayMenu(SecurityGroup userGroup) {
        System.out.println("\n--- DAYFLOW HRMS -> EMPLOYEE PROFILE MENU ---");
        System.out.println("Current Access Group: " + userGroup.getName() + " (" + userGroup.getDescription() + ")");
        System.out.println("1. List All Employee Profiles");
        System.out.println("2. View Sample Employee Form");
        System.out.println("----------------------------------------------");

        // Display views
        listView.renderListView(employeeStore);
        formView.renderFormView(employeeStore.get(0));
    }

    public List<DayflowEmployee> getEmployees() {
        return employeeStore;
    }
}
