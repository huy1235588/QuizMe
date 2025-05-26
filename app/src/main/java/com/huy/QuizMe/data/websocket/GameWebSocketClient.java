package com.huy.QuizMe.data.websocket;

import android.util.Log;

import java.util.Map;

/**
 * Client chuyên xử lý các WebSocket events cho multiplayer quiz game
 * Sử dụng WebSocketService làm underlying service
 * <p>
 * Hỗ trợ tất cả game lifecycle events theo tài liệu backend:
 * - Game Start/End events
 * - Question và Timer events
 * - Results và Leaderboard
 * - Player management events
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

    // ===========================
    // QUESTION & TIMER EVENTS  
    // ===========================

    /**
     * Đăng ký nhận câu hỏi mới
     * Topic: /topic/room/{roomId}/question
     * Payload: QuestionGameDTO
     */
    public <T> boolean subscribeToQuestions(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToQuestions", "Invalid roomId");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_EVENT);
        logDebug(roomId, "subscribeToQuestions", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, clazz, listener);
        if (success) {
            logDebug(roomId, "subscribeToQuestions", "Success");
        } else {
            logError(roomId, "subscribeToQuestions", "Failed to subscribe");
        }
        return success;
    }

    /**
     * Đăng ký nhận cập nhật timer (mỗi giây)
     * Topic: /topic/room/{roomId}/timer
     * Payload: Map<String, Integer> (remainingTime, totalTime)
     */
    @SuppressWarnings("unchecked")
    public boolean subscribeToTimer(Long roomId, WebSocketService.MessageListener<Map<String, Integer>> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToTimer", "Invalid roomId");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.TIMER_EVENT);
        logDebug(roomId, "subscribeToTimer", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, (Class<Map<String, Integer>>) (Class<?>) Map.class, listener);
        if (success) {
            logDebug(roomId, "subscribeToTimer", "Success");
        } else {
            logError(roomId, "subscribeToTimer", "Failed to subscribe");
        }
        return success;
    }

    /**
     * Đăng ký nhận thông báo câu hỏi tiếp theo
     * Topic: /topic/room/{roomId}/next-question
     * Payload: Map<String, Integer> (nextQuestionNumber, countdownSeconds)
     */
    public <T> boolean subscribeToNextQuestion(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToNextQuestion", "Invalid roomId");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.NEXT_QUESTION_EVENT);
        logDebug(roomId, "subscribeToNextQuestion", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, clazz, listener);
        if (success) {
            logDebug(roomId, "subscribeToNextQuestion", "Success");
        } else {
            logError(roomId, "subscribeToNextQuestion", "Failed to subscribe");
        }
        return success;
    }

    // ===========================
    // GAME LIFECYCLE EVENTS
    // ===========================

    /**
     * Đăng ký nhận sự kiện bắt đầu game
     * Topic: /topic/room/{roomId}/start
     */
    public <T> boolean subscribeToGameStart(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToGameStart", "Invalid roomId");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_START_EVENT);
        logDebug(roomId, "subscribeToGameStart", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, clazz, listener);
        if (success) {
            logDebug(roomId, "subscribeToGameStart", "Success");
        } else {
            logError(roomId, "subscribeToGameStart", "Failed to subscribe");
        }
        return success;
    }

    /**
     * Đăng ký nhận sự kiện kết thúc game
     * Topic: /topic/room/{roomId}/end
     */
    public <T> boolean subscribeToGameEnd(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToGameEnd", "Invalid roomId");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_END_EVENT);
        logDebug(roomId, "subscribeToGameEnd", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, clazz, listener);
        if (success) {
            logDebug(roomId, "subscribeToGameEnd", "Success");
        } else {
            logError(roomId, "subscribeToGameEnd", "Failed to subscribe");
        }
        return success;
    }

    // ===========================
    // RESULTS & LEADERBOARD
    // ===========================

    /**
     * Đăng ký nhận kết quả câu hỏi
     * Topic: /topic/room/{roomId}/question-result
     * Payload: QuestionResultDTO
     */
    public <T> boolean subscribeToQuestionResults(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToQuestionResults", "Invalid roomId");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_RESULT_EVENT);
        logDebug(roomId, "subscribeToQuestionResults", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, clazz, listener);
        if (success) {
            logDebug(roomId, "subscribeToQuestionResults", "Success");
        } else {
            logError(roomId, "subscribeToQuestionResults", "Failed to subscribe");
        }
        return success;
    }

    /**
     * Đăng ký nhận cập nhật bảng xếp hạng
     * Topic: /topic/room/{roomId}/leaderboard
     * Payload: LeaderboardDTO
     */
    public <T> boolean subscribeToLeaderboard(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToLeaderboard", "Invalid roomId");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.LEADERBOARD_EVENT);
        logDebug(roomId, "subscribeToLeaderboard", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, clazz, listener);
        if (success) {
            logDebug(roomId, "subscribeToLeaderboard", "Success");
        } else {
            logError(roomId, "subscribeToLeaderboard", "Failed to subscribe");
        }
        return success;
    }

    // ===========================
    // PLAYER ACTIONS
    // ===========================

    /**
     * Gửi câu trả lời của người chơi
     * Destination: /app/answer/{roomId}
     *
     * @param roomId        ID của phòng chơi
     * @param answerRequest Object chứa thông tin câu trả lời
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public boolean sendAnswer(Long roomId, Object answerRequest) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "sendAnswer", "Invalid roomId");
            return false;
        }
        if (answerRequest == null) {
            logError(roomId, "sendAnswer", "AnswerRequest is null");
            return false;
        }

        String destination = WebSocketConstants.createAppDestination(WebSocketConstants.ANSWER_DESTINATION + roomId);
        logDebug(roomId, "sendAnswer", "Destination: " + destination);

        boolean success = webSocketService.sendMessage(destination, answerRequest);
        if (success) {
            logDebug(roomId, "sendAnswer", "Success");
        } else {
            logError(roomId, "sendAnswer", "Failed to send");
        }
        return success;
    }

    // ===========================
    // SUBSCRIPTION MANAGEMENT
    // ===========================

    /**
     * Hủy đăng ký tất cả game events cho room
     * Bao gồm: questions, timer, results, leaderboard, next-question, game start/end
     */
    public void unsubscribeAllGameEvents(Long roomId) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "unsubscribeAllGameEvents", "Invalid roomId");
            return;
        }

        logDebug(roomId, "unsubscribeAllGameEvents", "Starting unsubscribe process");

        // Game lifecycle events
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_START_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_END_EVENT));

        // Question and timer events
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.TIMER_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.NEXT_QUESTION_EVENT));

        // Results and leaderboard
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.QUESTION_RESULT_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.LEADERBOARD_EVENT));

        logDebug(roomId, "unsubscribeAllGameEvents", "Completed");
    }

    /**
     * Đăng ký tất cả events cần thiết cho game multiplayer
     * Convenience method để subscribe cùng lúc nhiều events
     */
    public boolean subscribeToAllGameEvents(
            Long roomId,
            WebSocketService.MessageListener<Object> questionListener,
            WebSocketService.MessageListener<Map<String, Integer>> timerListener,
            WebSocketService.MessageListener<Object> resultListener,
            WebSocketService.MessageListener<Object> leaderboardListener,
            WebSocketService.MessageListener<Object> gameStartListener,
            WebSocketService.MessageListener<Object> gameEndListener
    ) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToAllGameEvents", "Invalid roomId");
            return false;
        }

        logDebug(roomId, "subscribeToAllGameEvents", "Starting subscription process");

        boolean success = true;

        // Subscribe to each event type
        if (questionListener != null) {
            success &= subscribeToQuestions(roomId, Object.class, questionListener);
        }
        if (timerListener != null) {
            success &= subscribeToTimer(roomId, timerListener);
        }
        if (resultListener != null) {
            success &= subscribeToQuestionResults(roomId, Object.class, resultListener);
        }
        if (leaderboardListener != null) {
            success &= subscribeToLeaderboard(roomId, Object.class, leaderboardListener);
        }
        if (gameStartListener != null) {
            success &= subscribeToGameStart(roomId, Object.class, gameStartListener);
        }
        if (gameEndListener != null) {
            success &= subscribeToGameEnd(roomId, Object.class, gameEndListener);
        }

        if (success) {
            logDebug(roomId, "subscribeToAllGameEvents", "All subscriptions successful");
        } else {
            logError(roomId, "subscribeToAllGameEvents", "Some subscriptions failed");
        }

        return success;
    }

    // ===========================
    // UTILITY METHODS
    // ===========================

    /**
     * Kiểm tra kết nối WebSocket
     *
     * @return true nếu WebSocket đã kết nối, false nếu chưa
     */
    public boolean isConnected() {
        return webSocketService.isConnected();
    }

    /**
     * Kiểm tra tính hợp lệ của roomId
     *
     * @param roomId ID của phòng cần kiểm tra
     * @return true nếu roomId hợp lệ, false nếu null hoặc <= 0
     */
    public boolean isValidRoomId(Long roomId) {
        return roomId == null || roomId <= 0;
    }

    /**
     * Log thông tin debug cho room
     *
     * @param roomId  ID của phòng
     * @param action  Hành động đang thực hiện
     * @param details Chi tiết bổ sung
     */
    private void logDebug(Long roomId, String action, String details) {
        Log.d(TAG, String.format("Room %d - %s: %s", roomId, action, details));
    }

    /**
     * Log thông tin lỗi cho room
     *
     * @param roomId ID của phòng
     * @param action Hành động gặp lỗi
     * @param error  Chi tiết lỗi
     */
    private void logError(Long roomId, String action, String error) {
        Log.e(TAG, String.format("Room %d - %s failed: %s", roomId, action, error));
    }

    /**
     * Subscribe to next question countdown events với enhanced validation
     * Topic: /topic/room/{roomId}/next-question
     */
    @SuppressWarnings("unchecked")
    public boolean subscribeToNextQuestionCountdown(Long roomId, WebSocketService.MessageListener<Map<String, Integer>> listener) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "subscribeToNextQuestionCountdown", "Invalid roomId");
            return false;
        }
        if (listener == null) {
            logError(roomId, "subscribeToNextQuestionCountdown", "Listener is null");
            return false;
        }

        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.NEXT_QUESTION_EVENT);
        logDebug(roomId, "subscribeToNextQuestionCountdown", "Topic: " + topicPath);

        boolean success = webSocketService.subscribeToTopic(topicPath, (Class<Map<String, Integer>>) (Class<?>) Map.class, listener);
        if (success) {
            logDebug(roomId, "subscribeToNextQuestionCountdown", "Success");
        } else {
            logError(roomId, "subscribeToNextQuestionCountdown", "Failed to subscribe");
        }
        return success;
    }

    /**
     * Gửi yêu cầu bắt đầu game (dành cho host)
     * Destination: /app/game/start/{roomId}
     */
    public boolean sendStartGameRequest(Long roomId, Object startRequest) {
        if (isValidRoomId(roomId)) {
            logError(roomId, "sendStartGameRequest", "Invalid roomId");
            return false;
        }

        // Tạo destination cho start game request
        String destination = WebSocketConstants.createAppDestination("game/start/" + roomId);
        logDebug(roomId, "sendStartGameRequest", "Destination: " + destination);

        boolean success = webSocketService.sendMessage(destination, startRequest);
        if (success) {
            logDebug(roomId, "sendStartGameRequest", "Success");
        } else {
            logError(roomId, "sendStartGameRequest", "Failed to send");
        }
        return success;
    }
}
