package com.huy.QuizMe.ui.main.join;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.PagedResponse;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.data.model.request.RoomRequest;
import com.huy.QuizMe.data.repository.QuizRepository;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.RoomRepository;
import com.huy.QuizMe.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateRoomViewModel extends ViewModel {

    // Quiz đã chọn để sử dụng cho phòng
    private Quiz selectedQuiz;
    private RoomRepository roomRepository;
    private final QuizRepository quizRepository;
    private final MediatorLiveData<Resource<PagedResponse<Quiz>>> trendingQuizzes = new MediatorLiveData<>();

    public CreateRoomViewModel() {
        roomRepository = new RoomRepository();
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

        // Luôn sử dụng "popular" cho sắp xếp và tab đối với quiz thịnh hành
        LiveData<Resource<PagedResponse<Quiz>>> source = quizRepository.getPagedQuizzes(
                page, pageSize, categoryId, difficulty, search, "popular", isPublic, "popular");
        trendingQuizzes.addSource(source, trendingQuizzes::setValue);
        return trendingQuizzes;
    }

    /**
     * Tạo một phòng mới với thông tin đã cung cấp
     *
     * @param roomRequest Thông tin phòng
     * @return LiveData với Resource phản hồi của phòng đã tạo
     */
    public LiveData<Resource<Room>> createRoom(RoomRequest roomRequest) {
        LiveData<Resource<Room>> result;

        result = roomRepository.createRoom(roomRequest);

        return result;
    }

    /**
     * Lấy quiz đã được chọn hiện tại
     *
     * @return Quiz đã chọn
     */
    public Quiz getSelectedQuiz() {
        return selectedQuiz;
    }

    /**
     * Đặt quiz đã chọn
     *
     * @param quiz Quiz sẽ được sử dụng cho phòng
     */
    public void setSelectedQuiz(Quiz quiz) {
        this.selectedQuiz = quiz;
    }
}