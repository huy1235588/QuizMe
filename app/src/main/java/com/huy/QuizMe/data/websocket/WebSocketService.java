package com.huy.QuizMe.data.websocket;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huy.QuizMe.BuildConfig;
import com.huy.QuizMe.utils.GsonUtils;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private static final String TAG = "WebSocketService";    // Đường dẫn WebSocket
    private static final String WEBSOCKET_URL = BuildConfig.BASE_URL.replace("http", "ws") + "ws/websocket";

    // Client STOMP
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    // Singleton instance
    private static WebSocketService instance;

    // Map để lưu trữ các subscription
    private final Map<String, Disposable> topicSubscriptions = new HashMap<>();

    // Quản lý SharedPreferences
    private final SharedPreferencesManager prefsManager;

    /**
     * WebSocketMessage wrapper - phải khớp với định dạng từ backend nhưng không dùng Lombok
     */
    public static class WebSocketMessage<T> {
        private String type;
        private T payload;
        private String timestamp;

        // Constructor đầy đủ
        public WebSocketMessage(String type, T payload, String timestamp) {
            this.type = type;
            this.payload = payload;
            this.timestamp = timestamp;
        }

        // Constructor chỉ với type và payload
        public WebSocketMessage(String type, T payload) {
            this.type = type;
            this.payload = payload;
            // Không đặt timestamp ở đây vì timestamp được set từ backend
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public T getPayload() {
            return payload;
        }

        public void setPayload(T payload) {
            this.payload = payload;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        // equals và hashCode để so sánh đối tượng
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WebSocketMessage<?> that = (WebSocketMessage<?>) o;
            return Objects.equals(type, that.type) &&
                    Objects.equals(payload, that.payload) &&
                    Objects.equals(timestamp, that.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, payload, timestamp);
        }

        @NonNull
        @Override
        public String toString() {
            return "WebSocketMessage{" +
                    "type='" + type + '\'' +
                    ", payload=" + payload +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }

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
            // Clean up existing connection if present
            disconnect();

            Log.d(TAG, "Connecting to WebSocket server at " + WEBSOCKET_URL);

            // Create new client
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL);

            // Set heartbeats for stable connection
            stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

            // Add authentication header matching Spring Security expectations
            String authToken = prefsManager.getAuthToken();
            List<StompHeader> headers = new ArrayList<>();
            if (authToken != null && !authToken.isEmpty()) {
                headers.add(new StompHeader("Authorization", "Bearer " + authToken));
            }

            // Listen for connection lifecycle events
            Disposable lifecycleDisposable = stompClient.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lifecycleEvent -> {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                                Log.d(TAG, "WebSocket connection established");
                                break;
                            case ERROR:
                                Log.e(TAG, "WebSocket connection error", lifecycleEvent.getException());
                                // Consider implementing reconnection logic here
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
            Log.e(TAG, "Failed to connect to WebSocket server", e);
            return false;
        }
    }

    /**
     * Kết nối tới WebSocket server với ConnectionListener callback
     *
     * @param listener callback để xử lý sự kiện kết nối
     * @return true nếu thành công, false nếu có lỗi
     */
    public boolean connect(ConnectionListener listener) {
        try {
            // Clean up existing connection if present
            disconnect();

            Log.d(TAG, "Connecting to WebSocket server at " + WEBSOCKET_URL);

            // Create new client
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEBSOCKET_URL);

            // Set heartbeats for stable connection
            stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

            // Add authentication header matching Spring Security expectations
            String authToken = prefsManager.getAuthToken();
            List<StompHeader> headers = new ArrayList<>();
            if (authToken != null && !authToken.isEmpty()) {
                headers.add(new StompHeader("Authorization", "Bearer " + authToken));
            }

            // Listen for connection lifecycle events
            Disposable lifecycleDisposable = stompClient.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(lifecycleEvent -> {
                        switch (lifecycleEvent.getType()) {
                            case OPENED:
                                Log.d(TAG, "WebSocket connection established");
                                if (listener != null) {
                                    listener.onConnected();
                                }
                                break;
                            case ERROR:
                                Log.e(TAG, "WebSocket connection error", lifecycleEvent.getException());
                                if (listener != null) {
                                    String errorMsg = lifecycleEvent.getException() != null
                                            ? lifecycleEvent.getException().getMessage()
                                            : "Unknown connection error";
                                    listener.onError(errorMsg);
                                }
                                break;
                            case CLOSED:
                                Log.d(TAG, "WebSocket connection closed");
                                if (listener != null) {
                                    listener.onDisconnected();
                                }
                                break;
                        }
                    }, throwable -> {
                        Log.e(TAG, "WebSocket lifecycle error", throwable);
                        if (listener != null) {
                            listener.onError(throwable.getMessage());
                        }
                    });

            compositeDisposable.add(lifecycleDisposable);
            stompClient.connect(headers);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Failed to connect to WebSocket server", e);
            if (listener != null) {
                listener.onError(e.getMessage());
            }
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
     * Đăng ký lắng nghe một topic cụ thể (Generic method)
     *
     * @param topicPath    Đường dẫn đầy đủ đến topic
     * @param payloadClass Lớp đối tượng dữ liệu
     * @param listener     Callback xử lý tin nhắn
     * @return boolean     Trạng thái đã đăng ký thành công hay không
     */
    public <T> boolean subscribeToTopic(String topicPath, Class<T> payloadClass, MessageListener<T> listener) {
        return subscribe(topicPath, payloadClass, listener);
    }

    /**
     * Đăng ký một topic tùy chỉnh cho room
     *
     * @param roomId    ID của phòng
     * @param eventType Loại sự kiện (ví dụ: "/custom-event")
     * @param clazz     Lớp đối tượng dữ liệu
     * @param listener  Callback xử lý sự kiện
     * @return boolean  Trạng thái đăng ký thành công hay không
     */
    public <T> boolean subscribeToCustomRoomEvent(Long roomId, String eventType, Class<T> clazz, MessageListener<T> listener) {
        if (roomId == null || eventType == null || eventType.trim().isEmpty()) {
            Log.e(TAG, "Không thể đăng ký sự kiện tùy chỉnh: roomId hoặc eventType không hợp lệ");
            return false;
        }
        String topicPath = WebSocketConstants.createRoomTopicPath(roomId, eventType);
        return subscribe(topicPath, clazz, listener);
    }

    /**
     * Đăng ký một topic và xử lý tin nhắn - phân tích cấu trúc WebSocketMessage từ server
     *
     * @param topicPath    Đường dẫn đến topic
     * @param payloadClass Lớp đối tượng dữ liệu
     * @param listener     Callback xử lý tin nhắn
     * @return boolean  Trạng thái đã đăng ký thành công hay không
     */
    private <T> boolean subscribe(String topicPath, Class<T> payloadClass, MessageListener<T> listener) {
        // Kiểm tra điều kiện đầu vào
        if (topicPath == null || listener == null) {
            Log.e(TAG, "Không thể đăng ký: đường dẫn topic hoặc listener là null");
            return false;
        }

        if (!isConnected()) {
            Log.e(TAG, "WebSocket chưa kết nối. Hãy kết nối trước khi đăng ký.");
            return false;
        }

        // Hủy subscription cũ nếu có để tránh đăng ký trùng lặp
        unsubscribe(topicPath);

        try {
            Disposable disposable = stompClient.topic(topicPath)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            topicMessage -> {
                                try {
                                    // Lấy dữ liệu JSON từ tin nhắn
                                    String json = topicMessage.getPayload();
                                    if (BuildConfig.DEBUG) {
                                        Log.d(TAG, "Nhận tin nhắn từ " + topicPath + ": " + json);
                                    }

                                    // Xử lý JSON - tối ưu quá trình parse
                                    JsonElement jsonElement = JsonParser.parseString(json);
                                    if (!jsonElement.isJsonObject()) {
                                        Log.w(TAG, "Tin nhắn không phải là JSON object: " + json);
                                        return;
                                    }

                                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                                    // Trích xuất các trường từ WebSocketMessage
                                    if (jsonObject.has("payload")) {
                                        JsonElement payloadElement = jsonObject.get("payload");

                                        // Xử lý đặc biệt cho payload dạng String
                                        if (payloadClass == String.class) {
                                            String stringPayload = payloadElement.isJsonPrimitive() ?
                                                    payloadElement.getAsString() : payloadElement.toString();
                                            listener.onMessage((T) stringPayload);
                                        } else {
                                            // Cho các đối tượng phức tạp
                                            try {
                                                T payloadData = GsonUtils.fromJson(payloadElement.toString(), payloadClass);
                                                if (payloadData != null) {
                                                    listener.onMessage(payloadData);
                                                } else {
                                                    Log.w(TAG, "Parse payload thất bại: kết quả null");
                                                }
                                            } catch (Exception e) {
                                                Log.e(TAG, "Lỗi khi chuyển đổi payload sang " + payloadClass.getSimpleName(), e);
                                            }
                                        }
                                    } else {
                                        Log.w(TAG, "Tin nhắn thiếu trường payload: " + json);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Lỗi xử lý tin nhắn từ " + topicPath, e);
                                }
                            },
                            throwable -> Log.e(TAG, "Lỗi đăng ký cho " + topicPath, throwable)
                    );

            // Lưu subscription
            topicSubscriptions.put(topicPath, disposable);
            compositeDisposable.add(disposable);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to subscribe to " + topicPath, e);
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
     * Gửi một tin nhắn tới một destination cụ thể
     *
     * @param destination Đích đến của tin nhắn
     * @param message     Nội dung tin nhắn (sẽ được chuyển thành JSON)
     * @return boolean    Trạng thái đã gửi thành công hay không
     */
    public boolean sendMessage(String destination, Object message) {
        if (!isConnected()) {
            Log.e(TAG, "WebSocket chưa kết nối. Hãy kết nối trước khi gửi tin nhắn.");
            return false;
        }

        if (destination == null || destination.trim().isEmpty() || message == null) {
            Log.e(TAG, "Không thể gửi tin nhắn: destination hoặc message là null");
            return false;
        }

        try {
            String jsonMessage = GsonUtils.toJson(message);
            compositeDisposable.add(
                    stompClient.send(destination, jsonMessage)
                            .compose(applySchedulers())
                            .subscribe(
                                    () -> {
                                        if (BuildConfig.DEBUG) {
                                            Log.d(TAG, "Gửi tin nhắn thành công tới " + destination);
                                        }
                                    },
                                    throwable -> Log.e(TAG, "Lỗi gửi tin nhắn tới " + destination, throwable)
                            )
            );
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi chuẩn bị gửi tin nhắn: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Áp dụng scheduler để chuyển đổi luồng xử lý cho Completable
     * Giúp đảm bảo các hoạt động mạng được thực hiện trên background thread
     * và kết quả được xử lý trên main thread
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
     * Interface cho callback xử lý sự kiện kết nối
     */
    public interface ConnectionListener {
        void onConnected();

        void onDisconnected();

        void onError(String error);
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
