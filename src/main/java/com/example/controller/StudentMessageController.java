package com.example.controller;

import com.example.model.Message;
import com.example.model.Student;
import com.example.repository.MessageRepository;
import com.example.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/message")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentMessageController {
    private final MessageRepository messageRepo;
    private final StudentRepository studentRepo;

    public StudentMessageController(MessageRepository messageRepo, StudentRepository studentRepo) {
        this.messageRepo = messageRepo;
        this.studentRepo = studentRepo;
    }

    // Student sends message to another student
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, Object> req) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long senderId = Long.parseLong(req.get("senderId").toString());
            Long recipientId = Long.parseLong(req.get("recipientId").toString());
            String subject = req.get("subject") != null ? req.get("subject").toString() : "Message";
            String messageText = req.get("message").toString();
            
            Optional<Student> senderOpt = studentRepo.findById(senderId);
            Optional<Student> recipientOpt = studentRepo.findById(recipientId);
            
            if (!senderOpt.isPresent()) {
                response.put("error", "Sender not found");
                response.put("success", false);
                return response;
            }
            
            if (!recipientOpt.isPresent()) {
                response.put("error", "Recipient not found");
                response.put("success", false);
                return response;
            }
            
            Message message = new Message();
            message.setSenderStudent(senderOpt.get());
            message.setStudent(recipientOpt.get());
            message.setRecruiter(null); // Student-to-student message
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

    // Get messages received by a student
    @GetMapping("/received/{studentId}")
    public List<Map<String, Object>> getReceivedMessages(@PathVariable Long studentId) {
        List<Message> messages = messageRepo.findByStudentIdOrderBySentAtDesc(studentId);
        return messages.stream().map(msg -> {
            Map<String, Object> msgData = new HashMap<>();
            msgData.put("id", msg.getId());
            msgData.put("subject", msg.getSubject());
            msgData.put("message", msg.getMessage());
            msgData.put("sentAt", msg.getSentAt());
            
            if (msg.getRecruiter() != null) {
                msgData.put("senderType", "recruiter");
                msgData.put("senderId", msg.getRecruiter().getId());
                msgData.put("senderName", msg.getRecruiter().getName());
            } else if (msg.getSenderStudent() != null) {
                msgData.put("senderType", "student");
                msgData.put("senderId", msg.getSenderStudent().getId());
                msgData.put("senderName", msg.getSenderStudent().getName());
            }
            
            return msgData;
        }).collect(Collectors.toList());
    }

    // Get messages sent by a student
    @GetMapping("/sent/{studentId}")
    public List<Map<String, Object>> getSentMessages(@PathVariable Long studentId) {
        List<Message> messages = messageRepo.findBySenderStudentIdOrderBySentAtDesc(studentId);
        return messages.stream().map(msg -> {
            Map<String, Object> msgData = new HashMap<>();
            msgData.put("id", msg.getId());
            msgData.put("recipientId", msg.getStudent().getId());
            msgData.put("recipientName", msg.getStudent().getName());
            msgData.put("subject", msg.getSubject());
            msgData.put("message", msg.getMessage());
            msgData.put("sentAt", msg.getSentAt());
            return msgData;
        }).collect(Collectors.toList());
    }

    // Get unread message count
    @GetMapping("/unread-count/{studentId}")
    public Map<String, Object> getUnreadCount(@PathVariable Long studentId) {
        long count = messageRepo.countByStudentIdAndSenderStudentIsNotNull(studentId);
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", count);
        return response;
    }
}

