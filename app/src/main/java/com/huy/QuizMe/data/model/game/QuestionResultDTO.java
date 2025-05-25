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

    @SerializedName("correctAnswer")
    private String correctAnswer; // Dành cho TYPE_ANSWER

    @SerializedName("userAnswer")
    private UserAnswerDTO userAnswer;

    @SerializedName("optionStats")
    private List<OptionStatDTO> optionStats;

    @SerializedName("explanations")
    private String explanations;

    // Constructors
    public QuestionResultDTO() {
    }

    public QuestionResultDTO(Long questionId, List<Long> correctOptions, String correctAnswer,
                             UserAnswerDTO userAnswer, List<OptionStatDTO> optionStats, String explanations) {
        this.questionId = questionId;
        this.correctOptions = correctOptions;
        this.correctAnswer = correctAnswer;
        this.userAnswer = userAnswer;
        this.optionStats = optionStats;
        this.explanations = explanations;
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public UserAnswerDTO getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(UserAnswerDTO userAnswer) {
        this.userAnswer = userAnswer;
    }

    public List<OptionStatDTO> getOptionStats() {
        return optionStats;
    }

    public void setOptionStats(List<OptionStatDTO> optionStats) {
        this.optionStats = optionStats;
    }

    public String getExplanations() {
        return explanations;
    }

    public void setExplanations(String explanations) {
        this.explanations = explanations;
    }

    /**
     * DTO cho câu trả lời của người dùng
     */
    public static class UserAnswerDTO implements Serializable {
        @SerializedName("selectedOptions")
        private List<Long> selectedOptions;

        @SerializedName("textAnswer")
        private String textAnswer;

        @SerializedName("isCorrect")
        private Boolean isCorrect;

        @SerializedName("score")
        private Integer score;

        @SerializedName("answerTime")
        private Long answerTime;

        public UserAnswerDTO() {
        }

        public UserAnswerDTO(List<Long> selectedOptions, String textAnswer, Boolean isCorrect,
                             Integer score, Long answerTime) {
            this.selectedOptions = selectedOptions;
            this.textAnswer = textAnswer;
            this.isCorrect = isCorrect;
            this.score = score;
            this.answerTime = answerTime;
        }

        // Getters and Setters
        public List<Long> getSelectedOptions() {
            return selectedOptions;
        }

        public void setSelectedOptions(List<Long> selectedOptions) {
            this.selectedOptions = selectedOptions;
        }

        public String getTextAnswer() {
            return textAnswer;
        }

        public void setTextAnswer(String textAnswer) {
            this.textAnswer = textAnswer;
        }

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

        public Long getAnswerTime() {
            return answerTime;
        }

        public void setAnswerTime(Long answerTime) {
            this.answerTime = answerTime;
        }
    }

    /**
     * DTO cho thống kê tùy chọn
     */
    public static class OptionStatDTO implements Serializable {
        @SerializedName("optionId")
        private Long optionId;

        @SerializedName("content")
        private String content;

        @SerializedName("selectedCount")
        private Integer selectedCount;

        @SerializedName("percentage")
        private Double percentage;

        public OptionStatDTO() {
        }

        public OptionStatDTO(Long optionId, String content, Integer selectedCount, Double percentage) {
            this.optionId = optionId;
            this.content = content;
            this.selectedCount = selectedCount;
            this.percentage = percentage;
        }

        // Getters and Setters
        public Long getOptionId() {
            return optionId;
        }

        public void setOptionId(Long optionId) {
            this.optionId = optionId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Integer getSelectedCount() {
            return selectedCount;
        }

        public void setSelectedCount(Integer selectedCount) {
            this.selectedCount = selectedCount;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }
}
