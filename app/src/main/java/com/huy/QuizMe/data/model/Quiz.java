package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Lớp mô hình cho quiz
 */
public class Quiz implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("quizThumbnails")
    private String quizThumbnails;

    @SerializedName("categoryId")
    private Integer categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("creatorId")
    private Integer creatorId;

    @SerializedName("creatorName")
    private String creatorName;

    @SerializedName("creatorAvatar")
    private String creatorAvatar;

    @SerializedName("difficulty")
    private String difficulty;

    @SerializedName("isPublic")
    private Boolean isPublic;

    @SerializedName("playCount")
    private Integer playCount;

    @SerializedName("questionCount")
    private Integer questionCount;

    @SerializedName("favoriteCount")
    private Integer favoriteCount;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Các phương thức getter
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getQuizThumbnails() {
        return quizThumbnails;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
    
    // Setter methods needed for creating quiz objects
    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
}