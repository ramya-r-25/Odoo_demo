package com.dayflow.leave.service;

import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.model.Employee;

import java.util.List;
import java.util.Optional;

public interface LeaveService {

    LeaveRequest applyLeave(Employee employee, LeaveRequestDTO dto);

    List<LeaveRequest> getLeavesByEmployee(Long employeeId);

    Optional<LeaveRequest> getLeaveById(Long id);

    List<LeaveRequest> getAllLeaves();

    LeaveRequest approveLeave(Long id, String hrComment);

    LeaveRequest rejectLeave(Long id, String hrComment);
}
