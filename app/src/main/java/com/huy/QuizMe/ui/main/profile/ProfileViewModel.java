package com.huy.QuizMe.ui.main.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.UserProfile;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.UserRepository;
import com.huy.QuizMe.utils.SharedPreferencesManager;

public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MediatorLiveData<Resource<ProfileData>> profileData;
    private final SharedPreferencesManager sharedPreferencesManager;

    public ProfileViewModel() {
        userRepository = new UserRepository();
        profileData = new MediatorLiveData<>();
        profileData.setValue(Resource.loading(null));
        sharedPreferencesManager = SharedPreferencesManager.getInstance();
        
        loadProfileData();
    }

    /**
     * Tải dữ liệu profile từ nhiều nguồn và kết hợp lại
     */
    private void loadProfileData() {
        LiveData<Resource<UserProfile>> userProfileLiveData = userRepository.getCurrentUserProfile();
        
        // Lấy dữ liệu User từ SharedPreferences
        User cachedUser = sharedPreferencesManager.getUser();
        
        // Theo dõi và kết hợp dữ liệu từ các nguồn khác nhau
        profileData.addSource(userProfileLiveData, userProfileResource -> {
            if (userProfileResource != null && userProfileResource.getData() != null) {
                // Tạo đối tượng ProfileData với thông tin UserProfile và User
                ProfileData data = new ProfileData();
                data.setUserProfile(userProfileResource.getData());
                data.setUser(cachedUser); // Thêm dữ liệu User từ SharedPreferences
                profileData.setValue(Resource.success(data, userProfileResource.getMessage()));
            } else if (userProfileResource != null && userProfileResource.getStatus() == Resource.Status.ERROR) {
                ProfileData data = new ProfileData();
                data.setUser(cachedUser); // Thêm dữ liệu User từ SharedPreferences ngay cả khi có lỗi
                profileData.setValue(Resource.error(userProfileResource.getMessage(), data));
            }
        });
    }

    /**
     * Lấy dữ liệu profile đã kết hợp
     */
    public LiveData<Resource<ProfileData>> getProfileData() {
        return profileData;
    }

    /**
     * Làm mới dữ liệu profile
     */
    public void refreshProfileData() {
        profileData.setValue(Resource.loading(profileData.getValue() != null ? profileData.getValue().getData() : null));
        loadProfileData();
    }

    /**
     * Class chứa đủ thông tin để hiển thị profile
     */
    public static class ProfileData {
        private UserProfile userProfile;
        private User user;

        public UserProfile getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(UserProfile userProfile) {
            this.userProfile = userProfile;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}