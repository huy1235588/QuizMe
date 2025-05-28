package com.huy.QuizMe.ui.main.join;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

public class QuizSelectionDialog {

    public interface OnQuizSelectedListener {
        void onQuizSelected(Quiz quiz);
    }

    private final Context context;
    private final CreateRoomViewModel viewModel;
    private final LifecycleOwner lifecycleOwner;
    private final OnQuizSelectedListener listener;

    private Dialog dialog;
    private QuizAdapter quizAdapter;
    private TextInputEditText etSearchQuiz;
    private ProgressBar progressBar;
    private TextView tvNoResults;
    private MaterialButton btnSelect;
    private Quiz selectedQuiz;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final int SEARCH_DELAY = 500; // 500ms delay for search
    private static final int DEFAULT_PAGE_SIZE = 20;

    public QuizSelectionDialog(Context context, CreateRoomViewModel viewModel,
                               LifecycleOwner lifecycleOwner, OnQuizSelectedListener listener) {
        this.context = context;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.listener = listener;
        initDialog();
    }

    private void initDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quiz_selection, null);

        // Initialize views
        etSearchQuiz = dialogView.findViewById(R.id.et_search_quiz);
        progressBar = dialogView.findViewById(R.id.progress_bar_dialog);
        tvNoResults = dialogView.findViewById(R.id.tv_no_results);
        btnSelect = dialogView.findViewById(R.id.btn_select);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        RecyclerView rvQuizSelection = dialogView.findViewById(R.id.rv_quiz_selection);

        // Setup RecyclerView
        setupRecyclerView(rvQuizSelection);

        // Setup search functionality
        setupSearchFunctionality();

        // Setup buttons
        btnCancel.setOnClickListener(v -> dismiss());
        btnSelect.setOnClickListener(v -> {
            if (selectedQuiz != null && listener != null) {
                listener.onQuizSelected(selectedQuiz);
                dismiss();
            }
        });

        // Create dialog
        dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Load initial data
        loadQuizzes("");
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        quizAdapter = new QuizAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(quizAdapter);
        recyclerView.setHasFixedSize(true);

        // Set quiz selection listener
        quizAdapter.setOnQuizClickListener(quiz -> {
            selectedQuiz = quiz;
            btnSelect.setEnabled(true);
        });
    }

    private void setupSearchFunctionality() {
        etSearchQuiz.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Schedule new search
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    loadQuizzes(query);
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadQuizzes(String searchQuery) {
        // Reset selection
        selectedQuiz = null;
        btnSelect.setEnabled(false);

        // Determine search parameters
        String title = searchQuery.isEmpty() ? null : searchQuery;

        // Load quizzes with search
        viewModel.loadTrendingQuizzes(0, DEFAULT_PAGE_SIZE, null, null, title, true)
                .observe(lifecycleOwner, resource -> {
                    toggleLoading(ApiUtils.isLoading(resource));

                    if (ApiUtils.isSuccess(resource)) {
                        PagedResponse<Quiz> quizzes = resource.getData();
                        if (quizzes != null && quizzes.getContent() != null && !quizzes.getContent().isEmpty()) {
                            quizAdapter.updateItems(quizzes.getContent());
                            showNoResults(false);
                        } else {
                            quizAdapter.updateItems(new ArrayList<>());
                            showNoResults(true);
                        }
                    } else if (!ApiUtils.isLoading(resource)) {
                        quizAdapter.updateItems(new ArrayList<>());
                        showNoResults(true);
                    }
                });
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showNoResults(boolean show) {
        tvNoResults.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        // Clean up
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
}
