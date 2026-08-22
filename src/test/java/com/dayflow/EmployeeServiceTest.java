package com.dayflow;

import com.dayflow.controller.EmployeeController;
import com.dayflow.model.Employee;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.security.SecurityGroup;
import com.dayflow.service.EmployeeService;

import java.util.Arrays;
import java.util.List;

public class EmployeeServiceTest {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("           RUNNING DAYFLOW EMPLOYEE PROFILE UNIT & INTEGRATION TESTS      ");
        System.out.println("==========================================================================");

        EmployeeRepository repository = new EmployeeRepository();
        EmployeeService service = new EmployeeService(repository);
        EmployeeController controller = new EmployeeController(service);

        int passed = 0;
        int failed = 0;

        // Test 1: Create Employee with Required Fields
        try {
            Employee emp1 = new Employee(
                    null, "EMP-101", "Alex Morgan", "alex@dayflow.com", "+1-555-0101",
                    "100 Tech Lane", "Software Engineer", "Engineering", 90000.0,
                    "alex.png", Arrays.asList("resume.pdf")
            );
            Employee saved1 = service.createEmployee(emp1);
            assert saved1.getId() != null : "ID should be generated";
            assert "EMP-101".equals(saved1.getEmployeeId()) : "Employee ID mismatch";
            System.out.println("[PASS] Test 1: Employee creation with required fields");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 1: " + e.getMessage());
            failed++;
        }

        // Test 2: Validation of Required Fields (employeeId, fullName, email)
        try {
            Employee invalidEmp = new Employee(null, "", "John Doe", "john@dayflow.com", null, null, null, null, null, null, null);
            service.createEmployee(invalidEmp);
            System.err.println("[FAIL] Test 2: Should fail when employeeId is empty");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Test 2: Validation rejected empty employeeId as expected");
            passed++;
        }

        // Test 3: Normal EMPLOYEE can view own profile
        try {
            Employee emp2 = new Employee(
                    null, "EMP-102", "Sarah Connor", "sarah@dayflow.com", "+1-555-0102",
                    "200 HR St", "HR Specialist", "Human Resources", 75000.0,
                    "sarah.png", Arrays.asList("id.pdf")
            );
            Employee saved2 = service.createEmployee(emp2);
            Employee viewed = controller.getEmployeeProfile(saved2.getId(), SecurityGroup.EMPLOYEE, saved2.getId());
            assert "Sarah Connor".equals(viewed.getFullName()) : "Profile name mismatch";
            System.out.println("[PASS] Test 3: EMPLOYEE can view own profile");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 3: " + e.getMessage());
            failed++;
        }

        // Test 4: EMPLOYEE cannot view another employee's profile
        try {
            controller.getEmployeeProfile(1001L, SecurityGroup.EMPLOYEE, 1002L);
            System.err.println("[FAIL] Test 4: Should deny EMPLOYEE viewing another profile");
            failed++;
        } catch (SecurityException e) {
            System.out.println("[PASS] Test 4: Denied EMPLOYEE viewing another profile");
            passed++;
        }

        // Test 5: EMPLOYEE editing field restrictions (phone, address, profilePicture ONLY)
        try {
            Employee original = service.getEmployeeByEmployeeId("EMP-101");
            Double originalSalary = original.getSalary();
            String originalDepartment = original.getDepartment();

            // Attempt to update salary, department, AND phone as EMPLOYEE
            Employee editAttempt = new Employee(
                    original.getId(), "EMP-HACKED", "Hacked Name", "hacked@email.com",
                    "+1-555-NEW-PHONE", "555 New Address", "Hacked Position", "Hacked Dept",
                    999999.0, "new_pic.png", null
            );

            Employee result = service.updateEmployee(original.getId(), editAttempt, SecurityGroup.EMPLOYEE);

            // Verify allowed fields were updated
            assert "+1-555-NEW-PHONE".equals(result.getPhone()) : "Phone should be updated";
            assert "555 New Address".equals(result.getAddress()) : "Address should be updated";
            assert "new_pic.png".equals(result.getProfilePicture()) : "Profile picture should be updated";

            // Verify restricted fields were NOT changed
            assert originalSalary.equals(result.getSalary()) : "Salary MUST NOT be edited by EMPLOYEE";
            assert originalDepartment.equals(result.getDepartment()) : "Department MUST NOT be edited by EMPLOYEE";
            assert "Alex Morgan".equals(result.getFullName()) : "Name MUST NOT be edited by EMPLOYEE";
            assert "EMP-101".equals(result.getEmployeeId()) : "Employee ID MUST NOT be edited by EMPLOYEE";

            System.out.println("[PASS] Test 5: EMPLOYEE role field restrictions enforced (Salary & Admin fields protected)");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 5: " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        // Test 6: HR_ADMIN can view and edit ALL employee fields (including salary)
        try {
            Employee original = service.getEmployeeByEmployeeId("EMP-101");
            Employee hrEdit = new Employee(
                    original.getId(), "EMP-101", "Alex Morgan", "alex@dayflow.com",
                    original.getPhone(), original.getAddress(), "Lead Engineer", "Core Engineering",
                    120000.0, original.getProfilePicture(), original.getDocuments()
            );

            Employee result = service.updateEmployee(original.getId(), hrEdit, SecurityGroup.HR_ADMIN);
            assert Double.valueOf(120000.0).equals(result.getSalary()) : "HR_ADMIN should be able to update salary";
            assert "Lead Engineer".equals(result.getJobPosition()) : "HR_ADMIN should be able to update job position";
            System.out.println("[PASS] Test 6: HR_ADMIN can edit all fields (Salary updated to $120,000.00)");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 6: " + e.getMessage());
            failed++;
        }

        // Test 7: HR_ADMIN Employee List Access
        try {
            List<Employee> list = controller.getEmployeeList(SecurityGroup.HR_ADMIN);
            assert list.size() >= 2 : "Employee list should return all employees";
            System.out.println("[PASS] Test 7: HR_ADMIN can access employee list (" + list.size() + " employees found)");
            passed++;
        } catch (Exception e) {
            System.err.println("[FAIL] Test 7: " + e.getMessage());
            failed++;
        }

        // Test 8: EMPLOYEE cannot access employee list
        try {
            controller.getEmployeeList(SecurityGroup.EMPLOYEE);
            System.err.println("[FAIL] Test 8: Should deny EMPLOYEE list access");
            failed++;
        } catch (SecurityException e) {
            System.out.println("[PASS] Test 8: Denied EMPLOYEE access to employee list");
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
