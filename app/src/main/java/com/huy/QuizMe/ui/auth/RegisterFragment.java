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
import com.huy.QuizMe.data.repository.Resource;
import com.huy.QuizMe.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        
        binding.tvLogin.setOnClickListener(v -> {
            // Trở lại màn hình đăng nhập
            if (getActivity() instanceof AuthActivity) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void observeViewModel() {
        authViewModel.getRegisterResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                switch (result.getStatus()) {
                    case LOADING:
                        showLoading(true);
                        break;
                    case SUCCESS:
                        showLoading(false);
                        Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                        break;
                    case ERROR:
                        showLoading(false);
                        showRegisterError(result.getMessage());
                        break;
                }
            }
        });
    }

    private void attemptRegister() {
        // Đặt lại lỗi
        binding.tilName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilPasswordConfirm.setError(null);
        binding.tilUsername.setError(null);

        // Lấy giá trị đầu vào
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        String passwordConfirm = binding.etPasswordConfirm.getText().toString();
        String username = binding.etUsername.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Kiểm tra xác nhận mật khẩu
        if (TextUtils.isEmpty(passwordConfirm)) {
            binding.tilPasswordConfirm.setError("Please confirm your password");
            focusView = binding.tilPasswordConfirm;
            cancel = true;
        } else if (!password.equals(passwordConfirm)) {
            binding.tilPasswordConfirm.setError("Password confirmation does not match");
            focusView = binding.tilPasswordConfirm;
            cancel = true;
        }

        // Kiểm tra mật khẩu
        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("Please enter a password");
            focusView = binding.tilPassword;
            cancel = true;
        } else if (password.length() < 2) {
            binding.tilPassword.setError("Password must be at least 2 characters");
            focusView = binding.tilPassword;
            cancel = true;
        }

        // Kiểm tra email
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError("Please enter your email");
            focusView = binding.tilEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            binding.tilEmail.setError("Invalid email address");
            focusView = binding.tilEmail;
            cancel = true;
        }

        // Kiểm tra tên
        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError("Please enter your name");
            focusView = binding.tilName;
            cancel = true;
        }

        // Kiểm tra username
        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError("Please enter your username");
            focusView = binding.tilUsername;
            cancel = true;
        } else if (username.length() < 2) {
            binding.tilUsername.setError("Username must be at least 2 characters");
            focusView = binding.tilUsername;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            binding.tilUsername.setError("Username can only contain letters, numbers and underscores");
            focusView = binding.tilUsername;
            cancel = true;
        }

        if (cancel) {
            // Có lỗi, tập trung vào trường đầu tiên có lỗi
            focusView.requestFocus();
        } else {
            // Thực hiện đăng ký
            authViewModel.register(username, email, password, passwordConfirm, name);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUsernameValid(String username) {
        return username.matches("[a-zA-Z0-9_]+");
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!isLoading);
        binding.etName.setEnabled(!isLoading);
        binding.etEmail.setEnabled(!isLoading);
        binding.etPassword.setEnabled(!isLoading);
        binding.etPasswordConfirm.setEnabled(!isLoading);
        binding.etUsername.setEnabled(!isLoading);
    }

    private void showRegisterError(String errorMsg) {
        Toast.makeText(requireContext(), errorMsg != null ? errorMsg : "Registration failed", Toast.LENGTH_LONG).show();
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