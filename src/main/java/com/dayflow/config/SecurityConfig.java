package com.dayflow.config;

import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/access-denied", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                // Attendance: Employee & HR Admin
                .requestMatchers("/attendance", "/api/attendance/check-in", "/api/attendance/check-out",
                    "/api/attendance/my-attendance", "/api/attendance/daily",
                    "/api/attendance/weekly", "/api/attendance/employee/*")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                // Attendance admin endpoints
                .requestMatchers("/api/attendance", "/api/attendance/**")
                    .hasAnyAuthority("ROLE_HR_ADMIN", "HR_ADMIN")
                // Leave: Employee (apply, view own) + HR Admin (approve/reject)
                .requestMatchers("/leave", "/api/leave/apply", "/api/leave/my")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                .requestMatchers("/api/leave/all", "/api/leave/pending", "/api/leave/*/approve", "/api/leave/*/reject")
                    .hasAnyAuthority("ROLE_HR_ADMIN", "HR_ADMIN")
                .requestMatchers("/api/leave/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                // Dashboard, profile, employees
                .requestMatchers("/dashboard", "/employees/**", "/api/employees/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                // Payroll & reports: HR Admin only
                .requestMatchers("/payroll", "/reports", "/api/payroll/**", "/api/reports/**")
                    .hasAnyAuthority("ROLE_HR_ADMIN", "HR_ADMIN")
                // General admin protection
                .requestMatchers("/admin/**", "/api/admin/**").hasAnyAuthority("ROLE_HR_ADMIN", "HR_ADMIN")
                .requestMatchers("/employee/**", "/api/employee/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    request -> request.getRequestURI().startsWith("/api/")
                )
                .accessDeniedPage("/access-denied")
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Seeds initial demo users into the database on startup if not present.
     */
    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (!userRepository.existsByEmployeeId("EMP001")) {
                User emp = new User("EMP001", "John Employee", "employee@dayflow.com", encoder.encode("employee123"), Role.EMPLOYEE);
                userRepository.save(emp);
            }
            if (!userRepository.existsByEmployeeId("HR001")) {
                User admin = new User("HR001", "Sarah HR Admin", "admin@dayflow.com", encoder.encode("admin123"), Role.HR_ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
