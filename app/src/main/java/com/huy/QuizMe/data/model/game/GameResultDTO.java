package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * DTO cho kết quả trò chơi cuối cùng
 */
public class GameResultDTO implements Serializable {
    @SerializedName("roomId")
    private Long roomId;

    @SerializedName("quizTitle")
    private String quizTitle;

    @SerializedName("totalQuestions")
    private Integer totalQuestions;

    @SerializedName("duration")
    private Integer duration; // Tổng thời gian chơi (giây)

    @SerializedName("finalRankings")
    private List<FinalPlayerRankingDTO> finalRankings;

    @SerializedName("questionStats")
    private List<QuestionStatDTO> questionStats;

    // Constructors
    public GameResultDTO() {
    }

    public GameResultDTO(Long roomId, String quizTitle, Integer totalQuestions, Integer duration,
                         List<FinalPlayerRankingDTO> finalRankings, List<QuestionStatDTO> questionStats) {
        this.roomId = roomId;
        this.quizTitle = quizTitle;
        this.totalQuestions = totalQuestions;
        this.duration = duration;
        this.finalRankings = finalRankings;
        this.questionStats = questionStats;
    }

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<FinalPlayerRankingDTO> getFinalRankings() {
        return finalRankings;
    }

    public void setFinalRankings(List<FinalPlayerRankingDTO> finalRankings) {
        this.finalRankings = finalRankings;
    }

    public List<QuestionStatDTO> getQuestionStats() {
        return questionStats;
    }

    public void setQuestionStats(List<QuestionStatDTO> questionStats) {
        this.questionStats = questionStats;
    }

    /**
     * DTO cho xếp hạng cuối cùng của người chơi (extends PlayerRankingDTO)
     */
    public static class FinalPlayerRankingDTO extends LeaderboardDTO.PlayerRankingDTO {
        @SerializedName("correctAnswers")
        private Integer correctAnswers; // Số câu trả lời đúng

        @SerializedName("totalAnswered")
        private Integer totalAnswered; // Tổng số câu đã trả lời

        @SerializedName("averageAnswerTime")
        private Double averageAnswerTime; // Thời gian trả lời trung bình

        // Constructors
        public FinalPlayerRankingDTO() {
            super();
        }

        public FinalPlayerRankingDTO(Long userId, String username, Integer score, Integer rank,
                                     String avatar, Boolean isGuest, Integer correctCount,
                                     Integer correctAnswers, Integer totalAnswered, Double averageAnswerTime) {
            super(userId, username, score, rank, avatar, isGuest, correctCount);
            this.correctAnswers = correctAnswers;
            this.totalAnswered = totalAnswered;
            this.averageAnswerTime = averageAnswerTime;
        }

        // Getters and Setters
        public Integer getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(Integer correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public Integer getTotalAnswered() {
            return totalAnswered;
        }

        public void setTotalAnswered(Integer totalAnswered) {
            this.totalAnswered = totalAnswered;
        }

        public Double getAverageAnswerTime() {
            return averageAnswerTime;
        }

        public void setAverageAnswerTime(Double averageAnswerTime) {
            this.averageAnswerTime = averageAnswerTime;
        }
    }

    /**
     * DTO cho thống kê câu hỏi
     */
    public static class QuestionStatDTO implements Serializable {
        @SerializedName("questionId")
        private Long questionId;

        @SerializedName("correctPercentage")
        private Double correctPercentage;

        @SerializedName("averageAnswerTime")
        private Double averageAnswerTime;

        @SerializedName("totalAnswered")
        private Integer totalAnswered;

        // Constructors
        public QuestionStatDTO() {
        }

        public QuestionStatDTO(Long questionId, Double correctPercentage, Double averageAnswerTime, Integer totalAnswered) {
            this.questionId = questionId;
            this.correctPercentage = correctPercentage;
            this.averageAnswerTime = averageAnswerTime;
            this.totalAnswered = totalAnswered;
        }

        // Getters and Setters
        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public Double getCorrectPercentage() {
            return correctPercentage;
        }

        public void setCorrectPercentage(Double correctPercentage) {
            this.correctPercentage = correctPercentage;
        }

        public Double getAverageAnswerTime() {
            return averageAnswerTime;
        }

        public void setAverageAnswerTime(Double averageAnswerTime) {
            this.averageAnswerTime = averageAnswerTime;
        }

        public Integer getTotalAnswered() {
            return totalAnswered;
        }

        public void setTotalAnswered(Integer totalAnswered) {
            this.totalAnswered = totalAnswered;
        }
    }
}
