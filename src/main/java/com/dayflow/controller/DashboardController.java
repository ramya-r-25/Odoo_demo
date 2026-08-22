package com.dayflow.controller;

import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            String identifier = authentication.getName();
            Optional<User> userOpt = userRepository.findByEmailOrEmployeeId(identifier, identifier);
            userOpt.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        return "employee-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            String identifier = authentication.getName();
            Optional<User> userOpt = userRepository.findByEmailOrEmployeeId(identifier, identifier);
            userOpt.ifPresent(user -> model.addAttribute("currentUser", user));
        }
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("userList", userRepository.findAll());
        return "admin-dashboard";
    }
}
