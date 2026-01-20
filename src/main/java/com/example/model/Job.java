package com.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 500)
    private String role;
    
    @Column(length = 50)
    private String level; // Beginner/Intermediate/Expert
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "recruiter_id")
    private Long recruiterId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getRecruiterId() { return recruiterId; }
    public void setRecruiterId(Long recruiterId) { this.recruiterId = recruiterId; }
}
