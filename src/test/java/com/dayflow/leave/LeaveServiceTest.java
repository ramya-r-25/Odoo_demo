package com.dayflow.leave;

import com.dayflow.attendance.repository.EmployeeRepository;
import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.model.LeaveType;
import com.dayflow.leave.repository.LeaveRequestRepository;
import com.dayflow.leave.service.LeaveServiceImpl;
import com.dayflow.model.Employee;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LeaveServiceImpl.
 * Covers: happy path, overlap detection, invalid dates, approval/rejection lifecycle.
 */
@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private Employee testEmployee;
    private User testUser;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee("EMP001", "John Employee", "employee@dayflow.com", "Engineering");
        testEmployee.setId(1L);

        testUser = new User("EMP001", "John Employee", "employee@dayflow.com", "encoded_pass", Role.EMPLOYEE);
    }

    // ─── Apply Leave: Happy Path ──────────────────────────────────────────────

    @Test
    @DisplayName("Employee can apply for a valid future leave")
    void applyLeave_validFutureDates_success() {
        LocalDate from = LocalDate.now().plusDays(3);
        LocalDate to = LocalDate.now().plusDays(5);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveRepository.findOverlappingLeaves(1L, from, to, null)).thenReturn(List.of());
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(inv -> {
            LeaveRequest lr = inv.getArgument(0);
            // Simulate save by returning same object
            return lr;
        });

        LeaveRequestDTO dto = leaveService.applyLeave(1L, LeaveType.ANNUAL,
                from.toString(), to.toString(), "Vacation");

        assertNotNull(dto);
        assertEquals(LeaveStatus.PENDING, dto.getStatus());
        assertEquals(LeaveType.ANNUAL, dto.getLeaveType());
        assertEquals(3L, dto.getTotalDays());
        verify(leaveRepository, times(1)).save(any(LeaveRequest.class));
    }

    // ─── Apply Leave: Invalid Dates ───────────────────────────────────────────

    @Test
    @DisplayName("Leave with toDate before fromDate throws exception")
    void applyLeave_toDateBeforeFromDate_throwsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        LocalDate from = LocalDate.now().plusDays(5);
        LocalDate to = LocalDate.now().plusDays(2); // BEFORE from

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                leaveService.applyLeave(1L, LeaveType.SICK, from.toString(), to.toString(), "Sick"));

        assertTrue(ex.getMessage().contains("cannot be before"));
        verify(leaveRepository, never()).save(any());
    }

    @Test
    @DisplayName("Leave with past fromDate throws exception")
    void applyLeave_pastDate_throwsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        LocalDate pastDate = LocalDate.now().minusDays(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                leaveService.applyLeave(1L, LeaveType.CASUAL,
                        pastDate.toString(), pastDate.toString(), "Past reason"));

        assertTrue(ex.getMessage().contains("past dates"));
    }

    // ─── Apply Leave: Overlapping Leave ──────────────────────────────────────

    @Test
    @DisplayName("Overlapping leave request throws exception")
    void applyLeave_overlappingLeave_throwsException() {
        LocalDate from = LocalDate.now().plusDays(3);
        LocalDate to = LocalDate.now().plusDays(5);

        // Simulate existing approved leave that overlaps
        LeaveRequest existing = new LeaveRequest(testEmployee, LeaveType.ANNUAL,
                from.minusDays(1), to.plusDays(1), "Existing");
        existing.approve("admin@dayflow.com");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(leaveRepository.findOverlappingLeaves(1L, from, to, null))
                .thenReturn(List.of(existing));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                leaveService.applyLeave(1L, LeaveType.CASUAL,
                        from.toString(), to.toString(), "Another reason"));

        assertTrue(ex.getMessage().contains("Overlapping"));
        verify(leaveRepository, never()).save(any());
    }

    // ─── Employee Not Found ───────────────────────────────────────────────────

    @Test
    @DisplayName("Apply leave for unknown employee throws exception")
    void applyLeave_employeeNotFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        LocalDate from = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class, () ->
                leaveService.applyLeave(99L, LeaveType.ANNUAL,
                        from.toString(), from.toString(), "Test"));
    }

    // ─── Approve Leave ────────────────────────────────────────────────────────

    @Test
    @DisplayName("HR Admin can approve a PENDING leave request")
    void approveLeave_pendingLeave_setsApprovedStatus() {
        LeaveRequest lr = new LeaveRequest(testEmployee, LeaveType.ANNUAL,
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), "Vacation");

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(lr));
        when(leaveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequestDTO dto = leaveService.approveLeave(1L, "admin@dayflow.com");

        assertEquals(LeaveStatus.APPROVED, dto.getStatus());
        assertEquals("admin@dayflow.com", dto.getReviewedBy());
    }

    @Test
    @DisplayName("Cannot approve an already REJECTED leave request")
    void approveLeave_alreadyRejected_throwsException() {
        LeaveRequest lr = new LeaveRequest(testEmployee, LeaveType.ANNUAL,
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), "Vacation");
        lr.reject("admin@dayflow.com", "Not approved");

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(lr));

        assertThrows(IllegalStateException.class, () ->
                leaveService.approveLeave(1L, "admin@dayflow.com"));
    }

    // ─── Reject Leave ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("HR Admin can reject a PENDING leave request")
    void rejectLeave_pendingLeave_setsRejectedStatus() {
        LeaveRequest lr = new LeaveRequest(testEmployee, LeaveType.CASUAL,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(3), "Personal");

        when(leaveRepository.findById(2L)).thenReturn(Optional.of(lr));
        when(leaveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequestDTO dto = leaveService.rejectLeave(2L, "admin@dayflow.com", "Insufficient staff");

        assertEquals(LeaveStatus.REJECTED, dto.getStatus());
        assertEquals("Insufficient staff", dto.getRejectionReason());
    }

    @Test
    @DisplayName("Cannot reject an already APPROVED leave request")
    void rejectLeave_alreadyApproved_throwsException() {
        LeaveRequest lr = new LeaveRequest(testEmployee, LeaveType.ANNUAL,
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(5), "Vacation");
        lr.approve("admin@dayflow.com");

        when(leaveRepository.findById(2L)).thenReturn(Optional.of(lr));

        assertThrows(IllegalStateException.class, () ->
                leaveService.rejectLeave(2L, "admin@dayflow.com", "Changed mind"));
    }

    // ─── Get My Leaves ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Employee can view their own leave history")
    void getMyLeaves_returnsEmployeeLeaves() {
        LeaveRequest lr1 = new LeaveRequest(testEmployee, LeaveType.ANNUAL,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), "Holiday");
        LeaveRequest lr2 = new LeaveRequest(testEmployee, LeaveType.SICK,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(10), "Fever");

        when(leaveRepository.findByEmployeeIdOrderByFromDateDesc(1L)).thenReturn(List.of(lr1, lr2));

        List<LeaveRequestDTO> result = leaveService.getMyLeaves(1L);

        assertEquals(2, result.size());
        verify(leaveRepository).findByEmployeeIdOrderByFromDateDesc(1L);
    }

    // ─── Resolve Employee by Username ─────────────────────────────────────────

    @Test
    @DisplayName("Resolves employee ID from email login")
    void resolveEmployeeIdFromUsername_byEmail_returnsId() {
        when(userRepository.findByEmailOrEmployeeId("employee@dayflow.com", "employee@dayflow.com"))
                .thenReturn(Optional.of(testUser));
        when(employeeRepository.findByEmployeeCode("EMP001")).thenReturn(Optional.of(testEmployee));

        Long id = leaveService.resolveEmployeeIdFromUsername("employee@dayflow.com");

        assertEquals(1L, id);
    }

    @Test
    @DisplayName("Returns null for unknown username")
    void resolveEmployeeIdFromUsername_unknown_returnsNull() {
        when(userRepository.findByEmailOrEmployeeId("unknown@test.com", "unknown@test.com"))
                .thenReturn(Optional.empty());

        Long id = leaveService.resolveEmployeeIdFromUsername("unknown@test.com");

        assertNull(id);
    }
}
