package com.huy.QuizMe.data.model.request;

import com.google.gson.annotations.SerializedName;

public class ChatMessageRequest {
    @SerializedName("roomId")
    private Long roomId;

    @SerializedName("message")
    private String message;

    @SerializedName("guestName")
    private String guestName;

    public ChatMessageRequest(Long roomId, String message, String guestName) {
        this.roomId = roomId;
        this.message = message;
        this.guestName = guestName;
    }

    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
}
