package com.huy.QuizMe.data.websocket;

import android.util.Log;

import com.huy.QuizMe.data.model.ChatMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Client chuyên xử lý các WebSocket events cho chat và player management
 * Sử dụng WebSocketService làm underlying service
 */
public class ChatWebSocketClient {
    private static final String TAG = "ChatWebSocketClient";

    private final WebSocketService webSocketService;

    // Singleton pattern
    private static ChatWebSocketClient instance;

    private ChatWebSocketClient() {
        this.webSocketService = WebSocketService.getInstance();
    }

    public static synchronized ChatWebSocketClient getInstance() {
        if (instance == null) {
            instance = new ChatWebSocketClient();
        }
        return instance;
    }

    /**
     * Đăng ký nhận tin nhắn chat
     */
    public boolean subscribeToChat(Long roomId, WebSocketService.MessageListener<ChatMessage> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to chat: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.CHAT_EVENT);
        return webSocketService.subscribeToTopic(topicPath, ChatMessage.class, listener);
    }

    /**
     * Đăng ký nhận sự kiện player join
     */
    public <T> boolean subscribeToPlayerJoin(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to player join: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.PLAYER_JOIN_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận sự kiện player leave
     */
    public <T> boolean subscribeToPlayerLeave(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to player leave: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.PLAYER_LEAVE_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận sự kiện game progress
     */
    public <T> boolean subscribeToGameProgress(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to game progress: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_PROGRESS_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Đăng ký nhận sự kiện game close
     */
    public <T> boolean subscribeToGameClose(Long roomId, Class<T> clazz, WebSocketService.MessageListener<T> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to game close: roomId is null");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_CLOSE_EVENT);
        return webSocketService.subscribeToTopic(topicPath, clazz, listener);
    }

    /**
     * Gửi tin nhắn chat
     */
    public boolean sendChatMessage(Long roomId, String message) {
        return sendChatMessage(roomId, message, null);
    }

    /**
     * Gửi tin nhắn chat với guest name
     */
    public boolean sendChatMessage(Long roomId, String message, String guestName) {
        if (roomId == null) {
            Log.e(TAG, "Cannot send chat message: roomId is null");
            return false;
        }

        // Tạo đúng định dạng request theo ChatMessageRequest trên server
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("roomId", roomId);
        chatMessage.put("message", message);
        chatMessage.put("guestName", guestName);

        String destination = WebSocketConstants.createAppDestination(WebSocketConstants.CHAT_DESTINATION + roomId);
        return webSocketService.sendMessage(destination, chatMessage);
    }

    /**
     * Gửi yêu cầu join room
     */
    public boolean sendJoinRoom(Long roomId, Object joinRequest) {
        if (roomId == null) {
            Log.e(TAG, "Cannot join room: roomId is null");
            return false;
        }
        String destination = WebSocketConstants.createAppDestination(WebSocketConstants.JOIN_ROOM_DESTINATION + roomId);
        return webSocketService.sendMessage(destination, joinRequest);
    }

    /**
     * Gửi yêu cầu leave room
     */
    public boolean sendLeaveRoom(Long roomId, Object leaveRequest) {
        if (roomId == null) {
            Log.e(TAG, "Cannot leave room: roomId is null");
            return false;
        }
        String destination = WebSocketConstants.createAppDestination(WebSocketConstants.LEAVE_ROOM_DESTINATION + roomId);
        return webSocketService.sendMessage(destination, leaveRequest);
    }

    /**
     * Hủy đăng ký tất cả chat/player events cho room
     */
    public void unsubscribeAllRoomEvents(Long roomId) {
        if (roomId == null) {
            Log.e(TAG, "Cannot unsubscribe: roomId is null");
            return;
        }

        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.CHAT_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.PLAYER_JOIN_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.PLAYER_LEAVE_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_PROGRESS_EVENT));
        webSocketService.unsubscribe(WebSocketConstants.createRoomTopicPath(roomId, WebSocketConstants.GAME_CLOSE_EVENT));
    }

    /**
     * Kiểm tra kết nối WebSocket
     */
    public boolean isConnected() {
        return webSocketService.isConnected();
    }
}
