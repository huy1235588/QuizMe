package com.huy.QuizMe.ui.quiz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.game.QuestionGameDTO;
import com.huy.QuizMe.data.model.game.QuestionResultDTO;
import com.huy.QuizMe.data.repository.Resource;

import java.util.Arrays;

public class QuizGameActivity extends AppCompatActivity {

    private QuizGameViewModel viewModel;    // Các thành phần UI
    private TextView tvQuizProgress;
    private TextView tvQuizType;
    private LinearProgressIndicator progressBarQuiz;
    private ImageView ivQuestionImage;
    private TextView tvQuestionText;
    private MaterialButton btnAnswerHow;
    private MaterialButton btnAnswerWhat;
    private MaterialButton btnAnswerWhich;
    private MaterialButton btnAnswerWhere;
    private ProgressBar loadingProgressBar;
    private View gameContentView;    // Dữ liệu
    private Room currentRoom;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 10;
    private boolean isWaitingForFirstQuestion = true;

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
        btnAnswerHow = findViewById(R.id.btn_answer_how);
        btnAnswerWhat = findViewById(R.id.btn_answer_what);
        btnAnswerWhich = findViewById(R.id.btn_answer_which);
        btnAnswerWhere = findViewById(R.id.btn_answer_where);

        // Khởi tạo loading components
        loadingProgressBar = findViewById(R.id.loading_progress_bar);
        gameContentView = findViewById(R.id.game_content);

        // Thiết lập listeners cho các nút trả lời
        setupAnswerButtonListeners();
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
                // Cập nhật giao diện bộ đếm thời gian nếu bạn có
                // Hiện tại, chỉ ghi log
                // Log.d("QuizGameActivity", "Remaining time: " + remainingTime);
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
    }

    private void setupAnswerButtonListeners() {
        btnAnswerHow.setOnClickListener(v -> onAnswerSelected(0));
        btnAnswerWhat.setOnClickListener(v -> onAnswerSelected(1));
        btnAnswerWhich.setOnClickListener(v -> onAnswerSelected(2));
        btnAnswerWhere.setOnClickListener(v -> onAnswerSelected(3));
    }

    private void onAnswerSelected(int optionIndex) {
        // Kiểm tra xem câu trả lời đã được gửi chưa
        if (viewModel.isAnswerSubmittedForCurrentQuestion()) {
            Toast.makeText(this, "Bạn đã trả lời câu hỏi này", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy câu hỏi hiện tại để tìm ID lựa chọn được chọn
        QuestionGameDTO currentQuestion = viewModel.getCurrentQuestion().getValue();
        if (currentQuestion == null || currentQuestion.getOptions() == null) {
            Toast.makeText(this, "Không tìm thấy thông tin câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (optionIndex >= currentQuestion.getOptions().size()) {
            Toast.makeText(this, "Lựa chọn không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy ID của lựa chọn
        Long selectedOptionId = currentQuestion.getOptions().get(optionIndex).getId();

        // Gửi câu trả lời thông qua ViewModel
        viewModel.submitAnswer(Arrays.asList(selectedOptionId));

        // Hiển thị phản hồi
        Toast.makeText(this, "Đã gửi câu trả lời", Toast.LENGTH_SHORT).show();
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
            // TODO: Load image from URL using Glide or Picasso
            // For now, just show the image view
            ivQuestionImage.setVisibility(android.view.View.VISIBLE);
        } else {
            ivQuestionImage.setVisibility(android.view.View.GONE);
        }

        // Hiển thị options
        if (question.getOptions() != null && question.getOptions().size() >= 4) {
            btnAnswerHow.setText(question.getOptions().get(0).getContent());
            btnAnswerWhat.setText(question.getOptions().get(1).getContent());
            btnAnswerWhich.setText(question.getOptions().get(2).getContent());
            btnAnswerWhere.setText(question.getOptions().get(3).getContent());
        }

        // Enable answer buttons
        updateAnswerButtonsState(true);
    }

    /**
     * Hiển thị kết quả câu hỏi
     */
    private void showQuestionResult(QuestionResultDTO result) {
        if (result == null) return;

        // TODO: Thực hiện logic hiển thị kết quả
        // Có thể hiển thị câu trả lời đúng, câu trả lời của người dùng, giải thích, v.v.

        if (result.getExplanations() != null && !result.getExplanations().isEmpty()) {
            Toast.makeText(this, "Giải thích: " + result.getExplanations(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Cập nhật trạng thái các nút trả lời
     */
    private void updateAnswerButtonsState(boolean enabled) {
        btnAnswerHow.setEnabled(enabled);
        btnAnswerWhat.setEnabled(enabled);
        btnAnswerWhich.setEnabled(enabled);
        btnAnswerWhere.setEnabled(enabled);
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

    private void displaySampleQuestion() {
        // Giữ phương thức này để tương thích ngược nhưng không sử dụng nó nữa
        // Các câu hỏi thực tế sẽ đến thông qua các observer của ViewModel
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
}