package com.dayflow.attendance.service;

import com.dayflow.attendance.dto.AttendanceDTO;
import com.dayflow.attendance.model.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceService {

    AttendanceDTO createAttendance(AttendanceDTO dto);

    AttendanceDTO updateAttendance(Long id, AttendanceDTO dto);

    AttendanceDTO getAttendanceById(Long id);

    List<AttendanceDTO> getAllAttendances();

    List<AttendanceDTO> getDailyAttendance(LocalDate date);

    List<AttendanceDTO> getWeeklyAttendance(LocalDate startDate);

    List<AttendanceDTO> getEmployeeAttendance(Long employeeId);

    List<AttendanceDTO> getEmployeeAttendanceForRange(Long employeeId, LocalDate startDate, LocalDate endDate);

    AttendanceDTO employeeCheckIn(Long employeeId, LocalDateTime checkInTime);

    AttendanceDTO employeeCheckOut(Long employeeId, LocalDateTime checkOutTime);
}
