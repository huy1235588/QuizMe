package com.huy.QuizMe.ui.home;

import android.content.Intent;
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
import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.ui.quiz.QuizDetailActivity;
import com.huy.QuizMe.ui.quizlist.QuizListActivity;
import com.huy.QuizMe.utils.ApiUtils;

/**
 * HomeFragment - Fragment hiển thị màn hình trang chủ của ứng dụng
 * Hiển thị các danh mục và quiz nổi bật cho người dùng
 */
public class HomeFragment extends Fragment {

    // ViewModel để lấy dữ liệu
    private HomeViewModels.CategoryViewModel categoryViewModel;
    private HomeViewModels.QuizViewModel quizViewModel;
    private HomeViewModels.UserViewModel userViewModel;

    // UI components
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvDiscoverCategories;
    private RecyclerView rvCategory;
    private RecyclerView rvTrendingQuiz;
    private RecyclerView rvAuthors;

    // Adapters
    private HomeAdapters.QuizAdapter discoverAdapter;
    private HomeAdapters.CategoryAdapter categoryAdapter;
    private HomeAdapters.QuizAdapter trendingQuizAdapter;
    private HomeAdapters.AuthorAdapter authorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo ViewModel
        categoryViewModel = new ViewModelProvider(this).get(HomeViewModels.CategoryViewModel.class);
        quizViewModel = new ViewModelProvider(this).get(HomeViewModels.QuizViewModel.class);
        userViewModel = new ViewModelProvider(this).get(HomeViewModels.UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo SwipeRefreshLayout để làm mới dữ liệu
        setupSwipeRefresh(view);

        // Khởi tạo các sections
        setupDiscoverSection(view);
        setupCategorySection(view);
        setupTrendingQuizSection(view);
        setupAuthorsSection(view);

        // Tải dữ liệu từ API
        loadData();

        return view;
    }

    /**
     * Thiết lập SwipeRefreshLayout để người dùng có thể kéo xuống làm mới dữ liệu
     */
    private void setupSwipeRefresh(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.purple);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    /**
     * Thiết lập phần hiển thị danh mục khám phá
     */
    private void setupDiscoverSection(View view) {
        // Khởi tạo RecyclerView cho danh mục khám phá
        rvDiscoverCategories = view.findViewById(R.id.rv_discover_categories);
        rvDiscoverCategories.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo adapter và thiết lập xử lý sự kiện click
        discoverAdapter = new HomeAdapters.QuizAdapter(getContext());
        discoverAdapter.setOnQuizClickListener(quiz -> {
            // Xử lý khi người dùng chọn một quiz trong phần discover
            Intent intent = new Intent(getActivity(), QuizDetailActivity.class);
            intent.putExtra("EXTRA_QUIZ_ID", quiz.getId());
            startActivity(intent);
        });

        // Gán adapter cho RecyclerView
        rvDiscoverCategories.setAdapter(discoverAdapter);

        // Thiết lập nút "Xem tất cả"
        view.findViewById(R.id.tv_discover_view_all).setOnClickListener(v -> {
            // Chuyển đến màn hình hiển thị tất cả quiz
            Intent intent = new Intent(getActivity(), QuizListActivity.class);
            intent.putExtra("TITLE", "Popular Quizzes");
            intent.putExtra("TYPE", "discover");
            startActivity(intent);
        });
    }

    /**
     * Thiết lập phần hiển thị danh mục phổ biến
     */
    private void setupCategorySection(View view) {
        // Khởi tạo RecyclerView cho danh mục phổ biến
        rvCategory = view.findViewById(R.id.rv_category);
        rvCategory.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo adapter và thiết lập xử lý sự kiện click
        categoryAdapter = new HomeAdapters.CategoryAdapter(getContext());
        categoryAdapter.setOnCategoryClickListener(category -> {
            // Xử lý khi người dùng chọn một danh mục
            Toast.makeText(getContext(), "Đã chọn danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
        });

        // Gán adapter cho RecyclerView
        rvCategory.setAdapter(categoryAdapter);

        // Thiết lập nút "Xem tất cả"
        view.findViewById(R.id.tv_category_view_all).setOnClickListener(v -> {
            // Chuyển đến màn hình hiển thị tất cả danh mục
            Toast.makeText(getContext(), "Xem tất cả danh mục", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Thiết lập phần hiển thị quiz đang thịnh hành
     */
    private void setupTrendingQuizSection(View view) {
        // Khởi tạo RecyclerView cho quiz thịnh hành
        rvTrendingQuiz = view.findViewById(R.id.rv_trending_quiz);
        rvTrendingQuiz.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo adapter và thiết lập xử lý sự kiện click
        trendingQuizAdapter = new HomeAdapters.QuizAdapter(getContext());
        trendingQuizAdapter.setOnQuizClickListener(quiz -> {
            // Xử lý khi người dùng chọn một quiz - chuyển đến trang chi tiết
            Intent intent = new Intent(getActivity(), QuizDetailActivity.class);
            intent.putExtra("EXTRA_QUIZ_ID", quiz.getId());
            startActivity(intent);
        });

        // Gán adapter cho RecyclerView
        rvTrendingQuiz.setAdapter(trendingQuizAdapter);

        // Thiết lập nút "Xem tất cả"
        view.findViewById(R.id.tv_trending_view_all).setOnClickListener(v -> {
            // Chuyển đến
            Intent intent = new Intent(getActivity(), QuizListActivity.class);
            intent.putExtra("TITLE", "Trending Quizzes");
            intent.putExtra("TYPE", "trending");
            startActivity(intent);
        });
    }

    /**
     * Thiết lập phần hiển thị tác giả hàng đầu
     */
    private void setupAuthorsSection(View view) {
        // Khởi tạo RecyclerView cho tác giả
        rvAuthors = view.findViewById(R.id.rv_authors);
        rvAuthors.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tạo adapter và thiết lập xử lý sự kiện click
        authorAdapter = new HomeAdapters.AuthorAdapter(getContext());
        authorAdapter.setOnAuthorClickListener(author -> {
            // Xử lý khi người dùng chọn một tác giả
            Toast.makeText(getContext(), "Đã chọn tác giả: " + author.getUsername(), Toast.LENGTH_SHORT).show();
        });

        // Gán adapter cho RecyclerView
        rvAuthors.setAdapter(authorAdapter);

        // Thiết lập nút "Xem tất cả"
        view.findViewById(R.id.tv_authors_view_all).setOnClickListener(v -> {
            // Chuyển đến màn hình hiển thị tất cả tác giả
            Toast.makeText(getContext(), "Xem tất cả tác giả", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Tải dữ liệu cho tất cả các sections
     */
    private void loadData() {
        loadCategories();
        loadTrendingQuizzes();
        loadDiscoverQuizzes();
        loadTopAuthors();
    }

    /**
     * Tải dữ liệu các danh mục từ API
     */
    private void loadCategories() {
        // Tải các danh mục đang hoạt động
        categoryViewModel.loadActiveCategories().observe(getViewLifecycleOwner(), resource -> {
            if (ApiUtils.isLoading(resource)) {
                // Hiển thị trạng thái đang tải
                swipeRefreshLayout.setRefreshing(true);
            } else if (ApiUtils.isSuccess(resource)) {
                // Cập nhật giao diện với danh sách danh mục
                swipeRefreshLayout.setRefreshing(false);

                // Cập nhật dữ liệu cho RecyclerView danh mục
                categoryAdapter.updateItems(resource.getData());
            } else {
                // Hiển thị thông báo lỗi
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Tải dữ liệu quiz đang thịnh hành từ API
     */
    private void loadTrendingQuizzes() {
        // Tải quiz thịnh hành với sắp xếp theo độ phổ biến
        quizViewModel.loadTrendingQuizzes(0, 10, null, null, null, true)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (ApiUtils.isLoading(resource)) {
                        // Hiển thị trạng thái đang tải
                        swipeRefreshLayout.setRefreshing(true);
                    } else if (ApiUtils.isSuccess(resource)) {
                        // Cập nhật giao diện với danh sách quiz
                        swipeRefreshLayout.setRefreshing(false);
                        PagedResponse<Quiz> pagedResponse = resource.getData();
                        if (pagedResponse != null && pagedResponse.getContent() != null) {
                            trendingQuizAdapter.updateItems(pagedResponse.getContent());
                        }
                    } else {
                        // Hiển thị thông báo lỗi
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Tải dữ liệu quiz discover từ API
     */
    private void loadDiscoverQuizzes() {
        // Tải quiz khám phá với sắp xếp theo mới nhất
        quizViewModel.loadDiscoverQuizzes(0, 10, null, null, null, true)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (ApiUtils.isLoading(resource)) {
                        // Hiển thị trạng thái đang tải
                        swipeRefreshLayout.setRefreshing(true);
                    } else if (ApiUtils.isSuccess(resource)) {
                        // Cập nhật giao diện với danh sách quiz
                        swipeRefreshLayout.setRefreshing(false);
                        PagedResponse<Quiz> pagedResponse = resource.getData();
                        if (pagedResponse != null && pagedResponse.getContent() != null) {
                            discoverAdapter.updateItems(pagedResponse.getContent());
                        }
                    } else {
                        // Hiển thị thông báo lỗi
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Tải dữ liệu về top tác giả từ API
     */
    private void loadTopAuthors() {
        // Tải danh sách top tác giả
        userViewModel.getTopUsers().observe(getViewLifecycleOwner(), resource -> {
            if (ApiUtils.isLoading(resource)) {
                // Hiển thị trạng thái đang tải
                swipeRefreshLayout.setRefreshing(true);
            } else if (ApiUtils.isSuccess(resource)) {
                // Cập nhật giao diện với danh sách tác giả
                swipeRefreshLayout.setRefreshing(false);
                if (resource.getData() != null) {
                    authorAdapter.updateItems(resource.getData());
                }
            } else {
                // Hiển thị thông báo lỗi
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}