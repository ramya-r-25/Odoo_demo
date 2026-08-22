package com.dayflow.service;

import com.dayflow.model.Payroll;
import com.dayflow.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PayrollService {

    private final PayrollRepository payrollRepository;

    public PayrollService(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<Payroll> findAll() {
        return payrollRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Payroll> findById(Long id) {
        return payrollRepository.findById(id);
    }

    public Payroll save(Payroll payroll) {
        return payrollRepository.save(payroll);
    }

    public void deleteById(Long id) {
        payrollRepository.deleteById(id);
    }

    // ----------------------------------------------------------------
    // Queries — navigate through the JPA association (employee.id)
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public Optional<Payroll> findByEmployeeId(Long employeeId) {
        return payrollRepository.findByEmployee_Id(employeeId);
    }
}
