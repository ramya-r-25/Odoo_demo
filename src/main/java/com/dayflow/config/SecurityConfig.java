package com.dayflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails employeeUser = User.builder()
                .username("alex")
                .password(passwordEncoder.encode("password"))
                .roles("EMPLOYEE")
                .build();

        UserDetails hrAdminUser = User.builder()
                .username("sarah")
                .password(passwordEncoder.encode("password"))
                .roles("HR_ADMIN")
                .build();

        return new InMemoryUserDetailsManager(employeeUser, hrAdminUser);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/attendance/check-in", "/api/attendance/check-out", "/api/attendance/my-attendance").hasAnyRole("EMPLOYEE", "HR_ADMIN")
                .requestMatchers("/api/attendance/daily", "/api/attendance/weekly").hasAnyRole("EMPLOYEE", "HR_ADMIN")
                .requestMatchers("/api/attendance/employee/*").hasAnyRole("EMPLOYEE", "HR_ADMIN")
                .requestMatchers("/api/attendance", "/api/attendance/all").hasRole("HR_ADMIN")
                .requestMatchers("/attendance", "/").hasAnyRole("EMPLOYEE", "HR_ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    request -> request.getRequestURI().startsWith("/api/")
                )
            )
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
