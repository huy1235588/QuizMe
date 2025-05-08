package com.huy.QuizMe.ui.main.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.data.model.UserProfile;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private CircleImageView imgProfile;
    private TextView tvFullName, tvUsername;
    private TextView tvTotalScore, tvQuizzesPlayed, tvQuizzesCreated;
    private TextView tvEmail, tvPhone, tvBirthday;
    private TextView tvRecentActivity;
    private Button btnLogout;
    private ProgressBar progressBar;
    private ImageView btnBack, btnEditProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
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
        
        btnBack = view.findViewById(R.id.btn_back);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        btnEditProfile.setOnClickListener(v -> {
            // TODO: Navigate to Edit Profile screen
            Toast.makeText(getContext(), "Edit Profile (Coming Soon)", Toast.LENGTH_SHORT).show();
        });
        
        btnLogout.setOnClickListener(v -> {
            // TODO: Implement logout functionality
            Toast.makeText(getContext(), "Logging out...", Toast.LENGTH_SHORT).show();
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
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
}