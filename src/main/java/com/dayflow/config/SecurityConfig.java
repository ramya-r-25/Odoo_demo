package com.dayflow.config;

import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central Security Configuration for Dayflow HRMS.
 * Commit 5: Integrated with all modules — Employee, Attendance, Leave, Payroll, Salary.
 */
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
            // Disable CSRF for REST API endpoints (form-based pages still use session)
            .csrf(csrf -> csrf.disable())
            // Allow H2 console frames
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth

                // ── Public endpoints ──────────────────────────────────────────────────
                .requestMatchers(
                    "/", "/login", "/register", "/access-denied",
                    "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/h2-console/**"
                ).permitAll()

                // ── Employee Attendance (both roles) ──────────────────────────────────
                .requestMatchers(
                    "/attendance",
                    "/api/attendance/check-in",
                    "/api/attendance/check-out",
                    "/api/attendance/my-attendance",
                    "/api/attendance/daily",
                    "/api/attendance/weekly",
                    "/api/attendance/employee/*"
                ).hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")

                // ── Admin-only Attendance (full list) ─────────────────────────────────
                .requestMatchers(
                    "/api/attendance",
                    "/api/attendance/**"
                ).hasAnyAuthority("ROLE_HR_ADMIN", "HR_ADMIN")

                // ── Employee API — EMPLOYEE can only access own; HR_ADMIN all ─────────
                // Fine-grained access handled by @PreAuthorize in EmployeeController
                .requestMatchers("/api/employees/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")

                // ── Leave Requests — EMPLOYEE can submit/view own; HR_ADMIN can manage ─
                .requestMatchers("/api/leave-requests/by-employee/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                .requestMatchers("/api/leave-requests/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")

                // ── Payroll — EMPLOYEE can view own; HR_ADMIN can manage all ──────────
                .requestMatchers("/api/payroll/by-employee/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                .requestMatchers("/api/payroll/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")

                // ── Salary Structures — active list visible to all authenticated ──────
                .requestMatchers("/api/salary-structures/active")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")
                .requestMatchers("/api/salary-structures/**")
                    .hasAnyAuthority("ROLE_HR_ADMIN", "HR_ADMIN")

                // ── Admin UI / API ────────────────────────────────────────────────────
                .requestMatchers("/admin/**", "/api/admin/**")
                    .hasAnyAuthority("ROLE_HR_ADMIN", "HR_ADMIN")

                // ── Employee UI ───────────────────────────────────────────────────────
                .requestMatchers("/employee/**", "/api/employee/**")
                    .hasAnyAuthority("ROLE_EMPLOYEE", "EMPLOYEE", "ROLE_HR_ADMIN", "HR_ADMIN")

                // ── All other requests must be authenticated ──────────────────────────
                .anyRequest().authenticated()
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
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
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
     * Credentials: employee@dayflow.com / employee123 | admin@dayflow.com / admin123
     */
    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (!userRepository.existsByEmployeeId("EMP001")) {
                User emp = new User("EMP001", "John Employee", "employee@dayflow.com",
                        encoder.encode("employee123"), Role.EMPLOYEE);
                userRepository.save(emp);
            }
            if (!userRepository.existsByEmployeeId("HR001")) {
                User admin = new User("HR001", "Sarah HR Admin", "admin@dayflow.com",
                        encoder.encode("admin123"), Role.HR_ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
