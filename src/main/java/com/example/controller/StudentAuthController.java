package com.example.controller;

import com.example.model.Student;
import com.example.repository.StudentRepository;
import com.example.service.OtpService;
import com.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentAuthController {

    @Autowired
    private StudentRepository studentRepo;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private EmailService emailService;

    // REGISTER - Send OTP to email
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        
        String email = req.get("email");
        String name = req.get("name");
        
        // Validate input
        if (email == null || email.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            res.put("message", "Email and name are required!");
            res.put("success", false);
            return res;
        }
        
        // Check if email already exists
        Optional<Student> existingStudent = studentRepo.findByEmail(email);
        if (existingStudent.isPresent()) {
            res.put("message", "Email already registered! Please login instead.");
            res.put("success", false);
            return res;
        }
        
        try {
            // Generate and send OTP
            String otp = otpService.generateOtp(email, name);
            emailService.sendOtpEmail(email, otp, name);
            
            res.put("message", "OTP sent to your email. Please check your inbox and verify.");
            res.put("success", true);
        } catch (Exception e) {
            res.put("message", "Error sending OTP: " + e.getMessage());
            res.put("success", false);
            e.printStackTrace();
        }
        
        return res;
    }
    
    // VERIFY OTP AND COMPLETE REGISTRATION
    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOtp(@RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        
        String email = req.get("email");
        String otp = req.get("otp");
        
        if (email == null || otp == null) {
            res.put("message", "Email and OTP are required!");
            res.put("success", false);
            return res;
        }
        
        // Get user data before verifying (verifyOtp removes the data)
        String name = otpService.getName(email);
        if (name == null) {
            res.put("message", "OTP not found or expired. Please try registering again.");
            res.put("success", false);
            return res;
        }
        
        // Verify OTP
        if (!otpService.verifyOtp(email, otp)) {
            res.put("message", "Invalid or expired OTP!");
            res.put("success", false);
            return res;
        }
        
        // Check if email already exists (double check)
        Optional<Student> existingStudent = studentRepo.findByEmail(email);
        if (existingStudent.isPresent()) {
            res.put("message", "Email already registered! Please login instead.");
            res.put("success", false);
            return res;
        }
        
        try {
            // Create and save student
            Student s = new Student();
            s.setName(name);
            s.setEmail(email);
            s.setLevel("Beginner");
            s.setScore(0);
            
            Student saved = studentRepo.save(s);
            
            // Send confirmation email
            emailService.sendRegistrationConfirmation(email, name, "Student");
            
            res.put("message", "Registration successful! Welcome to SkillFit!");
            res.put("studentId", saved.getId());
            res.put("name", saved.getName());
            res.put("success", true);
        } catch (Exception e) {
            res.put("message", "Error completing registration: " + e.getMessage());
            res.put("success", false);
            e.printStackTrace();
        }
        
        return res;
    }

    // LOGIN
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {

        String email = req.get("email");
        Optional<Student> s = studentRepo.findByEmail(email);

        Map<String, Object> res = new HashMap<>();

        if (s.isPresent()) {
            res.put("message", "Login successful!");
            res.put("studentId", s.get().getId());
            res.put("name", s.get().getName());
        } else {
            res.put("message", "User not found!");
        }

        return res;
    }
}
