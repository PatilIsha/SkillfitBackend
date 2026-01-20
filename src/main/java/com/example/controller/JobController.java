package com.example.controller;

import com.example.model.Job;
import com.example.model.Recruiter;
import com.example.repository.JobRepository;
import com.example.repository.RecruiterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:3000")
public class JobController {
    private final JobRepository jobRepo;
    private final RecruiterRepository recruiterRepo;

    public JobController(JobRepository jobRepo, RecruiterRepository recruiterRepo) { 
        this.jobRepo = jobRepo;
        this.recruiterRepo = recruiterRepo;
    }

    @GetMapping("/recommend/{level}")
    public List<Map<String, Object>> recommendByLevel(@PathVariable String level) {
        List<Job> jobs = jobRepo.findByLevel(level);
        return jobs.stream().map(job -> {
            Map<String, Object> jobData = new HashMap<>();
            jobData.put("id", job.getId());
            jobData.put("role", job.getRole());
            jobData.put("level", job.getLevel());
            jobData.put("description", job.getDescription());
            jobData.put("recruiterId", job.getRecruiterId());
            
            // Get company name from recruiter
            if (job.getRecruiterId() != null) {
                Optional<Recruiter> recruiterOpt = recruiterRepo.findById(job.getRecruiterId());
                if (recruiterOpt.isPresent()) {
                    Recruiter recruiter = recruiterOpt.get();
                    jobData.put("companyName", recruiter.getCompany() != null ? recruiter.getCompany() : "Unknown Company");
                } else {
                    jobData.put("companyName", "Unknown Company");
                }
            } else {
                jobData.put("companyName", "General");
            }
            
            return jobData;
        }).collect(Collectors.toList());
    }

    // Get all jobs with company name
    @GetMapping("/all")
    public List<Map<String, Object>> getAllJobs() {
        List<Job> jobs = jobRepo.findAll();
        return jobs.stream().map(job -> {
            Map<String, Object> jobData = new HashMap<>();
            jobData.put("id", job.getId());
            jobData.put("role", job.getRole());
            jobData.put("level", job.getLevel());
            jobData.put("description", job.getDescription());
            jobData.put("recruiterId", job.getRecruiterId());
            
            // Get company name from recruiter
            if (job.getRecruiterId() != null) {
                Optional<Recruiter> recruiterOpt = recruiterRepo.findById(job.getRecruiterId());
                if (recruiterOpt.isPresent()) {
                    Recruiter recruiter = recruiterOpt.get();
                    jobData.put("companyName", recruiter.getCompany() != null ? recruiter.getCompany() : "Unknown Company");
                } else {
                    jobData.put("companyName", "Unknown Company");
                }
            } else {
                jobData.put("companyName", "General");
            }
            
            return jobData;
        }).collect(Collectors.toList());
    }
    
    // Get jobs by recruiter ID with company name
    @GetMapping("/by-recruiter/{recruiterId}")
    public List<Map<String, Object>> getJobsByRecruiter(@PathVariable Long recruiterId) {
        List<Job> jobs = jobRepo.findByRecruiterId(recruiterId);
        Optional<Recruiter> recruiterOpt = recruiterRepo.findById(recruiterId);
        String companyName = recruiterOpt.isPresent() && recruiterOpt.get().getCompany() != null 
            ? recruiterOpt.get().getCompany() 
            : "Unknown Company";
        
        return jobs.stream().map(job -> {
            Map<String, Object> jobData = new HashMap<>();
            jobData.put("id", job.getId());
            jobData.put("role", job.getRole());
            jobData.put("level", job.getLevel());
            jobData.put("description", job.getDescription());
            jobData.put("recruiterId", job.getRecruiterId());
            jobData.put("companyName", companyName);
            return jobData;
        }).collect(Collectors.toList());
    }

    // Test endpoint to verify the controller is working
    @GetMapping("/test")
    public Map<String, Object> testEndpoint() {
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Job Controller is working!");
        res.put("endpoints", List.of(
            "GET /api/jobs/all - Get all jobs",
            "GET /api/jobs/recommend/{level} - Get jobs by level",
            "POST /api/jobs/create - Create a new job",
            "PUT /api/jobs/update/{id} - Update a job",
            "DELETE /api/jobs/delete/{id} - Delete a job"
        ));
        return res;
    }

    // Test database connection
    @GetMapping("/test-db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> res = new HashMap<>();
        try {
            long count = jobRepo.count();
            res.put("message", "Database connection successful!");
            res.put("jobCount", count);
            res.put("status", "OK");
        } catch (Exception e) {
            res.put("message", "Database error: " + e.getMessage());
            res.put("error", e.getClass().getSimpleName());
            res.put("status", "ERROR");
            e.printStackTrace();
        }
        return res;
    }

    // Create a new job
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<Map<String, Object>> createJob(@RequestBody(required = false) Map<String, Object> req) {
        Map<String, Object> res = new HashMap<>();
        
        try {
            System.out.println("=== RECEIVED CREATE JOB REQUEST ===");
            System.out.println("Request body: " + req);
            System.out.println("Request body type: " + (req != null ? req.getClass().getName() : "null"));
            
            if (req == null) {
                System.err.println("ERROR: Request body is null!");
                res.put("message", "Request body is required");
                res.put("error", "INVALID_REQUEST");
                return ResponseEntity.badRequest().body(res);
            }
            
            String role = req.get("role") != null ? req.get("role").toString() : null;
            String level = req.get("level") != null ? req.get("level").toString() : null;
            String description = req.get("description") != null ? req.get("description").toString() : null;
            Long recruiterId = null;
            if (req.get("recruiterId") != null) {
                try {
                    recruiterId = Long.parseLong(req.get("recruiterId").toString());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid recruiterId format: " + req.get("recruiterId"));
                }
            }
            
            System.out.println("Extracted values - Role: " + role + ", Level: " + level + ", Description length: " + (description != null ? description.length() : 0) + ", RecruiterId: " + recruiterId);
            
            if (role == null || role.trim().isEmpty()) {
                res.put("message", "Job role is required");
                res.put("error", "VALIDATION_ERROR");
                return ResponseEntity.badRequest().body(res);
            }
            
            if (level == null || level.trim().isEmpty()) {
                res.put("message", "Job level is required");
                res.put("error", "VALIDATION_ERROR");
                return ResponseEntity.badRequest().body(res);
            }
            
            if (description == null || description.trim().isEmpty()) {
                res.put("message", "Job description is required");
                res.put("error", "VALIDATION_ERROR");
                return ResponseEntity.badRequest().body(res);
            }
            
            Job job = new Job();
            job.setRole(role.trim());
            job.setLevel(level.trim());
            // Clean description - remove leading/trailing quotes if present
            String cleanDescription = description.trim();
            if (cleanDescription.startsWith("\"") && cleanDescription.endsWith("\"")) {
                cleanDescription = cleanDescription.substring(1, cleanDescription.length() - 1);
            }
            job.setDescription(cleanDescription);
            if (recruiterId != null) {
                job.setRecruiterId(recruiterId);
            }
            
            System.out.println("Attempting to save job to database...");
            System.out.println("Job object: role=" + job.getRole() + ", level=" + job.getLevel() + ", description length=" + (job.getDescription() != null ? job.getDescription().length() : 0));
            
            Job saved = jobRepo.save(job);
            
            System.out.println("Job saved successfully with ID: " + saved.getId());
            
            res.put("message", "Job created successfully!");
            res.put("jobId", saved.getId());
            return ResponseEntity.ok(res);
            
        } catch (org.springframework.dao.DataAccessException e) {
            res.put("message", "Database error: " + e.getMessage());
            res.put("error", "DATABASE_ERROR");
            res.put("details", e.getClass().getSimpleName());
            res.put("rootCause", e.getRootCause() != null ? e.getRootCause().getMessage() : "No root cause");
            
            System.err.println("=== DATABASE ERROR CREATING JOB ===");
            System.err.println("Exception: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            if (e.getRootCause() != null) {
                System.err.println("Root Cause: " + e.getRootCause().getMessage());
            }
            e.printStackTrace();
            System.err.println("===================================");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        } catch (Exception e) {
            res.put("message", "Error creating job: " + e.getMessage());
            res.put("error", "SERVER_ERROR");
            res.put("details", e.getClass().getSimpleName());
            res.put("cause", e.getCause() != null ? e.getCause().getMessage() : "No cause");
            
            // Log full stack trace
            System.err.println("=== ERROR CREATING JOB ===");
            System.err.println("Exception Type: " + e.getClass().getName());
            System.err.println("Exception Message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("==========================");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    // Update a job
    @PutMapping("/update/{id}")
    public Map<String, Object> updateJob(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        Map<String, Object> res = new java.util.HashMap<>();
        
        try {
            if (req == null) {
                res.put("message", "Request body is required");
                res.put("error", "INVALID_REQUEST");
                return res;
            }
            
            Optional<Job> jobOpt = jobRepo.findById(id);
            
            if (jobOpt.isPresent()) {
                Job job = jobOpt.get();
                if (req.containsKey("role") && req.get("role") != null) {
                    job.setRole(req.get("role").toString().trim());
                }
                if (req.containsKey("level") && req.get("level") != null) {
                    job.setLevel(req.get("level").toString().trim());
                }
                if (req.containsKey("description") && req.get("description") != null) {
                    job.setDescription(req.get("description").toString().trim());
                }
                if (req.containsKey("recruiterId") && req.get("recruiterId") != null) {
                    try {
                        job.setRecruiterId(Long.parseLong(req.get("recruiterId").toString()));
                    } catch (NumberFormatException e) {
                        // Ignore invalid recruiterId
                    }
                }
                
                jobRepo.save(job);
                res.put("message", "Job updated successfully!");
            } else {
                res.put("message", "Job not found!");
                res.put("error", "NOT_FOUND");
            }
        } catch (Exception e) {
            res.put("message", "Error updating job: " + e.getMessage());
            res.put("error", "SERVER_ERROR");
            e.printStackTrace();
        }
        return res;
    }

    // Delete a job
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteJob(@PathVariable Long id) {
        Map<String, Object> res = new java.util.HashMap<>();
        if (jobRepo.existsById(id)) {
            jobRepo.deleteById(id);
            res.put("message", "Job deleted successfully!");
        } else {
            res.put("message", "Job not found!");
        }
        return res;
    }
}
