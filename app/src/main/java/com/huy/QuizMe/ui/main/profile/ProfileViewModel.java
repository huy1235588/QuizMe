package com.huy.QuizMe.ui.main.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.UserProfile;
import com.huy.QuizMe.data.repository.AuthRepository;
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.data.repository.UserRepository;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final MediatorLiveData<Resource<ProfileData>> profileData;

    private final SharedPreferencesManager sharedPreferencesManager;

    public ProfileViewModel() {
        userRepository = new UserRepository();
        authRepository = new AuthRepository();
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

        // Lấy dữ liệu User từ repository
        LiveData<Resource<User>> userLiveData = userRepository.getUserById(cachedUser.getId());

        // Theo dõi và kết hợp dữ liệu từ các nguồn khác nhau
        // Theo dõi và kết hợp dữ liệu từ các nguồn khác nhau
        profileData.addSource(userProfileLiveData, userProfileResource -> {
            if (userProfileResource != null && userProfileResource.getData() != null) {
                profileData.addSource(userLiveData, userResource -> {
                    // Kiểm tra trạng thái của UserProfile
                    if (userResource != null && userResource.getStatus() == Resource.Status.SUCCESS) {
                        User user = userResource.getData();

                        // Cập nhật User vào SharedPreferences
                        ProfileData data = new ProfileData();
                        data.setUserProfile(userProfileResource.getData());
                        data.setUser(user);
                        profileData.setValue(Resource.success(data, userProfileResource.getMessage()));
                    } else if (userResource != null && userResource.getStatus() == Resource.Status.ERROR) {
                        ProfileData data = new ProfileData();
                        data.setUser(null); // Fix: Explicitly set user to null in case of error
                        profileData.setValue(Resource.error(userResource.getMessage(), data));
                    }
                });
            } else if (userProfileResource != null && userProfileResource.getStatus() == Resource.Status.ERROR) {
                ProfileData data = new ProfileData();
                data.setUser(null); // Fix: Explicitly set user to null in case of error
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
     * Đăng xuất khỏi tài khoản hiện tại
     *
     * @return LiveData<Resource < Void>> kết quả đăng xuất
     */
    public LiveData<Resource<Void>> logout() {
        return authRepository.logout();
    }

    /**
     * Upload avatar cho người dùng hiện tại
     *
     * @param avatarFile File ảnh avatar để upload
     * @return LiveData<Resource < UserProfile>> kết quả upload
     */
    public LiveData<Resource<UserProfile>> uploadAvatar(MultipartBody.Part avatarFile) {
        return userRepository.uploadAvatar(avatarFile);
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