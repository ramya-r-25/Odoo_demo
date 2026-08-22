# Dayflow HRMS - Human Resource Management System

Dayflow is a modular Human Resource Management System (HRMS) built using Java and Spring Boot.

## Security & Access Control
- **EMPLOYEE** (`ROLE_EMPLOYEE`): Access to employee dashboard, self-profile, attendance check-in/out, and personal leave.
- **HR_ADMIN** (`ROLE_HR_ADMIN`): Full administrative access for employee records, attendance monitoring, leave approvals, salary management, and system configuration.

## Features & Modules

### 1. Authentication & Security
- Form login, BCrypt password hashing, role-based access control, 403 access-denied handling.

### 2. Attendance Module (`/api/attendance`)
- Check-in/check-out, daily/weekly attendance logs, status tracking (`PRESENT`, `ABSENT`, `HALF_DAY`, `LEAVE`).

### 3. Leave Requests (`/api/leave-requests`)
- Apply for leave, view status, HR approval/rejection (`PENDING`, `APPROVED`, `REJECTED`).

### 4. Payroll & Salary (`/api/payroll` & `/api/salary-structures`)
- Employee salary structures, earnings (Basic, HRA, DA), deductions (PF, ESI, Tax), and net salary computations.

## Quick Start

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run
```
The application starts at `http://localhost:8080`.
