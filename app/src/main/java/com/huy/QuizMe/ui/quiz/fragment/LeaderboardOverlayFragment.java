package com.huy.QuizMe.ui.quiz.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.game.LeaderboardDTO;
import com.huy.QuizMe.ui.quiz.adapter.LeaderboardAdapter;
import com.huy.QuizMe.utils.SharedPreferencesManager;

/**
 * Fragment overlay hiển thị bảng xếp hạng theo thời gian thực trong trò chơi quiz
 */
public class LeaderboardOverlayFragment extends Fragment {

    private RecyclerView rvLeaderboard;
    private ProgressBar pbLoading;
    private TextView tvEmpty;
    private ImageButton btnClose;

    private LeaderboardAdapter adapter;
    private OnLeaderboardCloseListener closeListener;

    public interface OnLeaderboardCloseListener {
        void onLeaderboardClose();
    }

    public static LeaderboardOverlayFragment newInstance() {
        return new LeaderboardOverlayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard_overlay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();

        showLoading(true);
    }

    private void initViews(View view) {
        rvLeaderboard = view.findViewById(R.id.rv_leaderboard);
        pbLoading = view.findViewById(R.id.pb_leaderboard_loading);
        tvEmpty = view.findViewById(R.id.tv_leaderboard_empty);
        btnClose = view.findViewById(R.id.btn_close_leaderboard);
    }

    private void setupRecyclerView() {
        // Lấy currentUserId với xử lý an toàn
        Long currentUserId = null;
        try {
            if (SharedPreferencesManager.getInstance().getUser() != null) {
                currentUserId = SharedPreferencesManager.getInstance().getUser().getId();
            }
        } catch (Exception e) {
            // Log lỗi nếu cần
        }

        adapter = new LeaderboardAdapter(getContext(), currentUserId);

        rvLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLeaderboard.setAdapter(adapter);
        rvLeaderboard.setHasFixedSize(true);
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> {
            if (closeListener != null) {
                closeListener.onLeaderboardClose();
            }
        });
    }

    /**
     * Cập nhật dữ liệu bảng xếp hạng
     */
    public void updateLeaderboard(LeaderboardDTO leaderboard) {
        showLoading(false);

        if (leaderboard != null && leaderboard.getRankings() != null && !leaderboard.getRankings().isEmpty()) {
            adapter.updateLeaderboardWithAnimation(leaderboard.getRankings());
            showEmpty(false);
        } else {
            showEmpty(true);
        }
    }

    /**
     * Hiển thị trạng thái đang tải
     */
    public void showLoading(boolean show) {
        if (pbLoading != null) {
            pbLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvLeaderboard != null) {
            rvLeaderboard.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Hiển thị trạng thái trống
     */
    public void showEmpty(boolean show) {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvLeaderboard != null) {
            rvLeaderboard.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Hiển thị trạng thái lỗi khi không thể tải dữ liệu
     */
    public void showError(String errorMessage) {
        showLoading(false);
        showEmpty(true);

        if (tvEmpty != null) {
            tvEmpty.setText(errorMessage != null ? errorMessage : "Không thể tải bảng xếp hạng");
        }
    }

    /**
     * Làm mới dữ liệu leaderboard
     */
    public void refreshLeaderboard() {
        showLoading(true);
        showEmpty(false);
        // Trigger refresh từ activity/viewmodel
    }

    /**
     * Kiểm tra xem có dữ liệu hay không
     */
    public boolean hasData() {
        return adapter != null && adapter.getItemCount() > 0;
    }

    /**
     * Lấy số lượng người chơi hiện tại
     */
    public int getPlayerCount() {
        return adapter != null ? adapter.getItemCount() : 0;
    }

    /**
     * Thiết lập listener đóng
     */
    public void setOnLeaderboardCloseListener(OnLeaderboardCloseListener listener) {
        this.closeListener = listener;
    }
}
