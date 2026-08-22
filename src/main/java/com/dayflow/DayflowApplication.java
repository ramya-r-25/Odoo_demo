package com.dayflow;

import com.dayflow.menu.EmployeeProfileMenu;
import com.dayflow.security.SecurityGroup;

public class DayflowApplication {

    public static void main(String[] args) {
        System.out.println("Starting Dayflow HRMS - Employee Profile Module...");
        
        try {
            EmployeeProfileMenu menu = new EmployeeProfileMenu();
            
            System.out.println("\nLoading module with HR/Admin Security Group:");
            menu.displayMenu(SecurityGroup.HR_ADMIN);

            System.out.println("\nLoading module with Employee Security Group:");
            menu.displayMenu(SecurityGroup.EMPLOYEE);

            System.out.println("\n[SUCCESS] Dayflow Employee Profile module loaded cleanly with zero errors!");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load Dayflow Employee Profile module: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
