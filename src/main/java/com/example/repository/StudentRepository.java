package com.example.repository;

import com.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;   // <-- FIX: import Optional

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Correctly placed inside the interface
    Optional<Student> findByEmail(String email);
}
