package com.huy.QuizMe.data.model.game;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * DTO cho bảng xếp hạng
 */
public class LeaderboardDTO implements Serializable {
    @SerializedName("rankings")
    private List<PlayerRankingDTO> rankings;

    // Constructors
    public LeaderboardDTO() {
    }

    public LeaderboardDTO(List<PlayerRankingDTO> rankings) {
        this.rankings = rankings;
    }

    // Getters and Setters
    public List<PlayerRankingDTO> getRankings() {
        return rankings;
    }

    public void setRankings(List<PlayerRankingDTO> rankings) {
        this.rankings = rankings;
    }

    /**
     * DTO cho xếp hạng người chơi
     */
    public static class PlayerRankingDTO implements Serializable {
        @SerializedName("userId")
        private Long userId; // null nếu là khách

        @SerializedName("username")
        private String username;

        @SerializedName("score")
        private Integer score;

        @SerializedName("rank")
        private Integer rank;

        @SerializedName("avatar")
        private String avatar; // URL avatar (tùy chọn)

        @SerializedName("isGuest")
        private Boolean isGuest;

        @SerializedName("correctCount")
        private Integer correctCount; // Số câu trả lời đúng (tùy chọn)

        // Constructors
        public PlayerRankingDTO() {
        }

        public PlayerRankingDTO(Long userId, String username, Integer score, Integer rank,
                                String avatar, Boolean isGuest, Integer correctCount) {
            this.userId = userId;
            this.username = username;
            this.score = score;
            this.rank = rank;
            this.avatar = avatar;
            this.isGuest = isGuest;
            this.correctCount = correctCount;
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

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Boolean getIsGuest() {
            return isGuest;
        }

        public void setIsGuest(Boolean isGuest) {
            this.isGuest = isGuest;
        }

        public Integer getCorrectCount() {
            return correctCount;
        }

        public void setCorrectCount(Integer correctCount) {
            this.correctCount = correctCount;
        }
    }
}
