package com.dayflow.security;

import com.dayflow.DayflowApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DayflowApplication.class)
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSuccessfulEmployeeLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "employee@dayflow.com")
                        .param("password", "employee123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/dashboard"));
    }

    @Test
    public void testSuccessfulHrAdminLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "admin@dayflow.com")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    public void testInvalidLoginCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "employee@dayflow.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    public void testUnauthenticatedUserAccessEmployeeDashboardRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void testUnauthenticatedUserAccessAdminDashboardRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "employee@dayflow.com", roles = {"EMPLOYEE"})
    public void testEmployeeUserAccessEmployeeDashboardSuccess() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-dashboard"));
    }

    @Test
    @WithMockUser(username = "employee@dayflow.com", roles = {"EMPLOYEE"})
    public void testForbiddenEmployeeAccessToAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "employee@dayflow.com", roles = {"EMPLOYEE"})
    public void testForbiddenEmployeeAccessToAdminApi() throws Exception {
        mockMvc.perform(post("/api/admin/config").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void testHrAdminUserAccessAdminDashboardSuccess() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"));
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void testHrAdminUserAccessEmployeeDashboardSuccess() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-dashboard"));
    }
}
