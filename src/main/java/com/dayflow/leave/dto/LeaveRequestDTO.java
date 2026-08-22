package com.dayflow.leave.dto;

import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.model.LeaveType;

import java.time.LocalDate;

/**
 * Data Transfer Object for LeaveRequest.
 * Used for both API responses and form submissions.
 */
public class LeaveRequestDTO {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private LeaveType leaveType;
    private String leaveTypeDisplay;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long totalDays;
    private String reason;
    private LeaveStatus status;
    private String statusDisplay;
    private String reviewedBy;
    private LocalDate reviewedAt;
    private String rejectionReason;

    public LeaveRequestDTO() {}

    /** Construct DTO from entity. */
    public LeaveRequestDTO(LeaveRequest lr) {
        this.id = lr.getId();
        if (lr.getEmployee() != null) {
            this.employeeId = lr.getEmployee().getId();
            this.employeeName = lr.getEmployee().getName();
            this.employeeCode = lr.getEmployee().getEmployeeCode();
        }
        this.leaveType = lr.getLeaveType();
        this.leaveTypeDisplay = lr.getLeaveType() != null ? lr.getLeaveType().getDisplayName() : "";
        this.fromDate = lr.getFromDate();
        this.toDate = lr.getToDate();
        this.totalDays = lr.getTotalDays();
        this.reason = lr.getReason();
        this.status = lr.getStatus();
        this.statusDisplay = lr.getStatus() != null ? lr.getStatus().getDisplayName() : "";
        this.reviewedBy = lr.getReviewedBy();
        this.reviewedAt = lr.getReviewedAt();
        this.rejectionReason = lr.getRejectionReason();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public String getLeaveTypeDisplay() { return leaveTypeDisplay; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public Long getTotalDays() { return totalDays; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public String getStatusDisplay() { return statusDisplay; }

    public String getReviewedBy() { return reviewedBy; }
    public LocalDate getReviewedAt() { return reviewedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
