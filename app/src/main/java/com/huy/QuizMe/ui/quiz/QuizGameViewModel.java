package com.huy.QuizMe.ui.quiz;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.game.AnswerRequest;
import com.huy.QuizMe.data.model.game.LeaderboardDTO;
import com.huy.QuizMe.data.model.game.QuestionGameDTO;
import com.huy.QuizMe.data.model.game.QuestionResultDTO;
import com.huy.QuizMe.data.repository.GameRepository;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.websocket.GameWebSocketClient;
import com.huy.QuizMe.data.websocket.WebSocketManager;
import com.huy.QuizMe.data.websocket.WebSocketService;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

/**
 * ViewModel cho màn hình chơi Quiz
 * Quản lý dữ liệu trò chơi và tương tác với server qua WebSocket
 */
public class QuizGameViewModel extends ViewModel {
    private static final String TAG = "QuizGameViewModel";

    // WebSocket clients và managers
    private final WebSocketManager webSocketManager;
    private final GameWebSocketClient gameClient;
    private final SharedPreferencesManager sharedPreferencesManager;
    private final GameRepository gameRepository;

    // LiveData cho trạng thái game
    private final MutableLiveData<Room> currentRoom = new MutableLiveData<>();
    private final MutableLiveData<QuestionGameDTO> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Integer> remainingTime = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalTime = new MutableLiveData<>();
    private final MutableLiveData<QuestionResultDTO> questionResult = new MutableLiveData<>();
    private final MutableLiveData<LeaderboardDTO> leaderboard = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> nextQuestionEvent = new MutableLiveData<>();

    // LiveData cho trạng thái UI
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> gameEnded = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> answerSubmitted = new MutableLiveData<>(false);    // LiveData cho startGame
    private final MediatorLiveData<Resource<Boolean>> startGameResult = new MediatorLiveData<>();

    // Game state
    private Long currentQuestionId;
    private int currentQuestionNumber = 0;
    private int totalQuestions = 0;
    private long questionStartTime = 0;

    /**
     * Constructor - Khởi tạo ViewModel với các thành phần cần thiết
     */
    public QuizGameViewModel() {
        this.webSocketManager = WebSocketManager.getInstance();
        this.gameClient = GameWebSocketClient.getInstance();
        this.sharedPreferencesManager = SharedPreferencesManager.getInstance();
        this.gameRepository = new GameRepository();
    }

    // ===========================
    // CÁC PHƯƠNG THỨC CÔNG KHAI
    // ===========================

    /**
     * Thiết lập game và kết nối WebSocket
     *
     * @param room Thông tin phòng chơi
     */
    public void setupGame(Room room) {
        if (room == null) {
            errorMessage.setValue("Không tìm thấy thông tin phòng");
            return;
        }

        currentRoom.setValue(room);
        if (room.getQuiz() != null) {
            totalQuestions = room.getQuiz().getQuestionCount();
        }

        // Kết nối WebSocket và đăng ký events
        connectAndSubscribeToGameEvents(room.getId());
    }

    /**
     * Bắt đầu trò chơi
     *
     * @param roomId ID của phòng
     */
    public void startGame(Long roomId) {
        if (roomId == null) {
            errorMessage.setValue("Room ID không hợp lệ");
            return;
        }

        Log.d(TAG, "Starting game for room: " + roomId);
        LiveData<Resource<Boolean>> result = gameRepository.startGame(roomId);

        // Use MediatorLiveData to properly handle the result
        startGameResult.addSource(result, resource -> {
            startGameResult.setValue(resource);

            if (resource != null) {
                Log.d(TAG, "StartGame result: " + resource.getStatus());
                if (resource.getStatus() == Resource.Status.ERROR) {
                    errorMessage.setValue(resource.getMessage());
                } else if (resource.getStatus() == Resource.Status.SUCCESS) {
                    Log.d(TAG, "Game started successfully, waiting for first question...");
                }
            }
        });
    }

    /**
     * Gửi câu trả lời cho câu hỏi hiện tại
     *
     * @param selectedOptionIds Danh sách ID các option được chọn
     */
    public void submitAnswer(List<Long> selectedOptionIds) {
        if (currentQuestionId == null) {
            errorMessage.setValue("Không tìm thấy câu hỏi hiện tại");
            return;
        }

        if (answerSubmitted.getValue() == Boolean.TRUE) {
            Log.w(TAG, "Answer already submitted for current question");
            return;
        }

        Room room = currentRoom.getValue();
        if (room == null) {
            errorMessage.setValue("Không tìm thấy thông tin phòng");
            return;
        }

        // Tính thời gian trả lời
        long answerTime = System.currentTimeMillis() - questionStartTime;

        // Tạo request
        AnswerRequest answerRequest = new AnswerRequest(
                currentQuestionId,
                selectedOptionIds,
                null, // textAnswer cho TYPE_ANSWER
                answerTime
        );

        // Gửi qua WebSocket
        boolean success = gameClient.sendAnswer(room.getId(), answerRequest);
        if (success) {
            answerSubmitted.setValue(true);
            Log.d(TAG, "Answer submitted successfully");
        } else {
            errorMessage.setValue("Không thể gửi câu trả lời");
        }
    }

    /**
     * Gửi câu trả lời dạng text (cho TYPE_ANSWER)
     *
     * @param textAnswer Câu trả lời dạng text
     */
    public void submitTextAnswer(String textAnswer) {
        if (currentQuestionId == null) {
            errorMessage.setValue("Không tìm thấy câu hỏi hiện tại");
            return;
        }

        if (answerSubmitted.getValue() == Boolean.TRUE) {
            Log.w(TAG, "Answer already submitted for current question");
            return;
        }

        Room room = currentRoom.getValue();
        if (room == null) {
            errorMessage.setValue("Không tìm thấy thông tin phòng");
            return;
        }

        // Tính thời gian trả lời
        long answerTime = System.currentTimeMillis() - questionStartTime;

        // Tạo request
        AnswerRequest answerRequest = new AnswerRequest(
                currentQuestionId,
                null, // selectedOptions
                textAnswer,
                answerTime
        );

        // Gửi qua WebSocket
        boolean success = gameClient.sendAnswer(room.getId(), answerRequest);
        if (success) {
            answerSubmitted.setValue(true);
            Log.d(TAG, "Text answer submitted successfully");
        } else {
            errorMessage.setValue("Không thể gửi câu trả lời");
        }
    }

    /**
     * Hủy đăng ký tất cả events và cleanup
     */
    public void cleanup() {
        Room room = currentRoom.getValue();
        if (room != null) {
            gameClient.unsubscribeAllGameEvents(room.getId());
        }
        Log.d(TAG, "QuizGameViewModel cleaned up");
    }

    // ===========================
    // KẾT NỐI WEBSOCKET & SỰ KIỆN
    // ===========================

    /**
     * Kết nối WebSocket và đăng ký lắng nghe các sự kiện game
     */
    private void connectAndSubscribeToGameEvents(Long roomId) {
        if (roomId == null) {
            errorMessage.setValue("Room ID không hợp lệ");
            return;
        }

        // Kiểm tra kết nối
        if (webSocketManager.isConnected()) {
            subscribeToGameEvents(roomId);
            isConnected.setValue(true);
            return;
        }

        // Chưa kết nối, thực hiện kết nối
        isLoading.setValue(true);
        isConnected.setValue(false);

        webSocketManager.connect(new WebSocketService.ConnectionListener() {
            @Override
            public void onConnected() {
                Log.d(TAG, "WebSocket connected for quiz game");
                isLoading.setValue(false);
                isConnected.setValue(true);
                subscribeToGameEvents(roomId);
            }

            @Override
            public void onDisconnected() {
                Log.w(TAG, "WebSocket disconnected during quiz game");
                isConnected.setValue(false);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "WebSocket connection error: " + error);
                isLoading.setValue(false);
                isConnected.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + error);
            }
        });
    }

    /**
     * Đăng ký lắng nghe tất cả các sự kiện game
     */
    private void subscribeToGameEvents(Long roomId) {
        Log.d(TAG, "Subscribing to game events for room: " + roomId);

        // Đăng ký nhận câu hỏi
        gameClient.subscribeToQuestions(roomId, QuestionGameDTO.class, this::onQuestionReceived);

        // Đăng ký nhận cập nhật thời gian
        gameClient.subscribeToTimer(roomId, this::onTimerUpdate);

        // Đăng ký nhận kết quả câu hỏi
        gameClient.subscribeToQuestionResults(roomId, QuestionResultDTO.class, this::onQuestionResult);

        // Đăng ký nhận bảng xếp hạng
        gameClient.subscribeToLeaderboard(roomId, LeaderboardDTO.class, this::onLeaderboardUpdate);

        // Đăng ký nhận sự kiện câu hỏi tiếp theo
        gameClient.subscribeToNextQuestion(roomId, Map.class, this::onNextQuestionEvent);

        // Đăng ký nhận sự kiện kết thúc trò chơi
        gameClient.subscribeToGameEnd(roomId, Object.class, this::onGameEnd);

        Log.d(TAG, "Successfully subscribed to all game events");
    }

    // ===========================
    // XỬ LÝ SỰ KIỆN WEBSOCKET
    // ===========================

    /**
     * Xử lý khi nhận được câu hỏi mới
     */
    private void onQuestionReceived(QuestionGameDTO question) {
        Log.d(TAG, "Received new question: " + question.getQuestionNumber());

        currentQuestion.setValue(question);
        currentQuestionId = question.getQuestionId();
        currentQuestionNumber = question.getQuestionNumber();
        questionStartTime = System.currentTimeMillis();

        // Reset answer submitted state
        answerSubmitted.setValue(false);

        // Clear previous results
        questionResult.setValue(null);
    }

    /**
     * Xử lý cập nhật timer
     */
    private void onTimerUpdate(Map<String, Object> timerData) {
        Log.d(TAG, "Received timer update: " + timerData);
        if (timerData != null) {
            Object remainingObj = timerData.get("remainingTime");
            Object totalObj = timerData.get("totalTime");

            // Chuyển đổi Object sang Integer an toàn
            if (remainingObj != null) {
                Integer remaining = convertToInteger(remainingObj);
                if (remaining != null) {
                    remainingTime.setValue(remaining);
                }
            }
            if (totalObj != null) {
                Integer total = convertToInteger(totalObj);
                if (total != null) {
                    totalTime.setValue(total);
                }
            }
        }
    }

    /**
     * Phương thức tiện ích để chuyển đổi Object sang Integer
     */
    private Integer convertToInteger(Object value) {
        if (value == null) return null;
        
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                Log.w(TAG, "Cannot convert to Integer: " + value);
                return null;
            }
        }
    }

    /**
     * Xử lý kết quả câu hỏi
     */
    private void onQuestionResult(QuestionResultDTO result) {
        Log.d(TAG, "Received question result");
        questionResult.setValue(result);
    }

    /**
     * Xử lý cập nhật leaderboard
     */
    private void onLeaderboardUpdate(LeaderboardDTO leaderboardData) {
        Log.d(TAG, "Received leaderboard update");
        leaderboard.setValue(leaderboardData);
    }

    /**
     * Xử lý sự kiện câu hỏi tiếp theo
     */
    private void onNextQuestionEvent(Map<String, Object> nextQuestionData) {
        Log.d(TAG, "Received next question event: " + nextQuestionData);
        if (nextQuestionData != null) {
            // Convert Object values to Integer safely
            Map<String, Integer> convertedData = new HashMap<>();
            for (Map.Entry<String, Object> entry : nextQuestionData.entrySet()) {
                Integer convertedValue = convertToInteger(entry.getValue());
                if (convertedValue != null) {
                    convertedData.put(entry.getKey(), convertedValue);
                }
            }
            nextQuestionEvent.setValue(convertedData);
        }
    }

    /**
     * Xử lý sự kiện kết thúc game
     */
    private void onGameEnd(Object gameEndData) {
        Log.d(TAG, "Game ended");
        gameEnded.setValue(true);
    }

    // ===========================
    // CÁC PHƯƠNG THỨC GETTER CHO LIVEDATA
    // ===========================

    public LiveData<Room> getCurrentRoom() {
        return currentRoom;
    }

    public LiveData<QuestionGameDTO> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<Integer> getRemainingTime() {
        return remainingTime;
    }

    public LiveData<Integer> getTotalTime() {
        return totalTime;
    }

    public LiveData<QuestionResultDTO> getQuestionResult() {
        return questionResult;
    }

    public LiveData<LeaderboardDTO> getLeaderboard() {
        return leaderboard;
    }

    public LiveData<Map<String, Integer>> getNextQuestionEvent() {
        return nextQuestionEvent;
    }

    public LiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getGameEnded() {
        return gameEnded;
    }

    public LiveData<Boolean> getAnswerSubmitted() {
        return answerSubmitted;
    }

    public LiveData<Resource<Boolean>> getStartGameResult() {
        return startGameResult;
    }

    // ===========================
    // CÁC PHƯƠNG THỨC TIỆN ÍCH
    // ===========================

    /**
     * Lấy số câu hỏi hiện tại
     */
    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    /**
     * Lấy tổng số câu hỏi
     */
    public int getTotalQuestions() {
        return totalQuestions;
    }

    /**
     * Kiểm tra xem đã gửi câu trả lời cho câu hỏi hiện tại chưa
     */
    public boolean isAnswerSubmittedForCurrentQuestion() {
        return answerSubmitted.getValue() == Boolean.TRUE;
    }

    /**
     * Reset trạng thái lỗi
     */
    public void clearError() {
        errorMessage.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanup();
    }
}
