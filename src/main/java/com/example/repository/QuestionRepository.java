package com.example.repository;

import com.example.model.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByIsActiveTrue();
    List<QuestionEntity> findByJobIdAndIsActiveTrue(Long jobId);
    List<QuestionEntity> findByJobIdIsNullAndIsActiveTrue();
    List<QuestionEntity> findByRecruiterIdAndIsActiveTrue(Long recruiterId);
}

