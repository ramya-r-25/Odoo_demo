package com.dayflow.view;

import com.dayflow.model.DayflowEmployee;
import java.util.List;

public class EmployeeListView {

    public void renderListView(List<DayflowEmployee> employees) {
        System.out.println("==========================================================================================");
        System.out.println("                                DAYFLOW EMPLOYEE LIST VIEW                                ");
        System.out.println("==========================================================================================");
        System.out.printf("%-12s | %-20s | %-25s | %-18s | %-15s\n", "ID", "Name", "Email", "Job Position", "Department");
        System.out.println("------------------------------------------------------------------------------------------");
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employee records found.");
        } else {
            for (DayflowEmployee emp : employees) {
                System.out.printf("%-12s | %-20s | %-25s | %-18s | %-15s\n",
                        emp.getEmployeeId(),
                        emp.getName(),
                        emp.getEmail(),
                        emp.getJobPosition(),
                        emp.getDepartment());
            }
        }
        System.out.println("==========================================================================================");
    }
}
