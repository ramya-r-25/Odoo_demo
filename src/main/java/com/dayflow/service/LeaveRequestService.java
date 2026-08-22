package com.dayflow.service;

import com.dayflow.model.LeaveRequest;
import com.dayflow.model.LeaveStatus;
import com.dayflow.model.LeaveType;
import com.dayflow.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------
    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    public Optional<LeaveRequest> findById(Long id) {
        return leaveRequestRepository.findById(id);
    }

    public LeaveRequest save(LeaveRequest leaveRequest) {
        validateDates(leaveRequest);
        return leaveRequestRepository.save(leaveRequest);
    }

    public void deleteById(Long id) {
        leaveRequestRepository.deleteById(id);
    }

    // ----------------------------------------------------------------
    // Queries
    // ----------------------------------------------------------------
    public List<LeaveRequest> findByEmployeeId(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public List<LeaveRequest> findByStatus(LeaveStatus status) {
        return leaveRequestRepository.findByStatus(status);
    }

    public List<LeaveRequest> findByLeaveType(LeaveType leaveType) {
        return leaveRequestRepository.findByLeaveType(leaveType);
    }

    // ----------------------------------------------------------------
    // Validation
    // ----------------------------------------------------------------
    private void validateDates(LeaveRequest leaveRequest) {
        if (leaveRequest.getStartDate() != null
                && leaveRequest.getEndDate() != null
                && leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate())) {
            throw new IllegalArgumentException(
                    "End Date cannot be earlier than Start Date.");
        }
    }
}
