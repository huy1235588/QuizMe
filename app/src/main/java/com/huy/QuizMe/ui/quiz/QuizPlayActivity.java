package com.huy.QuizMe.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Question;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.databinding.ActivityQuizPlayBinding;
import com.huy.QuizMe.ui.quiz.adapters.QuestionAdapter;
import com.huy.QuizMe.ui.quiz.fragments.MultipleChoiceFragment;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuizPlayActivity extends AppCompatActivity implements MultipleChoiceFragment.OnAnswerSelectedListener {
    private ActivityQuizPlayBinding binding;
    private QuizPlayViewModel viewModel;
    private QuestionAdapter questionAdapter;
    private int quizId;
    private List<Question> questionList;
    private boolean canMoveToNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Khởi tạo ViewBinding
        binding = ActivityQuizPlayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thêm padding cho view để tránh bị che bởi thanh trạng thái
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return insets;
        });

        // Lấy Quiz ID từ intent
        quizId = getIntent().getIntExtra("EXTRA_QUIZ_ID", -1);
        if (quizId == -1) {
            Toast.makeText(this, "Quiz không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(QuizPlayViewModel.class);

        // Thiết lập UI
        setupUI();

        // Theo dõi vị trí câu hỏi hiện tại
        observeQuestionPosition();
        
        // Tải dữ liệu quiz
        loadQuizData();
    }

    // Thiết lập UI
    private void setupUI() {
        // Hiển thị loading
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.quizContent.setVisibility(View.GONE);

        // Cài đặt ViewPager cho câu hỏi (disabled swipe)
        binding.viewPagerQuestions.setUserInputEnabled(false);

        // Thiết lập listener cho nút Next
        binding.btnNext.setOnClickListener(v -> moveToNextQuestion());
        
        // Thiết lập listener cho nút quay lại
//        binding.btnBack.setOnClickListener(v -> {
//            if (viewModel.getCurrentQuestionPosition().getValue() != null &&
//                viewModel.getCurrentQuestionPosition().getValue() > 0) {
//                onBackPressed();
//            } else {
//                showExitConfirmDialog();
//            }
//        });
        
        // Thiết lập timer
        viewModel.getRemainingTime().observe(this, this::updateTimerDisplay);
    }
    
    private void updateTimerDisplay(long timeMillis) {
        if (timeMillis <= 0) {
            binding.tvTimer.setText("00:00");
            // Tự động chuyển sang câu hỏi tiếp theo nếu hết thời gian
            if (!canMoveToNext) {
                canMoveToNext = true;
                moveToNextQuestion();
            }
            return;
        }
        
        // Format mm:ss
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", 
                TimeUnit.MILLISECONDS.toMinutes(timeMillis),
                TimeUnit.MILLISECONDS.toSeconds(timeMillis) - 
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMillis)));
        binding.tvTimer.setText(timeFormatted);
        
        // Hiển thị màu đỏ khi thời gian còn ít
        if (timeMillis <= 10000) { // 10 giây
            binding.tvTimer.setTextColor(getResources().getColor(R.color.errorColor, getTheme()));
        } else {
            binding.tvTimer.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }
    }
    
    private void showExitConfirmDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Thoát bài kiểm tra");
        builder.setMessage("Bạn có chắc chắn muốn thoát? Tiến trình làm bài sẽ không được lưu.");
        builder.setPositiveButton("Thoát", (dialog, which) -> finish());
        builder.setNegativeButton("Tiếp tục", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void observeQuestionPosition() {
        viewModel.getCurrentQuestionPosition().observe(this, position -> {
            if (questionList != null && position < questionList.size()) {
                Question currentQuestion = questionList.get(position);
                // Bắt đầu đếm thời gian cho câu hỏi
                int timeLimit = currentQuestion.getTimeLimit() != null ? currentQuestion.getTimeLimit() : 30;
                viewModel.startQuestionTimer(timeLimit);
                
                // Kiểm tra xem đã trả lời câu hỏi này chưa
                canMoveToNext = viewModel.hasAnsweredQuestion(currentQuestion.getId());
                updateNextButtonState();
            }
        });
    }

    // Tải câu hỏi từ ViewModel
    private void loadQuizData() {
        // Tải thông tin quiz
        viewModel.loadQuiz(quizId).observe(this, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                Quiz quiz = resource.getData();
                binding.tvQuizTitle.setText(quiz.getTitle());

                // Tải câu hỏi
                loadQuestions();
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // Thiết lập câu hỏi cho ViewPager
    private void loadQuestions() {
        viewModel.loadQuestions(quizId).observe(this, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                questionList = resource.getData();
                setupQuestions(questionList);

                // Ẩn loading, hiện quiz content
                binding.loadingLayout.setVisibility(View.GONE);
                binding.quizContent.setVisibility(View.VISIBLE);
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(this, resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupQuestions(List<Question> questions) {
        // Khởi tạo adapter với danh sách câu hỏi
        questionAdapter = new QuestionAdapter(this, questions);
        binding.viewPagerQuestions.setAdapter(questionAdapter);
        
        // Bắt đầu từ câu hỏi đầu tiên
        viewModel.setCurrentQuestionPosition(0);

        // Cập nhật progress và số câu hỏi
        updateQuestionIndicator(1, questions.size());
        updateProgressBar(1, questions.size());
    }

    private void moveToNextQuestion() {
        if (!canMoveToNext) return;
        
        viewModel.stopTimer(); // Dừng timer khi di chuyển câu hỏi
        
        int currentItem = binding.viewPagerQuestions.getCurrentItem();
        int totalQuestions = questionAdapter.getItemCount();

        if (currentItem < totalQuestions - 1) {
            // Chuyển đến câu hỏi tiếp theo
            binding.viewPagerQuestions.setCurrentItem(currentItem + 1);
            updateQuestionIndicator(currentItem + 2, totalQuestions);
            updateProgressBar(currentItem + 2, totalQuestions);
            viewModel.setCurrentQuestionPosition(currentItem + 1);
            canMoveToNext = false;
            updateNextButtonState();
        } else {
            // Đã hoàn thành quiz, chuyển đến trang kết quả
            showResults();
        }
    }

    private void updateQuestionIndicator(int current, int total) {
        binding.tvQuestionIndicator.setText(String.format(Locale.getDefault(), "%d/%d", current, total));
    }

    private void updateProgressBar(int current, int total) {
        int progress = (current * 100) / total;
        binding.progressBar.setProgress(progress);
    }
    
    private void updateNextButtonState() {
        // Thay đổi text và state của nút Next dựa vào trạng thái hiện tại
        if (binding.viewPagerQuestions.getCurrentItem() == questionAdapter.getItemCount() - 1) {
            binding.btnNext.setText("Hoàn thành");
        } else {
            binding.btnNext.setText("Tiếp theo");
        }
        
        binding.btnNext.setEnabled(canMoveToNext);
        binding.btnNext.setAlpha(canMoveToNext ? 1.0f : 0.5f);
    }

    private void showResults() {
        viewModel.stopTimer(); // Dừng timer khi kết thúc quiz
        
        // Hoàn thành quiz, chuyển đến trang kết quả
        int score = viewModel.calculateScore();
        Intent resultIntent = new Intent(this, QuizResultActivity.class);
        resultIntent.putExtra("EXTRA_SCORE", score);
        resultIntent.putExtra("EXTRA_QUIZ_ID", quizId);
        resultIntent.putExtra("EXTRA_TOTAL_QUESTIONS", questionList.size());
        resultIntent.putExtra("EXTRA_CORRECT_ANSWERS", viewModel.getCorrectAnswerCount());
        startActivity(resultIntent);
        finish();
    }
    
    @Override
    public void onAnswerSelected(boolean isCorrect) {
        // Được gọi khi người dùng chọn một đáp án từ fragment
        canMoveToNext = true;
        updateNextButtonState();
        
        // Hiển thị phản hồi thích hợp
        String feedback = isCorrect ? "Chính xác!" : "Chưa đúng!";
        Toast.makeText(this, feedback, Toast.LENGTH_SHORT).show();
    }
    
    public QuizPlayViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int currentPosition = binding.viewPagerQuestions.getCurrentItem();
        if (currentPosition > 0) {
            binding.viewPagerQuestions.setCurrentItem(currentPosition - 1);
            updateQuestionIndicator(currentPosition, questionAdapter.getItemCount());
            updateProgressBar(currentPosition, questionAdapter.getItemCount());
            viewModel.setCurrentQuestionPosition(currentPosition - 1);
        } else {
            showExitConfirmDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopTimer();
        binding = null;
    }
}