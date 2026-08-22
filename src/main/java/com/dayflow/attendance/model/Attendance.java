package com.dayflow.attendance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Employee is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "working_duration")
    private Double workingDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    public Attendance() {
    }

    public Attendance(Employee employee, LocalDate date, LocalDateTime checkIn, LocalDateTime checkOut, AttendanceStatus status) {
        this.employee = employee;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status != null ? status : AttendanceStatus.PRESENT;
        this.recalculateDurationAndStatus();
    }

    public void recalculateDurationAndStatus() {
        if (checkIn != null && checkOut != null) {
            long minutes = Duration.between(checkIn, checkOut).toMinutes();
            if (minutes < 0) {
                minutes = 0;
            }
            this.workingDuration = Math.round((minutes / 60.0) * 100.0) / 100.0;

            // Auto status logic if not set manually to LEAVE or ABSENT
            if (this.status != AttendanceStatus.LEAVE && this.status != AttendanceStatus.ABSENT) {
                if (this.workingDuration >= 7.0) {
                    this.status = AttendanceStatus.PRESENT;
                } else if (this.workingDuration > 0.0) {
                    this.status = AttendanceStatus.HALF_DAY;
                }
            }
        } else if (checkIn != null && checkOut == null) {
            // Checked in but not yet checked out
            if (this.workingDuration == null) {
                this.workingDuration = 0.0;
            }
            if (this.status == null) {
                this.status = AttendanceStatus.PRESENT;
            }
        } else {
            if (this.workingDuration == null) {
                this.workingDuration = 0.0;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
        recalculateDurationAndStatus();
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
        recalculateDurationAndStatus();
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
