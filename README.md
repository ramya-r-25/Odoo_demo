# Dayflow HRMS

Spring Boot application providing base **Leave** and **Payroll** structures for the Dayflow HRMS project.

## Tech Stack

- Java 17+
- Spring Boot 3.2.5
- Spring Data JPA
- H2 In-Memory Database
- Maven

## Quick Start

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```

The application starts at `http://localhost:8080`.  
H2 Console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:dayflowdb`).

## REST API Endpoints

### Leave Requests (`/api/leave-requests`)

| Method | Endpoint                              | Description                |
|--------|---------------------------------------|----------------------------|
| GET    | `/api/leave-requests`                 | List all leave requests    |
| GET    | `/api/leave-requests/{id}`            | Get by ID                  |
| POST   | `/api/leave-requests`                 | Create leave request       |
| PUT    | `/api/leave-requests/{id}`            | Update leave request       |
| DELETE | `/api/leave-requests/{id}`            | Delete leave request       |
| GET    | `/api/leave-requests/by-employee/{id}`| Filter by employee         |
| GET    | `/api/leave-requests/by-status/{s}`   | Filter by status           |
| GET    | `/api/leave-requests/by-type/{t}`     | Filter by leave type       |

### Salary Structures (`/api/salary-structures`)

| Method | Endpoint                        | Description                |
|--------|---------------------------------|----------------------------|
| GET    | `/api/salary-structures`        | List all structures        |
| GET    | `/api/salary-structures/active` | List active only           |
| GET    | `/api/salary-structures/{id}`   | Get by ID                  |
| POST   | `/api/salary-structures`        | Create structure           |
| PUT    | `/api/salary-structures/{id}`   | Update structure           |
| DELETE | `/api/salary-structures/{id}`   | Delete structure           |

### Employee Payroll (`/api/payroll`)

| Method | Endpoint                            | Description                |
|--------|-------------------------------------|----------------------------|
| GET    | `/api/payroll`                      | List all payroll records   |
| GET    | `/api/payroll/{id}`                 | Get by ID                  |
| GET    | `/api/payroll/by-employee/{id}`     | Get by employee            |
| POST   | `/api/payroll`                      | Create payroll record      |
| PUT    | `/api/payroll/{id}`                 | Update payroll record      |
| DELETE | `/api/payroll/{id}`                 | Delete payroll record      |

## Models

### LeaveRequest
- **Employee** (FK) — linked employee
- **LeaveType** — `PAID`, `SICK`, `UNPAID`
- **Start Date / End Date** — leave period
- **Remarks** — optional notes
- **Status** — `PENDING`, `APPROVED`, `REJECTED`
- **HR Comment** — HR response field

### SalaryStructure
- Earnings: Basic, HRA, DA, Other Allowance → **Gross Salary** (computed)
- Deductions: PF, ESI, Tax, Other → **Total Deductions** (computed)
- **Net Salary** = Gross − Deductions (computed)

### Payroll
- Links one **Employee** to one **Salary Structure** (unique constraint)
- Exposes salary summary via delegated getters
