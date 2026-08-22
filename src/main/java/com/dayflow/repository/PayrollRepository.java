package com.dayflow.repository;

import com.dayflow.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    /**
     * Find payroll by the employee's primary key (employee.id).
     * Spring Data navigates through the ManyToOne join: employee -> id.
     */
    Optional<Payroll> findByEmployee_Id(Long employeeId);
}
