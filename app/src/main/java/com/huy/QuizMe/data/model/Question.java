package com.huy.QuizMe.data.model;

import java.util.List;

public class Question {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private String content;
    private String imageUrl;
    private String audioUrl;
    private Integer timeLimit;
    private Integer points;
    private Integer orderNumber;
    private String type;
    private String createdAt;
    private String updatedAt;
    private List<QuestionOption> options;

    // Default constructor
    public Question() {
    }

    // Parameterized constructor
    public Question(Long id, Long quizId, String quizTitle, String content, String imageUrl, String audioUrl,
                    Integer timeLimit, Integer points, Integer orderNumber, String type, 
                    String createdAt, String updatedAt, List<QuestionOption> options) {
        this.id = id;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.content = content;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.timeLimit = timeLimit;
        this.points = points;
        this.orderNumber = orderNumber;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.options = options;
    }

    public String getCorrectAnswer() {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getIsCorrect()) {
                return options.get(i).getContent();
            }
        }

        return null;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }
}
