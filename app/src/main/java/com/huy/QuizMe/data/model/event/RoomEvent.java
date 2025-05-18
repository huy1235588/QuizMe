package com.huy.QuizMe.data.model.event;

/**
 * Model biểu diễn các sự kiện phòng như người dùng tham gia hoặc rời phòng
 */
public class RoomEvent {
    private Long userId;
    private String username;
    private String profileImage;
    private boolean isGuest;
    private String guestName;
    private String message;
    private String eventType;

    // Constructor không tham số cho thư viện deserialize (Gson)
    public RoomEvent() {
    }

    // Constructor đầy đủ
    public RoomEvent(Long userId, String username, String profileImage, boolean isGuest, 
                     String guestName, String message, String eventType) {
        this.userId = userId;
        this.username = username;
        this.profileImage = profileImage;
        this.isGuest = isGuest;
        this.guestName = guestName;
        this.message = message;
        this.eventType = eventType;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "RoomEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", isGuest=" + isGuest +
                ", message='" + message + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
