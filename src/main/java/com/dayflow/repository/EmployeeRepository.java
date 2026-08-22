package com.dayflow.repository;

import com.dayflow.model.Employee;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Transactional
public class EmployeeRepository {
    private final Map<Long, Employee> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1000);

    @PersistenceContext
    private EntityManager entityManager;

    public Employee save(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(idGenerator.incrementAndGet());
        }
        store.put(employee.getId(), employee);
        
        if (entityManager != null) {
            entityManager.merge(employee);
        }
        return employee;
    }

    public Optional<Employee> findById(Long id) {
        if (entityManager != null) {
            Employee emp = entityManager.find(Employee.class, id);
            if (emp != null) {
                store.put(id, emp);
                return Optional.of(emp);
            }
        }
        return Optional.ofNullable(store.get(id));
    }

    public Optional<Employee> findByEmployeeId(String employeeId) {
        if (employeeId == null) return Optional.empty();
        if (entityManager != null) {
            try {
                Employee emp = entityManager.createQuery("SELECT e FROM Employee e WHERE LOWER(e.employeeId) = :empId", Employee.class)
                        .setParameter("empId", employeeId.toLowerCase())
                        .getSingleResult();
                store.put(emp.getId(), emp);
                return Optional.of(emp);
            } catch (Exception e) {
                // fallback
            }
        }
        return store.values().stream()
                .filter(e -> employeeId.equalsIgnoreCase(e.getEmployeeId()))
                .findFirst();
    }

    public Optional<Employee> findByEmail(String email) {
        if (email == null) return Optional.empty();
        if (entityManager != null) {
            try {
                Employee emp = entityManager.createQuery("SELECT e FROM Employee e WHERE LOWER(e.email) = :email", Employee.class)
                        .setParameter("email", email.toLowerCase())
                        .getSingleResult();
                store.put(emp.getId(), emp);
                return Optional.of(emp);
            } catch (Exception e) {
                // fallback
            }
        }
        return store.values().stream()
                .filter(e -> email.equalsIgnoreCase(e.getEmail()))
                .findFirst();
    }

    public List<Employee> findAll() {
        if (entityManager != null) {
            List<Employee> list = entityManager.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();
            list.forEach(emp -> store.put(emp.getId(), emp));
            return list;
        }
        return new ArrayList<>(store.values());
    }

    public boolean deleteById(Long id) {
        if (entityManager != null) {
            Employee emp = entityManager.find(Employee.class, id);
            if (emp != null) {
                entityManager.remove(emp);
            }
        }
        return store.remove(id) != null;
    }

    public boolean existsById(Long id) {
        if (entityManager != null) {
            return entityManager.find(Employee.class, id) != null;
        }
        return store.containsKey(id);
    }

    public void clear() {
        store.clear();
        if (entityManager != null) {
            entityManager.createQuery("DELETE FROM Employee").executeUpdate();
        }
    }
}
