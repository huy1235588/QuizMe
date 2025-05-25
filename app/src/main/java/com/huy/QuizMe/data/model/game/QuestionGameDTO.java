package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * DTO cho câu hỏi trong game multiplayer
 * Chứa thông tin câu hỏi gửi đến client, ẩn thông tin đáp án đúng
 */
public class QuestionGameDTO implements Serializable {
    @SerializedName("questionId")
    private Long questionId;

    @SerializedName("content")
    private String content;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("audioUrl")
    private String audioUrl;

    @SerializedName("videoUrl")
    private String videoUrl;

    @SerializedName("type")
    private String type; // QUIZ, TRUE_FALSE, TYPE_ANSWER, QUIZ_AUDIO, QUIZ_VIDEO, CHECKBOX, POLL

    @SerializedName("timeLimit")
    private Integer timeLimit;

    @SerializedName("points")
    private Integer points;

    @SerializedName("questionNumber")
    private Integer questionNumber;

    @SerializedName("totalQuestions")
    private Integer totalQuestions;

    @SerializedName("options")
    private List<QuestionOptionDTO> options;

    // Constructors
    public QuestionGameDTO() {
    }

    public QuestionGameDTO(Long questionId, String content, String imageUrl, String audioUrl, String videoUrl,
                           String type, Integer timeLimit, Integer points, Integer questionNumber,
                           Integer totalQuestions, List<QuestionOptionDTO> options) {
        this.questionId = questionId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.videoUrl = videoUrl;
        this.type = type;
        this.timeLimit = timeLimit;
        this.points = points;
        this.questionNumber = questionNumber;
        this.totalQuestions = totalQuestions;
        this.options = options;
    }

    // Getters and Setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public List<QuestionOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionDTO> options) {
        this.options = options;
    }

    /**
     * DTO cho tùy chọn câu hỏi (không chứa thông tin đáp án đúng)
     */
    public static class QuestionOptionDTO implements Serializable {
        @SerializedName("id")
        private Long id;

        @SerializedName("content")
        private String content;

        public QuestionOptionDTO() {
        }

        public QuestionOptionDTO(Long id, String content) {
            this.id = id;
            this.content = content;
        }

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
    }
}
