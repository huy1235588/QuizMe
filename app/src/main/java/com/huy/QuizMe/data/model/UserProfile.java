package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class UserProfile {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("dateOfBirth")
    private LocalDate dateOfBirth;

    @SerializedName("city")
    private String city;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("totalScore")
    private Integer totalScore;

    @SerializedName("quizzesPlayed")
    private Integer quizzesPlayed;

    @SerializedName("quizzesCreated")
    private Integer quizzesCreated;

    @SerializedName("totalQuizPlays")
    private Integer totalQuizPlays;

    // Getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCity() {
        return city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public Integer getQuizzesPlayed() {
        return quizzesPlayed;
    }

    public Integer getQuizzesCreated() {
        return quizzesCreated;
    }

    public Integer getTotalQuizPlays() {
        return totalQuizPlays;
    }
}