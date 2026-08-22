package com.dayflow.service;

import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmployeeId) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrEmployeeId(usernameOrEmployeeId, usernameOrEmployeeId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or employee ID: " + usernameOrEmployeeId));

        List<GrantedAuthority> authorities = new ArrayList<>();
        // Add both ROLE_ prefix and exact role string for Spring Security flexibility
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
