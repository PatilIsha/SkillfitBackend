package com.example.controller;

import com.example.model.ResultEntity;
import com.example.model.Student;
import com.example.repository.ResultRepository;
import com.example.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {
    private final StudentRepository studentRepo;
    private final ResultRepository resultRepo;

    public StudentController(StudentRepository studentRepo, ResultRepository resultRepo) {
        this.studentRepo = studentRepo;
        this.resultRepo = resultRepo;
    }

    @GetMapping("/result/{id}")
    public Map<String, Object> getLatestResult(@PathVariable Long id) {
        Student s = studentRepo.findById(id).orElse(null);
        ResultEntity latest = resultRepo.findByStudentIdOrderByTakenAtDesc(id).stream().findFirst().orElse(null);
        Map<String, Object> resp = new HashMap<>();
        resp.put("studentName", s != null ? s.getName() : "Student");
        if (latest != null) {
            resp.put("score", latest.getScore());
            resp.put("level", latest.getLevel());
            resp.put("totalQuestions", latest.getTotalQuestions() != null ? latest.getTotalQuestions() : 10);
        } else {
            resp.put("score", 0);
            resp.put("level", "Beginner");
            resp.put("totalQuestions", 10);
        }
        return resp;
    }

    // GET ALL ASSESSMENT RESULTS
    @GetMapping("/assessments/{id}")
    public Map<String, Object> getAllAssessments(@PathVariable Long id) {
        Map<String, Object> resp = new HashMap<>();
        List<ResultEntity> results = resultRepo.findByStudentIdOrderByTakenAtDesc(id);
        
        List<Map<String, Object>> assessments = results.stream().map(result -> {
            Map<String, Object> assessment = new HashMap<>();
            assessment.put("id", result.getId());
            assessment.put("score", result.getScore());
            assessment.put("level", result.getLevel());
            assessment.put("totalQuestions", result.getTotalQuestions() != null ? result.getTotalQuestions() : 10);
            assessment.put("takenAt", result.getTakenAt());
            return assessment;
        }).collect(Collectors.toList());
        
        resp.put("assessments", assessments);
        resp.put("total", assessments.size());
        return resp;
    }

    // GET PROFILE
    @GetMapping("/profile/{id}")
    public Map<String, Object> getProfile(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Student> studentOpt = studentRepo.findById(id);
        
        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            res.put("id", s.getId());
            res.put("name", s.getName());
            res.put("email", s.getEmail());
            res.put("college", s.getCollege());
            res.put("degree", s.getDegree());
            res.put("resumePath", s.getResumePath());
            res.put("message", "Profile retrieved successfully");
        } else {
            res.put("message", "Student not found!");
            res.put("error", "NOT_FOUND");
        }
        
        return res;
    }

    // UPDATE PROFILE
    @PutMapping("/profile/{id}")
    public Map<String, Object> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        Optional<Student> studentOpt = studentRepo.findById(id);
        
        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            
            if (req.containsKey("name") && req.get("name") != null && !req.get("name").trim().isEmpty()) {
                s.setName(req.get("name").trim());
            }
            if (req.containsKey("email") && req.get("email") != null && !req.get("email").trim().isEmpty()) {
                s.setEmail(req.get("email").trim());
            }
            if (req.containsKey("college") && req.get("college") != null) {
                s.setCollege(req.get("college").trim());
            }
            if (req.containsKey("degree") && req.get("degree") != null) {
                s.setDegree(req.get("degree").trim());
            }
            if (req.containsKey("resumePath") && req.get("resumePath") != null) {
                s.setResumePath(req.get("resumePath").trim());
            }
            
            studentRepo.save(s);
            res.put("message", "Profile updated successfully!");
            res.put("id", s.getId());
            res.put("name", s.getName());
            res.put("email", s.getEmail());
            res.put("college", s.getCollege());
            res.put("degree", s.getDegree());
            res.put("resumePath", s.getResumePath());
        } else {
            res.put("message", "Student not found!");
            res.put("error", "NOT_FOUND");
        }
        
        return res;
    }
}
