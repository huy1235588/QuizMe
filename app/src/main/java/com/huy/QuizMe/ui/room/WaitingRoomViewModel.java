package com.huy.QuizMe.ui.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.ChatMessage;
import com.huy.QuizMe.data.model.Participant;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.RoomRepository;
import com.huy.QuizMe.data.websocket.WebSocketService;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel cho màn hình phòng chờ (Waiting Room)
 * Quản lý dữ liệu và tương tác với server thông qua REST API và WebSocket
 */
public class WaitingRoomViewModel extends ViewModel {
    private final RoomRepository roomRepository;
    private final WebSocketService webSocketService;
    private final SharedPreferencesManager sharedPreferencesManager;

    // Lưu thông tin phòng hiện tại
    private final MutableLiveData<Room> currentRoom = new MutableLiveData<>();

    // Trạng thái đang tải
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // LiveData cho tin nhắn chat
    private final MutableLiveData<List<ChatMessage>> chatMessages = new MutableLiveData<>(new ArrayList<>());

    // LiveData cho danh sách người tham gia
    private final MutableLiveData<List<Participant>> participants = new MutableLiveData<>(new ArrayList<>());

    // LiveData cho trạng thái kết nối WebSocket
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);

    // LiveData cho thông báo lỗi
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // LiveData cho sự kiện bắt đầu trò chơi
    private final MediatorLiveData<Resource<Room>> gameStartEvent = new MediatorLiveData<>();

    /**
     * Constructor
     */
    public WaitingRoomViewModel() {
        this.roomRepository = new RoomRepository();
        this.webSocketService = WebSocketService.getInstance();
        this.sharedPreferencesManager = SharedPreferencesManager.getInstance();
    }

    /**
     * Thiết lập phòng chờ và đăng ký lắng nghe các sự kiện WebSocket
     *
     * @param room Thông tin phòng
     */
    public void setupWaitingRoom(Room room) {
        if (room != null) {
            currentRoom.setValue(room);

            // Đặt danh sách người tham gia ban đầu
            if (room.getParticipants() != null) {
                participants.setValue(room.getParticipants());
            }

            // Kết nối WebSocket trước khi đăng ký lắng nghe sự kiện
            connectAndSubscribe(room.getId());
        }
    }

    /**
     * Kết nối WebSocket và đăng ký lắng nghe các sự kiện khi kết nối thành công
     *
     * @param roomId ID của phòng
     */
    private void connectAndSubscribe(Long roomId) {
        if (roomId == null) {
            errorMessage.setValue("Không tìm thấy thông tin phòng");
            return;
        }

        // Kiểm tra nếu đã kết nối
        if (webSocketService.isConnected()) {
            // Đã kết nối, đăng ký các sự kiện
            subscribeToWebSocketEvents(roomId);
            isConnected.setValue(true);
            return;
        }

        // Chưa kết nối, thực hiện kết nối
        boolean connectionStarted = webSocketService.connect();
        if (!connectionStarted) {
            errorMessage.setValue("Không thể kết nối đến máy chủ");
            isConnected.setValue(false);
            return;
        }

        // Đợi một khoảng thời gian ngắn để kết nối được thiết lập
        new Thread(() -> {
            try {
                // Đợi tối đa 3 giây cho kết nối
                Thread.sleep(2000);

                // Kiểm tra lại trạng thái kết nối
                boolean connected = webSocketService.isConnected();
                isConnected.postValue(connected);

                if (connected) {
                    // Kết nối thành công, đăng ký các sự kiện
                    subscribeToWebSocketEvents(roomId);
                } else {
                    // Kết nối thất bại
                    errorMessage.postValue("Không thể kết nối đến máy chủ trò chuyện");
                }
            } catch (InterruptedException e) {
                errorMessage.postValue("Lỗi kết nối: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Đăng ký các sự kiện WebSocket cho phòng chờ
     *
     * @param roomId ID của phòng
     */
    private void subscribeToWebSocketEvents(Long roomId) {
        if (roomId == null) {
            errorMessage.setValue("ID phòng không hợp lệ");
            return;
        }

        try {
            // Đăng ký nhận tin nhắn chat
            boolean chatSubscribed = webSocketService.subscribeToChatMessages(roomId, this::handleNewChatMessage);
            if (!chatSubscribed) {
                errorMessage.setValue("Không thể kết nối đến kênh trò chuyện");
            }

            // Đăng ký sự kiện người chơi tham gia
            webSocketService.subscribeToPlayerJoinEvents(roomId, Room.class, this::handlePlayerJoinEvent);

            // Đăng ký sự kiện người chơi rời phòng
            webSocketService.subscribeToPlayerLeaveEvents(roomId, Room.class, this::handlePlayerLeaveEvent);

            // Đăng ký sự kiện bắt đầu trò chơi
            webSocketService.subscribeToGameStartEvents(roomId, Room.class, this::handleGameStartEvent);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi đăng ký sự kiện: " + e.getMessage());
        }
    }

    /**
     * Hủy đăng ký các sự kiện WebSocket khi không cần nữa
     */
    public void unsubscribeFromEvents() {
        Room room = currentRoom.getValue();
        if (room != null && room.getId() != null) {
            Long roomId = room.getId();
            // Hủy đăng ký các topic WebSocket
            webSocketService.unsubscribe("/topic/chat/" + roomId);
            webSocketService.unsubscribe("/topic/room/" + roomId + "/player-join");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/player-leave");
            webSocketService.unsubscribe("/topic/room/" + roomId + "/game-start");
        }
    }

    /**
     * Làm mới thông tin phòng từ server
     */
    public LiveData<Resource<Room>> refreshRoomInfo() {
        isLoading.setValue(true);
        Room room = currentRoom.getValue();
        if (room != null && room.getId() != null) {
            Long roomId = room.getId();
            return roomRepository.getRoomById(roomId);
        }
        return new MutableLiveData<>(Resource.error("Không tìm thấy thông tin phòng", null));
    }

    /**
     * Gửi tin nhắn chat
     *
     * @param message Nội dung tin nhắn
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public boolean sendChatMessage(String message) {
        Room room = currentRoom.getValue();
        if (room == null || room.getId() == null || message == null || message.trim().isEmpty()) {
            return false;
        }
        return webSocketService.sendChatMessage(room.getId(), message);
    }

    /**
     * Bắt đầu trò chơi (chỉ chủ phòng)
     */
    public LiveData<Resource<Room>> startGame() {
        isLoading.setValue(true);
        Room room = currentRoom.getValue();
        if (room != null && room.getId() != null) {
            return roomRepository.startGame(room.getId());
        }
        return new MutableLiveData<>(Resource.error("Không thể bắt đầu trò chơi", null));
    }

    /**
     * Đóng phòng (chỉ chủ phòng)
     */
    public LiveData<Resource<Room>> closeRoom() {
        isLoading.setValue(true);
        Room room = currentRoom.getValue();
        if (room != null && room.getId() != null) {
            return roomRepository.closeRoom(room.getId());
        }
        return new MutableLiveData<>(Resource.error("Không thể đóng phòng", null));
    }

    /**
     * Rời khỏi phòng
     */
    public LiveData<Resource<Boolean>> leaveRoom() {
        Room room = currentRoom.getValue();
        if (room != null && room.getId() != null) {
            return roomRepository.leaveRoom(room.getId());
        }
        return new MutableLiveData<>(Resource.error("Không thể rời khỏi phòng", null));
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là chủ phòng hay không
     *
     * @return true nếu là chủ phòng, false nếu không phải
     */
    public boolean isCurrentUserHost() {
        Room room = currentRoom.getValue();
        if (room != null && room.getHost() != null) {
            User currentUser = sharedPreferencesManager.getUser();
            return currentUser != null && currentUser.getId().equals(room.getHost().getId());
        }
        return false;
    }

    /**
     * Xử lý tin nhắn mới nhận được
     *
     * @param chatMessage Tin nhắn chat mới
     */
    private void handleNewChatMessage(ChatMessage chatMessage) {
        List<ChatMessage> currentMessages = chatMessages.getValue();
        if (currentMessages != null) {
            currentMessages.add(chatMessage);
            chatMessages.setValue(currentMessages);
        }
    }

    /**
     * Xử lý sự kiện người chơi tham gia phòng
     *
     * @param updatedRoom Phòng đã được cập nhật
     */
    private void handlePlayerJoinEvent(Room updatedRoom) {
        if (updatedRoom != null) {
            currentRoom.setValue(updatedRoom);
            if (updatedRoom.getParticipants() != null) {
                participants.setValue(updatedRoom.getParticipants());
            }
        }
    }

    /**
     * Xử lý sự kiện người chơi rời phòng
     *
     * @param updatedRoom Phòng đã được cập nhật
     */
    private void handlePlayerLeaveEvent(Room updatedRoom) {
        if (updatedRoom != null) {
            currentRoom.setValue(updatedRoom);
            if (updatedRoom.getParticipants() != null) {
                participants.setValue(updatedRoom.getParticipants());
            }
        }
    }

    /**
     * Xử lý sự kiện bắt đầu trò chơi
     *
     * @param updatedRoom Phòng đã được cập nhật
     */
    private void handleGameStartEvent(Room updatedRoom) {
        if (updatedRoom != null) {
            gameStartEvent.setValue(Resource.success(updatedRoom, "Trò chơi đã bắt đầu"));
        }
    }

    // Getter methods
    public LiveData<Room> getCurrentRoom() {
        return currentRoom;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessages;
    }

    public LiveData<List<Participant>> getParticipants() {
        return participants;
    }

    public LiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Resource<Room>> getGameStartEvent() {
        return gameStartEvent;
    }

    /**
     * Kiểm tra xem WebSocket đã kết nối hay chưa
     *
     * @return true nếu đã kết nối, false nếu chưa kết nối
     */
    public boolean isWebSocketConnected() {
        return webSocketService.isConnected();
    }

    /**
     * Kết nối lại WebSocket nếu mất kết nối
     */
    public void reconnectWebSocket() {
        Room room = currentRoom.getValue();
        if (room != null && room.getId() != null) {
            // Đảm bảo hủy kết nối hiện tại trước khi kết nối lại
            webSocketService.disconnect();
            // Kết nối lại và đăng ký sự kiện
            connectAndSubscribe(room.getId());
        } else {
            errorMessage.setValue("Không thể kết nối lại: Thông tin phòng không hợp lệ");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Hủy đăng ký các sự kiện khi ViewModel bị hủy
        unsubscribeFromEvents();
    }
}
