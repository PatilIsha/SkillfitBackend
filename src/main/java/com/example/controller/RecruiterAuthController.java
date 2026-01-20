package com.example.controller;

import com.example.model.Recruiter;
import com.example.repository.RecruiterRepository;
import com.example.service.OtpService;
import com.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/recruiter")
@CrossOrigin(origins = "http://localhost:3000")
public class RecruiterAuthController {

    @Autowired
    private RecruiterRepository recruiterRepo;
    
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
        String password = req.get("password");
        
        // Validate input
        if (email == null || email.trim().isEmpty() || 
            name == null || name.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            res.put("message", "Email, name, and password are required!");
            res.put("success", false);
            return res;
        }
        
        // Check if email already exists
        Optional<Recruiter> existingRecruiter = recruiterRepo.findByEmail(email);
        if (existingRecruiter.isPresent()) {
            res.put("message", "Email already registered! Please login instead.");
            res.put("success", false);
            return res;
        }
        
        try {
            // Generate and send OTP
            String otp = otpService.generateOtp(email, name, password);
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
        String password = otpService.getPassword(email);
        
        if (name == null || password == null) {
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
        Optional<Recruiter> existingRecruiter = recruiterRepo.findByEmail(email);
        if (existingRecruiter.isPresent()) {
            res.put("message", "Email already registered! Please login instead.");
            res.put("success", false);
            return res;
        }
        
        try {
            // Create and save recruiter
            Recruiter r = new Recruiter();
            r.setName(name);
            r.setEmail(email);
            r.setPassword(password);  // Later we'll encrypt it

            Recruiter saved = recruiterRepo.save(r);
            
            // Send confirmation email
            emailService.sendRegistrationConfirmation(email, name, "Recruiter");
            
            res.put("message", "Recruiter registration successful! Welcome to SkillFit!");
            res.put("recruiterId", saved.getId());
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
        String password = req.get("password");

        Optional<Recruiter> r = recruiterRepo.findByEmail(email);

        Map<String, Object> res = new HashMap<>();

        if (r.isPresent() && r.get().getPassword().equals(password)) {
            res.put("message", "Login successful!");
            res.put("recruiterId", r.get().getId());
            res.put("name", r.get().getName());
        } else {
            res.put("message", "Invalid credentials!");
        }

        return res;
    }

    // GET PROFILE
    @GetMapping("/profile/{id}")
    public Map<String, Object> getProfile(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Recruiter> recruiterOpt = recruiterRepo.findById(id);
        
        if (recruiterOpt.isPresent()) {
            Recruiter r = recruiterOpt.get();
            res.put("id", r.getId());
            res.put("name", r.getName());
            res.put("email", r.getEmail());
            res.put("company", r.getCompany());
            res.put("role", r.getRole());
            res.put("location", r.getLocation());
            res.put("message", "Profile retrieved successfully");
        } else {
            res.put("message", "Recruiter not found!");
            res.put("error", "NOT_FOUND");
        }
        
        return res;
    }

    // UPDATE PROFILE
    @PutMapping("/profile/{id}")
    public Map<String, Object> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> req) {
        Map<String, Object> res = new HashMap<>();
        Optional<Recruiter> recruiterOpt = recruiterRepo.findById(id);
        
        if (recruiterOpt.isPresent()) {
            Recruiter r = recruiterOpt.get();
            
            if (req.containsKey("name") && req.get("name") != null && !req.get("name").trim().isEmpty()) {
                r.setName(req.get("name").trim());
            }
            if (req.containsKey("email") && req.get("email") != null && !req.get("email").trim().isEmpty()) {
                r.setEmail(req.get("email").trim());
            }
            if (req.containsKey("password") && req.get("password") != null && !req.get("password").trim().isEmpty()) {
                r.setPassword(req.get("password").trim());
            }
            if (req.containsKey("company") && req.get("company") != null) {
                r.setCompany(req.get("company").trim());
            }
            if (req.containsKey("role") && req.get("role") != null) {
                r.setRole(req.get("role").trim());
            }
            if (req.containsKey("location") && req.get("location") != null) {
                r.setLocation(req.get("location").trim());
            }
            
            recruiterRepo.save(r);
            res.put("message", "Profile updated successfully!");
            res.put("id", r.getId());
            res.put("name", r.getName());
            res.put("email", r.getEmail());
            res.put("company", r.getCompany());
            res.put("role", r.getRole());
            res.put("location", r.getLocation());
        } else {
            res.put("message", "Recruiter not found!");
            res.put("error", "NOT_FOUND");
        }
        
        return res;
    }
}
