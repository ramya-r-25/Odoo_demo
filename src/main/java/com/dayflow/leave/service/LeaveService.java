package com.dayflow.leave.service;

import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.model.LeaveType;

import java.util.List;

/**
 * Service interface for Leave Request workflow operations.
 */
public interface LeaveService {

    /** Apply for leave. employeeId is resolved from authenticated user. */
    LeaveRequestDTO applyLeave(Long employeeId, LeaveType leaveType, String fromDate, String toDate, String reason);

    /** Get all leave requests by a specific employee. */
    List<LeaveRequestDTO> getMyLeaves(Long employeeId);

    /** Get all leave requests (HR Admin view). */
    List<LeaveRequestDTO> getAllLeaves();

    /** Get pending leave requests only (HR Admin workflow). */
    List<LeaveRequestDTO> getPendingLeaves();

    /** Approve a leave request. Only HR Admin can call this. */
    LeaveRequestDTO approveLeave(Long leaveId, String reviewerName);

    /** Reject a leave request. Only HR Admin can call this. */
    LeaveRequestDTO rejectLeave(Long leaveId, String reviewerName, String rejectionReason);

    /** Get a leave request by ID. */
    LeaveRequestDTO getLeaveById(Long leaveId);

    /** Resolve employee DB ID from username (email or employeeId). */
    Long resolveEmployeeIdFromUsername(String username);
}
