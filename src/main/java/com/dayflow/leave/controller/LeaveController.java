package com.dayflow.leave.controller;

import com.dayflow.leave.dto.LeaveRequestDTO;
import com.dayflow.leave.model.LeaveRequest;
import com.dayflow.leave.service.LeaveService;
import com.dayflow.model.Employee;
import com.dayflow.service.EmployeeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/leave")
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;

    public LeaveController(LeaveService leaveService, EmployeeService employeeService) {
        this.leaveService = leaveService;
        this.employeeService = employeeService;
    }

    private Employee getCurrentEmployee() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return employeeService.getEmployeeByEmail(email);
    }

    @GetMapping
    public String myLeaves(Model model) {
        Employee employee = getCurrentEmployee();
        List<LeaveRequest> leaves = leaveService.getLeavesByEmployee(employee.getId());
        model.addAttribute("leaves", leaves);
        model.addAttribute("employee", employee);
        model.addAttribute("leaveRequest", new LeaveRequestDTO());
        model.addAttribute("isHrAdmin", false);
        model.addAttribute("currentUserRole", "EMPLOYEE");
        return "leave/my-leaves";
    }

    @PostMapping("/submit")
    public String submitLeave(@Valid @ModelAttribute("leaveRequest") LeaveRequestDTO dto,
                              BindingResult bindingResult, Model model,
                              RedirectAttributes redirectAttributes) {
        Employee employee = getCurrentEmployee();
        if (bindingResult.hasErrors()) {
            List<LeaveRequest> leaves = leaveService.getLeavesByEmployee(employee.getId());
            model.addAttribute("leaves", leaves);
            model.addAttribute("employee", employee);
            model.addAttribute("isHrAdmin", false);
            model.addAttribute("currentUserRole", "EMPLOYEE");
            return "leave/my-leaves";
        }
        try {
            leaveService.applyLeave(employee, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Leave submitted successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/leave";
    }

    @GetMapping("/approvals")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public String adminLeaves(Model model) {
        List<LeaveRequest> leaves = leaveService.getAllLeaves();
        model.addAttribute("leaves", leaves);
        model.addAttribute("isHrAdmin", true);
        model.addAttribute("currentUserRole", "HR_ADMIN");
        return "leave/admin-leaves";
    }

    @PostMapping("/approve/{id}")
    public String approveLeave(@PathVariable Long id,
                               @RequestParam(required = false, defaultValue = "") String hrComment,
                               RedirectAttributes redirectAttributes) {
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR_ADMIN"))) {
            throw new SecurityException("Only HR_ADMIN can approve leaves");
        }
        try {
            leaveService.approveLeave(id, hrComment);
            redirectAttributes.addFlashAttribute("successMessage", "Leave approved.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/leave/approvals";
    }

    @PostMapping("/reject/{id}")
    public String rejectLeave(@PathVariable Long id,
                              @RequestParam(required = false, defaultValue = "") String hrComment,
                              RedirectAttributes redirectAttributes) {
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR_ADMIN"))) {
            throw new SecurityException("Only HR_ADMIN can reject leaves");
        }
        try {
            leaveService.rejectLeave(id, hrComment);
            redirectAttributes.addFlashAttribute("successMessage", "Leave rejected.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/leave/approvals";
    }
}
