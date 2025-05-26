package com.huy.QuizMe.ui.room;

import android.util.Log;

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
import com.huy.QuizMe.data.websocket.WebSocketManager;
import com.huy.QuizMe.data.websocket.ChatWebSocketClient;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * ViewModel cho màn hình phòng chờ (Waiting Room)
 * Quản lý dữ liệu và tương tác với server thông qua REST API và WebSocket
 */
public class WaitingRoomViewModel extends ViewModel {
    private final RoomRepository roomRepository;
    private final WebSocketManager webSocketManager;
    private final ChatWebSocketClient chatClient;
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

    // LiveData cho sự kiện đóng trò chơi
    private final MediatorLiveData<Resource<Room>> gameCloseEvent = new MediatorLiveData<>();

    /**
     * Constructor - Khởi tạo ViewModel với các thành phần cần thiết
     * Sử dụng Singleton pattern để lấy instance từ các service và repository
     */
    public WaitingRoomViewModel() {
        // Khởi tạo với các Singleton service/repository
        this.roomRepository = new RoomRepository();
        this.webSocketManager = WebSocketManager.getInstance();
        this.chatClient = webSocketManager.getChatClient();
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
     * Sử dụng mô hình bất đồng bộ để không chặn UI thread
     *
     * @param roomId ID của phòng
     */
    private void connectAndSubscribe(Long roomId) {
        if (roomId == null) {
            errorMessage.postValue("Không tìm thấy thông tin phòng");
            return;
        }        // Kiểm tra nếu đã kết nối
        if (webSocketManager.isConnected()) {
            // Đã kết nối, đăng ký các sự kiện
            subscribeToWebSocketEvents(roomId);
            isConnected.postValue(true);
            return;
        }

        // Thông báo đang kết nối
        isConnected.postValue(false);
        isLoading.postValue(true);

        // Chưa kết nối, thực hiện kết nối
        if (!webSocketManager.connect()) {
            errorMessage.postValue("Không thể khởi tạo kết nối đến máy chủ");
            isConnected.postValue(false);
            isLoading.postValue(false);
            return;
        }

        // Đợi kết nối trong background thread
        new Thread(() -> {
            try {
                // Đợi tối đa WEBSOCKET_CONNECTION_TIMEOUT mili-giây cho kết nối
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                boolean isConnectedSuccessfully = false;
                while (!isConnectedSuccessfully && elapsedTime < WEBSOCKET_CONNECTION_TIMEOUT) {
                    Thread.sleep(200); // Kiểm tra mỗi 200ms
                    isConnectedSuccessfully = webSocketManager.isConnected();
                    elapsedTime = System.currentTimeMillis() - startTime;
                }

                // Cập nhật trạng thái kết nối
                isConnected.postValue(isConnectedSuccessfully);
                isLoading.postValue(false);

                if (isConnectedSuccessfully) {
                    // Kết nối thành công, đăng ký các sự kiện
                    subscribeToWebSocketEvents(roomId);
                } else {
                    // Kết nối thất bại sau khi timeout
                    errorMessage.postValue("Kết nối máy chủ bị gián đoạn, vui lòng thử lại");
                }
            } catch (InterruptedException e) {
                isLoading.postValue(false);
                errorMessage.postValue("Lỗi kết nối: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Đăng ký các sự kiện WebSocket cho phòng chờ
     * Đảm bảo mỗi sự kiện được xử lý đúng cách và báo lỗi chi tiết
     *
     * @param roomId ID của phòng
     */
    private void subscribeToWebSocketEvents(Long roomId) {
        if (roomId == null) {
            errorMessage.postValue("ID phòng không hợp lệ");
            return;
        }
        try {
            // Đăng ký nhận tin nhắn chat
            boolean chatSubscribed = chatClient.subscribeToChat(roomId, this::handleNewChatMessage);
            if (!chatSubscribed) {
                errorMessage.postValue("Không thể kết nối đến kênh trò chuyện");
            }

            // Đăng ký sự kiện người chơi tham gia phòng
            boolean joinSubscribed = chatClient.subscribeToPlayerJoin(roomId, RoomEvent.class, this::handleRoomEvent);
            if (!joinSubscribed) {
                Log.w("WaitingRoomViewModel", "Không thể đăng ký sự kiện tham gia phòng");
            }

            // Đăng ký sự kiện người chơi rời phòng
            boolean leaveSubscribed = chatClient.subscribeToPlayerLeave(roomId, RoomEvent.class, this::handleRoomEvent);
            if (!leaveSubscribed) {
                Log.w("WaitingRoomViewModel", "Không thể đăng ký sự kiện rời phòng");
            }

            // Đăng ký sự kiện bắt đầu trò chơi
            boolean gameStartSubscribed = webSocketManager.getGameClient().subscribeToGameStart(roomId, Room.class, this::handleGameStartEvent);
            if (!gameStartSubscribed) {
                Log.w("WaitingRoomViewModel", "Không thể đăng ký sự kiện bắt đầu trò chơi");
            }

            // Đăng ký sự kiện đóng trò chơi
            boolean gameEndSubscribed = chatClient.subscribeToGameClose(roomId, Room.class, this::handleGameCloseEvent);
            if (!gameEndSubscribed) {
                Log.w("WaitingRoomViewModel", "Không thể đăng ký sự kiện kết thúc trò chơi");
            }

            // Nếu tất cả đều thất bại, đưa ra thông báo
            if (!chatSubscribed && !joinSubscribed && !leaveSubscribed && !gameStartSubscribed) {
                errorMessage.postValue("Không thể kết nối với các sự kiện phòng chờ");
            }
        } catch (Exception e) {
            errorMessage.postValue("Lỗi khi đăng ký sự kiện: " + e.getMessage());
            Log.e("WaitingRoomViewModel", "Lỗi đăng ký sự kiện WebSocket", e);
        }
    }

    /**
     * Hủy đăng ký các sự kiện WebSocket khi không cần nữa
     * Sử dụng WebSocketManager để hủy tất cả sự kiện của room
     */
    public void unsubscribeFromEvents() {
        Room room = currentRoom.getValue();
        if (room == null || room.getId() == null) {
            return;
        }

        Long roomId = room.getId();

        // Sử dụng WebSocketManager để hủy tất cả sự kiện của room
        webSocketManager.unsubscribeAllRoomEvents(roomId);
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
     * Tin nhắn hệ thống được đánh dấu bằng prefix đặc biệt
     *
     * @param systemMessage Nội dung tin nhắn hệ thống
     */
    private void addSystemMessage(String systemMessage) {
        Room room = currentRoom.getValue();
        if (room == null || room.getId() == null || systemMessage == null || systemMessage.trim().isEmpty()) {
            return;
        }

        // Tạo tin nhắn hệ thống mới với định dạng ISO-8601 cho thời gian
        ChatMessage message = new ChatMessage(
                null,
                room.getId(),
                null,  // null user vì đây là tin nhắn hệ thống
                false, // không phải khách
                null,  // không có tên khách
                SYSTEM_MESSAGE_PREFIX + systemMessage, // thêm prefix để định danh
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(new Date()) // thời gian hiện tại
        );

        // Tạo một bản sao mới của danh sách tin nhắn để LiveData nhận biết thay đổi
        List<ChatMessage> currentMessages = chatMessages.getValue();
        if (currentMessages != null) {
            List<ChatMessage> updatedMessages = new ArrayList<>(currentMessages);
            updatedMessages.add(message);
            chatMessages.postValue(updatedMessages);
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
        return chatClient.sendChatMessage(room.getId(), message);
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
     * Xử lý tin nhắn mới nhận được từ WebSocket
     * Đảm bảo LiveData được cập nhật đúng cách để UI cập nhật
     *
     * @param chatMessage Tin nhắn chat mới
     */
    private void handleNewChatMessage(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return;
        }

        List<ChatMessage> currentMessages = chatMessages.getValue();
        if (currentMessages != null) {
            // Tạo bản sao mới để đảm bảo LiveData nhận biết thay đổi
            List<ChatMessage> updatedMessages = new ArrayList<>(currentMessages);
            updatedMessages.add(chatMessage);

            // Sử dụng postValue để đảm bảo an toàn khi cập nhật từ background thread
            chatMessages.postValue(updatedMessages);
        }
    }

    /**
     * Xử lý sự kiện phòng (tham gia/rời đi)
     * Cập nhật danh sách người tham gia và hiển thị thông báo hệ thống
     *
     * @param event Đối tượng sự kiện chứa thông tin về người dùng và loại sự kiện
     */
    private void handleRoomEvent(RoomEvent event) {
        if (event == null) {
            return;
        }

        // Thêm tin nhắn hệ thống nếu có
        if (event.getMessage() != null && !event.getMessage().trim().isEmpty()) {
            addSystemMessage(event.getMessage());
        }

        // Cập nhật danh sách người tham gia trực tiếp từ sự kiện
        String eventType = event.getEventType();
        if (eventType != null) {
            if ("join".equalsIgnoreCase(eventType)) {
                addParticipant(event);
            } else if ("leave".equalsIgnoreCase(eventType)) {
                removeParticipant(event);
            } else {
                Log.d("WaitingRoomViewModel", "Nhận sự kiện không xác định: " + eventType);
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

        List<Participant> currentParticipants = new ArrayList<>(Objects.requireNonNull(participants.getValue()));

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
     * Xóa người tham gia khỏi danh sách khi họ rời phòng
     * Đảm bảo danh sách người tham gia được cập nhật đúng cách
     *
     * @param event Đối tượng sự kiện chứa thông tin người rời đi
     */
    private void removeParticipant(RoomEvent event) {
        if (event == null || event.getUserId() == null) return;

        List<Participant> currentParticipants = participants.getValue();
        if (currentParticipants == null || currentParticipants.isEmpty()) {
            return;
        }

        // Tạo bản sao để không thay đổi trực tiếp LiveData
        List<Participant> updatedParticipants = new ArrayList<>(currentParticipants);
        Participant toRemove = null;

        // Tìm người tham gia cần xóa
        for (Participant participant : updatedParticipants) {
            if (!participant.isGuest() &&
                    participant.getUser() != null &&
                    participant.getUser().getId() != null &&
                    participant.getUser().getId().equals(event.getUserId())) {
                toRemove = participant;
                break;
            }
        }

        // Xóa người tham gia khỏi danh sách
        if (toRemove != null) {
            updatedParticipants.remove(toRemove);

            // Sử dụng postValue để đảm bảo an toàn khi gọi từ background thread
            participants.postValue(updatedParticipants);

            // Ghi log để dễ dàng debug
            Log.d("WaitingRoomViewModel", "Đã xóa người tham gia: " +
                    (toRemove.getUser() != null ? toRemove.getUser().getUsername() : "unknown"));
        }
    }

    /**
     * Xử lý sự kiện bắt đầu trò chơi từ WebSocket
     * Cập nhật thông tin phòng hiện tại và thông báo cho UI
     *
     * @param updatedRoom Phòng đã được cập nhật với trạng thái trò chơi mới
     */
    private void handleGameStartEvent(Room updatedRoom) {
        if (updatedRoom != null) {
            // Cập nhật thông tin phòng hiện tại
            currentRoom.postValue(updatedRoom);

            // Thông báo sự kiện bắt đầu trò chơi thành công
            gameStartEvent.postValue(Resource.success(updatedRoom, "Trò chơi đã bắt đầu"));

            // Ghi log cho mục đích debug
            Log.d("WaitingRoomViewModel", "Nhận sự kiện bắt đầu trò chơi: " + updatedRoom.getId());
        }
    }

    /**
     * Xử lý sự kiện đóng trò chơi từ WebSocket
     * <p>
     * Cập nhật thông tin phòng hiện tại và thông báo cho UI
     *
     * @param updatedRoom Phòng đã được cập nhật với trạng thái trò chơi đã đóng
     */
    private void handleGameCloseEvent(Room updatedRoom) {
        if (updatedRoom != null) {
            // Cập nhật thông tin phòng hiện tại
            currentRoom.postValue(updatedRoom);

            // Thông báo sự kiện đóng trò chơi thành công
            gameCloseEvent.postValue(Resource.success(updatedRoom, "Trò chơi đã kết thúc"));

            // Ghi log cho mục đích debug
            Log.d("WaitingRoomViewModel", "Nhận sự kiện đóng trò chơi: " + updatedRoom.getId());
        }
    }

    /**
     * Kết nối lại WebSocket nếu mất kết nối
     */
    public void reconnectWebSocket() {
        Room room = currentRoom.getValue();
        if (room != null && room.getId() != null) {
            // Đảm bảo hủy kết nối hiện tại trước khi kết nối lại
            webSocketManager.disconnect();
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

    public LiveData<Resource<Room>> getGameCloseEvent() {
        return gameCloseEvent;
    }

    /**
     * Kiểm tra xem WebSocket đã kết nối hay chưa
     *
     * @return true nếu đã kết nối, false nếu chưa kết nối
     */
    public boolean isWebSocketConnected() {
        return webSocketManager.isConnected();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Hủy đăng ký các sự kiện khi ViewModel bị hủy
        unsubscribeFromEvents();
    }
}
