package com.dayflow.service;

import com.dayflow.dto.RegisterDto;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserSuccess() {
        RegisterDto dto = new RegisterDto("EMP999", "Test User", "test@dayflow.com", "password123", Role.EMPLOYEE);

        when(userRepository.existsByEmail("test@dayflow.com")).thenReturn(false);
        when(userRepository.existsByEmployeeId("EMP999")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registeredUser = authService.registerUser(dto);

        assertNotNull(registeredUser);
        assertEquals("EMP999", registeredUser.getEmployeeId());
        assertEquals("test@dayflow.com", registeredUser.getEmail());
        assertEquals("encodedPassword123", registeredUser.getPassword());
        assertEquals(Role.EMPLOYEE, registeredUser.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserDuplicateEmailThrowsException() {
        RegisterDto dto = new RegisterDto("EMP999", "Test User", "test@dayflow.com", "password123", Role.EMPLOYEE);
        when(userRepository.existsByEmail("test@dayflow.com")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.registerUser(dto));
        assertTrue(exception.getMessage().contains("Email is already registered"));
    }

    @Test
    void testRegisterUserDuplicateEmployeeIdThrowsException() {
        RegisterDto dto = new RegisterDto("EMP999", "Test User", "test@dayflow.com", "password123", Role.EMPLOYEE);
        when(userRepository.existsByEmail("test@dayflow.com")).thenReturn(false);
        when(userRepository.existsByEmployeeId("EMP999")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> authService.registerUser(dto));
        assertTrue(exception.getMessage().contains("Employee ID is already in use"));
    }
}
