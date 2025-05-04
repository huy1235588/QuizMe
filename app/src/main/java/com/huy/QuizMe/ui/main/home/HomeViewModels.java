package com.huy.QuizMe.ui.main.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.huy.QuizMe.data.model.Category;
import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.repository.CategoryRepository;
import com.huy.QuizMe.data.repository.QuizRepository;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.UserRepository;
import com.huy.QuizMe.utils.NetworkUtils;

import java.util.List;

/**
 * - CategoryViewModel: Quản lý dữ liệu liên quan đến danh mục
 * - QuizViewModel: Quản lý dữ liệu liên quan đến quiz
 * - UserViewModel: Quản lý dữ liệu liên quan đến người dùng
 */
public class HomeViewModels {
    
    private static final String NETWORK_ERROR_MESSAGE = "No internet connection. Please check and try again.";

    /**
     * Lớp trừu tượng cho ViewModel cơ sở
     * Cung cấp các phương thức chung cho các ViewModel khác
     */
    public static abstract class BaseViewModel extends AndroidViewModel {
        
        protected BaseViewModel(@NonNull Application application) {
            super(application);
        }
        
        /**
         * Kiểm tra kết nối mạng
         * @return true nếu có kết nối mạng, false nếu không
         */
        protected boolean isNetworkConnected() {
            return !NetworkUtils.isNetworkAvailable(getApplication());
        }
        
        /**
         * Tạo Resource lỗi khi không có kết nối mạng
         * @param <T> Kiểu dữ liệu của Resource
         * @return Resource chứa thông báo lỗi
         */
        protected <T> Resource<T> createNetworkError() {
            return Resource.error(NETWORK_ERROR_MESSAGE, null);
        }
    }

    /**
     * ViewModel cho dữ liệu danh mục
     */
    public static class CategoryViewModel extends BaseViewModel {
        private final CategoryRepository categoryRepository;
        private final MediatorLiveData<Resource<List<Category>>> categories = new MediatorLiveData<>();
        private final MediatorLiveData<Resource<List<Category>>> activeCategories = new MediatorLiveData<>();
        private final MediatorLiveData<Resource<Category>> category = new MediatorLiveData<>();

        public CategoryViewModel(@NonNull Application application) {
            super(application);
            categoryRepository = new CategoryRepository();
        }

        /**
         * Tải tất cả danh mục từ API
         * @return LiveData với Resource danh sách danh mục
         */
        public LiveData<Resource<List<Category>>> loadAllCategories() {
            if (isNetworkConnected()) {
                categories.setValue(createNetworkError());
                return categories;
            }

            LiveData<Resource<List<Category>>> source = categoryRepository.getAllCategories();
            categories.addSource(source, categories::setValue);
            return categories;
        }

        /**
         * Tải các danh mục đang hoạt động từ API
         * @return LiveData với Resource danh sách danh mục đang hoạt động
         */
        public LiveData<Resource<List<Category>>> loadActiveCategories() {
            if (isNetworkConnected()) {
                activeCategories.setValue(createNetworkError());
                return activeCategories;
            }

            LiveData<Resource<List<Category>>> source = categoryRepository.getActiveCategories();
            activeCategories.addSource(source, activeCategories::setValue);
            return activeCategories;
        }

        /**
         * Tải một danh mục cụ thể theo ID
         * @param categoryId ID của danh mục cần tải
         * @return LiveData với Resource của danh mục
         */
        public LiveData<Resource<Category>> loadCategoryById(int categoryId) {
            if (isNetworkConnected()) {
                category.setValue(createNetworkError());
                return category;
            }

            LiveData<Resource<Category>> source = categoryRepository.getCategoryById(categoryId);
            category.addSource(source, category::setValue);
            return category;
        }
    }

    /**
     * ViewModel cho dữ liệu quiz
     */
    public static class QuizViewModel extends BaseViewModel {
        private final QuizRepository quizRepository;
        private final MediatorLiveData<Resource<PagedResponse<Quiz>>> trendingQuizzes = new MediatorLiveData<>();
        private final MediatorLiveData<Resource<PagedResponse<Quiz>>> discoverQuizzes = new MediatorLiveData<>();
        private final MediatorLiveData<Resource<Quiz>> quiz = new MediatorLiveData<>();

        public QuizViewModel(@NonNull Application application) {
            super(application);
            quizRepository = new QuizRepository();
        }

        /**
         * Tải quiz thịnh hành với các tùy chọn lọc
         *
         * @param page       Số trang (dựa trên 0)
         * @param pageSize   Số lượng item trên mỗi trang
         * @param categoryId Lọc theo ID danh mục (tùy chọn)
         * @param difficulty Lọc theo độ khó (tùy chọn)
         * @param search     Từ khóa tìm kiếm (tùy chọn)
         * @param isPublic   Lọc theo trạng thái công khai (tùy chọn)
         * @return LiveData với Resource phản hồi phân trang của quiz thịnh hành
         */
        public LiveData<Resource<PagedResponse<Quiz>>> loadTrendingQuizzes(
                Integer page,
                Integer pageSize,
                Integer categoryId,
                String difficulty,
                String search,
                Boolean isPublic) {

            if (isNetworkConnected()) {
                trendingQuizzes.setValue(createNetworkError());
                return trendingQuizzes;
            }

            // Luôn sử dụng "popular" cho sắp xếp và tab đối với quiz thịnh hành
            LiveData<Resource<PagedResponse<Quiz>>> source = quizRepository.getPagedQuizzes(
                    page, pageSize, categoryId, difficulty, search, "popular", isPublic, "popular");
            trendingQuizzes.addSource(source, trendingQuizzes::setValue);
            return trendingQuizzes;
        }

        /**
         * Tải quiz khám phá (mới nhất) với các tùy chọn lọc
         *
         * @param page       Số trang (dựa trên 0)
         * @param pageSize   Số lượng item trên mỗi trang
         * @param categoryId Lọc theo ID danh mục (tùy chọn)
         * @param difficulty Lọc theo độ khó (tùy chọn)
         * @param search     Từ khóa tìm kiếm (tùy chọn)
         * @param isPublic   Lọc theo trạng thái công khai (tùy chọn)
         * @return LiveData với Resource phản hồi phân trang của quiz mới nhất
         */
        public LiveData<Resource<PagedResponse<Quiz>>> loadDiscoverQuizzes(
                Integer page,
                Integer pageSize,
                Integer categoryId,
                String difficulty,
                String search,
                Boolean isPublic) {

            if (isNetworkConnected()) {
                discoverQuizzes.setValue(createNetworkError());
                return discoverQuizzes;
            }

            // Luôn sử dụng "newest" cho sắp xếp và tab đối với quiz khám phá
            LiveData<Resource<PagedResponse<Quiz>>> source = quizRepository.getPagedQuizzes(
                    page, pageSize, categoryId, difficulty, search, "newest", isPublic, "newest");
            discoverQuizzes.addSource(source, discoverQuizzes::setValue);
            return discoverQuizzes;
        }

        /**
         * Tải tất cả quiz với các tùy chọn lọc
         * Chỉ sử dụng trong trường hợp cần filter đặc biệt, thông thường nên sử dụng
         * loadTrendingQuizzes hoặc loadDiscoverQuizzes
         *
         * @param page       Số trang (dựa trên 0)
         * @param pageSize   Số lượng item trên mỗi trang
         * @param categoryId Lọc theo ID danh mục (tùy chọn)
         * @param difficulty Lọc theo độ khó (tùy chọn)
         * @param search     Từ khóa tìm kiếm (tùy chọn)
         * @param sort       Tùy chọn sắp xếp (tùy chọn)
         * @param isPublic   Lọc theo trạng thái công khai (tùy chọn)
         * @param tab        Lọc theo tab (tùy chọn)
         * @return LiveData với Resource phản hồi phân trang của quiz
         */
        public LiveData<Resource<PagedResponse<Quiz>>> loadPagedQuizzes(
                Integer page,
                Integer pageSize,
                Integer categoryId,
                String difficulty,
                String search,
                String sort,
                Boolean isPublic,
                String tab) {

            // Tùy thuộc vào sort và tab, sử dụng LiveData phù hợp
            if ("popular".equals(sort) || "popular".equals(tab)) {
                return loadTrendingQuizzes(page, pageSize, categoryId, difficulty, search, isPublic);
            } else if ("newest".equals(sort) || "newest".equals(tab)) {
                return loadDiscoverQuizzes(page, pageSize, categoryId, difficulty, search, isPublic);
            }

            // Nếu là trường hợp đặc biệt, tạo LiveData mới
            MediatorLiveData<Resource<PagedResponse<Quiz>>> customQuizzes = new MediatorLiveData<>();
            
            if (isNetworkConnected()) {
                customQuizzes.setValue(createNetworkError());
                return customQuizzes;
            }

            LiveData<Resource<PagedResponse<Quiz>>> source = quizRepository.getPagedQuizzes(
                    page, pageSize, categoryId, difficulty, search, sort, isPublic, tab);
            customQuizzes.addSource(source, customQuizzes::setValue);
            return customQuizzes;
        }

        /**
         * Tải một quiz cụ thể theo ID
         *
         * @param quizId ID của quiz cần tải
         * @return LiveData với Resource của quiz
         */
        public LiveData<Resource<Quiz>> loadQuizById(int quizId) {
            if (isNetworkConnected()) {
                quiz.setValue(createNetworkError());
                return quiz;
            }

            LiveData<Resource<Quiz>> source = quizRepository.getQuizById(quizId);
            quiz.addSource(source, quiz::setValue);
            return quiz;
        }
    }

    /**
     * ViewModel cho dữ liệu người dùng
     */
    public static class UserViewModel extends BaseViewModel {
        private final UserRepository userRepository;
        private final MediatorLiveData<Resource<List<User>>> topUsers = new MediatorLiveData<>();

        public UserViewModel(@NonNull Application application) {
            super(application);
            userRepository = new UserRepository();
        }

        /**
         * Lấy danh sách người dùng có tổng số quiz được chơi nhiều nhất
         *
         * @return Danh sách người dùng
         */
        public LiveData<Resource<List<User>>> getTopUsers() {
            if (isNetworkConnected()) {
                topUsers.setValue(createNetworkError());
                return topUsers;
            }

            LiveData<Resource<List<User>>> source = userRepository.getTopUsers();
            topUsers.addSource(source, topUsers::setValue);
            return topUsers;
        }
    }
}