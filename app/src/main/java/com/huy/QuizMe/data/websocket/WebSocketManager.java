package com.huy.QuizMe.data.websocket;

import android.util.Log;

/**
 * Manager tổng quát để quản lý tất cả WebSocket clients
 * Cung cấp single point of access cho tất cả WebSocket operations
 */
public class WebSocketManager {
    private static final String TAG = "WebSocketManager";

    private final WebSocketService webSocketService;
    private final GameWebSocketClient gameClient;
    private final ChatWebSocketClient chatClient;

    // Singleton pattern
    private static WebSocketManager instance;

    private WebSocketManager() {
        this.webSocketService = WebSocketService.getInstance();
        this.gameClient = GameWebSocketClient.getInstance();
        this.chatClient = ChatWebSocketClient.getInstance();
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    /**
     * Kết nối WebSocket
     */
    public boolean connect() {
        Log.d(TAG, "Connecting to WebSocket server");
        return webSocketService.connect();
    }

    /**
     * Ngắt kết nối WebSocket
     */
    public void disconnect() {
        Log.d(TAG, "Disconnecting from WebSocket server");
        webSocketService.disconnect();
    }

    /**
     * Kiểm tra trạng thái kết nối
     */
    public boolean isConnected() {
        return webSocketService.isConnected();
    }

    /**
     * Lấy Game WebSocket Client
     */
    public GameWebSocketClient getGameClient() {
        return gameClient;
    }

    /**
     * Lấy Chat WebSocket Client
     */
    public ChatWebSocketClient getChatClient() {
        return chatClient;
    }

    /**
     * Lấy Core WebSocket Service
     */
    public WebSocketService getCoreService() {
        return webSocketService;
    }

    /**
     * Hủy đăng ký tất cả events cho một room
     */
    public void unsubscribeAllRoomEvents(Long roomId) {
        if (roomId == null) {
            Log.e(TAG, "Cannot unsubscribe: roomId is null");
            return;
        }

        Log.d(TAG, "Unsubscribing all events for room: " + roomId);
        gameClient.unsubscribeAllGameEvents(roomId);
        chatClient.unsubscribeAllRoomEvents(roomId);
    }

    /**
     * Convenience method để join room và subscribe các events cơ bản
     */
    public void joinRoom(Long roomId,
                         WebSocketService.MessageListener<Object> chatListener,
                         WebSocketService.MessageListener<Object> playerJoinListener,
                         WebSocketService.MessageListener<Object> playerLeaveListener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot join room: roomId is null");
            return;
        }

        Log.d(TAG, "Joining room: " + roomId);

        // Subscribe to basic room events
        if (chatListener != null) {
            chatClient.subscribeToChat(roomId, msg -> chatListener.onMessage(msg));
        }
        if (playerJoinListener != null) {
            chatClient.subscribeToPlayerJoin(roomId, Object.class, playerJoinListener);
        }
        if (playerLeaveListener != null) {
            chatClient.subscribeToPlayerLeave(roomId, Object.class, playerLeaveListener);
        }
    }

    /**
     * Convenience method để subscribe game events
     */
    public void subscribeToGameEvents(Long roomId,
                                      WebSocketService.MessageListener<Object> questionListener,
                                      WebSocketService.MessageListener<Object> timerListener,
                                      WebSocketService.MessageListener<Object> resultListener,
                                      WebSocketService.MessageListener<Object> leaderboardListener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to game events: roomId is null");
            return;
        }

        Log.d(TAG, "Subscribing to game events for room: " + roomId);

        if (questionListener != null) {
            gameClient.subscribeToQuestions(roomId, Object.class, questionListener);
        }
        if (timerListener != null) {
            gameClient.subscribeToTimer(roomId, timerListener::onMessage);
        }
        if (resultListener != null) {
            gameClient.subscribeToQuestionResults(roomId, Object.class, resultListener);
        }
        if (leaderboardListener != null) {
            gameClient.subscribeToLeaderboard(roomId, Object.class, leaderboardListener);
        }
    }
}
