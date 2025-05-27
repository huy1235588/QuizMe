package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * DTO cho kết quả của một câu hỏi
 */
public class QuestionResultDTO implements Serializable {
    @SerializedName("questionId")
    private Long questionId;

    @SerializedName("correctOptions")
    private List<Long> correctOptions;

    @SerializedName("explanation")
    private String explanation;

    @SerializedName("funFact")
    private String funFact;

    @SerializedName("optionStats")
    private List<OptionStatDTO> optionStats;

    @SerializedName("userAnswer")
    private UserAnswerDTO userAnswer;

    // Constructors
    public QuestionResultDTO() {
    }

    public QuestionResultDTO(Long questionId, List<Long> correctOptions, String explanation,
                             String funFact, UserAnswerDTO userAnswer, List<OptionStatDTO> optionStats) {
        this.questionId = questionId;
        this.correctOptions = correctOptions;
        this.explanation = explanation;
        this.funFact = funFact;
        this.userAnswer = userAnswer;
        this.optionStats = optionStats;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Long> getCorrectOptions() {
        return correctOptions;
    }

    public void setCorrectOptions(List<Long> correctOptions) {
        this.correctOptions = correctOptions;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getFunFact() {
        return funFact;
    }

    public void setFunFact(String funFact) {
        this.funFact = funFact;
    }

    public List<OptionStatDTO> getOptionStats() {
        return optionStats;
    }

    public void setOptionStats(List<OptionStatDTO> optionStats) {
        this.optionStats = optionStats;
    }

    public UserAnswerDTO getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(UserAnswerDTO userAnswer) {
        this.userAnswer = userAnswer;
    }

    /**
     * DTO cho thông tin câu trả lời của người dùng
     */
    public static class UserAnswerDTO implements Serializable {
        @SerializedName("isCorrect")
        private Boolean isCorrect;

        @SerializedName("score")
        private Integer score;

        @SerializedName("timeTaken")
        private Double timeTaken;

        public UserAnswerDTO() {
        }

        public UserAnswerDTO(Boolean isCorrect, Integer score, Double timeTaken) {
            this.isCorrect = isCorrect;
            this.score = score;
            this.timeTaken = timeTaken;
        }

        // Getters and Setters
        public Boolean getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Double getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(Double timeTaken) {
            this.timeTaken = timeTaken;
        }
    }

    /**
     * DTO cho thống kê tùy chọn
     */
    public static class OptionStatDTO implements Serializable {
        @SerializedName("optionId")
        private Long optionId;

        @SerializedName("percentage")
        private Double percentage;

        public OptionStatDTO() {
        }

        public OptionStatDTO(Long optionId, Double percentage) {
            this.optionId = optionId;
            this.percentage = percentage;
        }

        // Getters and Setters
        public Long getOptionId() {
            return optionId;
        }

        public void setOptionId(Long optionId) {
            this.optionId = optionId;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }
}