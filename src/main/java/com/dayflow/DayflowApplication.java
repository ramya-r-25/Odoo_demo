package com.dayflow;

import com.dayflow.controller.EmployeeController;
import com.dayflow.model.Employee;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;

import java.util.Arrays;
import java.util.List;

public class DayflowApplication {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   STARTING DAYFLOW HRMS - STAGE 3 INTEGRATED APPLICATION       ");
        System.out.println("=================================================================");

        // Initialize Repository, Service, and Controller
        EmployeeRepository repository = new EmployeeRepository();
        EmployeeService service = new EmployeeService(repository);
        EmployeeController controller = new EmployeeController(service);

        // Seed Sample Employee Profiles
        System.out.println("\n[1] Seeding Initial Employee Data...");
        Employee emp1 = service.createEmployee(new Employee(
                null, "EMP-001", "Alex Morgan", "alex.morgan@dayflow.com",
                "+1-555-0192", "123 Tech Blvd, Suite 400", "Software Engineer",
                "Engineering", 85000.0, "avatar_alex.png",
                Arrays.asList("resume.pdf", "contract.pdf")
        ));

        Employee emp2 = service.createEmployee(new Employee(
                null, "EMP-002", "Sarah Jenkins", "sarah.jenkins@dayflow.com",
                "+1-555-0193", "456 HR Way, Floor 2", "HR Specialist",
                "Human Resources", 72000.0, "avatar_sarah.png",
                Arrays.asList("id_proof.pdf")
        ));

        System.out.println("Successfully seeded " + repository.findAll().size() + " employee records.");

        // Demonstrate HR_ADMIN viewing employee list
        System.out.println("\n[2] HR_ADMIN Accessing Employee Directory List:");
        List<Employee> allEmployees = controller.getEmployeeList(SecurityGroup.HR_ADMIN);
        for (Employee e : allEmployees) {
            System.out.println(" - " + e.getEmployeeId() + " | " + e.getFullName() + " | " + e.getJobPosition() + " | Salary: $" + e.getSalary());
        }

        // Demonstrate EMPLOYEE viewing own profile
        System.out.println("\n[3] EMPLOYEE (Alex Morgan) Viewing Own Profile:");
        Employee alexProfile = controller.getEmployeeProfile(emp1.getId(), SecurityGroup.EMPLOYEE, emp1.getId());
        System.out.println(" Profile Name: " + alexProfile.getFullName());
        System.out.println(" Email       : " + alexProfile.getEmail());
        System.out.println(" Phone       : " + alexProfile.getPhone());
        System.out.println(" Address     : " + alexProfile.getAddress());

        // Demonstrate EMPLOYEE editing allowed fields (Phone, Address, Profile Picture)
        System.out.println("\n[4] EMPLOYEE (Alex Morgan) Updating Phone & Address:");
        Employee updateAttempt = new Employee();
        updateAttempt.setPhone("+1-555-9999");
        updateAttempt.setAddress("789 Innovation Way, Silicon Valley");
        updateAttempt.setSalary(999999.0); // Attempting to tamper salary!

        Employee updatedAlex = controller.updateEmployee(emp1.getId(), updateAttempt, SecurityGroup.EMPLOYEE, emp1.getId());
        System.out.println(" Updated Phone  : " + updatedAlex.getPhone());
        System.out.println(" Updated Address: " + updatedAlex.getAddress());
        System.out.println(" Preserved Salary (Tamper Blocked): $" + updatedAlex.getSalary());

        // Demonstrate HR_ADMIN updating all employee fields (including salary)
        System.out.println("\n[5] HR_ADMIN Updating Salary for Sarah Jenkins:");
        Employee sarahHrEdit = new Employee(
                emp2.getId(), emp2.getEmployeeId(), emp2.getFullName(), emp2.getEmail(),
                emp2.getPhone(), emp2.getAddress(), "Senior HR Specialist", emp2.getDepartment(),
                80000.0, emp2.getProfilePicture(), emp2.getDocuments()
        );
        Employee updatedSarah = controller.updateEmployee(emp2.getId(), sarahHrEdit, SecurityGroup.HR_ADMIN, null);
        System.out.println(" Updated Position: " + updatedSarah.getJobPosition());
        System.out.println(" Updated Salary  : $" + updatedSarah.getSalary());

        System.out.println("\n[SUCCESS] Dayflow Stage 3 application executed cleanly without errors!");
    }
}
