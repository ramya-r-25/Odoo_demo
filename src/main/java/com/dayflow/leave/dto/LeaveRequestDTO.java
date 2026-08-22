package com.dayflow.leave.dto;

import com.dayflow.leave.model.LeaveType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LeaveRequestDTO {

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String remarks;

    public LeaveRequestDTO() {}

    public LeaveRequestDTO(LeaveType leaveType, LocalDate startDate, LocalDate endDate, String remarks) {
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.remarks = remarks;
    }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
