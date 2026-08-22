package com.dayflow.repository;

import com.dayflow.model.LeaveRequest;
import com.dayflow.model.LeaveStatus;
import com.dayflow.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    /**
     * Find leave requests by the employee's primary key (employee.id).
     * Spring Data navigates through the ManyToOne join: employee -> id.
     */
    List<LeaveRequest> findByEmployee_Id(Long employeeId);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByLeaveType(LeaveType leaveType);

    List<LeaveRequest> findByEmployee_IdAndStatus(Long employeeId, LeaveStatus status);
}
