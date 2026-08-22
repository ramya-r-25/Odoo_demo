package com.dayflow.attendance.service;

import com.dayflow.attendance.dto.AttendanceDTO;
import com.dayflow.attendance.model.Attendance;
import com.dayflow.attendance.model.AttendanceStatus;
import com.dayflow.model.Employee;
import com.dayflow.attendance.repository.AttendanceRepository;
import com.dayflow.attendance.repository.EmployeeRepository;
import com.dayflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                  EmployeeRepository employeeRepository,
                                  UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AttendanceDTO createAttendance(AttendanceDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + dto.getEmployeeId()));

        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now();
        AttendanceStatus status = dto.getStatus() != null ? dto.getStatus() : AttendanceStatus.PRESENT;

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(date);
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setStatus(status);
        attendance.recalculateDurationAndStatus();

        Attendance saved = attendanceRepository.save(attendance);
        return new AttendanceDTO(saved);
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found with ID: " + id));

        if (dto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + dto.getEmployeeId()));
            attendance.setEmployee(employee);
        }

        if (dto.getDate() != null) {
            attendance.setDate(dto.getDate());
        }
        if (dto.getCheckIn() != null) {
            attendance.setCheckIn(dto.getCheckIn());
        }
        if (dto.getCheckOut() != null) {
            attendance.setCheckOut(dto.getCheckOut());
        }
        if (dto.getStatus() != null) {
            attendance.setStatus(dto.getStatus());
        }

        attendance.recalculateDurationAndStatus();
        Attendance updated = attendanceRepository.save(attendance);
        return new AttendanceDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found with ID: " + id));
        return new AttendanceDTO(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAllAttendances() {
        return attendanceRepository.findAll().stream()
                .map(AttendanceDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getDailyAttendance(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return attendanceRepository.findByDate(targetDate).stream()
                .map(AttendanceDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getWeeklyAttendance(LocalDate startDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);
        return attendanceRepository.findByDateBetweenOrderByDateDesc(start, end).stream()
                .map(AttendanceDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getEmployeeAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeIdOrderByDateDesc(employeeId).stream()
                .map(AttendanceDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getEmployeeAttendanceForRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndDateBetweenOrderByDateDesc(employeeId, startDate, endDate).stream()
                .map(AttendanceDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO employeeCheckIn(Long employeeId, LocalDateTime checkInTime) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));

        LocalDateTime timestamp = checkInTime != null ? checkInTime : LocalDateTime.now();
        LocalDate today = timestamp.toLocalDate();

        List<Attendance> sameDayRecords = attendanceRepository.findByEmployeeIdAndDateBetweenOrderByDateDesc(employeeId, today, today);
        if (!sameDayRecords.isEmpty()) {
            throw new IllegalStateException("Employee is already checked in for today: " + today);
        }

        Attendance attendance = new Attendance(employee, today, timestamp, null, AttendanceStatus.PRESENT);
        Attendance saved = attendanceRepository.save(attendance);
        return new AttendanceDTO(saved);
    }

    @Override
    public AttendanceDTO employeeCheckOut(Long employeeId, LocalDateTime checkOutTime) {
        LocalDateTime timestamp = checkOutTime != null ? checkOutTime : LocalDateTime.now();

        Attendance activeAttendance = attendanceRepository.findTopByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(employeeId)
                .orElseThrow(() -> new IllegalStateException("No active check-in record found for employee ID: " + employeeId));

        activeAttendance.setCheckOut(timestamp);
        Attendance updated = attendanceRepository.save(activeAttendance);
        return new AttendanceDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEmployeeIdByUsername(String username) {
        // Resolve User by email or employeeId login identifier
        return userRepository.findByEmailOrEmployeeId(username, username)
                .map(user -> {
                    // First try to match by user's employeeId field → employee code
                    String empCode = user.getEmployeeId();
                    if (empCode != null) {
                        java.util.Optional<Employee> byCode = employeeRepository.findByEmployeeCode(empCode);
                        if (byCode.isPresent()) return byCode.get().getId();
                    }
                    // Fallback: match by email
                    return employeeRepository.findAll().stream()
                            .filter(e -> user.getEmail().equalsIgnoreCase(e.getEmail()))
                            .findFirst()
                            .map(Employee::getId)
                            .orElse(null);
                })
                .orElse(null);
    }
}
