package com.huy.QuizMe.ui.quiz;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import android.app.Application;
import android.os.CountDownTimer;

import com.huy.QuizMe.data.model.Question;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.repository.QuestionRepository;
import com.huy.QuizMe.data.repository.QuizRepository;
import com.huy.QuizMe.data.repository.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuizPlayViewModel extends AndroidViewModel {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final MediatorLiveData<Resource<Quiz>> quiz = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<Question>>> questions = new MediatorLiveData<>();
    
    // Lưu trữ câu trả lời của người dùng (QuestionId -> UserAnswer)
    private final Map<Long, String> userAnswers = new HashMap<>();
    // Lưu trữ trạng thái đúng/sai của từng câu hỏi
    private final Map<Long, Boolean> answerResults = new HashMap<>();
    
    // Thời gian làm bài
    private final MutableLiveData<Long> remainingTime = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionPosition = new MutableLiveData<>(0);
    private CountDownTimer timer;
    private long quizStartTime;

    public QuizPlayViewModel(@NonNull Application application) {
        super(application);
        quizRepository = new QuizRepository();
        questionRepository = new QuestionRepository();
        quizStartTime = System.currentTimeMillis();
    }

    /**
     * Tải thông tin quiz theo ID
     *
     * @param quizId ID của quiz cần tải
     * @return LiveData với Resource của quiz
     */
    public LiveData<Resource<Quiz>> loadQuiz(int quizId) {
        LiveData<Resource<Quiz>> source = quizRepository.getQuizById(quizId);
        quiz.addSource(source, quiz::setValue);
        return quiz;
    }

    /**
     * Tải tất cả câu hỏi của quiz
     *
     * @param quizId ID của quiz
     * @return LiveData với Resource danh sách câu hỏi
     */
    public LiveData<Resource<List<Question>>> loadQuestions(int quizId) {
        LiveData<Resource<List<Question>>> source = questionRepository.getAllQuestions(quizId);
        questions.addSource(source, questions::setValue);
        return questions;
    }

    /**
     * Lưu câu trả lời của người dùng
     *
     * @param questionId ID của câu hỏi
     * @param answer     Câu trả lời của người dùng
     * @param isCorrect  Câu trả lời có đúng không
     */
    public void saveUserAnswer(Long questionId, String answer, boolean isCorrect) {
        userAnswers.put(questionId, answer);
        answerResults.put(questionId, isCorrect);
    }

    /**
     * Tính điểm dựa trên số câu trả lời đúng và thời gian làm bài
     *
     * @return Số điểm đạt được
     */
    public int calculateScore() {
        int correctAnswers = 0;
        for (Boolean isCorrect : answerResults.values()) {
            if (isCorrect) correctAnswers++;
        }

        // Số điểm = (số câu đúng / tổng số câu) * 1000
        if (answerResults.isEmpty()) return 0;
        
        // Cộng điểm thưởng nếu hoàn thành nhanh (tối đa 200 điểm)
        int baseScore = (correctAnswers * 1000) / answerResults.size();
        long quizDuration = System.currentTimeMillis() - quizStartTime;
        int timeBonus = calculateTimeBonus(quizDuration, answerResults.size());
        
        return baseScore + timeBonus;
    }
    
    /**
     * Tính điểm thưởng dựa trên thời gian làm bài
     * 
     * @param duration Thời gian làm bài (milliseconds)
     * @param questionCount Số lượng câu hỏi
     * @return Điểm thưởng (tối đa 200 điểm)
     */
    private int calculateTimeBonus(long duration, int questionCount) {
        // Giả sử mỗi câu hỏi cần trung bình 15 giây
        long expectedDuration = questionCount * 15 * 1000L;
        if (duration < expectedDuration) {
            // Tính phần trăm thời gian tiết kiệm được
            double savedTimePercentage = (double)(expectedDuration - duration) / expectedDuration;
            // Tối đa 200 điểm thưởng nếu hoàn thành trong một nửa thời gian dự kiến
            return (int)(savedTimePercentage * 200);
        }
        return 0;
    }
    
    /**
     * Bắt đầu bộ đếm thời gian cho câu hỏi hiện tại
     * 
     * @param timeLimit Thời gian giới hạn cho câu hỏi (giây)
     */
    public void startQuestionTimer(int timeLimit) {
        if (timer != null) {
            timer.cancel();
        }
        
        // Mặc định 30 giây nếu không có giới hạn
        long timeLimitMillis = (timeLimit > 0) ? timeLimit * 1000L : 30000L;
        
        timer = new CountDownTimer(timeLimitMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime.setValue(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                remainingTime.setValue(0L);
            }
        }.start();
    }
    
    /**
     * Dừng bộ đếm thời gian
     */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
    
    /**
     * Lấy thời gian còn lại
     */
    public LiveData<Long> getRemainingTime() {
        return remainingTime;
    }
    
    /**
     * Lấy vị trí câu hỏi hiện tại
     */
    public LiveData<Integer> getCurrentQuestionPosition() {
        return currentQuestionPosition;
    }
    
    /**
     * Cập nhật vị trí câu hỏi hiện tại
     */
    public void setCurrentQuestionPosition(int position) {
        currentQuestionPosition.setValue(position);
    }
    
    /**
     * Lấy số lượng câu hỏi đã trả lời
     */
    public int getAnsweredQuestionCount() {
        return answerResults.size();
    }
    
    /**
     * Lấy số lượng câu trả lời đúng
     */
    public int getCorrectAnswerCount() {
        int count = 0;
        for (Boolean isCorrect : answerResults.values()) {
            if (isCorrect) count++;
        }
        return count;
    }
    
    /**
     * Kiểm tra xem người dùng đã trả lời câu hỏi chưa
     */
    public boolean hasAnsweredQuestion(Long questionId) {
        return userAnswers.containsKey(questionId);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        if (timer != null) {
            timer.cancel();
        }
    }
}
