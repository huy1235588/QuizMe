package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Container class for game events
 * Contains nested DTOs for different game event types
 */
public class GameEventDTO implements Serializable {

    /**
     * DTO cho sự kiện timer
     */
    public static class TimerEventDTO implements Serializable {
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

    /**
     * DTO cho sự kiện câu hỏi tiếp theo
     */
    public static class NextQuestionEventDTO implements Serializable {
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
}
