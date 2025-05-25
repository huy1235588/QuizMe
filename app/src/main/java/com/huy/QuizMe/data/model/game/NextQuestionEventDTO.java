package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * DTO cho sự kiện câu hỏi tiếp theo
 */
public class NextQuestionEventDTO implements Serializable {
    @SerializedName("questionNumber")
    private Integer questionNumber;

    @SerializedName("countdown")
    private Integer countdown; // Thời gian đếm ngược trước khi hiển thị câu hỏi tiếp theo

    @SerializedName("totalQuestions")
    private Integer totalQuestions;

    // Constructors
    public NextQuestionEventDTO() {
    }

    public NextQuestionEventDTO(Integer questionNumber, Integer countdown, Integer totalQuestions) {
        this.questionNumber = questionNumber;
        this.countdown = countdown;
        this.totalQuestions = totalQuestions;
    }

    // Getters and Setters
    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Integer getCountdown() {
        return countdown;
    }

    public void setCountdown(Integer countdown) {
        this.countdown = countdown;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    @Override
    public String toString() {
        return "NextQuestionEventDTO{" +
                "questionNumber=" + questionNumber +
                ", countdown=" + countdown +
                ", totalQuestions=" + totalQuestions +
                '}';
    }
}
