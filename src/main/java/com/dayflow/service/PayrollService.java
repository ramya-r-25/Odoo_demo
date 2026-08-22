package com.dayflow.service;

import com.dayflow.model.Payroll;
import com.dayflow.repository.PayrollRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;

    public PayrollService(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------
    public List<Payroll> findAll() {
        return payrollRepository.findAll();
    }

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
    // Queries
    // ----------------------------------------------------------------
    public Optional<Payroll> findByEmployeeId(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }
}
