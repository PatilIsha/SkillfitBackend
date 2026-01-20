package com.example.controller;

import com.example.model.Message;
import com.example.model.Recruiter;
import com.example.model.ResultEntity;
import com.example.model.Student;
import com.example.repository.MessageRepository;
import com.example.repository.RecruiterRepository;
import com.example.repository.ResultRepository;
import com.example.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recruiter")
@CrossOrigin(origins = "http://localhost:3000")
public class RecruiterController {
    private final StudentRepository studentRepo;
    private final ResultRepository resultRepo;
    private final MessageRepository messageRepo;
    private final RecruiterRepository recruiterRepo;

    public RecruiterController(StudentRepository studentRepo, ResultRepository resultRepo, 
                               MessageRepository messageRepo, RecruiterRepository recruiterRepo) {
        this.studentRepo = studentRepo;
        this.resultRepo = resultRepo;
        this.messageRepo = messageRepo;
        this.recruiterRepo = recruiterRepo;
    }

    // Get all students with their latest results (filtered by recruiter if provided)
    @GetMapping("/students")
    public List<Map<String, Object>> getAllStudents(@RequestParam(required = false) Long recruiterId) {
        List<Student> students = studentRepo.findAll();
        return students.stream().map(student -> {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", student.getId());
            studentData.put("name", student.getName());
            studentData.put("email", student.getEmail());
            
            // Get latest result, filtered by recruiterId if provided
            List<ResultEntity> results;
            if (recruiterId != null) {
                results = resultRepo.findByStudentIdAndRecruiterIdOrderByTakenAtDesc(student.getId(), recruiterId);
            } else {
                results = resultRepo.findByStudentIdOrderByTakenAtDesc(student.getId());
            }
            
            if (!results.isEmpty()) {
                ResultEntity latest = results.get(0);
                studentData.put("score", latest.getScore());
                studentData.put("level", latest.getLevel());
                studentData.put("totalQuestions", latest.getTotalQuestions() != null ? latest.getTotalQuestions() : 10);
                studentData.put("takenAt", latest.getTakenAt());
            } else {
                studentData.put("score", 0);
                studentData.put("level", "No test taken");
                studentData.put("totalQuestions", 10);
                studentData.put("takenAt", null);
            }
            
            return studentData;
        }).collect(Collectors.toList());
    }

    // Get student details with all results (filtered by recruiter if provided)
    @GetMapping("/students/{id}")
    public Map<String, Object> getStudentDetails(@PathVariable Long id,
                                                 @RequestParam(required = false) Long recruiterId) {
        Optional<Student> studentOpt = studentRepo.findById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            response.put("id", student.getId());
            response.put("name", student.getName());
            response.put("email", student.getEmail());
            
            // Filter results by recruiterId if provided
            List<ResultEntity> results;
            if (recruiterId != null) {
                results = resultRepo.findByStudentIdAndRecruiterIdOrderByTakenAtDesc(id, recruiterId);
            } else {
                results = resultRepo.findByStudentIdOrderByTakenAtDesc(id);
            }
            
            List<Map<String, Object>> resultsData = results.stream().map(result -> {
                Map<String, Object> resultData = new HashMap<>();
                resultData.put("id", result.getId());
                resultData.put("score", result.getScore());
                resultData.put("level", result.getLevel());
                resultData.put("totalQuestions", result.getTotalQuestions() != null ? result.getTotalQuestions() : 10);
                resultData.put("takenAt", result.getTakenAt());
                return resultData;
            }).collect(Collectors.toList());
            
            response.put("results", resultsData);
        } else {
            response.put("error", "Student not found");
        }
        
        return response;
    }

    // Send a message to a candidate
    @PostMapping("/message/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, Object> req) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long recruiterId = Long.parseLong(req.get("recruiterId").toString());
            Long studentId = Long.parseLong(req.get("studentId").toString());
            String subject = req.get("subject") != null ? req.get("subject").toString() : "Job Opportunity";
            String messageText = req.get("message").toString();
            
            Optional<Recruiter> recruiterOpt = recruiterRepo.findById(recruiterId);
            Optional<Student> studentOpt = studentRepo.findById(studentId);
            
            if (!recruiterOpt.isPresent()) {
                response.put("error", "Recruiter not found");
                return response;
            }
            
            if (!studentOpt.isPresent()) {
                response.put("error", "Student not found");
                return response;
            }
            
            Message message = new Message();
            message.setRecruiter(recruiterOpt.get());
            message.setStudent(studentOpt.get());
            message.setSubject(subject);
            message.setMessage(messageText);
            message.setSentAt(LocalDateTime.now());
            
            messageRepo.save(message);
            
            response.put("message", "Message sent successfully!");
            response.put("success", true);
            return response;
            
        } catch (Exception e) {
            response.put("error", "Error sending message: " + e.getMessage());
            response.put("success", false);
            e.printStackTrace();
            return response;
        }
    }

    // Get messages sent by a recruiter
    @GetMapping("/messages/{recruiterId}")
    public List<Map<String, Object>> getRecruiterMessages(@PathVariable Long recruiterId) {
        List<Message> messages = messageRepo.findByRecruiterIdOrderBySentAtDesc(recruiterId);
        return messages.stream().map(msg -> {
            Map<String, Object> msgData = new HashMap<>();
            msgData.put("id", msg.getId());
            msgData.put("studentId", msg.getStudent().getId());
            msgData.put("studentName", msg.getStudent().getName());
            msgData.put("subject", msg.getSubject());
            msgData.put("message", msg.getMessage());
            msgData.put("sentAt", msg.getSentAt());
            return msgData;
        }).collect(Collectors.toList());
    }
}

