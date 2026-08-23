package com.dayflow;

import com.dayflow.controller.EmployeeController;
import com.dayflow.model.Employee;
import com.dayflow.model.Role;
import com.dayflow.model.User;
import com.dayflow.repository.EmployeeRepository;
import com.dayflow.service.EmployeeService;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DayflowApplication {

    private static EmployeeRepository repository;
    private static EmployeeService service;
    private static EmployeeController controller;

    private static final User alexUser = new User(1L, "alex.morgan", "alex.morgan@dayflow.com", "pass123", Role.EMPLOYEE, "EMP-001");
    private static final User adminUser = new User(3L, "hr.admin", "admin@dayflow.com", "admin123", Role.HR_ADMIN, "EMP-000");

    private static User currentSessionUser = adminUser;

    public static void main(String[] args) throws IOException {
        System.out.println("=================================================================");
        System.out.println("   STARTING DAYFLOW HRMS WEB SERVER ON HTTP://LOCALHOST:8080    ");
        System.out.println("=================================================================");

        // 1. Initialize Repository, Service, and Controller
        repository = new EmployeeRepository();
        service = new EmployeeService(repository);
        controller = new EmployeeController(service);

        // 2. Seed Initial Employee Records
        service.createEmployee(new Employee(
                null, "EMP-001", "Alex Morgan", "alex.morgan@dayflow.com",
                "+1-555-0192", "123 Tech Blvd, Suite 400", "Software Engineer",
                "Engineering", 85000.0, "avatar_alex.png",
                Arrays.asList("resume.pdf", "contract.pdf")
        ));

        service.createEmployee(new Employee(
                null, "EMP-002", "Sarah Jenkins", "sarah.jenkins@dayflow.com",
                "+1-555-0193", "456 HR Way, Floor 2", "HR Specialist",
                "Human Resources", 85000.0, "avatar_sarah.png",
                Arrays.asList("id_proof.pdf")
        ));

        System.out.println("[INFO] Seeded " + repository.findAll().size() + " employee records.");

        // 3. Create HTTP Server on port 8080
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new DayflowHttpHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("=================================================================");
        System.out.println("   DAYFLOW HRMS WEB APP RUNNING PERFECTLY!                        ");
        System.out.println("   OPEN IN BROWSER: http://localhost:" + port + "                     ");
        System.out.println("=================================================================");
    }

    static class DayflowHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();

            System.out.println("[HTTP " + method + "] " + path + (query != null ? "?" + query : ""));

            try {
                if (path.equals("/") || path.equals("/index.html")) {
                    redirect(exchange, "/login");
                    return;
                }

                if (path.equals("/login")) {
                    if ("POST".equalsIgnoreCase(method)) {
                        Map<String, String> formData = parseFormData(exchange);
                        String username = formData.get("username");
                        if (username != null && username.toLowerCase().contains("alex")) {
                            currentSessionUser = alexUser;
                        } else {
                            currentSessionUser = adminUser;
                        }
                        redirect(exchange, "/dashboard");
                        return;
                    }
                    serveTemplate(exchange, "login.html", new HashMap<>());
                    return;
                }

                if (path.equals("/logout")) {
                    currentSessionUser = adminUser;
                    redirect(exchange, "/login?logout=true");
                    return;
                }

                if (path.equals("/dashboard")) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("currentUserName", currentSessionUser.getUsername());
                    model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                    serveTemplate(exchange, "dashboard.html", model);
                    return;
                }

                if (path.equals("/employees")) {
                    if (currentSessionUser.getRole() != Role.HR_ADMIN) {
                        redirect(exchange, "/employees/me");
                        return;
                    }
                    List<Employee> list = controller.getEmployeeList(currentSessionUser);
                    Map<String, Object> model = new HashMap<>();
                    model.put("employees", list);
                    model.put("isHrAdmin", true);
                    model.put("currentUserName", currentSessionUser.getUsername());
                    serveTemplate(exchange, "employees/list.html", model);
                    return;
                }

                if (path.equals("/employees/me")) {
                    Employee ownProfile = controller.getMyProfile(currentSessionUser);
                    Map<String, Object> model = new HashMap<>();
                    model.put("employee", ownProfile);
                    model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                    model.put("currentUserName", currentSessionUser.getUsername());
                    serveTemplate(exchange, "employees/profile.html", model);
                    return;
                }

                if (path.startsWith("/employees/")) {
                    String subPath = path.substring("/employees/".length());
                    
                    if (subPath.equals("new")) {
                        Map<String, Object> model = new HashMap<>();
                        model.put("employee", new Employee());
                        model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                        model.put("currentUserName", currentSessionUser.getUsername());
                        serveTemplate(exchange, "employees/form.html", model);
                        return;
                    }

                    if (subPath.endsWith("/edit")) {
                        String idStr = subPath.substring(0, subPath.indexOf("/edit"));
                        Long id = Long.parseLong(idStr);
                        Employee target = controller.getEmployeeProfile(id, currentSessionUser);
                        Map<String, Object> model = new HashMap<>();
                        model.put("employee", target);
                        model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                        model.put("currentUserName", currentSessionUser.getUsername());
                        serveTemplate(exchange, "employees/form.html", model);
                        return;
                    }

                    if (subPath.endsWith("/update") && "POST".equalsIgnoreCase(method)) {
                        String idStr = subPath.substring(0, subPath.indexOf("/update"));
                        Long id = Long.parseLong(idStr);
                        Map<String, String> formData = parseFormData(exchange);
                        
                        Employee input = new Employee();
                        input.setEmployeeId(formData.get("employeeId"));
                        input.setFullName(formData.get("fullName"));
                        input.setEmail(formData.get("email"));
                        input.setJobPosition(formData.get("jobPosition"));
                        input.setDepartment(formData.get("department"));
                        input.setPhone(formData.get("phone"));
                        input.setAddress(formData.get("address"));
                        input.setProfilePicture(formData.get("profilePicture"));
                        if (formData.get("salary") != null && !formData.get("salary").isEmpty()) {
                            try { input.setSalary(Double.parseDouble(formData.get("salary"))); } catch (Exception ignored){}
                        }

                        controller.updateEmployee(id, input, currentSessionUser);
                        redirect(exchange, "/employees/" + id);
                        return;
                    }

                    // View individual profile
                    try {
                        Long id = Long.parseLong(subPath);
                        Employee target = controller.getEmployeeProfile(id, currentSessionUser);
                        Map<String, Object> model = new HashMap<>();
                        model.put("employee", target);
                        model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                        model.put("currentUserName", currentSessionUser.getUsername());
                        serveTemplate(exchange, "employees/profile.html", model);
                        return;
                    } catch (NumberFormatException ignored) {}
                }

                if (path.equals("/attendance")) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                    model.put("currentUserName", currentSessionUser.getUsername());
                    serveTemplate(exchange, "attendance.html", model);
                    return;
                }

                if (path.equals("/leave")) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                    model.put("currentUserName", currentSessionUser.getUsername());
                    serveTemplate(exchange, "leave.html", model);
                    return;
                }

                if (path.equals("/payroll")) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                    model.put("currentUserName", currentSessionUser.getUsername());
                    serveTemplate(exchange, "payroll.html", model);
                    return;
                }

                if (path.equals("/reports")) {
                    Map<String, Object> model = new HashMap<>();
                    model.put("isHrAdmin", currentSessionUser.getRole() == Role.HR_ADMIN);
                    model.put("currentUserName", currentSessionUser.getUsername());
                    serveTemplate(exchange, "reports.html", model);
                    return;
                }

                // Fallback static resource file server
                serveStaticFile(exchange, path);

            } catch (SecurityException secEx) {
                sendResponse(exchange, 403, "<h1>403 Forbidden</h1><p>" + secEx.getMessage() + "</p>");
            } catch (Exception ex) {
                ex.printStackTrace();
                sendResponse(exchange, 500, "<h1>500 Internal Error</h1><p>" + ex.getMessage() + "</p>");
            }
        }

        private void serveTemplate(HttpExchange exchange, String templatePath, Map<String, Object> model) throws IOException {
            Path file = Paths.get("src/main/resources/templates", templatePath);
            if (!Files.exists(file)) {
                sendResponse(exchange, 404, "<h1>404 Not Found</h1><p>Template file not found: " + templatePath + "</p>");
                return;
            }

            String content = Files.readString(file, StandardCharsets.UTF_8);

            // Populate Thymeleaf / Dynamic Model Variables
            boolean isHrAdmin = model.containsKey("isHrAdmin") && (Boolean) model.get("isHrAdmin");
            String username = model.containsKey("currentUserName") ? (String) model.get("currentUserName") : "User";

            content = content.replace("th:if=\"${isHrAdmin}\"", isHrAdmin ? "" : "style=\"display:none;\"");
            content = content.replace("th:if=\"${!isHrAdmin}\"", !isHrAdmin ? "" : "style=\"display:none;\"");
            content = content.replace("th:text=\"${currentUserName}\"", username);
            content = content.replace("th:text=\"${isHrAdmin ? 'HR Admin' : 'Employee'}\"", isHrAdmin ? "HR Admin" : "Employee");

            if (model.containsKey("employee")) {
                Employee emp = (Employee) model.get("employee");
                if (emp != null) {
                    content = content.replace("th:text=\"${employee.fullName}\"", emp.getFullName() != null ? emp.getFullName() : "");
                    content = content.replace("th:text=\"${employee.employeeId}\"", emp.getEmployeeId() != null ? emp.getEmployeeId() : "");
                    content = content.replace("th:text=\"${employee.email}\"", emp.getEmail() != null ? emp.getEmail() : "");
                    content = content.replace("th:text=\"${employee.phone}\"", emp.getPhone() != null ? emp.getPhone() : "");
                    content = content.replace("th:text=\"${employee.address}\"", emp.getAddress() != null ? emp.getAddress() : "");
                    content = content.replace("th:text=\"${employee.jobPosition}\"", emp.getJobPosition() != null ? emp.getJobPosition() : "");
                    content = content.replace("th:text=\"${employee.department}\"", emp.getDepartment() != null ? emp.getDepartment() : "");
                    content = content.replace("th:text=\"${'$' + employee.salary}\"", "$" + (emp.getSalary() != null ? emp.getSalary() : 0.0));
                    content = content.replace("th:value=\"${employee != null ? employee.employeeId : 'EMP-001'}\"", "value=\"" + (emp.getEmployeeId() != null ? emp.getEmployeeId() : "") + "\"");
                    content = content.replace("th:value=\"${employee != null ? employee.fullName : 'Alex Morgan'}\"", "value=\"" + (emp.getFullName() != null ? emp.getFullName() : "") + "\"");
                    content = content.replace("th:value=\"${employee != null ? employee.email : 'alex.morgan@dayflow.com'}\"", "value=\"" + (emp.getEmail() != null ? emp.getEmail() : "") + "\"");
                    content = content.replace("th:value=\"${employee != null ? employee.phone : '+1-555-0192'}\"", "value=\"" + (emp.getPhone() != null ? emp.getPhone() : "") + "\"");
                    content = content.replace("th:value=\"${employee != null ? employee.jobPosition : 'Software Engineer'}\"", "value=\"" + (emp.getJobPosition() != null ? emp.getJobPosition() : "") + "\"");
                    content = content.replace("th:value=\"${employee != null ? employee.department : 'Engineering'}\"", "value=\"" + (emp.getDepartment() != null ? emp.getDepartment() : "") + "\"");
                    content = content.replace("th:value=\"${employee != null ? employee.salary : '85000'}\"", "value=\"" + (emp.getSalary() != null ? emp.getSalary() : 0.0) + "\"");
                }
            }

            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void serveStaticFile(HttpExchange exchange, String requestPath) throws IOException {
            Path file = Paths.get("src/main/resources/templates", requestPath);
            if (!Files.exists(file)) {
                file = Paths.get("src/main/resources/templates/employees", requestPath);
            }

            if (Files.exists(file) && !Files.isDirectory(file)) {
                byte[] bytes = Files.readAllBytes(file);
                String contentType = requestPath.endsWith(".css") ? "text/css" : "text/html";
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                sendResponse(exchange, 404, "<h1>404 File Not Found</h1><p>Resource path: " + requestPath + "</p>");
            }
        }

        private void redirect(HttpExchange exchange, String location) throws IOException {
            exchange.getResponseHeaders().set("Location", location);
            exchange.sendResponseHeaders(302, -1);
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
            byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
            Map<String, String> map = new HashMap<>();
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) return map;

            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
                map.put(key, value);
            }
            return map;
        }
    }
}
