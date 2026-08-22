package com.dayflow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/leave/approvals/**").hasRole("HR_ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails employee1 = User.withDefaultPasswordEncoder()
                .username("alex@dayflow.com")
                .password("password")
                .roles("EMPLOYEE")
                .build();

        UserDetails employee2 = User.withDefaultPasswordEncoder()
                .username("sarah@dayflow.com")
                .password("password")
                .roles("EMPLOYEE")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin@dayflow.com")
                .password("password")
                .roles("HR_ADMIN")
                .build();

        return new InMemoryUserDetailsManager(employee1, employee2, admin);
    }
}
