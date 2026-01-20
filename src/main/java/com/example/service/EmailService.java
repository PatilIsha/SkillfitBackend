package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Send OTP email for registration
     * @param to Recipient email
     * @param otp OTP code
     * @param name User's name
     */
    public void sendOtpEmail(String to, String otp, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("SkillFit - Email Verification OTP");
            message.setText(
                "Hello " + name + ",\n\n" +
                "Thank you for registering with SkillFit!\n\n" +
                "Your OTP for email verification is: " + otp + "\n\n" +
                "This OTP will expire in 5 minutes.\n\n" +
                "If you didn't register for SkillFit, please ignore this email.\n\n" +
                "Best regards,\n" +
                "SkillFit Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending OTP email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send registration confirmation email
     * @param to Recipient email
     * @param name User's name
     * @param userType Type of user (Student/Recruiter)
     */
    public void sendRegistrationConfirmation(String to, String name, String userType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to SkillFit - Registration Successful!");
            message.setText(
                "Hello " + name + ",\n\n" +
                "Congratulations! Your account has been successfully registered with SkillFit as a " + userType + ".\n\n" +
                "You can now:\n" +
                "- Take assessments and improve your skills\n" +
                "- View your performance and progress\n" +
                "- Connect with recruiters and explore job opportunities\n\n" +
                "We're excited to have you on board!\n\n" +
                "Best regards,\n" +
                "SkillFit Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
