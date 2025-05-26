package com.huy.QuizMe.data.websocket;

/**
 * Constants cho WebSocket topics và events
 * Tất cả các constants phải khớp với backend
 */
public final class WebSocketConstants {
    
    private WebSocketConstants() {
        // Utility class - không cho phép khởi tạo
    }
    
    // Topic prefixes
    public static final String ROOM_TOPIC_PREFIX = "/topic/room/";
    public static final String APP_PREFIX = "/app/";
    
    // Basic room events
    public static final String CHAT_EVENT = "/chat";
    public static final String GAME_START_EVENT = "/start";
    public static final String GAME_CLOSE_EVENT = "/close";
    public static final String PLAYER_JOIN_EVENT = "/player-join";
    public static final String PLAYER_LEAVE_EVENT = "/player-leave";
    public static final String GAME_PROGRESS_EVENT = "/progress";
    public static final String GAME_END_EVENT = "/end";
    
    // Multiplayer quiz game events
    public static final String QUESTION_EVENT = "/question";
    public static final String TIMER_EVENT = "/timer";
    public static final String QUESTION_RESULT_EVENT = "/question-result";
    public static final String LEADERBOARD_EVENT = "/leaderboard";
    public static final String NEXT_QUESTION_EVENT = "/next-question";
    
    // App destinations
    public static final String CHAT_DESTINATION = "chat/";
    public static final String ANSWER_DESTINATION = "answer/";
    public static final String JOIN_ROOM_DESTINATION = "room/join/";
    public static final String LEAVE_ROOM_DESTINATION = "room/leave/";
    
    /**
     * Tạo topic path cho room event
     */
    public static String createRoomTopicPath(Long roomId, String event) {
        return ROOM_TOPIC_PREFIX + roomId + event;
    }
    
    /**
     * Tạo app destination path
     */
    public static String createAppDestination(String destination) {
        return APP_PREFIX + destination;
    }
}
