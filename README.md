# Dayflow HRMS

A full-featured **Human Resource Management System** built with **Spring Boot**, providing Leave Management, Payroll/Salary management, Employee Profiles, Attendance, Role-Based Access Control, and a responsive Thymeleaf UI.

---

## Project Overview

Dayflow HRMS is a modular, Spring Boot–based HRMS designed for small-to-medium organizations. The system supports two roles: **EMPLOYEE** and **HR_ADMIN**, each with tailored dashboards and strictly enforced server-side security.

---

## Features

| Module | EMPLOYEE | HR_ADMIN |
|---|---|---|
| Dashboard | ✅ Role-aware cards | ✅ Full HR dashboard |
| Employee Profile | ✅ View & edit own (phone/address) | ✅ View & edit all |
| Attendance | ✅ View own | ✅ View all |
| Leave Management | ✅ Apply, view own, check status & HR comment | ✅ View all, approve/reject, add comments |
| Salary / Payroll | ✅ View own (read-only) | ✅ View all, update salary |

---

## User Roles

### EMPLOYEE
- Login and access personal dashboard
- View and edit limited own profile fields (phone, address, profile picture)
- View own attendance records
- Apply for leave (PAID / SICK / UNPAID)
- View own leave history with status and HR comments
- View own salary (read-only)

### HR_ADMIN
- Login and access full HR dashboard
- Manage all employee records
- View all attendance records
- View all leave requests
- Approve or reject leave requests with HR comments
- View and update all employee salaries

---

## Leave Management

**Leave Types:** `PAID`, `SICK`, `UNPAID`

**Leave Status:** `PENDING` → `APPROVED` or `REJECTED`

**Status Transitions:**
- Only `PENDING → APPROVED` or `PENDING → REJECTED` are allowed
- `APPROVED → REJECTED` and `REJECTED → APPROVED` are blocked server-side

**Validations:**
- Leave type required
- Start date required
- End date required
- End date cannot be before start date

**Security:**
- Employees can only view their own leave requests
- Only HR_ADMIN can approve or reject leaves
- URL tampering is blocked server-side

---

## Payroll / Salary

**Employee:**
- Can view own salary (read-only display)
- Cannot update salary via any method (blocked server-side for direct POST/PUT too)

**HR_ADMIN:**
- Can view all employees' salaries in a table
- Can update salary inline per employee

**SalaryStructure model** (for detailed payroll tracking):
- Earnings: Basic, HRA (House Rent Allowance), DA (Dearness Allowance), Other Allowance
- Deductions: PF (Provident Fund), ESI, Tax, Other
- Computed: Gross Salary = Basic + HRA + DA + OtherAllowance
- Computed: Net Salary = Gross − Total Deductions

---

## Attendance

Managed by Member 3. Integrated into the dashboard for both roles.

---

## Employee Profile

Managed by Member 2. Integration preserved:
- EMPLOYEE can update: phone, address, profile picture only
- HR_ADMIN can update all fields including salary
- Server-side role enforcement prevents tampering

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Web / MVC | Spring Web, Thymeleaf |
| Security | Spring Security 6 |
| Database | H2 In-Memory (JPA / Hibernate) |
| Validation | Jakarta Bean Validation |
| Build | Apache Maven 3.x |
| Testing | JUnit 5, Spring Boot Test, Spring Security Test |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/dayflow/
│   │   ├── DayflowApplication.java
│   │   ├── controller/
│   │   │   ├── DashboardController.java       # Dashboard routing
│   │   │   ├── EmployeeController.java         # Employee CRUD (Member 2)
│   │   │   ├── PayrollController.java          # REST payroll (legacy)
│   │   │   └── SalaryStructureController.java  # REST salary structure
│   │   ├── leave/
│   │   │   ├── controller/
│   │   │   │   ├── LeaveController.java        # Leave UI controller
│   │   │   │   └── PayrollController.java      # Salary UI controller
│   │   │   ├── dto/
│   │   │   │   └── LeaveRequestDTO.java
│   │   │   ├── model/
│   │   │   │   ├── LeaveRequest.java
│   │   │   │   ├── LeaveStatus.java
│   │   │   │   └── LeaveType.java
│   │   │   ├── repository/
│   │   │   │   └── LeaveRequestRepository.java
│   │   │   └── service/
│   │   │       ├── LeaveService.java
│   │   │       └── LeaveServiceImpl.java
│   │   ├── model/
│   │   │   ├── Employee.java
│   │   │   ├── Payroll.java
│   │   │   └── SalaryStructure.java
│   │   ├── repository/
│   │   │   ├── EmployeeRepository.java
│   │   │   ├── PayrollRepository.java
│   │   │   └── SalaryStructureRepository.java
│   │   ├── security/
│   │   │   ├── SecurityConfig.java             # Spring Security configuration
│   │   │   └── SecurityGroup.java              # Role enum
│   │   └── service/
│   │       ├── EmployeeService.java
│   │       ├── PayrollService.java
│   │       └── SalaryStructureService.java
│   └── resources/
│       ├── application.properties
│       └── templates/
│           ├── dashboard.html
│           ├── employees/
│           │   ├── form.html
│           │   ├── list.html
│           │   └── profile.html
│           ├── leave/
│           │   ├── my-leaves.html              # Employee leave view + form
│           │   └── admin-leaves.html           # HR leave approval view
│           └── payroll/
│               └── salary.html                 # Employee & HR salary view
└── test/
    └── java/com/dayflow/
        ├── EmployeeServiceTest.java            # Standalone security test (9 tests)
        └── leave/
            └── LeaveAndPayrollTest.java        # Integration tests (11 tests)
```

---

## Database Information

Uses **H2 In-Memory Database** (auto-reset on restart).

| Table | Description |
|---|---|
| `employees` | Employee records with salary field |
| `employee_documents` | Employee uploaded documents (collection) |
| `leave_requests` | All employee leave applications |
| `payroll` | Employee-to-salary-structure mapping |
| `salary_structures` | Named payroll structures with earnings/deductions |

**H2 Console:** `http://localhost:8080/h2-console`  
**JDBC URL:** `jdbc:h2:mem:dayflowdb`  
**Username:** `sa` | **Password:** *(empty)*

---

## Setup Instructions

### Prerequisites
- Java 17+
- Apache Maven 3.6+
- (Optional) IDE: IntelliJ IDEA / VS Code

### Clone & Build

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application starts at: **http://localhost:8080**

---

## How to Run

```bash
# Run all tests
mvn test

# Run standalone employee security tests
mvn exec:java "-Dexec.mainClass=com.dayflow.EmployeeServiceTest" "-Dexec.classpathScope=test"

# Start the application
mvn spring-boot:run
```

---

## Demo / Test Credentials

| Role | Email | Password |
|---|---|---|
| EMPLOYEE | `alex@dayflow.com` | `password` |
| EMPLOYEE | `sarah@dayflow.com` | `password` |
| HR_ADMIN | `admin@dayflow.com` | `password` |

> **Note:** Users are defined in `SecurityConfig.java` using in-memory authentication. Employee records must be created by HR_ADMIN after login.

---

## Complete HRMS Workflow

### EMPLOYEE Flow
```
Login (alex@dayflow.com / password)
  → Dashboard
  → My Profile (view/edit phone, address, profile picture)
  → Attendance (view check-in/check-out records)
  → Leave Requests
      → Apply Leave (select type, date range, remarks)
      → View status (PENDING / APPROVED / REJECTED)
      → View HR comments
  → My Salary (read-only)
  → Logout
```

### HR_ADMIN Flow
```
Login (admin@dayflow.com / password)
  → Dashboard
  → Employee Management (view all employees)
  → Attendance Management (view all attendance)
  → Leave Approvals
      → View all leave requests
      → Add HR comment
      → Approve or Reject
  → Payroll / Salary
      → View all employee salaries
      → Update salary per employee
  → Logout
```

### Post-Approval Employee Check
```
EMPLOYEE Login
  → Leave Requests
  → Verify updated status (APPROVED / REJECTED)
  → Verify HR comment is visible
```

---

## Security Implementation

- **Spring Security 6** with form-based login/logout
- Role-based URL access: `/leave/approvals/**` requires `ROLE_HR_ADMIN`
- **Server-side enforcement** on all sensitive operations:
  - Employees cannot view another employee's leave or salary
  - Employees cannot approve/reject leave
  - Employees cannot update salary (blocked via controller + service)
  - URL ID tampering is detected and rejected
- CSRF disabled for API compatibility; H2 console frame options disabled for dev

---

## Maven Commands

```bash
mvn clean compile          # Clean and compile
mvn test                   # Run all JUnit tests
mvn spring-boot:run        # Start the application
mvn clean package          # Package into JAR
```
