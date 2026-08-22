package com.dayflow.leave.controller;

import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveStatus;
import com.dayflow.leave.model.LeaveType;
import com.dayflow.leave.service.LeaveService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Thymeleaf View Controller for the /leave page.
 * Serves the leave.html template with the correct model data for both
 * EMPLOYEE (own leave history + apply form) and HR_ADMIN (all requests + approve/reject).
 */
@Controller
public class LeaveViewController {

    private final LeaveService leaveService;

    public LeaveViewController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/leave")
    public String renderLeavePage(
            @RequestParam(required = false) String view,
            Authentication authentication,
            Model model) {

        String username = authentication != null ? authentication.getName() : "";
        boolean isHrAdmin = authentication != null
                && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"));

        List<LeaveRequestDTO> leaveList;
        if (isHrAdmin) {
            if ("pending".equalsIgnoreCase(view)) {
                leaveList = leaveService.getPendingLeaves();
            } else {
                leaveList = leaveService.getAllLeaves();
            }
        } else {
            Long employeeId = leaveService.resolveEmployeeIdFromUsername(username);
            leaveList = (employeeId != null) ? leaveService.getMyLeaves(employeeId) : List.of();
        }

        // Stats for HR admin
        long pendingCount = leaveList.stream().filter(l -> l.getStatus() == LeaveStatus.PENDING).count();
        long approvedCount = leaveList.stream().filter(l -> l.getStatus() == LeaveStatus.APPROVED).count();
        long rejectedCount = leaveList.stream().filter(l -> l.getStatus() == LeaveStatus.REJECTED).count();

        model.addAttribute("username", username);
        model.addAttribute("isHrAdmin", isHrAdmin);
        model.addAttribute("leaveList", leaveList);
        model.addAttribute("leaveTypes", LeaveType.values());
        model.addAttribute("currentView", view != null ? view : (isHrAdmin ? "all" : "my"));
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);

        return "leave";
    }
}
