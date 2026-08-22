package com.dayflow.repository;

import com.dayflow.model.Employee;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class EmployeeRepository {
    private final Map<Long, Employee> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1000);

    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(idGenerator.incrementAndGet());
        }
        store.put(employee.getId(), employee);
        return employee;
    }

    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Employee> findByEmployeeId(String employeeId) {
        if (employeeId == null) return Optional.empty();
        return store.values().stream()
                .filter(e -> employeeId.equalsIgnoreCase(e.getEmployeeId()))
                .findFirst();
    }

    public Optional<Employee> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return store.values().stream()
                .filter(e -> email.equalsIgnoreCase(e.getEmail()))
                .findFirst();
    }

    public List<Employee> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    public void clear() {
        store.clear();
    }
}
