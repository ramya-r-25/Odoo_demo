package com.dayflow.service;

import com.dayflow.dto.RegisterDto;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        if (userRepository.existsByEmployeeId(dto.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID is already in use!");
        }

        Role role = (dto.getRole() != null) ? dto.getRole() : Role.EMPLOYEE;

        User user = new User(
                dto.getEmployeeId().trim(),
                dto.getName().trim(),
                dto.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(dto.getPassword()),
                role
        );

        return userRepository.save(user);
    }

    public Optional<User> findByEmailOrEmployeeId(String identifier) {
        return userRepository.findByEmailOrEmployeeId(identifier, identifier);
    }
}
