package com.dayflow.leave.service;

import com.dayflow.attendance.repository.EmployeeRepository;
import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.model.LeaveType;
import com.dayflow.leave.repository.LeaveRequestRepository;
import com.dayflow.model.Employee;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of LeaveService with full validation:
 * - Duplicate/overlapping leave detection
 * - Invalid date validation (toDate before fromDate)
 * - Status lifecycle (PENDING → APPROVED / REJECTED)
 * - Approved leave reflected in attendance as LEAVE status
 */
@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public LeaveServiceImpl(LeaveRequestRepository leaveRepository,
                            EmployeeRepository employeeRepository,
                            UserRepository userRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LeaveRequestDTO applyLeave(Long employeeId, LeaveType leaveType, String fromDateStr, String toDateStr, String reason) {
        // 1. Resolve employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));

        // 2. Validate dates
        LocalDate fromDate = LocalDate.parse(fromDateStr);
        LocalDate toDate = LocalDate.parse(toDateStr);

        if (toDate.isBefore(fromDate)) {
            throw new IllegalArgumentException("Invalid leave dates: 'To Date' (" + toDate + ") cannot be before 'From Date' (" + fromDate + ").");
        }
        if (fromDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid leave dates: Leave cannot be applied for past dates.");
        }

        // 3. Check for overlapping leave requests (PENDING or APPROVED)
        List<LeaveRequest> overlaps = leaveRepository.findOverlappingLeaves(employeeId, fromDate, toDate, null);
        if (!overlaps.isEmpty()) {
            LeaveRequest overlap = overlaps.get(0);
            throw new IllegalStateException("Overlapping leave request found: " + overlap.getLeaveType().getDisplayName()
                    + " from " + overlap.getFromDate() + " to " + overlap.getToDate()
                    + " (" + overlap.getStatus().getDisplayName() + ").");
        }

        // 4. Create and save leave request
        LeaveRequest leaveRequest = new LeaveRequest(employee, leaveType, fromDate, toDate, reason);
        LeaveRequest saved = leaveRepository.save(leaveRequest);
        return new LeaveRequestDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestDTO> getMyLeaves(Long employeeId) {
        return leaveRepository.findByEmployeeIdOrderByFromDateDesc(employeeId)
                .stream().map(LeaveRequestDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestDTO> getAllLeaves() {
        return leaveRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getFromDate().compareTo(a.getFromDate()))
                .map(LeaveRequestDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestDTO> getPendingLeaves() {
        return leaveRepository.findByStatusOrderByFromDateDesc(LeaveStatus.PENDING)
                .stream().map(LeaveRequestDTO::new).collect(Collectors.toList());
    }

    @Override
    public LeaveRequestDTO approveLeave(Long leaveId, String reviewerName) {
        LeaveRequest lr = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with ID: " + leaveId));
        lr.approve(reviewerName);
        LeaveRequest saved = leaveRepository.save(lr);
        return new LeaveRequestDTO(saved);
    }

    @Override
    public LeaveRequestDTO rejectLeave(Long leaveId, String reviewerName, String rejectionReason) {
        LeaveRequest lr = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with ID: " + leaveId));
        lr.reject(reviewerName, rejectionReason != null ? rejectionReason : "Rejected by HR Admin");
        LeaveRequest saved = leaveRepository.save(lr);
        return new LeaveRequestDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequestDTO getLeaveById(Long leaveId) {
        LeaveRequest lr = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found with ID: " + leaveId));
        return new LeaveRequestDTO(lr);
    }

    @Override
    @Transactional(readOnly = true)
    public Long resolveEmployeeIdFromUsername(String username) {
        // Try by email first, then by employeeId field
        return userRepository.findByEmailOrEmployeeId(username, username)
                .map(user -> {
                    // Find matching employee by employeeId or email
                    return employeeRepository.findByEmployeeCode(user.getEmployeeId())
                            .or(() -> employeeRepository.findAll().stream()
                                    .filter(e -> user.getEmail().equalsIgnoreCase(e.getEmail()))
                                    .findFirst())
                            .map(Employee::getId)
                            .orElse(null);
                })
                .orElse(null);
    }
}
