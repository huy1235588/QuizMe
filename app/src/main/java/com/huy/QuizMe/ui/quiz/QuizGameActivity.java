package com.huy.QuizMe.ui.quiz;

import android.os.Bundle;
import android.widget.ImageView;
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

public class QuizGameActivity extends AppCompatActivity {

    private QuizGameViewModel viewModel;

    // UI Components
    private TextView tvQuizProgress;
    private TextView tvQuizType;
    private LinearProgressIndicator progressBarQuiz;
    private ImageView ivQuestionImage;
    private TextView tvQuestionText;
    private MaterialButton btnAnswerHow;
    private MaterialButton btnAnswerWhat;
    private MaterialButton btnAnswerWhich;
    private MaterialButton btnAnswerWhere;

    // Data
    private Room currentRoom;
    private int currentQuestionIndex = 0;
    private int totalQuestions = 10;

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
            // Có thông tin phòng - sẽ implement logic sau
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

        // Hiển thị dữ liệu mẫu
        displaySampleQuestion();
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

        // Thiết lập thông tin quiz từ room
        if (currentRoom != null && currentRoom.getQuiz() != null) {
            tvQuizType.setText(currentRoom.getQuiz().getTitle());
        }

        // Thiết lập progress ban đầu
        updateProgress();

        // Thiết lập listeners cho các nút trả lời
        setupAnswerButtonListeners();
    }

    private void setupObservers() {
        // TODO: Thiết lập observers cho ViewModel khi có logic game
        // Hiện tại chỉ là giao diện mẫu
    }

    private void setupAnswerButtonListeners() {
        btnAnswerHow.setOnClickListener(v -> onAnswerSelected("How"));
        btnAnswerWhat.setOnClickListener(v -> onAnswerSelected("What"));
        btnAnswerWhich.setOnClickListener(v -> onAnswerSelected("Which"));
        btnAnswerWhere.setOnClickListener(v -> onAnswerSelected("Where"));
    }

    private void onAnswerSelected(String answer) {
        // Hiển thị thông báo tạm thời
        Toast.makeText(this, "Đã chọn đáp án: " + answer, Toast.LENGTH_SHORT).show();

        // Tạm thời chuyển sang câu hỏi tiếp theo
        currentQuestionIndex++;
        if (currentQuestionIndex < totalQuestions) {
            updateProgress();
            displaySampleQuestion();
        } else {
            // Kết thúc quiz
            Toast.makeText(this, "Hoàn thành quiz!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void updateProgress() {
        // Cập nhật text progress
        String progressText = (currentQuestionIndex + 1) + "/" + totalQuestions;
        tvQuizProgress.setText(progressText);

        // Cập nhật progress bar (0-100)
        int progressPercent = (int) (((float) (currentQuestionIndex + 1) / totalQuestions) * 100);
        progressBarQuiz.setProgress(progressPercent);
    }

    private void displaySampleQuestion() {
        // Hiển thị câu hỏi mẫu
        String[] sampleQuestions = {
                "What is the capital of Vietnam?",
                "Which programming language is used for Android development?",
                "How many continents are there?",
                "Where is the Eiffel Tower located?",
                "What is the largest ocean on Earth?",
                "Which planet is known as the Red Planet?",
                "How many bones are in the human body?",
                "What is the smallest country in the world?",
                "Which element has the chemical symbol 'O'?",
                "Who painted the Mona Lisa?"
        };

        String[][] sampleOptions = {
                {"Hanoi", "Ho Chi Minh City", "Da Nang", "Hue"},
                {"Java", "Python", "C++", "JavaScript"},
                {"5", "6", "7", "8"},
                {"London", "Berlin", "Paris", "Rome"},
                {"Atlantic", "Pacific", "Indian", "Arctic"},
                {"Venus", "Mars", "Jupiter", "Saturn"},
                {"206", "208", "210", "212"},
                {"Monaco", "Vatican City", "Liechtenstein", "San Marino"},
                {"Gold", "Oxygen", "Silver", "Iron"},
                {"Van Gogh", "Picasso", "Da Vinci", "Monet"}
        };

        if (currentQuestionIndex < sampleQuestions.length) {
            tvQuestionText.setText(sampleQuestions[currentQuestionIndex]);

            // Cập nhật text cho các nút
            String[] options = sampleOptions[currentQuestionIndex];
            btnAnswerHow.setText(options[0]);
            btnAnswerWhat.setText(options[1]);
            btnAnswerWhich.setText(options[2]);
            btnAnswerWhere.setText(options[3]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký các sự kiện WebSocket
        if (viewModel != null) {
            // TODO: Implement cleanup when ViewModel has actual functionality
        }
    }
}