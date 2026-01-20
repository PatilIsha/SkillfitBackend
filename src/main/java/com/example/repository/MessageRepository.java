package com.example.repository;

import com.example.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByStudentIdOrderBySentAtDesc(Long studentId);
    List<Message> findByRecruiterIdOrderBySentAtDesc(Long recruiterId);
    List<Message> findByRecruiterIdAndStudentIdOrderBySentAtDesc(Long recruiterId, Long studentId);
    List<Message> findBySenderStudentIdOrderBySentAtDesc(Long senderStudentId);
    List<Message> findByStudentIdAndSenderStudentIsNotNullOrderBySentAtDesc(Long studentId);
    long countByStudentIdAndSenderStudentIsNotNull(Long studentId); // For unread count
}

