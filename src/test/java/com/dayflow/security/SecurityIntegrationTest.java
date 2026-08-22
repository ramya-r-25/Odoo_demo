package com.dayflow.security;

import com.dayflow.DayflowApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DayflowApplication.class)
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void unauthenticatedUserAccessEmployeeDashboardRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void unauthenticatedUserAccessAdminDashboardRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "employee@dayflow.com", roles = {"EMPLOYEE"})
    public void employeeUserAccessEmployeeDashboardSuccess() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-dashboard"));
    }

    @Test
    @WithMockUser(username = "employee@dayflow.com", roles = {"EMPLOYEE"})
    public void employeeUserAccessAdminDashboardForbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void hrAdminUserAccessAdminDashboardSuccess() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-dashboard"));
    }

    @Test
    @WithMockUser(username = "admin@dayflow.com", roles = {"HR_ADMIN"})
    public void hrAdminUserAccessEmployeeDashboardSuccess() throws Exception {
        mockMvc.perform(get("/employee/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-dashboard"));
    }
}
