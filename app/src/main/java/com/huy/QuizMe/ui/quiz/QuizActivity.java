package com.huy.QuizMe.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Question;
import com.huy.QuizMe.data.repository.QuestionRepository;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.ui.views.QuestionView;
import com.huy.QuizMe.ui.views.QuizQuestionView;

import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private LinearLayout loadingLayout;
    private ConstraintLayout quizContent;
    private FrameLayout questionContainer;
    private Button nextButton;
    private TextView questionNumber, timerText;
    private ProgressBar timerProgress;
    private List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;
    private CountDownTimer timer;
    private QuestionRepository questionRepository;
    private static final String EXTRA_QUIZ_ID = "EXTRA_QUIZ_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Thêm padding cho view để tránh bị che bởi thanh trạng thái
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return insets;
        });

        // Khởi tạo các view
        initViews();

        // Khởi tạo repository
        questionRepository = new QuestionRepository();

        nextButton.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                displayQuestion();
            } else {
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("total", questions.size());
                startActivity(intent);
                finish();
            }
        });

        fetchQuestions();
    }

    // Khởi tạo các view
    private void initViews() {
        loadingLayout = findViewById(R.id.loading_layout);
        quizContent = findViewById(R.id.quiz_content);
        questionContainer = findViewById(R.id.question_container);
        nextButton = findViewById(R.id.next_button);
        questionNumber = findViewById(R.id.question_number);
        timerText = findViewById(R.id.timer_text);
        timerProgress = findViewById(R.id.timer_progress);
    }

    // Lấy danh sách câu hỏi từ Intent
    private void fetchQuestions() {
        int quizId = getIntent().getIntExtra(EXTRA_QUIZ_ID, -1);
        if (quizId == -1) {
            // Handle error
            return;
        }

        // Hiển thị trạng thái loading
        loadingLayout.setVisibility(View.VISIBLE);
        quizContent.setVisibility(View.GONE);

        // Sử dụng repository để lấy dữ liệu
        questionRepository.getAllQuestions(quizId).observe(this, resource -> {
            loadingLayout.setVisibility(View.GONE);
            
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                questions = resource.getData();
                quizContent.setVisibility(View.VISIBLE);
                
                if (!questions.isEmpty()) {
                    displayQuestion();
                } else {
                    // Hiển thị thông báo không có câu hỏi
                    showError("Không có câu hỏi nào cho bài kiểm tra này");
                }
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                // Hiển thị lỗi
                showError(resource.getMessage());
            }
        });
    }

    private void showError(String message) {
        // TODO: Hiển thị thông báo lỗi cho người dùng
        // Có thể sử dụng Toast, Dialog hoặc TextView
    }

    // Hiển thị câu hỏi
    private void displayQuestion() {
        questionContainer.removeAllViews();
        Question question = questions.get(currentIndex);
        QuestionView questionView;
        switch (question.getType()) {
            case "QUIZ":
                questionView = new QuizQuestionView(this);
                break;
            // Add other types
            default:
                return;
        }
        questionView.setQuestion(question);
        questionView.setOnAnswerSelectedListener(answer -> {
            if (timer != null) {
                timer.cancel();
            }
            boolean isCorrect = answer.equals(question.getCorrectAnswer());
            if (isCorrect) {
                score++;
            }
            questionView.showFeedback(isCorrect);
            nextButton.setVisibility(View.VISIBLE);
        });
        questionContainer.addView(questionView);
        questionNumber.setText((currentIndex + 1) + "/" + questions.size());
        startTimer();
    }

    // Hàm đếm ngược thời gian
    private void startTimer() {
        timerProgress.setMax(16);
        timer = new CountDownTimer(16000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                timerText.setText(String.valueOf(seconds));
                timerProgress.setProgress(seconds);
            }

            @Override
            public void onFinish() {
                // Time's up, handle as incorrect
                QuestionView questionView = (QuestionView) questionContainer.getChildAt(0);
                questionView.showFeedback(false);
                nextButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}