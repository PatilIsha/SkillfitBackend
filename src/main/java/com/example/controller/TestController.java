package com.example.controller;

import com.example.model.Job;
import com.example.model.QuestionEntity;
import com.example.model.ResultEntity;
import com.example.model.Student;
import com.example.repository.JobRepository;
import com.example.repository.QuestionRepository;
import com.example.repository.ResultRepository;
import com.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private JobRepository jobRepository;

    // Get questions without answers (for students to take test)
    @GetMapping("/questions")
    public List<Map<String, Object>> getQuestions(@RequestParam(required = false) Long jobId) {
        List<QuestionEntity> questions;
        
        if (jobId != null) {
            // Get questions specific to this job
            questions = questionRepository.findByJobIdAndIsActiveTrue(jobId);
            // If no job-specific questions, fall back to general questions
            if (questions.isEmpty()) {
                questions = questionRepository.findByJobIdIsNullAndIsActiveTrue();
            }
        } else {
            // Get general questions (not linked to any specific job)
            questions = questionRepository.findByJobIdIsNullAndIsActiveTrue();
        }
        
        // If no questions in database, return default questions without answers
        if (questions.isEmpty()) {
            return getDefaultQuestionsWithoutAnswers();
        }
        
        // Return questions without correct answers
        return questions.stream().map(q -> {
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("id", q.getId());
            questionData.put("questionText", q.getQuestionText());
            questionData.put("options", q.getOptions());
            // Don't include correctAnswer
            return questionData;
        }).collect(Collectors.toList());
    }

    // Get questions with answers (for admin/recruiter to view)
    @GetMapping("/questions/with-answers")
    public List<QuestionEntity> getQuestionsWithAnswers(@RequestParam(required = false) Long recruiterId) {
        if (recruiterId != null) {
            return questionRepository.findByRecruiterIdAndIsActiveTrue(recruiterId);
        }
        return questionRepository.findByIsActiveTrue();
    }

    // Create/Store test questions with answers
    @PostMapping("/questions/create")
    public Map<String, Object> createQuestion(@RequestBody Map<String, Object> req) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuestionEntity question = new QuestionEntity();
            question.setQuestionText(req.get("questionText").toString());
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) req.get("options");
            question.setOptions(options);
            question.setCorrectAnswer(req.get("correctAnswer").toString());
            question.setActive(true);
            
            // Set jobId if provided (optional field)
            if (req.containsKey("jobId") && req.get("jobId") != null) {
                try {
                    Long jobId = Long.parseLong(req.get("jobId").toString());
                    question.setJobId(jobId);
                } catch (NumberFormatException e) {
                    // If jobId is not a valid number, leave it as null
                    question.setJobId(null);
                }
            } else {
                question.setJobId(null);
            }
            
            // Set recruiterId if provided (optional field)
            if (req.containsKey("recruiterId") && req.get("recruiterId") != null) {
                try {
                    Long recruiterId = Long.parseLong(req.get("recruiterId").toString());
                    question.setRecruiterId(recruiterId);
                } catch (NumberFormatException e) {
                    // If recruiterId is not a valid number, leave it as null
                    question.setRecruiterId(null);
                }
            } else {
                question.setRecruiterId(null);
            }
            
            QuestionEntity saved = questionRepository.save(question);
            
            response.put("message", "Question created successfully!");
            response.put("questionId", saved.getId());
            response.put("success", true);
            return response;
            
        } catch (Exception e) {
            response.put("error", "Error creating question: " + e.getMessage());
            response.put("success", false);
            e.printStackTrace();
            return response;
        }
    }

    // Update question
    @PutMapping("/questions/update/{id}")
    public Map<String, Object> updateQuestion(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<QuestionEntity> questionOpt = questionRepository.findById(id);
            
            if (!questionOpt.isPresent()) {
                response.put("error", "Question not found");
                response.put("success", false);
                return response;
            }
            
            QuestionEntity question = questionOpt.get();
            
            if (req.containsKey("questionText") && req.get("questionText") != null) {
                question.setQuestionText(req.get("questionText").toString());
            }
            
            if (req.containsKey("options")) {
                @SuppressWarnings("unchecked")
                List<String> options = (List<String>) req.get("options");
                question.setOptions(options);
            }
            
            if (req.containsKey("correctAnswer") && req.get("correctAnswer") != null) {
                question.setCorrectAnswer(req.get("correctAnswer").toString());
            }
            
            if (req.containsKey("jobId")) {
                if (req.get("jobId") != null && !req.get("jobId").toString().isEmpty()) {
                    try {
                        Long jobId = Long.parseLong(req.get("jobId").toString());
                        question.setJobId(jobId);
                    } catch (NumberFormatException e) {
                        question.setJobId(null);
                    }
                } else {
                    question.setJobId(null);
                }
            }
            
            questionRepository.save(question);
            
            response.put("message", "Question updated successfully!");
            response.put("questionId", question.getId());
            response.put("success", true);
            return response;
            
        } catch (Exception e) {
            response.put("error", "Error updating question: " + e.getMessage());
            response.put("success", false);
            e.printStackTrace();
            return response;
        }
    }

    // Delete question (soft delete by setting isActive to false)
    @DeleteMapping("/questions/delete/{id}")
    public Map<String, Object> deleteQuestion(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<QuestionEntity> questionOpt = questionRepository.findById(id);
            
            if (!questionOpt.isPresent()) {
                response.put("error", "Question not found");
                response.put("success", false);
                return response;
            }
            
            QuestionEntity question = questionOpt.get();
            question.setActive(false); // Soft delete
            questionRepository.save(question);
            
            response.put("message", "Question deleted successfully!");
            response.put("success", true);
            return response;
            
        } catch (Exception e) {
            response.put("error", "Error deleting question: " + e.getMessage());
            response.put("success", false);
            e.printStackTrace();
            return response;
        }
    }

    // Helper method to return default questions without answers
    private List<Map<String, Object>> getDefaultQuestionsWithoutAnswers() {
        List<Map<String, Object>> defaultQuestions = new ArrayList<>();
        
        Map<String, Object> q1 = new HashMap<>();
        q1.put("id", 1);
        q1.put("questionText", "Which language is used for backend development?");
        q1.put("options", Arrays.asList("HTML", "Java", "CSS", "Bootstrap"));
        defaultQuestions.add(q1);
        
        Map<String, Object> q2 = new HashMap<>();
        q2.put("id", 2);
        q2.put("questionText", "React is a ______ library?");
        q2.put("options", Arrays.asList("Backend", "Frontend", "Database", "Testing"));
        defaultQuestions.add(q2);
        
        Map<String, Object> q3 = new HashMap<>();
        q3.put("id", 3);
        q3.put("questionText", "Spring Boot is written in which language?");
        q3.put("options", Arrays.asList("Python", "C#", "Java", "Kotlin"));
        defaultQuestions.add(q3);
        
        Map<String, Object> q4 = new HashMap<>();
        q4.put("id", 4);
        q4.put("questionText", "Which database is NoSQL?");
        q4.put("options", Arrays.asList("MySQL", "MongoDB", "Oracle", "PostgreSQL"));
        defaultQuestions.add(q4);
        
        return defaultQuestions;
    }

    // Get correct answers for scoring (internal use)
    private Map<Long, String> getCorrectAnswers() {
        List<QuestionEntity> questions = questionRepository.findByIsActiveTrue();
        if (questions.isEmpty()) {
            // Return default answers
            Map<Long, String> defaultAnswers = new HashMap<>();
            defaultAnswers.put(1L, "Java");
            defaultAnswers.put(2L, "Frontend");
            defaultAnswers.put(3L, "Java");
            defaultAnswers.put(4L, "MongoDB");
            return defaultAnswers;
        }
        
        return questions.stream()
            .collect(Collectors.toMap(QuestionEntity::getId, QuestionEntity::getCorrectAnswer));
    }

    @PostMapping("/submit")
    public Map<String, Object> submitTest(@RequestBody Map<String, Object> request,
                                          @RequestParam Long studentId,
                                          @RequestParam(required = false) Long jobId) {
        @SuppressWarnings("unchecked")
        Map<String, String> userAnswers = (Map<String, String>) request.get("answers");
        
        if (userAnswers == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Answers not provided");
            return error;
        }

        int score = 0;
        int totalQuestions = 0;
        Long recruiterId = null;

        // Determine recruiterId: first try from job, then from questions
        if (jobId != null) {
            Optional<Job> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isPresent() && jobOpt.get().getRecruiterId() != null) {
                recruiterId = jobOpt.get().getRecruiterId();
            }
        }

        // Get correct answers from database
        Map<Long, String> correctAnswers = getCorrectAnswers();
        totalQuestions = correctAnswers.size();

        // Calculate score and track question details
        List<Map<String, Object>> questionDetails = new ArrayList<>();
        for (Map.Entry<Long, String> entry : correctAnswers.entrySet()) {
            Long questionId = entry.getKey();
            String correctAnswer = entry.getValue();
            String userAnswer = userAnswers.get(questionId.toString());
            
            boolean isCorrect = userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer);
            if (isCorrect) {
                score++;
            }
            
            // Get question details for response
            QuestionEntity question = questionRepository.findById(questionId).orElse(null);
            if (question != null) {
                // If recruiterId not set yet, try to get it from the question
                if (recruiterId == null && question.getRecruiterId() != null) {
                    recruiterId = question.getRecruiterId();
                }
                
                Map<String, Object> qDetail = new HashMap<>();
                qDetail.put("questionId", questionId);
                qDetail.put("questionText", question.getQuestionText());
                qDetail.put("options", question.getOptions());
                qDetail.put("correctAnswer", correctAnswer);
                qDetail.put("userAnswer", userAnswer != null ? userAnswer : "Not answered");
                qDetail.put("isCorrect", isCorrect);
                questionDetails.add(qDetail);
            }
        }

        String level;
        if (totalQuestions == 0) {
            level = "Beginner";
        } else if (score <= totalQuestions * 0.25) {
            level = "Beginner";
        } else if (score <= totalQuestions * 0.75) {
            level = "Intermediate";
        } else {
            level = "Advanced";
        }

        // Save Result
        ResultEntity result = new ResultEntity();
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student != null) {
            result.setStudent(student);
            result.setScore(score);
            result.setLevel(level);
            result.setTotalQuestions(totalQuestions);
            result.setTakenAt(LocalDateTime.now());
            if (recruiterId != null) {
                result.setRecruiterId(recruiterId);
            }
            resultRepository.save(result);

            // Update student latest result
            student.setScore(score);
            student.setLevel(level);
            studentRepository.save(student);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("totalQuestions", totalQuestions);
        response.put("level", level);
        response.put("questionDetails", questionDetails); // Include question details
        response.put("message", "Test submitted successfully! Score saved to profile.");
        return response;
    }
}
