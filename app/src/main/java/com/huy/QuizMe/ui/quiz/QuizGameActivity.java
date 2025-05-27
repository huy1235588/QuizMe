package com.huy.QuizMe.ui.quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.game.QuestionGameDTO;
import com.huy.QuizMe.data.model.game.QuestionResultDTO;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.utils.ImageLoader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuizGameActivity extends AppCompatActivity {

    private QuizGameViewModel viewModel;    // Các thành phần UI
    private TextView tvQuizProgress;
    private TextView tvQuizType;
    private LinearProgressIndicator progressBarQuiz;
    private ImageView ivQuestionImage;
    private TextView tvQuestionText;
    private MaterialButton btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4;
    private ProgressBar loadingProgressBar;
    private View gameContentView;
    private TextView tvFeedback;

    // New feedback overlay components
    private View feedbackOverlay;
    private TextView tvFeedbackResult;
    private TextView tvPoints;

    // Timer UI components
    private com.google.android.material.progressindicator.CircularProgressIndicator timerProgress;
    private TextView tvTimer;
    private View timerContainer;    // Dữ liệu
    private Room currentRoom;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 10;
    private boolean isWaitingForFirstQuestion = true;
    private Long userSelectedOptionId = null; // Track user's selected answer

    // Timeout handling
    private Handler timeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable timeoutRunnable;
    private static final long QUESTION_TIMEOUT_MS = 15000; // 15 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(QuizGameViewModel.class);

        // Lấy dữ liệu từ intent
        currentRoom = (Room) getIntent().getSerializableExtra("ROOM");
        if (currentRoom != null) {
            // Có thông tin phòng - thiết lập game
            if (currentRoom.getQuiz() != null) {
                totalQuestions = currentRoom.getQuiz().getQuestionCount();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo giao diện
        initializeViews();
        setupObservers();

        // Hiển thị loading ban đầu
        showLoadingState(true);

        // Thiết lập game với ViewModel và bắt đầu game
        viewModel.setupGame(currentRoom);
        // Bắt đầu game để nhận câu hỏi đầu tiên
        if (currentRoom.getId() != null) {
            viewModel.startGame(currentRoom.getId());
            startQuestionTimeout();
        }
    }

    private void initializeViews() {
        // Khởi tạo các thành phần UI
        tvQuizProgress = findViewById(R.id.tv_quiz_progress);
        tvQuizType = findViewById(R.id.tv_quiz_type);
        progressBarQuiz = findViewById(R.id.progress_bar_quiz);
        ivQuestionImage = findViewById(R.id.iv_question_image);
        tvQuestionText = findViewById(R.id.tv_question_text);
        btnAnswer1 = findViewById(R.id.btn_answer_1);
        btnAnswer2 = findViewById(R.id.btn_answer_2);
        btnAnswer3 = findViewById(R.id.btn_answer_3);
        btnAnswer4 = findViewById(R.id.btn_answer_4);        // Khởi tạo loading components
        loadingProgressBar = findViewById(R.id.loading_progress_bar);
        gameContentView = findViewById(R.id.game_content);        // Khởi tạo feedback component

        // Khởi tạo new feedback overlay components
        feedbackOverlay = findViewById(R.id.feedback_overlay);
        tvFeedbackResult = findViewById(R.id.tv_feedback_result);
        tvPoints = findViewById(R.id.tv_points);

        // Khởi tạo timer components
        timerProgress = findViewById(R.id.timer_progress);
        tvTimer = findViewById(R.id.tv_timer);
        timerContainer = findViewById(R.id.timer_container);

        // Thiết lập listeners cho các nút trả lời
        setupAnswerButtonListeners();

        // Khởi tạo timer UI
        initializeTimerUI();
    }

    private void setupObservers() {
        // Theo dõi kết quả startGame
        viewModel.getStartGameResult().observe(this, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        // Game đã bắt đầu thành công, đợi câu hỏi đầu tiên
                        break;
                    case ERROR:
                        // Lỗi khi bắt đầu game
                        showLoadingState(false);
                        Toast.makeText(this, "Lỗi khi bắt đầu game: " + resource.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    case LOADING:
                        // Đang bắt đầu game
                        break;
                }
            }
        });

        // Theo dõi câu hỏi hiện tại
        viewModel.getCurrentQuestion().observe(this, question -> {
            if (question != null) {
                // Ẩn loading và hiển thị câu hỏi đầu tiên
                if (isWaitingForFirstQuestion) {
                    showLoadingState(false);
                    isWaitingForFirstQuestion = false;
                    cancelQuestionTimeout();
                }

                displayQuestion(question);
                currentQuestionIndex = question.getQuestionNumber() - 1; // Chuyển đổi thành chỉ số dựa trên 0
                updateProgress();
            }
        });

        // Theo dõi bộ đếm thời gian
        viewModel.getRemainingTime().observe(this, remainingTime -> {
            if (remainingTime != null) {
                // Cập nhật giao diện bộ đếm thời gian
                updateTimerUI(remainingTime);
            }
        });

        // Theo dõi tổng thời gian của câu hỏi
        viewModel.getTotalTime().observe(this, totalTime -> {
            if (totalTime != null && timerProgress != null) {
                // Thiết lập timer progress maximum khi có tổng thời gian mới
                timerProgress.setMax(100);
            }
        });

        // Theo dõi kết quả câu hỏi
        viewModel.getQuestionResult().observe(this, result -> {
            if (result != null) {
                // Xử lý kết quả câu hỏi - hiển thị câu trả lời đúng, câu trả lời của người dùng, v.v.
                showQuestionResult(result);
            }
        });

        // Theo dõi bảng xếp hạng
        viewModel.getLeaderboard().observe(this, leaderboard -> {
            if (leaderboard != null) {
                // Cập nhật giao diện bảng xếp hạng nếu bạn có
                // Hiện tại, chỉ ghi log thứ hạng
                // Log.d("QuizGameActivity", "Leaderboard updated with " + leaderboard.getRankings().size() + " players");
            }
        });

        // Theo dõi trạng thái kết nối
        viewModel.getIsConnected().observe(this, isConnected -> {
            if (isConnected != null) {
                // Cập nhật giao diện trạng thái kết nối
                // Có thể hiển thị chỉ báo kết nối
            }
        });

        // Theo dõi trạng thái đang tải
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                // Hiển thị/ẩn chỉ báo đang tải
                // progressBarQuiz.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Theo dõi lỗi
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearError();
            }
        });

        // Theo dõi kết thúc trò chơi
        viewModel.getGameEnded().observe(this, gameEnded -> {
            if (gameEnded != null && gameEnded) {
                Toast.makeText(this, "Trò chơi kết thúc!", Toast.LENGTH_SHORT).show();
                // Chuyển đến màn hình kết quả hoặc quay lại phòng chờ
                finish();
            }
        });

        // Theo dõi trạng thái gửi câu trả lời
        viewModel.getAnswerSubmitted().observe(this, answerSubmitted -> {
            if (answerSubmitted != null) {
                updateAnswerButtonsState(!answerSubmitted);
            }
        });

        // Theo dõi sự kiện câu hỏi tiếp theo
        viewModel.getNextQuestionEvent().observe(this, nextQuestionData -> {
            if (nextQuestionData != null) {
                handleNextQuestionEvent(nextQuestionData);
            }
        });
    }

    private void setupAnswerButtonListeners() {
        btnAnswer1.setOnClickListener(v -> onAnswerSelected(0, btnAnswer1));
        btnAnswer2.setOnClickListener(v -> onAnswerSelected(1, btnAnswer2));
        btnAnswer3.setOnClickListener(v -> onAnswerSelected(2, btnAnswer3));
        btnAnswer4.setOnClickListener(v -> onAnswerSelected(3, btnAnswer4));
    }

    private void onAnswerSelected(int optionIndex, MaterialButton button) {
        // Kiểm tra xem câu trả lời đã được gửi chưa
        if (viewModel.isAnswerSubmittedForCurrentQuestion()) {
            return;
        }

        // Lấy câu hỏi hiện tại để tìm ID lựa chọn được chọn
        QuestionGameDTO currentQuestion = viewModel.getCurrentQuestion().getValue();
        if (currentQuestion == null || currentQuestion.getOptions() == null ||
                optionIndex >= currentQuestion.getOptions().size()) {
            return;
        }

        // Lưu lại lựa chọn của người dùng
        userSelectedOptionId = currentQuestion.getOptions().get(optionIndex).getId();

        // Highlight the selected button
        resetAnswerButtonStyles();
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_selected));
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        // Gửi câu trả lời thông qua ViewModel
        viewModel.submitAnswer(Collections.singletonList(userSelectedOptionId));
    }

    private void updateProgress() {
        // Cập nhật text progress
        String progressText = (currentQuestionIndex + 1) + "/" + totalQuestions;
        tvQuizProgress.setText(progressText);

        // Cập nhật progress bar (0-100)
        int progressPercent = (int) (((float) (currentQuestionIndex + 1) / totalQuestions) * 100);
        progressBarQuiz.setProgress(progressPercent);
    }

    /**
     * Hiển thị câu hỏi từ ViewModel
     */
    private void displayQuestion(QuestionGameDTO question) {
        if (question == null) return;

        // Hiển thị nội dung câu hỏi
        tvQuestionText.setText(question.getContent());

        // Hiển thị loại câu hỏi
        String questionType = question.getType() != null ? question.getType() : "Không xác định";
        tvQuizType.setText(questionType);

        // Hiển thị hình ảnh nếu có
        if (question.getImageUrl() != null && !question.getImageUrl().isEmpty()) {
            // Tải hình ảnh bằng ImageLoader
            ImageLoader.loadImage(this,
                    ivQuestionImage,
                    question.getImageUrl(),
                    R.drawable.bg_quiz,
                    R.drawable.bg_quiz
            );

            // Hiển thị hình ảnh câu hỏi
            ivQuestionImage.setVisibility(android.view.View.VISIBLE);
        } else {
            ivQuestionImage.setVisibility(android.view.View.GONE);
        }

        // Hiển thị options
        if (question.getOptions() != null && question.getOptions().size() >= 4) {
            btnAnswer1.setText(question.getOptions().get(0).getContent());
            btnAnswer2.setText(question.getOptions().get(1).getContent());
            btnAnswer3.setText(question.getOptions().get(2).getContent());
            btnAnswer4.setText(question.getOptions().get(3).getContent());
        }

        // Enable answer buttons
        updateAnswerButtonsState(true);

        // Reset button styles and hide feedback
        resetAnswerButtonStyles();
        hideFeedback();

        // Reset user selected option
        userSelectedOptionId = null;
    }

    /**
     * Hiển thị kết quả câu hỏi
     */
    private void showQuestionResult(QuestionResultDTO result) {
        if (result == null) return;

        // Chặn các nút trả lời để không thể chọn nữa
        updateAnswerButtonsState(false);

        // Lấy câu hỏi hiện tại từ ViewModel
        QuestionGameDTO currentQuestion = viewModel.getCurrentQuestion().getValue();
        if (currentQuestion == null || currentQuestion.getOptions() == null) {
            return;
        }

        // Hiển thị câu trả lời đúng và sai
        highlightAnswerResults(result, currentQuestion);

        // Show feedback/explanation
        showFeedback(result);
    }

    /**
     * Cập nhật trạng thái các nút trả lời
     */
    private void updateAnswerButtonsState(boolean enabled) {
        btnAnswer1.setEnabled(enabled);
        btnAnswer2.setEnabled(enabled);
        btnAnswer3.setEnabled(enabled);
        btnAnswer4.setEnabled(enabled);
    }

    /**
     * Hiển thị/ẩn trạng thái loading
     */
    private void showLoadingState(boolean isLoading) {
        if (loadingProgressBar != null && gameContentView != null) {
            loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            gameContentView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Khởi tạo giao diện timer
     */
    private void initializeTimerUI() {
        // Ẩn timer container lúc đầu
        if (timerContainer != null) {
            timerContainer.setVisibility(View.GONE);
        }

        // Thiết lập timer progress initial state
        if (timerProgress != null) {
            timerProgress.setProgress(0);
            timerProgress.setMax(100); // Progress từ 0-100%
        }

        // Thiết lập timer text
        if (tvTimer != null) {
            tvTimer.setText("--:--");
        }
    }

    /**
     * Cập nhật giao diện timer
     */
    private void updateTimerUI(Integer remainingTimeSeconds) {
        if (remainingTimeSeconds == null || remainingTimeSeconds < 0) {
            return;
        }

        // Hiển thị timer container nếu chưa hiển thị
        if (timerContainer != null && timerContainer.getVisibility() != View.VISIBLE) {
            timerContainer.setVisibility(View.VISIBLE);
        }

        // Tính toán phút và giây
        int minutes = remainingTimeSeconds / 60;
        int seconds = remainingTimeSeconds % 60;

        // Cập nhật text hiển thị thời gian
        if (tvTimer != null) {
            tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
        }

        // Cập nhật progress của timer (assuming total time của mỗi câu hỏi)
        // Lấy total time từ ViewModel nếu có
        Integer totalTimeSeconds = viewModel.getTotalTime().getValue();
        if (timerProgress != null && totalTimeSeconds != null && totalTimeSeconds > 0) {
            int progressPercent = (int) (((float) remainingTimeSeconds / totalTimeSeconds) * 100);
            timerProgress.setProgress(progressPercent);

            // Đổi màu timer khi thời gian sắp hết
            if (remainingTimeSeconds <= 10) {
                // Màu đỏ khi còn 10 giây
                timerProgress.setIndicatorColor(getResources().getColor(android.R.color.holo_red_dark, null));
                tvTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
            } else if (remainingTimeSeconds <= 30) {
                // Màu cam khi còn 30 giây
                timerProgress.setIndicatorColor(getResources().getColor(android.R.color.holo_orange_dark, null));
                tvTimer.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
            } else {
                // Màu xanh bình thường
                timerProgress.setIndicatorColor(getResources().getColor(android.R.color.holo_blue_bright, null));
                tvTimer.setTextColor(getResources().getColor(android.R.color.black, null));
            }
        }
    }

    /**
     * Bắt đầu timeout cho việc đợi câu hỏi đầu tiên
     */
    private void startQuestionTimeout() {
        cancelQuestionTimeout(); // Hủy timeout trước đó nếu có

        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (isWaitingForFirstQuestion) {
                    // Timeout - ẩn loading và hiển thị thông báo lỗi
                    showLoadingState(false);
                    Toast.makeText(QuizGameActivity.this,
                            "Không thể tải câu hỏi. Vui lòng thử lại.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };

        timeoutHandler.postDelayed(timeoutRunnable, QUESTION_TIMEOUT_MS);
    }

    /**
     * Hủy timeout cho việc đợi câu hỏi
     */
    private void cancelQuestionTimeout() {
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy timeout handler
        cancelQuestionTimeout();

        // Hủy đăng ký các sự kiện WebSocket thông qua ViewModel
        if (viewModel != null) {
            viewModel.cleanup();
        }
    }

    /**
     * Xử lý sự kiện câu hỏi tiếp theo
     */
    private void handleNextQuestionEvent(Map<String, Integer> nextQuestionData) {
        if (nextQuestionData != null) {
            Integer countdown = nextQuestionData.get("countdown");
            Integer nextQuestionNumber = nextQuestionData.get("nextQuestionNumber");

        }
    }

    /**
     * Highlight câu trả lời đúng và sai
     */
    private void highlightAnswerResults(QuestionResultDTO result, QuestionGameDTO currentQuestion) {
        MaterialButton[] buttons = {btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4};

        for (int i = 0; i < buttons.length && i < currentQuestion.getOptions().size(); i++) {
            MaterialButton button = buttons[i];
            Long optionId = currentQuestion.getOptions().get(i).getId();

            // Kiểm tra xem đây có phải là câu trả lời đúng không
            boolean isCorrect = result.getCorrectOptions() != null &&
                    result.getCorrectOptions().contains(optionId);

            // Kiểm tra xem đây có phải là lựa chọn của người dùng không
            boolean isUserSelected = optionId.equals(userSelectedOptionId);

            if (isCorrect) {
                // Câu trả lời đúng - màu xanh
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct));
                button.setTextColor(ContextCompat.getColor(this, android.R.color.white));

            } else {
                // Câu trả lời sai
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_incorrect));
                button.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            }
            // Các tùy chọn khác giữ nguyên style default
        }
    }

    /**
     * Reset style của các nút trả lời về trạng thái ban đầu
     */
    private void resetAnswerButtonStyles() {
        MaterialButton[] buttons = {btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4};

        for (MaterialButton button : buttons) {
            button.setBackgroundColor(getColor(R.color.white));
            button.setTextColor(ContextCompat.getColor(this, R.color.black));
            button.setStrokeColorResource(R.color.gray);
            button.setStrokeWidth(2);
        }
    }

    /**
     * Hiển thị feedback/giải thích
     */
    private void showFeedback(QuestionResultDTO result) {
        // Danh sách các lựa chọn đúng
        List<Long> correctOptions = result.getCorrectOptions();
        boolean isCorrect = correctOptions != null && correctOptions.contains(userSelectedOptionId);

        if (isCorrect) {
            // Hiển thị overlay với kết quả đúng
            tvFeedbackResult.setText("Correct!");
            feedbackOverlay.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_correct));

        } else {
            // Hiển thị overlay với kết quả sai
            tvFeedbackResult.setText("Incorrect!");
            feedbackOverlay.setBackgroundColor(ContextCompat.getColor(this, R.color.answer_incorrect));
        }

        // Hiển thị điểm (nếu có từ kết quả, nếu không thì mặc định)
//            int points = result.getPoints() != null ? result.getPoints() : 945;
//            tvPoints.setText("+" + points);

        // Hiển thị overlay với hiệu ứng slide từ trên xuống
        showFeedbackWithSlideAnimation();
    }

    /**
     * Ẩn feedback
     */
    private void hideFeedback() {
        if (feedbackOverlay != null) {
            hideFeedbackWithSlideAnimation();
        }
    }

    /**
     * Hiển thị feedback overlay với hiệu ứng slide từ trên xuống
     */
    private void showFeedbackWithSlideAnimation() {
        if (feedbackOverlay == null) return;

        // Hiển thị view trước để có thể đo chiều cao
        feedbackOverlay.setVisibility(View.VISIBLE);
        feedbackOverlay.setAlpha(0f); // Bắt đầu với độ trong suốt

        // Đợi view được layout xong rồi mới bắt đầu animation
        feedbackOverlay.post(() -> {
            // Đặt vị trí ban đầu ở trên ngoài màn hình
            feedbackOverlay.setTranslationY(-feedbackOverlay.getHeight());

            // Tạo animation slide từ trên xuống với fade in
            feedbackOverlay.animate()
                    .translationY(0)
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        });
    }

    /**
     * Ẩn feedback overlay với hiệu ứng slide lên trên
     */
    private void hideFeedbackWithSlideAnimation() {
        if (feedbackOverlay == null) return;

        // Tạo animation slide lên trên với fade out
        feedbackOverlay.animate()
                .translationY(-feedbackOverlay.getHeight())
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new android.view.animation.AccelerateInterpolator())
                .withEndAction(() -> {
                    // Ẩn view sau khi animation hoàn thành
                    feedbackOverlay.setVisibility(View.GONE);
                    feedbackOverlay.setTranslationY(0); // Reset vị trí
                    feedbackOverlay.setAlpha(1f); // Reset alpha
                })
                .start();
    }
}