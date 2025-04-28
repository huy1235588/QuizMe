package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp mô hình cho danh mục quiz
 */
public class Category {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("iconUrl")
    private String iconUrl;

    @SerializedName("quizCount")
    private Integer quizCount;

    @SerializedName("totalPlayCount")
    private Integer totalPlayCount;

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Các phương thức getter
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public Integer getQuizCount() {
        return quizCount;
    }

    public Integer getTotalPlayCount() {
        return totalPlayCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}