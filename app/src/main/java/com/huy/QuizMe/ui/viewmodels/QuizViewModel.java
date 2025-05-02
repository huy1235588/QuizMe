package com.huy.QuizMe.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.repository.QuizRepository;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.utils.NetworkUtils;

/**
 * ViewModel cho dữ liệu quiz
 */
public class QuizViewModel extends AndroidViewModel {
    private final QuizRepository quizRepository;
    private final MediatorLiveData<Resource<PagedResponse<Quiz>>> quizzes = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<Quiz>> quiz = new MediatorLiveData<>();

    public QuizViewModel(@NonNull Application application) {
        super(application);
        quizRepository = new QuizRepository();
    }

    /**
     * Tải tất cả quiz với các tùy chọn lọc
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
    public LiveData<Resource<PagedResponse<Quiz>>> loadQuizzes(
            Integer page,
            Integer pageSize,
            Integer categoryId,
            String difficulty,
            String search,
            String sort,
            Boolean isPublic,
            String tab) {

        if (!checkNetworkConnection()) {
            return quizzes;
        }

        LiveData<Resource<PagedResponse<Quiz>>> source = quizRepository.getAllQuizzes(
                page, pageSize, categoryId, difficulty, search, sort, isPublic, tab);
        quizzes.addSource(source, quizzes::setValue);
        return quizzes;
    }

    /**
     * Tải quiz phân trang
     *
     * @param page     Số trang (dựa trên 0)
     * @param pageSize Số lượng item trên mỗi trang
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
            String tab
    ) {

        if (!checkNetworkConnection()) {
            return quizzes;
        }

        LiveData<Resource<PagedResponse<Quiz>>> source = quizRepository.getPagedQuizzes(
                page,
                pageSize,
                categoryId,
                difficulty,
                search,
                sort,
                isPublic,
                tab
        );
        quizzes.addSource(source, quizzes::setValue);
        return quizzes;
    }

    /**
     * Tải một quiz cụ thể theo ID
     *
     * @param quizId ID của quiz cần tải
     * @return LiveData với Resource của quiz
     */
    public LiveData<Resource<Quiz>> loadQuizById(int quizId) {
        if (!checkNetworkConnection()) {
            return quiz;
        }

        LiveData<Resource<Quiz>> source = quizRepository.getQuizById(quizId);
        quiz.addSource(source, quiz::setValue);
        return quiz;
    }

    /**
     * Kiểm tra kết nối mạng và đặt trạng thái lỗi nếu không có kết nối
     *
     * @return true nếu có kết nối mạng, false nếu không
     */
    private boolean checkNetworkConnection() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            String errorMsg = "No internet connection. Please check and try again.";
            quizzes.setValue(Resource.error(errorMsg, null));
            quiz.setValue(Resource.error(errorMsg, null));
            return false;
        }
        return true;
    }
}