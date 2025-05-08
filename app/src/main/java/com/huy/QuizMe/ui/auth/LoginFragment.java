package com.huy.QuizMe.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.huy.QuizMe.MainActivity;
import com.huy.QuizMe.databinding.FragmentLoginBinding;
import com.huy.QuizMe.data.repository.Resource;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Kiểm tra nếu đã đăng nhập thì chuyển đến MainActivity
        if (authViewModel.isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }
        
        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        
        binding.tvRegister.setOnClickListener(v -> {
            // Chuyển đến fragment đăng ký
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).showFragmentWithBackStack(new RegisterFragment());
            }
        });
        
        binding.tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        authViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                switch (result.getStatus()) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        navigateToMainActivity();
                        break;
                    case ERROR:
                        showLoading(false);
                        showLoginError(result.getMessage());
                        break;
                }
            }
        });
    }

    private void attemptLogin() {
        // Đặt lại lỗi
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        String userOrEmail = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Kiểm tra mật khẩu
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Vui lòng nhập mật khẩu");
            focusView = binding.tilPassword;
            cancel = true;
        }

        // Kiểm tra user hoặc email
        if (TextUtils.isEmpty(userOrEmail)) {
            binding.tilEmail.setError("Vui lòng nhập tên người dùng hoặc email");
            focusView = binding.tilEmail;
            cancel = true;
        }

        if (cancel) {
            // Có lỗi, tập trung vào trường đầu tiên có lỗi
            focusView.requestFocus();
        } else {
            // Thực hiện đăng nhập
            authViewModel.login(userOrEmail, password);
        }
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
    }

    private void showLoginError(String errorMsg) {
        Toast.makeText(requireContext(), errorMsg != null ? errorMsg : "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}