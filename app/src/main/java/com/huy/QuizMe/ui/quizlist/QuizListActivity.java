package com.huy.QuizMe.ui.quizlist;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.repository.QuizRepository;

import java.util.ArrayList;

public class QuizListActivity extends AppCompatActivity {
    private RecyclerView rvQuizzes;
    private TextView tvTitle;
    private ImageView ivBack;
    private ImageView ivSearch;

    private QuizListAdapter quizListAdapter;
    private QuizRepository quizRepository;
    private String quizType;
    private int currentPage = 1;
    private final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean lastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        // Get the quiz type from intent
        quizType = getIntent().getStringExtra("TYPE");
        String title = getIntent().getStringExtra("TITLE");

        // Initialize repository
        quizRepository = new QuizRepository();

        // Initialize views
        initViews();

        // Set title
        tvTitle.setText(title);

        // Setup recyclerView
        setupRecyclerView();

        // Set click listeners
        setupClickListeners();

        // Load quizzes
        loadQuizzes();
    }

    private void initViews() {
        rvQuizzes = findViewById(R.id.rv_quizzes);
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        ivSearch = findViewById(R.id.iv_search);
    }

    private void setupRecyclerView() {
        quizListAdapter = new QuizListAdapter(this, new ArrayList<>());
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvQuizzes.setLayoutManager(layoutManager);
        rvQuizzes.setAdapter(quizListAdapter);

        // Setup pagination
        rvQuizzes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { // Scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !lastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= PAGE_SIZE) {
                            // Load more
                            currentPage++;
                            loadQuizzes();
                        }
                    }
                }
            }
        });

        // Set click listener for quiz items
        quizListAdapter.setOnQuizClickListener(quiz -> {
            // Handle quiz click
            Toast.makeText(QuizListActivity.this, "Quiz: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        ivSearch.setOnClickListener(v -> {
            // Handle search
            Toast.makeText(QuizListActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadQuizzes() {
        isLoading = true;

        if ("discover".equals(quizType)) {
            loadDiscoverQuizzes();
        } else if ("trending".equals(quizType)) {
            loadTrendingQuizzes();
        }
    }

    private void loadDiscoverQuizzes() {
        // Tải quiz khám phá với sắp xếp theo mới nhất

    }

    private void loadTrendingQuizzes() {

    }
}