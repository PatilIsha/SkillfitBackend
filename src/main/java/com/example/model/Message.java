package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Recruiter recruiter; // null if student-to-student message

    @ManyToOne
    private Student student; // recipient

    @ManyToOne
    private Student senderStudent; // sender (for student-to-student messages)

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentAt;

    private String subject;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Recruiter getRecruiter() { return recruiter; }
    public void setRecruiter(Recruiter recruiter) { this.recruiter = recruiter; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Student getSenderStudent() { return senderStudent; }
    public void setSenderStudent(Student senderStudent) { this.senderStudent = senderStudent; }
}

