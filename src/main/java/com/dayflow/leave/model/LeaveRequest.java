package com.dayflow.leave.model;

import com.dayflow.model.Employee;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * JPA Entity representing a Leave Request in Dayflow HRMS.
 * Lifecycle: PENDING → APPROVED / REJECTED by HR Admin.
 */
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column(nullable = false)
    private LocalDate toDate;

    @Column
    private Long totalDays;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    @Column
    private String reviewedBy;

    @Column
    private LocalDate reviewedAt;

    @Column
    private String rejectionReason;

    public LeaveRequest() {}

    public LeaveRequest(Employee employee, LeaveType leaveType, LocalDate fromDate, LocalDate toDate, String reason) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.status = LeaveStatus.PENDING;
        this.totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
    }

    // --- Business Methods ---

    public void approve(String reviewerName) {
        if (this.status != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be approved. Current status: " + this.status);
        }
        this.status = LeaveStatus.APPROVED;
        this.reviewedBy = reviewerName;
        this.reviewedAt = LocalDate.now();
    }

    public void reject(String reviewerName, String rejectionReason) {
        if (this.status != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be rejected. Current status: " + this.status);
        }
        this.status = LeaveStatus.REJECTED;
        this.reviewedBy = reviewerName;
        this.reviewedAt = LocalDate.now();
        this.rejectionReason = rejectionReason;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return status == LeaveStatus.APPROVED && !fromDate.isAfter(today) && !toDate.isBefore(today);
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
        recalculateDays();
    }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
        recalculateDays();
    }

    public Long getTotalDays() { return totalDays; }

    private void recalculateDays() {
        if (fromDate != null && toDate != null) {
            this.totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        }
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public LocalDate getReviewedAt() { return reviewedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
