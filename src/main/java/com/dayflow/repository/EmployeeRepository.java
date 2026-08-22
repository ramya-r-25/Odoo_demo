package com.dayflow.repository;

import com.dayflow.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    /**
     * Finds employee by either employeeCode or employeeId (both fields are kept in sync).
     */
    @Query("SELECT e FROM Employee e WHERE e.employeeCode = :id OR e.employeeId = :id")
    Optional<Employee> findByEmployeeId(@Param("id") String employeeId);

    /**
     * Finds employee by email address.
     */
    Optional<Employee> findByEmail(String email);
}
