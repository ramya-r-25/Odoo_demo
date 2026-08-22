package com.dayflow.repository;

import com.dayflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findByEmailOrEmployeeId(String email, String employeeId);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);
}
