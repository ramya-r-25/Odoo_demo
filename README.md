# Dayflow HRMS - Human Resource Management System

Dayflow is a modular Human Resource Management System (HRMS) built using Java and Spring Boot.

## Security & Access Control
- **EMPLOYEE** (`ROLE_EMPLOYEE`): Access to employee dashboard, self-profile, attendance check-in/out, and personal leave.
- **HR_ADMIN** (`ROLE_HR_ADMIN`): Full administrative access for employee records, attendance monitoring, leave approvals, salary management, and system configuration.

## Attendance Module Features
- **Core Entity**: `Attendance` (`date`, `checkIn`, `checkOut`, `workDuration`, `status`)
- **Status**: `PRESENT`, `ABSENT`, `HALF_DAY`, `LEAVE`

## Running the Application
```bash
mvn spring-boot:run
```
