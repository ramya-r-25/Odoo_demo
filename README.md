# Dayflow HRMS - Human Resource Management System

Dayflow is a modular Human Resource Management System (HRMS) built using Java and Spring Boot.

## Security & Access Control
- **EMPLOYEE** (`ROLE_EMPLOYEE`): Access to employee dashboard, self-profile, attendance check-in/out, and personal leave.
- **HR_ADMIN** (`ROLE_HR_ADMIN`): Full administrative access for employee records, attendance monitoring, leave approvals, salary management, and system configuration.

## Attendance Module Features
- **Core Entity**: `Attendance` (`date`, `checkIn`, `checkOut`, `workDuration`, `status`)
- **Status**: `PRESENT`, `ABSENT`, `HALF_DAY`, `LEAVE`

### API Endpoints
| HTTP Method | Endpoint | Description |
|-------------|----------|-------------|
| `POST` | `/api/attendance` | Create attendance record |
| `PUT` | `/api/attendance/{id}` | Update existing attendance record |
| `GET` | `/api/attendance/{id}` | Detail read |
| `GET` | `/api/attendance` | List View / HR Admin View |
| `POST` | `/api/attendance/check-in` | Employee Check-in action |
| `POST` | `/api/attendance/check-out` | Employee Check-out action |
| `GET` | `/api/attendance/daily` | Daily Attendance View |
| `GET` | `/api/attendance/weekly` | Weekly Attendance View |
| `GET` | `/api/attendance/employee/{employeeId}` | Employee-Specific Attendance View |

## Running the Application
```bash
mvn spring-boot:run
```
