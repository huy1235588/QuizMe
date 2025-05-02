package com.huy.QuizMe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Category;
import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.ui.adapters.CategoryAdapter;
import com.huy.QuizMe.ui.adapters.QuizAdapter;
import com.huy.QuizMe.ui.viewmodels.CategoryViewModel;
import com.huy.QuizMe.ui.viewmodels.QuizViewModel;
import com.huy.QuizMe.utils.ApiUtils;

public class HomeFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    private QuizViewModel quizViewModel;
    
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvDiscoverCategories;
    private RecyclerView rvCollections;
    private RecyclerView rvTrendingQuiz;
    
    private CategoryAdapter discoverAdapter;
    private CategoryAdapter collectionsAdapter;
    private QuizAdapter trendingQuizAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.purple);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        
        // Initialize RecyclerViews and adapters
//        setupDiscoverSection(view);
        setupCollectionsSection(view);
//        setupTrendingQuizSection(view);
        
        // Load data from API
        loadData();
        
        return view;
    }
    
    private void setupDiscoverSection(View view) {
        rvDiscoverCategories = view.findViewById(R.id.rv_discover_categories);
        rvDiscoverCategories.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        
        discoverAdapter = new CategoryAdapter(getContext());
        discoverAdapter.setOnCategoryClickListener(category -> {
            // Handle category click - navigate to category detail
            Toast.makeText(getContext(), "Selected category: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        
        rvDiscoverCategories.setAdapter(discoverAdapter);
        
        // Set up view all button
        view.findViewById(R.id.tv_discover_view_all).setOnClickListener(v -> {
            // Navigate to all categories screen
            Toast.makeText(getContext(), "View all categories", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupCollectionsSection(View view) {
        rvCollections = view.findViewById(R.id.rv_collections);
        rvCollections.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        
        collectionsAdapter = new CategoryAdapter(getContext());
        collectionsAdapter.setOnCategoryClickListener(category -> {
            // Handle collection click
            Toast.makeText(getContext(), "Selected collection: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        
        rvCollections.setAdapter(collectionsAdapter);
        
        // Set up view all button
        view.findViewById(R.id.tv_collections_view_all).setOnClickListener(v -> {
            // Navigate to all collections screen
            Toast.makeText(getContext(), "View all collections", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupTrendingQuizSection(View view) {
        rvTrendingQuiz = view.findViewById(R.id.rv_trending_quiz);
        rvTrendingQuiz.setLayoutManager(new LinearLayoutManager(getContext()));
        
        trendingQuizAdapter = new QuizAdapter(getContext());
        trendingQuizAdapter.setOnQuizClickListener(quiz -> {
            // Handle quiz click - navigate to quiz detail
            Toast.makeText(getContext(), "Selected quiz: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
        });
        
        rvTrendingQuiz.setAdapter(trendingQuizAdapter);
        
        // Set up view all button
        view.findViewById(R.id.tv_trending_view_all).setOnClickListener(v -> {
            // Navigate to all quizzes screen
            Toast.makeText(getContext(), "View all quizzes", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void loadData() {
        loadCategories();
//        loadTrendingQuizzes();
    }
    
    private void loadCategories() {
        // Load active categories for Discover section
        categoryViewModel.loadActiveCategories().observe(getViewLifecycleOwner(), resource -> {
            if (ApiUtils.isLoading(resource)) {
                // Show loading indicator if needed
                swipeRefreshLayout.setRefreshing(true);
            } else if (ApiUtils.isSuccess(resource)) {
                // Update UI with categories
                swipeRefreshLayout.setRefreshing(false);
//                discoverAdapter.updateCategories(resource.getData());
                // For demo purposes, we'll use the same categories for Collections
                collectionsAdapter.updateCategories(resource.getData());
            } else {
                // Show error message
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadTrendingQuizzes() {
        // Load trending quizzes with sorting by popularity
        quizViewModel.loadQuizzes(0, 10, null, null, null, "popular", true, null)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (ApiUtils.isLoading(resource)) {
                        // Show loading indicator if needed
                        swipeRefreshLayout.setRefreshing(true);
                    } else if (ApiUtils.isSuccess(resource)) {
                        // Update UI with quizzes
                        swipeRefreshLayout.setRefreshing(false);
                        PagedResponse<Quiz> pagedResponse = resource.getData();
                        if (pagedResponse != null && pagedResponse.getContent() != null) {
                            trendingQuizAdapter.updateQuizzes(pagedResponse.getContent());
                        }
                    } else {
                        // Show error message
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}