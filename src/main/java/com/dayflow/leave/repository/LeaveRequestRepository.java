package com.dayflow.leave.repository;

import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * JPA Repository for LeaveRequest entity.
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    /** All leave requests by a specific employee, newest first. */
    List<LeaveRequest> findByEmployeeIdOrderByFromDateDesc(Long employeeId);

    /** All leave requests with a specific status. */
    List<LeaveRequest> findByStatusOrderByFromDateDesc(LeaveStatus status);

    /** All pending leave requests for HR approval view. */
    List<LeaveRequest> findByStatusInOrderByFromDateDesc(List<LeaveStatus> statuses);

    /** Check for overlapping approved/pending leaves for the same employee.
     *  Used for duplicate/overlap validation. */
    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.id = :employeeId " +
           "AND lr.status IN ('PENDING', 'APPROVED') " +
           "AND lr.fromDate <= :toDate AND lr.toDate >= :fromDate " +
           "AND (:excludeId IS NULL OR lr.id <> :excludeId)")
    List<LeaveRequest> findOverlappingLeaves(
            @Param("employeeId") Long employeeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("excludeId") Long excludeId);

    /** Count leaves by employee and status. */
    long countByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
}
