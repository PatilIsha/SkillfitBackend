package com.example.repository;

import com.example.model.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultRepository extends JpaRepository<ResultEntity, Long> {
    List<ResultEntity> findByStudentIdOrderByTakenAtDesc(Long studentId);
    List<ResultEntity> findByStudentIdAndRecruiterIdOrderByTakenAtDesc(Long studentId, Long recruiterId);
}
