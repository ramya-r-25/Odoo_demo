package com.dayflow;

import com.dayflow.controller.EmployeeController;
import com.dayflow.model.Employee;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.service.EmployeeService;

import java.util.Arrays;
import java.util.List;

public class DayflowApplication {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   STARTING DAYFLOW HRMS - USER-EMPLOYEE RBAC APPLICATION       ");
        System.out.println("=================================================================");

        // Initialize Repository, Service, and Controller
        EmployeeRepository repository = new EmployeeRepository();
        EmployeeService service = new EmployeeService(repository);
        EmployeeController controller = new EmployeeController(service);

        // Seed Users
        User alexUser = new User(1L, "alex.morgan", "alex.morgan@dayflow.com", "pass123", Role.EMPLOYEE, "EMP-001");
        User sarahUser = new User(2L, "sarah.jenkins", "sarah.jenkins@dayflow.com", "pass123", Role.EMPLOYEE, "EMP-002");
        User adminUser = new User(3L, "hr.admin", "admin@dayflow.com", "admin123", Role.HR_ADMIN, "EMP-000");

        // Seed Sample Employee Profiles
        System.out.println("\n[1] Seeding Initial Employee Profiles...");
        Employee emp1 = service.createEmployee(new Employee(
                null, "EMP-001", "Alex Morgan", "alex.morgan@dayflow.com",
                "+1-555-0192", "123 Tech Blvd, Suite 400", "Software Engineer",
                "Engineering", 85000.0, "avatar_alex.png",
                Arrays.asList("resume.pdf", "contract.pdf")
        ));

        Employee emp2 = service.createEmployee(new Employee(
                null, "EMP-002", "Sarah Jenkins", "sarah.jenkins@dayflow.com",
                "+1-555-0193", "456 HR Way, Floor 2", "HR Specialist",
                "Human Resources", 85000.0, "avatar_sarah.png",
                Arrays.asList("id_proof.pdf")
        ));

        System.out.println("Successfully seeded " + repository.findAll().size() + " employee records.");

        // Demonstrate HR_ADMIN viewing employee directory
        System.out.println("\n[2] HR_ADMIN Accessing Employee Directory List:");
        List<Employee> allEmployees = controller.getEmployeeList(adminUser);
        for (Employee e : allEmployees) {
            System.out.println(" - " + e.getEmployeeId() + " | " + e.getFullName() + " | " + e.getJobPosition() + " | Salary: $" + e.getSalary());
        }

        // Demonstrate EMPLOYEE viewing own profile via User Principal (getMyProfile)
        System.out.println("\n[3] EMPLOYEE (Alex Morgan) Accessing Own Profile via Authenticated User Principal:");
        Employee alexProfile = controller.getMyProfile(alexUser);
        System.out.println(" Profile Name: " + alexProfile.getFullName());
        System.out.println(" Email       : " + alexProfile.getEmail());

        System.out.println("\n[SUCCESS] Dayflow User-Employee RBAC application executed cleanly without errors!");
    }
}
