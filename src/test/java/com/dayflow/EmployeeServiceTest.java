package com.dayflow;

import com.dayflow.controller.EmployeeController;
import com.dayflow.model.Employee;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;

import java.util.Arrays;
import java.util.List;

public class EmployeeServiceTest {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("     RUNNING DAYFLOW USER-EMPLOYEE & RBAC INTEGRATION TEST SUITE           ");
        System.out.println("==========================================================================");

        EmployeeRepository repository = new EmployeeRepository();
        EmployeeService service = new EmployeeService(repository);
        EmployeeController controller = new EmployeeController(service);

        int passed = 0;
        int failed = 0;

        // Seed Users
        User alexUser = new User(1L, "alex.morgan", "alex@dayflow.com", "pass123", Role.EMPLOYEE, "EMP-101");
        User sarahUser = new User(2L, "sarah.connor", "sarah@dayflow.com", "pass123", Role.EMPLOYEE, "EMP-102");
        User adminUser = new User(3L, "hr.admin", "admin@dayflow.com", "admin123", Role.HR_ADMIN, "EMP-999");

        // Test 1: Employee Creation & Email Validation
        Employee alexEmp = null;
        try {
            Employee emp1 = new Employee(
                    null, "EMP-101", "Alex Morgan", "alex@dayflow.com", "+1-555-0101",
                    "100 Tech Lane", "Software Engineer", "Engineering", 90000.0,
                    "alex.png", Arrays.asList("resume.pdf")
            );
            alexEmp = service.createEmployee(emp1);
            assert alexEmp.getId() != null : "ID should be generated";
            assert "EMP-101".equals(alexEmp.getEmployeeId()) : "Employee ID mismatch";
            System.out.println("[PASS] Test 1: Employee creation with valid email & required fields");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 1: " + e.getMessage());
            failed++;
        }

        // Test 2: Validation Rejects Invalid Email Format
        try {
            Employee invalidEmp = new Employee(null, "EMP-999", "John Doe", "invalid-email-format", null, null, null, null, null, null, null);
            service.createEmployee(invalidEmp);
            System.err.println("[FAIL] Test 2: Should fail when email format is invalid");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 2: Validation rejected invalid email format as expected");
            passed++;
        }

        // Test 3: Authenticated EMPLOYEE viewing own profile via User Principal (getMyProfile)
        try {
            Employee emp2 = new Employee(
                    null, "EMP-102", "Sarah Connor", "sarah@dayflow.com", "+1-555-0102",
                    "200 HR St", "HR Specialist", "Human Resources", 75000.0,
                    "sarah.png", Arrays.asList("id.pdf")
            );
            service.createEmployee(emp2);

            Employee myProfile = controller.getMyProfile(alexUser);
            assert "Alex Morgan".equals(myProfile.getFullName()) : "Profile name mismatch";
            System.out.println("[PASS] Test 3: Authenticated EMPLOYEE viewing own profile via User Principal");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 3: " + e.getMessage());
            failed++;
        }

        // Test 4: EMPLOYEE blocked from viewing another profile via direct URL manipulation
        try {
            Employee sarahEmp = service.getEmployeeByEmployeeId("EMP-102");
            // Alex attempts to access Sarah's profile ID in URL
            controller.getEmployeeProfile(sarahEmp.getId(), alexUser);
            System.err.println("[FAIL] Test 4: Should deny EMPLOYEE viewing another profile in URL");
            failed++;
        } catch (SecurityException e) {
            System.out.println("[PASS] Test 4: Denied EMPLOYEE viewing another profile via direct URL manipulation");
            passed++;
        }

        // Test 5: EMPLOYEE restricted field editing (Phone, Address, Profile Picture ONLY)
        try {
            Employee original = service.getEmployeeByEmployeeId("EMP-101");
            Double originalSalary = original.getSalary();
            String originalDepartment = original.getDepartment();

            // Attempt to update salary, department, AND phone as EMPLOYEE
            Employee editAttempt = new Employee(
                    original.getId(), "EMP-HACKED", "Hacked Name", "hacked@dayflow.com",
                    "+1-555-NEW-PHONE", "555 New Address", "Hacked Position", "Hacked Dept",
                    999999.0, "new_pic.png", null
            );

            Employee result = controller.updateEmployee(original.getId(), editAttempt, alexUser);

            // Verify allowed fields were updated
            assert "+1-555-NEW-PHONE".equals(result.getPhone()) : "Phone should be updated";
            assert "555 New Address".equals(result.getAddress()) : "Address should be updated";
            assert "new_pic.png".equals(result.getProfilePicture()) : "Profile picture should be updated";

            // Verify restricted fields were NOT changed server-side
            assert originalSalary.equals(result.getSalary()) : "Salary MUST NOT be edited by EMPLOYEE";
            assert originalDepartment.equals(result.getDepartment()) : "Department MUST NOT be edited by EMPLOYEE";
            assert "Alex Morgan".equals(result.getFullName()) : "Name MUST NOT be edited by EMPLOYEE";
            assert "EMP-101".equals(result.getEmployeeId()) : "Employee ID MUST NOT be edited by EMPLOYEE";

            System.out.println("[PASS] Test 5: EMPLOYEE role field restrictions enforced server-side (Salary protected)");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 5: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        // Test 6: EMPLOYEE unauthorized profile edit attempt on another user throws SecurityException
        try {
            Employee sarahEmp = service.getEmployeeByEmployeeId("EMP-102");
            Employee editAttempt = new Employee();
            editAttempt.setPhone("+1-555-ILLEGAL");
            controller.updateEmployee(sarahEmp.getId(), editAttempt, alexUser);
            System.err.println("[FAIL] Test 6: Should deny EMPLOYEE editing another profile");
            failed++;
        } catch (SecurityException e) {
            System.out.println("[PASS] Test 6: Denied EMPLOYEE editing another profile server-side");
            passed++;
        }

        // Test 7: HR_ADMIN can view and edit ALL employee fields (including salary and job details)
        try {
            Employee original = service.getEmployeeByEmployeeId("EMP-101");
            Employee hrEdit = new Employee(
                    original.getId(), "EMP-101", "Alex Morgan", "alex@dayflow.com",
                    original.getPhone(), original.getAddress(), "Lead Principal Engineer", "Core Engineering",
                    135000.0, original.getProfilePicture(), original.getDocuments()
            );

            Employee result = controller.updateEmployee(original.getId(), hrEdit, adminUser);
            assert Double.valueOf(135000.0).equals(result.getSalary()) : "HR_ADMIN should be able to update salary";
            assert "Lead Principal Engineer".equals(result.getJobPosition()) : "HR_ADMIN should be able to update job position";
            System.out.println("[PASS] Test 7: HR_ADMIN can edit all fields (Salary updated to $135,000.00)");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 7: " + e.getMessage());
            failed++;
        }

        // Test 8: HR_ADMIN Employee List Access
        try {
            List<Employee> list = controller.getEmployeeList(adminUser);
            assert list.size() >= 2 : "Employee list should return all employees";
            System.out.println("[PASS] Test 8: HR_ADMIN can access employee directory (" + list.size() + " employees found)");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 8: " + e.getMessage());
            failed++;
        }

        // Test 9: EMPLOYEE cannot access employee directory list
        try {
            controller.getEmployeeList(alexUser);
            System.err.println("[FAIL] Test 9: Should deny EMPLOYEE list access");
            failed++;
        } catch (SecurityException e) {
            System.out.println("[PASS] Test 9: Denied EMPLOYEE access to employee list");
            passed++;
        }

        System.out.println("==========================================================================");
        System.out.println("TEST RESULTS: Passed: " + passed + " | Failed: " + failed);
        System.out.println("==========================================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
