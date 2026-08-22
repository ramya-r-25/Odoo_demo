package com.dayflow.attendance.repository;

import com.dayflow.attendance.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    // Daily View Query
    List<Attendance> findByDate(LocalDate date);

    // Weekly / Custom Range View Query
    List<Attendance> findByDateBetweenOrderByDateDesc(LocalDate startDate, LocalDate endDate);

    // Employee-Specific Attendance Query
    List<Attendance> findByEmployeeIdOrderByDateDesc(Long employeeId);

    // Employee-Specific Date Range Query
    List<Attendance> findByEmployeeIdAndDateBetweenOrderByDateDesc(Long employeeId, LocalDate startDate, LocalDate endDate);

    // Active Check-in Query for Employee check-in/out logic
    Optional<Attendance> findTopByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(Long employeeId);
}
