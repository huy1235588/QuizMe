package com.huy.QuizMe.data.model;

import com.google.gson.annotations.SerializedName;

/*
 * Lớp mô hình cho tin nhắn trong phòng chat
 */
public class ChatMessage {
    @SerializedName("id")
    private Long id;

    @SerializedName("roomId")
    private Long roomId;

    @SerializedName("user")
    private User user;

    @SerializedName("isGuest")
    private Boolean isGuest;

    @SerializedName("guestName")
    private String guestName;

    @SerializedName("message")
    private String message;

    @SerializedName("sentAt")
    private String sentAt;

    public ChatMessage(Long id, Long roomId, User user, Boolean isGuest, String guestName, String message, String sentAt) {
        this.id = id;
        this.roomId = roomId;
        this.user = user;
        this.isGuest = isGuest;
        this.guestName = guestName;
        this.message = message;
        this.sentAt = sentAt;
    }

    // Getter và setter cho các thuộc tính
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getGuest() {
        return isGuest;
    }

    public void setGuest(Boolean guest) {
        isGuest = guest;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}
