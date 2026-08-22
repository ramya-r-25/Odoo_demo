package com.dayflow.leave.controller;

import com.dayflow.model.Employee;
import com.dayflow.service.EmployeeService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller("leavePayrollControllerM4")
@RequestMapping("/payroll")
public class PayrollController {

    private final EmployeeService employeeService;

    public PayrollController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String salaryPage(Model model) {
        boolean isHrAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR_ADMIN"));
        
        if (isHrAdmin) {
            List<Employee> employees = employeeService.getAllEmployees();
            model.addAttribute("employees", employees);
            model.addAttribute("isHrAdmin", true);
            model.addAttribute("currentUserRole", "HR_ADMIN");
        } else {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Employee employee = employeeService.getEmployeeByEmail(email);
            model.addAttribute("employee", employee);
            model.addAttribute("isHrAdmin", false);
            model.addAttribute("currentUserRole", "EMPLOYEE");
        }
        return "payroll/salary";
    }

    @PostMapping("/update")
    public String updateSalary(@RequestParam("employeeId") Long employeeId,
                               @RequestParam("salary") Double salary,
                               RedirectAttributes redirectAttributes) {
        boolean isHrAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HR_ADMIN"));
        
        if (!isHrAdmin) {
            throw new SecurityException("Only HR_ADMIN can update salary");
        }
        try {
            Employee employee = employeeService.getEmployeeById(employeeId);
            employee.setSalary(salary);
            employeeService.updateEmployee(employeeId, employee, com.dayflow.security.SecurityGroup.HR_ADMIN);
            redirectAttributes.addFlashAttribute("successMessage", "Salary updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Salary update failed.");
        }
        return "redirect:/payroll";
    }
}
