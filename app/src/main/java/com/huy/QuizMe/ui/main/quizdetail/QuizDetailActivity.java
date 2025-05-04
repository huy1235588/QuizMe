package com.huy.QuizMe.ui.main.quizdetail;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.databinding.ActivityQuizDetailBinding;

public class QuizDetailActivity extends AppCompatActivity {

    private ActivityQuizDetailBinding binding;
    private QuizDetailViewModel viewModel;
    private QuestionAdapter questionAdapter;

    private static final String EXTRA_QUIZ_ID = "EXTRA_QUIZ_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupUI();
        loadQuizData();
        setupObservers();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(QuizDetailViewModel.class);
    }

    private void setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Setup questions RecyclerView
        questionAdapter = new QuestionAdapter();
        binding.rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvQuestions.setAdapter(questionAdapter);

        // Setup action buttons
        binding.btnFavorite.setOnClickListener(v -> handleFavoriteQuiz());
        binding.btnMore.setOnClickListener(v -> handleMoreOptions());

        // Setup play buttons
        binding.btnPlaySolo.setOnClickListener(v -> handlePlaySolo());
        binding.btnPlayWithFriends.setOnClickListener(v -> handlePlayWithFriends());
        
//        binding.tvViewAll.setOnClickListener(v -> handleViewAllQuestions());
    }

    private void setupObservers() {
        viewModel.getQuizDetail().observe(this, resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        showLoading(false);
                        if (resource.getData() != null) {
                            displayQuizData(resource.getData());
                        }
                        break;
                    case ERROR:
                        showLoading(false);
                        showError(resource.getMessage());
                        break;
                    case LOADING:
                        showLoading(true);
                        break;
                }
            }
        });
    }

    private void loadQuizData() {
        int quizId = getIntent().getIntExtra(EXTRA_QUIZ_ID, -1);
        if (quizId != -1) {
            viewModel.loadQuizDetail(quizId);
        } else {
            showError("Invalid quiz ID");
        }
    }

    private void displayQuizData(Quiz quiz) {
        // Set quiz title and banner
        binding.tvQuizTitle.setText(quiz.getTitle());
        if (quiz.getQuizThumbnails() != null && !quiz.getQuizThumbnails().isEmpty()) {
            Glide.with(this)
                    .load(quiz.getQuizThumbnails())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.img_back_to_school)
                            .error(R.drawable.img_back_to_school))
                    .into(binding.ivQuizBanner);
        }

        // Set quiz statistics
        binding.tvQuestionCountValue.setText(String.valueOf(quiz.getQuestionCount()));
        binding.tvPlayedCountValue.setText(String.valueOf(quiz.getPlayCount()));
        binding.tvFavoriteCountValue.setText(String.valueOf(quiz.getFavoriteCount()));

        // Set creator information
        binding.tvCreatorName.setText(quiz.getCreatorName());
        binding.tvCreatorUsername.setText("@" + quiz.getCreatorName());
        Glide.with(this)
                .load(quiz.getCreatorAvatar())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.avatar_placeholder))
                .into(binding.ivCreatorAvatar);

        // Set quiz description
        binding.tvDescription.setText(quiz.getDescription());

        // Set questions
//        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
//            questionAdapter.setQuestions(quiz.getQuestions());
//        }
    }

    private void showLoading(boolean isLoading) {
        // TODO: Implement loading state (e.g., progress bar)
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle button click actions
    private void handleFavoriteQuiz() {
        // TODO: Implement favorite quiz functionality
    }

    private void handleMoreOptions() {
        // TODO: Implement more options functionality
    }

    private void handlePlaySolo() {
        // TODO: Implement play solo functionality
    }

    private void handlePlayWithFriends() {
        // TODO: Implement play with friends functionality
    }

    private void handleViewAllQuestions() {
        // TODO: Implement view all questions functionality
    }
}