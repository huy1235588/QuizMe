package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Lớp mô hình cho người tham gia phòng quiz
 */
public class Participant implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("user")
    private User user;

    @SerializedName("score")
    private int score;

    @SerializedName("isHost")
    private boolean isHost;

    @SerializedName("joinedAt")
    private String joinedAt;

    @SerializedName("isGuest")
    private boolean isGuest;

    @SerializedName("guestName")
    private String guestName;

    // Getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
}