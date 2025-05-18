package com.huy.QuizMe.ui.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.ChatMessage;
import com.huy.QuizMe.data.model.Participant;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.event.RoomEvent;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.RoomRepository;
import com.huy.QuizMe.data.websocket.WebSocketService;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel cho màn hình phòng chờ (Waiting Room)
 * Quản lý dữ liệu và tương tác với server thông qua REST API và WebSocket
 */
public class WaitingRoomViewModel extends ViewModel {
    private final RoomRepository roomRepository;
    private final WebSocketService webSocketService;
    private final SharedPreferencesManager sharedPreferencesManager;

    // Prefix cho tin nhắn hệ thống
    private static final String SYSTEM_MESSAGE_PREFIX = "SYSTEM_MESSAGE:";

    // Timeout cho kết nối WebSocket
    private static final long WEBSOCKET_CONNECTION_TIMEOUT = 3000; // 3 giây

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
        if (!webSocketService.connect()) {
            errorMessage.setValue("Không thể kết nối đến máy chủ");
            isConnected.setValue(false);
            return;
        }

        // Đợi một khoảng thời gian ngắn để kết nối được thiết lập
        new Thread(() -> {
            try {
                // Đợi tối đa WEBSOCKET_CONNECTION_TIMEOUT mili-giây cho kết nối
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;

                while (!webSocketService.isConnected() && elapsedTime < WEBSOCKET_CONNECTION_TIMEOUT) {
                    Thread.sleep(200); // Kiểm tra mỗi 200ms
                    elapsedTime = System.currentTimeMillis() - startTime;
                }

                // Kiểm tra trạng thái kết nối sau khi đợi
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
                Thread.currentThread().interrupt();
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
        try {            // Đăng ký nhận tin nhắn chat
            if (!webSocketService.subscribeToChatMessages(roomId, this::handleNewChatMessage)) {
                errorMessage.setValue("Không thể kết nối đến kênh trò chuyện");
            }

            // Đăng ký sự kiện người chơi tham gia và rời phòng
            webSocketService.subscribeToPlayerJoinEvents(roomId, RoomEvent.class, this::handleRoomEvent);
            webSocketService.subscribeToPlayerLeaveEvents(roomId, RoomEvent.class, this::handleRoomEvent);

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
            String[] topics = {
                    "/topic/chat/" + roomId,
                    "/topic/room/" + roomId + "/player-join",
                    "/topic/room/" + roomId + "/player-leave",
                    "/topic/room/" + roomId + "/game-start"
            };

            for (String topic : topics) {
                webSocketService.unsubscribe(topic);
            }
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
     * Tạo và thêm tin nhắn hệ thống vào danh sách tin nhắn
     *
     * @param systemMessage Nội dung tin nhắn hệ thống
     */
    private void addSystemMessage(String systemMessage) {
        Room room = currentRoom.getValue();
        if (room == null || room.getId() == null || systemMessage == null) {
            return;
        }

        // Tạo tin nhắn hệ thống mới
        ChatMessage message = new ChatMessage(
                null,
                room.getId(),
                null,  // null user vì đây là tin nhắn hệ thống
                false, // không phải khách
                null,  // không có tên khách
                SYSTEM_MESSAGE_PREFIX + systemMessage, // thêm prefix để định danh
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(new Date()) // thời gian hiện tại
        );

        // Thêm vào danh sách tin nhắn
        List<ChatMessage> currentMessages = chatMessages.getValue();
        if (currentMessages != null) {
            currentMessages.add(message);
            chatMessages.setValue(currentMessages);
        }
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
        if (currentMessages != null && chatMessage != null) {
            currentMessages.add(chatMessage);
            chatMessages.setValue(currentMessages);
        }
    }

    /**
     * Xử lý sự kiện phòng (tham gia/rời đi)
     *
     * @param event Đối tượng sự kiện chứa thông tin về người dùng và loại sự kiện
     */
    private void handleRoomEvent(RoomEvent event) {
        if (event != null && event.getMessage() != null && !event.getMessage().isEmpty()) {
            // Thêm tin nhắn hệ thống
            addSystemMessage(event.getMessage());

            // Cập nhật danh sách người tham gia trực tiếp từ sự kiện
            if ("join".equals(event.getEventType())) {
                addParticipant(event);
            } else if ("leave".equals(event.getEventType())) {
                removeParticipant(event);
            }
        }
    }

    /**
     * Thêm người tham gia mới vào danh sách
     *
     * @param event Đối tượng sự kiện chứa thông tin người tham gia mới
     */
    private void addParticipant(RoomEvent event) {
        if (event == null || event.getUserId() == null) return;

        List<Participant> currentParticipants = new ArrayList<>(participants.getValue());

        // Kiểm tra xem người này đã có trong danh sách chưa
        for (Participant participant : currentParticipants) {
            if (!participant.isGuest() && participant.getUser() != null &&
                    participant.getUser().getId() != null &&
                    participant.getUser().getId().equals(event.getUserId())) {
                // Người dùng đã tồn tại trong danh sách
                return;
            }
        }

        // Tạo đối tượng User từ thông tin trong event
        User user = new User();
        user.setId(event.getUserId());
        user.setUsername(event.getUsername());
        user.setProfileImage(event.getProfileImage());

        // Tạo participant mới
        Participant newParticipant = new Participant();
        newParticipant.setUser(user);
        newParticipant.setGuest(event.isGuest());
        newParticipant.setGuestName(event.getGuestName());
        newParticipant.setHost(false); // Người tham gia mới không phải chủ phòng

        // Thêm vào danh sách
        currentParticipants.add(newParticipant);
        participants.setValue(currentParticipants);
    }

    /**
     * Xóa người tham gia khỏi danh sách
     *
     * @param event Đối tượng sự kiện chứa thông tin người rời đi
     */
    private void removeParticipant(RoomEvent event) {
        if (event == null || event.getUserId() == null) return;

        List<Participant> currentParticipants = new ArrayList<>(participants.getValue());
        Participant toRemove = null;

        // Tìm người tham gia cần xóa
        for (Participant participant : currentParticipants) {
            if (!participant.isGuest() && participant.getUser() != null &&
                    participant.getUser().getId() != null &&
                    participant.getUser().getId().equals(event.getUserId())) {
                toRemove = participant;
                break;
            }
        }

        // Xóa người tham gia khỏi danh sách
        if (toRemove != null) {
            currentParticipants.remove(toRemove);
            participants.setValue(currentParticipants);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        // Hủy đăng ký các sự kiện khi ViewModel bị hủy
        unsubscribeFromEvents();
    }
}
