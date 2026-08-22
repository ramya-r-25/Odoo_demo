package com.dayflow.service;

import com.dayflow.model.SalaryStructure;
import com.dayflow.repository.SalaryStructureRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;

    public SalaryStructureService(SalaryStructureRepository salaryStructureRepository) {
        this.salaryStructureRepository = salaryStructureRepository;
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------
    public List<SalaryStructure> findAll() {
        return salaryStructureRepository.findAll();
    }

    public List<SalaryStructure> findAllActive() {
        return salaryStructureRepository.findByActiveTrue();
    }

    public Optional<SalaryStructure> findById(Long id) {
        return salaryStructureRepository.findById(id);
    }

    public SalaryStructure save(SalaryStructure salaryStructure) {
        return salaryStructureRepository.save(salaryStructure);
    }

    public void deleteById(Long id) {
        salaryStructureRepository.deleteById(id);
    }
}
