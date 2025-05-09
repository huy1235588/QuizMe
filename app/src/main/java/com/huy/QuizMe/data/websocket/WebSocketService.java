package com.huy.QuizMe.data.websocket;

import android.util.Log;

import com.huy.QuizMe.BuildConfig;
import com.huy.QuizMe.data.model.ChatMessage;
import com.huy.QuizMe.utils.GsonUtils;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

/**
 * Service xử lý và quản lý kết nối WebSocket với server
 * Giúp các component khác trong ứng dụng không phải quan tâm đến chi tiết triển khai
 * của WebSocket mà vẫn có thể gửi và nhận tin nhắn một cách đơn giản
 */
public class WebSocketService {
    private static final String TAG = "WebSocketService";

    // Đường dẫn WebSocket
    private static final String WEBSOCKET_URL = BuildConfig.BASE_URL.replace("http", "ws") + "/ws";

    // Constants cho các topic WebSocket (phải khớp với backend)
    public static final String ROOM_TOPIC_PREFIX = "/topic/room/";
    public static final String CHAT_EVENT = "/chat";
    public static final String GAME_START_EVENT = "/start";
    public static final String PLAYER_JOIN_EVENT = "/player-join";
    public static final String PLAYER_LEAVE_EVENT = "/player-leave";
    public static final String GAME_PROGRESS_EVENT = "/progress";
    public static final String GAME_END_EVENT = "/end";

    // Client STOMP
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    // Singleton instance
    private static WebSocketService instance;

    // Map để lưu trữ các subscription
    private final Map<String, Disposable> topicSubscriptions = new HashMap<>();

    // Quản lý SharedPreferences
    private final SharedPreferencesManager prefsManager;

    private WebSocketService() {
        compositeDisposable = new CompositeDisposable();
        prefsManager = SharedPreferencesManager.getInstance();
    }

    public static synchronized WebSocketService getInstance() {
        if (instance == null) {
            instance = new WebSocketService();
        }
        return instance;
    }

    /**
     * Kết nối đến WebSocket server
     *
     * @return boolean Trạng thái kết nối đã được khởi tạo thành công hay không
     */
    public boolean connect() {
        try {
            // Disconnect nếu đã kết nối trước đó
            disconnect();

            // Tạo client mới
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL);

            // Log chi tiết nếu đang trong môi trường debug
            if (BuildConfig.DEBUG) {
                stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);
            }

            // Thêm header xác thực nếu có
            String authToken = prefsManager.getAuthToken();
            List<StompHeader> headers = new ArrayList<>();
            if (authToken != null && !authToken.isEmpty()) {
                headers.add(new StompHeader("Authorization", "Bearer " + authToken));
                stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);
            }

            // Kết nối và theo dõi trạng thái
            Disposable lifecycleDisposable = stompClient.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lifecycleEvent -> {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                                Log.d(TAG, "WebSocket connection opened");
                                break;
                            case ERROR:
                                Log.e(TAG, "WebSocket connection error", lifecycleEvent.getException());
                                break;
                            case CLOSED:
                                Log.d(TAG, "WebSocket connection closed");
                                break;
                        }
                    }, throwable -> {
                        Log.e(TAG, "WebSocket lifecycle error", throwable);
                    });

            compositeDisposable.add(lifecycleDisposable);
            stompClient.connect(headers);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error connecting to WebSocket server", e);
            return false;
        }
    }

    /**
     * Ngắt kết nối khỏi WebSocket server và dọn dẹp tài nguyên
     */
    public void disconnect() {
        if (stompClient != null) {
            // Hủy đăng ký tất cả các topic
            unsubscribeAll();

            // Ngắt kết nối
            if (stompClient.isConnected()) {
                stompClient.disconnect();
            }

            // Hủy tất cả disposables
            if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
                compositeDisposable = new CompositeDisposable();
            }
        }
    }

    /**
     * Hủy đăng ký tất cả các topic đã đăng ký
     */
    private void unsubscribeAll() {
        for (Disposable disposable : topicSubscriptions.values()) {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        topicSubscriptions.clear();
    }

    /**
     * Đăng ký lắng nghe tin nhắn trò chuyện trong một phòng
     *
     * @param roomId   ID của phòng
     * @param listener Callback xử lý tin nhắn
     * @return boolean Trạng thái đã đăng ký thành công hay không
     */
    public boolean subscribeToChatMessages(Long roomId, MessageListener<ChatMessage> listener) {
        if (roomId == null) {
            Log.e(TAG, "Cannot subscribe to chat: roomId is null");
            return false;
        }
        return subscribe(ROOM_TOPIC_PREFIX + roomId + CHAT_EVENT, ChatMessage.class, listener);
    }

    /**
     * Đăng ký sự kiện bắt đầu trò chơi
     *
     * @param roomId   ID của phòng
     * @param clazz    Lớp đối tượng dữ liệu
     * @param listener Callback xử lý sự kiện
     */
    public <T> void subscribeToGameStartEvents(Long roomId, Class<T> clazz, MessageListener<T> listener) {
        subscribe(ROOM_TOPIC_PREFIX + roomId + GAME_START_EVENT, clazz, listener);
    }

    /**
     * Đăng ký sự kiện người chơi tham gia phòng
     *
     * @param roomId   ID của phòng
     * @param clazz    Lớp đối tượng dữ liệu
     * @param listener Callback xử lý sự kiện
     */
    public <T> void subscribeToPlayerJoinEvents(Long roomId, Class<T> clazz, MessageListener<T> listener) {
        subscribe(ROOM_TOPIC_PREFIX + roomId + PLAYER_JOIN_EVENT, clazz, listener);
    }

    /**
     * Đăng ký sự kiện người chơi rời phòng
     *
     * @param roomId   ID của phòng
     * @param clazz    Lớp đối tượng dữ liệu
     * @param listener Callback xử lý sự kiện
     */
    public <T> void subscribeToPlayerLeaveEvents(Long roomId, Class<T> clazz, MessageListener<T> listener) {
        subscribe(ROOM_TOPIC_PREFIX + roomId + PLAYER_LEAVE_EVENT, clazz, listener);
    }

    /**
     * Đăng ký sự kiện cập nhật tiến trình trò chơi
     *
     * @param roomId   ID của phòng
     * @param clazz    Lớp đối tượng dữ liệu
     * @param listener Callback xử lý sự kiện
     */
    public <T> void subscribeToGameProgressEvents(Long roomId, Class<T> clazz, MessageListener<T> listener) {
        subscribe(ROOM_TOPIC_PREFIX + roomId + GAME_PROGRESS_EVENT, clazz, listener);
    }

    /**
     * Đăng ký sự kiện kết thúc trò chơi
     *
     * @param roomId   ID của phòng
     * @param clazz    Lớp đối tượng dữ liệu
     * @param listener Callback xử lý sự kiện
     */
    public <T> void subscribeToGameEndEvents(Long roomId, Class<T> clazz, MessageListener<T> listener) {
        subscribe(ROOM_TOPIC_PREFIX + roomId + GAME_END_EVENT, clazz, listener);
    }

    /**
     * Đăng ký một topic tùy chỉnh
     *
     * @param roomId    ID của phòng
     * @param eventType Loại sự kiện
     * @param clazz     Lớp đối tượng dữ liệu
     * @param listener  Callback xử lý sự kiện
     */
    public <T> void subscribeToCustomRoomEvent(Long roomId, String eventType, Class<T> clazz, MessageListener<T> listener) {
        subscribe(ROOM_TOPIC_PREFIX + roomId + "/" + eventType, clazz, listener);
    }

    /**
     * Đăng ký một topic và xử lý tin nhắn
     *
     * @param topicPath Đường dẫn đến topic
     * @param clazz     Lớp đối tượng dữ liệu
     * @param listener  Callback xử lý tin nhắn
     * @return boolean  Trạng thái đã đăng ký thành công hay không
     */
    private <T> boolean subscribe(String topicPath, Class<T> clazz, MessageListener<T> listener) {
        // Kiểm tra tham số
        if (topicPath == null || listener == null) {
            Log.e(TAG, "Cannot subscribe: topic path or listener is null");
            return false;
        }

        // Kiểm tra kết nối
        if (stompClient == null || !stompClient.isConnected()) {
            Log.e(TAG, "WebSocket is not connected. Please connect before subscribing.");
            return false;
        }

        // Hủy đăng ký topic cũ nếu có
        unsubscribe(topicPath);

        try {
            // Đăng ký topic mới
            Disposable disposable = stompClient.topic(topicPath)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            topicMessage -> {
                                try {
                                    // Chuyển đổi dữ liệu JSON thành đối tượng
                                    String payload = topicMessage.getPayload();
                                    T data = GsonUtils.fromJson(payload, clazz);
                                    listener.onMessage(data);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing message from topic " + topicPath, e);
                                }
                            },
                            throwable -> Log.e(TAG, "Error subscribing to topic " + topicPath, throwable)
                    );

            // Lưu subscription để có thể hủy sau này
            topicSubscriptions.put(topicPath, disposable);
            compositeDisposable.add(disposable);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error subscribing to topic " + topicPath, e);
            return false;
        }
    }

    /**
     * Hủy đăng ký lắng nghe một topic cụ thể
     *
     * @param topicPath Đường dẫn đến topic
     */
    public void unsubscribe(String topicPath) {
        if (topicSubscriptions.containsKey(topicPath)) {
            Disposable disposable = topicSubscriptions.remove(topicPath);
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    /**
     * Gửi tin nhắn trò chuyện đến một phòng
     *
     * @param roomId  ID của phòng
     * @param message Nội dung tin nhắn
     * @return boolean Trạng thái đã gửi thành công hay không
     */
    public boolean sendChatMessage(Long roomId, String message) {
        if (roomId == null) {
            Log.e(TAG, "Cannot send message: roomId is null");
            return false;
        }
        return sendMessage("/app/chat/" + roomId, message);
    }

    /**
     * Gửi một tin nhắn tới một destination cụ thể
     *
     * @param destination Đích đến của tin nhắn
     * @param message     Nội dung tin nhắn (sẽ được chuyển thành JSON)
     * @return boolean    Trạng thái đã gửi thành công hay không
     */
    public boolean sendMessage(String destination, Object message) {
        if (stompClient == null || !stompClient.isConnected()) {
            Log.e(TAG, "WebSocket is not connected. Please connect before sending a message.");
            return false;
        }

        try {
            String jsonMessage = GsonUtils.toJson(message);
            compositeDisposable.add(
                    stompClient.send(destination, jsonMessage)
                            .compose(applySchedulers())
                            .subscribe(
                                    () -> Log.d(TAG, "Message sent successfully to " + destination),
                                    throwable -> Log.e(TAG, "Error sending message to " + destination, throwable)
                            )
            );
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error sending message", e);
            return false;
        }
    }

    /**
     * Áp dụng scheduler để chuyển đổi luồng xử lý
     */
    private CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Interface cho callback xử lý tin nhắn
     */
    public interface MessageListener<T> {
        void onMessage(T message);
    }

    /**
     * Kiểm tra xem đã kết nối thành công chưa
     *
     * @return true nếu đã kết nối, false nếu chưa
     */
    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }
}
