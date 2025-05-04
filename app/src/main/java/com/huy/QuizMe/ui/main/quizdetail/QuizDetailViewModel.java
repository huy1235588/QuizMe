package com.huy.QuizMe.ui.main.quizdetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.repository.QuizRepository;
import com.huy.QuizMe.data.repository.Resource;

public class QuizDetailViewModel extends ViewModel {
    private final QuizRepository quizRepository;
    private final MutableLiveData<Resource<Quiz>> quizDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public QuizDetailViewModel() {
        this.quizRepository = new QuizRepository();
    }

    public void loadQuizDetail(int quizId) {
        isLoading.setValue(true);

        // Khởi tạo loading state
        quizDetail.setValue(Resource.loading(null));

        // Gọi repository để lấy thông tin quiz
        quizRepository.getQuizById(quizId).observeForever(resource -> {
            quizDetail.setValue(resource);
            isLoading.setValue(false);

            if (resource.getStatus() == Resource.Status.ERROR) {
                errorMessage.setValue(resource.getMessage());
            }
        });

    }

    public LiveData<Resource<Quiz>> getQuizDetail() {
        return quizDetail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}