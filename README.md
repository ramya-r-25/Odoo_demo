# Dayflow HRMS - Attendance Module (Java / Spring Boot)

This is the isolated, modular **Attendance** system for the **Dayflow** HRMS project.

## Module Features

### Core Entity: `Attendance`
- **Employee**: Reference to `Employee` entity (`employee_id`)
- **Date**: `LocalDate` of attendance
- **Check-in**: `LocalDateTime` check-in timestamp
- **Check-out**: `LocalDateTime` check-out timestamp
- **Working Duration**: Computed hours based on duration between check-in & check-out
- **Status**: Supports `PRESENT`, `ABSENT`, `HALF_DAY`, `LEAVE`

### Prepared API Endpoints
| HTTP Method | Endpoint | Description |
|-------------|----------|-------------|
| `POST` | `/api/attendance` | Basic Form View Submit / Create attendance |
| `PUT` | `/api/attendance/{id}` | Update existing attendance record |
| `GET` | `/api/attendance/{id}` | Basic Form View detail read |
| `GET` | `/api/attendance` | Basic List View / HR Admin View |
| `POST` | `/api/attendance/check-in` | Employee Check-in action |
| `POST` | `/api/attendance/check-out` | Employee Check-out action |
| `GET` | `/api/attendance/daily` | Daily Attendance View |
| `GET` | `/api/attendance/weekly` | Weekly Attendance View |
| `GET` | `/api/attendance/employee/{employeeId}` | Employee-Specific Attendance View |

### Future Extension Hooks
- Modular API controller for easy integration with Spring Security (Employee role vs HR/Admin role permissions).
- Prepared repository methods for date range reporting and status filters.
