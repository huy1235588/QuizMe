package com.huy.QuizMe.data.model.request;

import com.google.gson.annotations.SerializedName;

public class RoomRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("quizId")
    private Long quizId;

    @SerializedName("maxPlayers")
    private int maxPlayers;

    @SerializedName("password")
    private String password;

    @SerializedName("isPublic")
    private boolean isPublic;

    public RoomRequest(String name, Long quizId, int maxPlayers, String password, boolean isPublic) {
        this.name = name;
        this.quizId = quizId;
        this.maxPlayers = maxPlayers;
        this.password = password;
        this.isPublic = isPublic;
    }

    // Getters & Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
