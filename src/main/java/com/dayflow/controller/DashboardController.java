package com.dayflow.controller;

import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

/**
 * Handles dashboard routing for both EMPLOYEE and HR_ADMIN roles.
 * /dashboard → Bootstrap-based unified dashboard (dashboard.html)
 * /employee/dashboard → legacy employee-dashboard.html
 * /admin/dashboard → legacy admin-dashboard.html
 */
@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Unified dashboard served after login for all roles. */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            populateModel(authentication, model);
        }
        return "dashboard";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            populateModel(authentication, model);
        }
        return "employee-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            populateModel(authentication, model);
        }
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("userList", userRepository.findAll());
        return "admin-dashboard";
    }

    private void populateModel(Authentication authentication, Model model) {
        String identifier = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmailOrEmployeeId(identifier, identifier);
        userOpt.ifPresent(user -> {
            model.addAttribute("currentUser", user);
            model.addAttribute("currentUserName", user.getName());
        });
        boolean isHrAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_HR_ADMIN"));
        model.addAttribute("isHrAdmin", isHrAdmin);
    }
}
