package com.dayflow.attendance.dto;

import com.dayflow.attendance.model.Attendance;
import com.dayflow.attendance.model.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceDTO {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeCode;
    private String employeeName;

    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double workingDuration;
    private AttendanceStatus status;

    public AttendanceDTO() {
    }

    public AttendanceDTO(Attendance attendance) {
        if (attendance != null) {
            this.id = attendance.getId();
            if (attendance.getEmployee() != null) {
                this.employeeId = attendance.getEmployee().getId();
                this.employeeCode = attendance.getEmployee().getEmployeeCode();
                this.employeeName = attendance.getEmployee().getName();
            }
            this.date = attendance.getDate();
            this.checkIn = attendance.getCheckIn();
            this.checkOut = attendance.getCheckOut();
            this.workingDuration = attendance.getWorkingDuration();
            this.status = attendance.getStatus();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public Double getWorkingDuration() {
        return workingDuration;
    }

    public void setWorkingDuration(Double workingDuration) {
        this.workingDuration = workingDuration;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }
}
