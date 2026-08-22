package com.dayflow.leave.service;

import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.repository.LeaveRequestRepository;
import com.dayflow.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    public LeaveRequest applyLeave(Employee employee, LeaveRequestDTO dto) {
        if (dto.getLeaveType() == null) {
            throw new IllegalArgumentException("Leave type is required");
        }
        if (dto.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        LeaveRequest request = new LeaveRequest(
                employee,
                dto.getLeaveType(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getRemarks()
        );
        return leaveRequestRepository.save(request);
    }

    @Override
    public List<LeaveRequest> getLeavesByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<LeaveRequest> getLeaveById(Long id) {
        return leaveRequestRepository.findById(id);
    }

    @Override
    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAll();
    }

    @Override
    public LeaveRequest approveLeave(Long id, String hrComment) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with ID: " + id));

        if (request.getStatus() == LeaveStatus.APPROVED) {
            return request; // No-op or already done
        }
        if (request.getStatus() == LeaveStatus.REJECTED) {
            throw new IllegalArgumentException("Cannot transition from REJECTED to APPROVED");
        }

        request.setStatus(LeaveStatus.APPROVED);
        request.setHrComment(hrComment);
        return leaveRequestRepository.save(request);
    }

    @Override
    public LeaveRequest rejectLeave(Long id, String hrComment) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with ID: " + id));

        if (request.getStatus() == LeaveStatus.REJECTED) {
            return request; // No-op or already done
        }
        if (request.getStatus() == LeaveStatus.APPROVED) {
            throw new IllegalArgumentException("Cannot transition from APPROVED to REJECTED");
        }

        request.setStatus(LeaveStatus.REJECTED);
        request.setHrComment(hrComment);
        return leaveRequestRepository.save(request);
    }
}
