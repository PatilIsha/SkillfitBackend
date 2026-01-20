package com.example.model;

import java.util.List;

public class Question {
    private int id;
    private String questionText;
    private List<String> options;
    private String correctAnswer;

    public Question(int id, String questionText, List<String> options, String correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public int getId() { return id; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
}
