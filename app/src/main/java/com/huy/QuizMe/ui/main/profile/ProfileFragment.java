package com.huy.QuizMe.ui.main.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.UserProfile;
import com.huy.QuizMe.utils.FileUtils;
import com.huy.QuizMe.utils.LanguageUtils;
import com.huy.QuizMe.ui.dialog.LanguageSelectionDialog;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileFragment extends Fragment {
    LanguageUtils languageUtils;
    SharedPreferencesManager prefsManager;
    private ProfileViewModel viewModel;
    private CircleImageView imgProfile;
    private ImageView icCameraOverlay;
    private TextView tvFullName, tvUsername;
    private TextView tvTotalScore, tvQuizzesPlayed, tvQuizzesCreated;
    private TextView tvEmail, tvPhone, tvBirthday;
    private TextView tvRecentActivity;
    private Button btnLogout;
    private ProgressBar progressBar;
    private ImageView btnEditProfile;

    // Language setting views
    private LinearLayout layoutLanguageSetting;
    private TextView tvCurrentLanguage;

    // Activity result launcher for image selection
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Initialize SharedPreferencesManager
        prefsManager = SharedPreferencesManager.getInstance();
        languageUtils = new LanguageUtils(prefsManager);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            uploadAvatar(selectedImageUri);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
        observeViewModel();
    }

    private void initViews(View view) {
        // Find all views
        imgProfile = view.findViewById(R.id.img_profile);
        icCameraOverlay = view.findViewById(R.id.ic_camera_overlay);
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvUsername = view.findViewById(R.id.tv_username);

        tvTotalScore = view.findViewById(R.id.tv_total_score);
        tvQuizzesPlayed = view.findViewById(R.id.tv_quizzes_played);
        tvQuizzesCreated = view.findViewById(R.id.tv_quizzes_created);

        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvBirthday = view.findViewById(R.id.tv_birthday);

        tvRecentActivity = view.findViewById(R.id.tv_recent_activity);

        btnLogout = view.findViewById(R.id.btn_logout);
        progressBar = view.findViewById(R.id.progress_bar);

        btnEditProfile = view.findViewById(R.id.btn_edit_profile);

        // Language setting views
        layoutLanguageSetting = view.findViewById(R.id.layout_language_setting);
        tvCurrentLanguage = view.findViewById(R.id.tv_current_language);

        // Update current language display
        updateLanguageDisplay();
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> {
            // TODO: Navigate to Edit Profile screen
            Toast.makeText(getContext(), "Edit Profile (Coming Soon)", Toast.LENGTH_SHORT).show();
        });
        // Avatar click listener to change profile picture
        imgProfile.setOnClickListener(v -> openImagePicker());

        // Camera icon click listener to change profile picture
        icCameraOverlay.setOnClickListener(v -> openImagePicker());

        // Language setting click listener
        layoutLanguageSetting.setOnClickListener(v -> showLanguageSelectionDialog());

        btnLogout.setOnClickListener(v -> {
            // Show loading toast
            Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
            // Call logout from ViewModel
            viewModel.logout().observe(getViewLifecycleOwner(), resource -> {
                switch (resource.getStatus()) {
                    case LOADING:
                        // Already showing loading toast
                        break;
                    case SUCCESS:
                        // Logout successful, navigate to Auth Activity
                        navigateToAuthActivity();
                        break;
                    case ERROR:
                        // Show error message
                        Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });
    }

    private void observeViewModel() {
        viewModel.getProfileData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    hideLoading();
                    if (resource.getData() != null) {
                        updateUI(resource.getData());
                    }
                    break;
                case ERROR:
                    hideLoading();
                    Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    if (resource.getData() != null) {
                        updateUI(resource.getData());
                    }
                    break;
            }
        });
    }

    private void updateUI(ProfileViewModel.ProfileData profileData) {
        User user = profileData.getUser();
        UserProfile userProfile = profileData.getUserProfile();

        if (user != null) {
            // Set user details
            tvFullName.setText(user.getFullName() != null ? user.getFullName() : "");
            tvUsername.setText("@" + user.getUsername());
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");

            // Load profile image
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                Glide.with(this)
                        .load(user.getProfileImage())
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(imgProfile);
            }
        }

        if (userProfile != null) {
            // Set statistics
            tvTotalScore.setText(String.valueOf(userProfile.getTotalScore() != null ? userProfile.getTotalScore() : 0));
            tvQuizzesPlayed.setText(String.valueOf(userProfile.getQuizzesPlayed() != null ? userProfile.getQuizzesPlayed() : 0));
            tvQuizzesCreated.setText(String.valueOf(userProfile.getQuizzesCreated() != null ? userProfile.getQuizzesCreated() : 0));

            // Set personal info
            tvPhone.setText(userProfile.getPhoneNumber() != null ? userProfile.getPhoneNumber() : "");
            tvBirthday.setText(userProfile.getDateOfBirth() != null ? userProfile.getDateOfBirth() : "");
        }

        // Update current language display
        tvCurrentLanguage.setText(languageUtils.getCurrentLanguage());
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Chuyển đến màn hình đăng nhập sau khi đăng xuất
     */
    private void navigateToAuthActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), com.huy.QuizMe.ui.auth.AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    /**
     * Mở gallery để chọn ảnh
     */
    private void openImagePicker() {
        // Hiển thị dialog để chọn nguồn ảnh
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Chọn ảnh đại diện")
                .setMessage("Bạn muốn chọn ảnh từ đâu?")
                .setPositiveButton("Thư viện", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Upload avatar mới
     *
     * @param imageUri URI của ảnh được chọn
     */
    private void uploadAvatar(Uri imageUri) {
        try {
            // Hiển thị ảnh preview ngay lập tức
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(imgProfile);

            // Convert URI to File
            File imageFile = FileUtils.getFileFromUri(getContext(), imageUri);
            if (imageFile == null) {
                Toast.makeText(getContext(), "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo MultipartBody.Part cho ảnh
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("avatarFile", imageFile.getName(), requestBody);

            // Hiển thị loading và thông báo
            showLoading();
            Toast.makeText(getContext(), "Đang upload ảnh...", Toast.LENGTH_SHORT).show();

            // Gọi ViewModel để upload ảnh
            viewModel.uploadAvatar(imagePart).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.getStatus()) {
                    case LOADING:
                        // Already showing loading
                        break;
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(getContext(), "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
                        // Refresh profile data để cập nhật UI
                        viewModel.refreshProfileData();
                        break;
                    case ERROR:
                        hideLoading();
                        Toast.makeText(getContext(), "Lỗi upload ảnh: " + resource.getMessage(), Toast.LENGTH_SHORT).show();
                        // Refresh profile data để cập nhật UI
                        viewModel.refreshProfileData();
                        break;
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show language selection dialog
     */
    private void showLanguageSelectionDialog() {
        LanguageSelectionDialog dialog = new LanguageSelectionDialog(getContext(),
                (languageCode) -> {
                    // Apply the new language
                    languageUtils.saveLanguage(languageCode);
                    languageUtils.applyLanguage(getContext(), languageCode);

                    // Update the display
                    updateLanguageDisplay();

                    // Show confirmation message
                    Toast.makeText(getContext(), getString(R.string.language_changed), Toast.LENGTH_LONG).show();

                    // Restart activity to apply language changes completely
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                });
        dialog.show();
    }

    /**
     * Update language display based on current setting
     */
    private void updateLanguageDisplay() {
        if (tvCurrentLanguage != null) {
            String currentLanguage = languageUtils.getCurrentLanguage();
            switch (currentLanguage) {
                case "vi":
                    tvCurrentLanguage.setText(getString(R.string.vietnamese));
                    break;
                case "en":
                default:
                    tvCurrentLanguage.setText(getString(R.string.english));
                    break;
            }
        }
    }
}