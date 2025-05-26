package com.huy.QuizMe.data.websocket;

import android.util.Log;

import java.util.Map;

/**
 * Client chuyên xử lý các WebSocket events cho multiplayer quiz game
 * Sử dụng WebSocketService làm underlying service
 */
public class GameWebSocketClient {
    private static final String TAG = "GameWebSocketClient";

    private final WebSocketService webSocketService;

    // Singleton pattern
    private static GameWebSocketClient instance;

    private GameWebSocketClient() {
        this.webSocketService = WebSocketService.getInstance();
    }

    public static synchronized GameWebSocketClient getInstance() {
        if (instance == null) {
            instance = new GameWebSocketClient();
        }
        return instance;
    }

    /**
     * Đăng ký nhận câu hỏi mới
     */
    public <T> boolean subscribeToQuestions(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to questions: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận cập nhật timer
     */
    public boolean subscribeToTimer(Long roomId, WebSocketService.MessageListener<Map<String, Integer>> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to timer: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.TIMER_EVENT);
        return webSocketService.subscribeToTopic(topicPath, (Class<Map<String, Integer>>) (Class<?>) Map.class, listener);    }

    /**
     * Đăng ký nhận kết quả câu hỏi
     */
    public <T> boolean subscribeToQuestionResults(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to question results: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_RESULT_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận cập nhật bảng xếp hạng
     */
    public <T> boolean subscribeToLeaderboard(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to leaderboard: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.LEADERBOARD_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận thông báo câu hỏi tiếp theo
     */
    public <T> boolean subscribeToNextQuestion(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to next question: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.NEXT_QUESTION_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận sự kiện bắt đầu game
     */
    public <T> boolean subscribeToGameStart(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to game start: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_START_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận sự kiện kết thúc game
     */
    public <T> boolean subscribeToGameEnd(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to game end: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_END_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Gửi câu trả lời
     */
    public boolean sendAnswer(Long roomId, Object answerRequest) {
        if (roomId == null) {
            Log.e(TAG, "Cannot send answer: roomId is null");
            return false;
        }
        String destination = WebSocketConstants.createAppDestination(WebSocketConstants.ANSWER_DESTINATION + roomId);
        return webSocketService.sendMessage(destination, answerRequest);
    }

    /**
     * Hủy đăng ký tất cả game events cho room
     */
    public void unsubscribeAllGameEvents(Long roomId) {
        if (roomId == null) {
            Log.e(TAG, "Cannot unsubscribe: roomId is null");
            return;
        }

        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.TIMER_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_RESULT_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.LEADERBOARD_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.NEXT_QUESTION_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_START_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_END_EVENT));
    }

    /**
     * Kiểm tra kết nối WebSocket
     */
    public boolean isConnected() {
        return webSocketService.isConnected();
    }
}
