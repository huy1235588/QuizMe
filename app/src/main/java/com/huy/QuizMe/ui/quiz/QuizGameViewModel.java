package com.huy.QuizMe.ui.quiz;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.Question;
import com.huy.QuizMe.data.model.Room;

import java.util.List;

/**
 * ViewModel cho màn hình chơi Quiz
 * Quản lý dữ liệu trò chơi và tương tác với server qua WebSocket
 */
public class QuizGameViewModel extends ViewModel {
    
    // LiveData cho trạng thái game
    private final MutableLiveData<Room> currentRoom = new MutableLiveData<>();
    private final MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<List<Question>> questions = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> timeRemaining = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isGameStarted = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isGameFinished = new MutableLiveData<>(false);
    private final MutableLiveData<String> gameStatus = new MutableLiveData<>();
    
    // LiveData getters
    public LiveData<Room> getCurrentRoom() {
        return currentRoom;
    }
    
    public LiveData<Question> getCurrentQuestion() {
        return currentQuestion;
    }
    
    public LiveData<List<Question>> getQuestions() {
        return questions;
    }
    
    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    
    public LiveData<Integer> getTimeRemaining() {
        return timeRemaining;
    }
    
    public LiveData<Boolean> getIsGameStarted() {
        return isGameStarted;
    }
    
    public LiveData<Boolean> getIsGameFinished() {
        return isGameFinished;
    }
    
    public LiveData<String> getGameStatus() {
        return gameStatus;
    }
    
    // Phương thức khởi tạo trò chơi
    public void initializeGame(Room room) {
        currentRoom.setValue(room);
        // TODO: Khởi tạo danh sách câu hỏi từ room
    }
    
    public void startGame() {
        isGameStarted.setValue(true);
        gameStatus.setValue("Game Started");
        // TODO: Bắt đầu đếm ngược thời gian và hiển thị câu hỏi đầu tiên
    }
    
    public void submitAnswer(Long questionId, List<Long> selectedOptions) {
        // TODO: Xử lý việc gửi câu trả lời đến server qua WebSocket
        // Cập nhật trạng thái câu hỏi hiện tại
    }
    
    public void nextQuestion() {
        Integer current = currentQuestionIndex.getValue();
        if (current != null) {
            currentQuestionIndex.setValue(current + 1);
        }
        // TODO: Cập nhật câu hỏi hiện tại từ danh sách câu hỏi
    }
    
    public void finishGame() {
        isGameFinished.setValue(true);
        gameStatus.setValue("Game Finished");
        // TODO: Gửi kết quả đến server và xử lý kết thúc trò chơi
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // TODO: Giải phóng tài nguyên nếu cần thiết
        // Ví dụ: hủy đăng ký WebSocket nếu đang sử dụng
    }
}
