package com.huy.QuizMe.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.UserRepository;
import com.huy.QuizMe.utils.NetworkUtils;

import java.util.List;

/**
 * ViewModel cho dữ liệu người dùng
 */
public class UserViewModel extends AndroidViewModel {
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
        if (!checkNetworkConnection()) {
            return topUsers;
        }

        LiveData<Resource<List<User>>> source = userRepository.getTopUsers();
        topUsers.addSource(source, topUsers::setValue);
        return topUsers;
    }

    private boolean checkNetworkConnection() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            String errorMsg = "No internet connection. Please check and try again.";
            topUsers.setValue(Resource.error(errorMsg, null));

            return false;
        }

        return true;
    }
}
