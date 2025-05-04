package com.huy.QuizMe.data.model;

public class QuestionOption {
    private Long id;
    private String content;
    private Boolean isCorrect;

    // Default constructor
    public QuestionOption() {
    }

    // Parameterized constructor
    public QuestionOption(Long id, String content, Boolean isCorrect) {
        this.id = id;
        this.content = content;
        this.isCorrect = isCorrect;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}