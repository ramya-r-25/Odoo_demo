package com.dayflow.attendance.repository;

/**
 * Use com.dayflow.repository.EmployeeRepository instead.
 * This interface is kept as a type alias to avoid refactoring AttendanceServiceImpl imports.
 */
public interface EmployeeRepository extends com.dayflow.repository.EmployeeRepository {
    // Inherits all methods from the main EmployeeRepository.
    // Spring Data will resolve to the single Employee JPA repository bean.
}
