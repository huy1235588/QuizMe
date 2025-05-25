package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * DTO cho sự kiện timer
 */
public class TimerEventDTO implements Serializable {
    @SerializedName("timeRemaining")
    private Integer timeRemaining; // Thời gian còn lại (seconds)

    @SerializedName("totalTime")
    private Integer totalTime; // Tổng thời gian (seconds)

    @SerializedName("questionId")
    private Long questionId; // ID câu hỏi hiện tại

    // Constructors
    public TimerEventDTO() {
    }

    public TimerEventDTO(Integer timeRemaining, Integer totalTime, Long questionId) {
        this.timeRemaining = timeRemaining;
        this.totalTime = totalTime;
        this.questionId = questionId;
    }

    // Getters and Setters
    public Integer getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(Integer timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "TimerEventDTO{" +
                "timeRemaining=" + timeRemaining +
                ", totalTime=" + totalTime +
                ", questionId=" + questionId +
                '}';
    }
}
