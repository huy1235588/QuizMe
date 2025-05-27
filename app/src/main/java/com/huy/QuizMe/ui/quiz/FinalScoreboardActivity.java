package com.huy.QuizMe.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.MainActivity;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.game.LeaderboardDTO;
import com.huy.QuizMe.data.model.game.LeaderboardDTO.PlayerRankingDTO;
import com.huy.QuizMe.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FinalScoreboardActivity extends AppCompatActivity {

    // Các thành phần UI cho 3 người chơi hàng đầu
    private CircleImageView ivFirstAvatar, ivSecondAvatar, ivThirdAvatar;
    private TextView tvFirstName, tvSecondName, tvThirdName;
    private TextView tvFirstScore, tvSecondScore, tvThirdScore;

    // Những người chơi khác
    private RecyclerView rvOtherPlayers;
    private OtherPlayersAdapter otherPlayersAdapter;

    // Nút đóng
    private ImageButton btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_scoreboard);

        initializeViews();
        setupClickListeners();

        // Lấy dữ liệu bảng xếp hạng từ intent
        LeaderboardDTO leaderboard = (LeaderboardDTO) getIntent().getSerializableExtra("LEADERBOARD");
        if (leaderboard != null) {
            displayLeaderboard(leaderboard);
        } else {
            Toast.makeText(this, "No leaderboard data available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        // Khởi tạo UI cho 3 người chơi hàng đầu
        ivFirstAvatar = findViewById(R.id.iv_first_avatar);
        ivSecondAvatar = findViewById(R.id.iv_second_avatar);
        ivThirdAvatar = findViewById(R.id.iv_third_avatar);

        tvFirstName = findViewById(R.id.tv_first_name);
        tvSecondName = findViewById(R.id.tv_second_name);
        tvThirdName = findViewById(R.id.tv_third_name);

        tvFirstScore = findViewById(R.id.tv_first_score);
        tvSecondScore = findViewById(R.id.tv_second_score);
        tvThirdScore = findViewById(R.id.tv_third_score);

        // Khởi tạo RecyclerView cho những người chơi khác
        rvOtherPlayers = findViewById(R.id.rv_other_players);
        otherPlayersAdapter = new OtherPlayersAdapter();
        rvOtherPlayers.setLayoutManager(new LinearLayoutManager(this));
        rvOtherPlayers.setAdapter(otherPlayersAdapter);

        // Khởi tạo nút đóng
        btnClose = findViewById(R.id.btn_close);
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> {
            // Quay lại MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void displayLeaderboard(LeaderboardDTO leaderboard) {
        List<PlayerRankingDTO> rankings = leaderboard.getRankings();
        if (rankings == null || rankings.isEmpty()) {
            Toast.makeText(this, "No players in the leaderboard", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị 3 người chơi hàng đầu
        if (rankings.size() >= 1) {
            displayPlayerInPosition(rankings.get(0), 1);
        }

        if (rankings.size() >= 2) {
            displayPlayerInPosition(rankings.get(1), 2);
        }
        if (rankings.size() >= 3) {
            displayPlayerInPosition(rankings.get(2), 3);
        }

        // Hiển thị những người chơi khác (vị trí thứ 4 trở xuống)
        List<PlayerRankingDTO> otherPlayers = new ArrayList<>();
        if (rankings.size() > 3) {
            for (int i = 3; i < rankings.size(); i++) {
                otherPlayers.add(rankings.get(i));
            }
        }
        otherPlayersAdapter.updatePlayers(otherPlayers);
    }

    private void displayPlayerInPosition(PlayerRankingDTO player, int position) {
        switch (position) {
            case 1: // Vị trí thứ nhất
                tvFirstName.setText(player.getUsername());
                tvFirstScore.setText(formatScore(player.getScore()));
                loadPlayerAvatar(player.getAvatar(), ivFirstAvatar);
                break;

            case 2: // Vị trí thứ hai
                tvSecondName.setText(player.getUsername());
                tvSecondScore.setText(formatScore(player.getScore()));
                loadPlayerAvatar(player.getAvatar(), ivSecondAvatar);
                break;

            case 3: // Vị trí thứ ba
                tvThirdName.setText(player.getUsername());
                tvThirdScore.setText(formatScore(player.getScore()));
                loadPlayerAvatar(player.getAvatar(), ivThirdAvatar);
                break;
        }
    }

    private void loadPlayerAvatar(String avatarUrl, CircleImageView imageView) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            ImageLoader.loadImage(
                    this,
                    imageView,
                    avatarUrl,
                    R.drawable.avatar_placeholder,
                    R.drawable.avatar_placeholder
            );
        } else {
            imageView.setImageResource(R.drawable.avatar_placeholder);
        }
    }

    private String formatScore(Integer score) {
        if (score == null) return "0 Pt";
        return String.format("%,d Pt", score);
    }

    /**
     * Adapter để hiển thị người chơi xếp hạng thứ 4 trở xuống
     */
    private class OtherPlayersAdapter extends RecyclerView.Adapter<OtherPlayersAdapter.OtherPlayerViewHolder> {

        private final List<PlayerRankingDTO> players = new ArrayList<>();

        public void updatePlayers(List<PlayerRankingDTO> newPlayers) {
            players.clear();
            if (newPlayers != null) {
                players.addAll(newPlayers);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public OtherPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_other_player, parent, false);
            return new OtherPlayerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OtherPlayerViewHolder holder, int position) {
            PlayerRankingDTO player = players.get(position);
            holder.bind(player, position + 4); // Position + 4 because we start from 4th place
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        class OtherPlayerViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvRank;
            private final CircleImageView ivPlayerAvatar;
            private final TextView tvPlayerName;
            private final TextView tvScore;

            public OtherPlayerViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRank = itemView.findViewById(R.id.tv_rank);
                ivPlayerAvatar = itemView.findViewById(R.id.iv_player_avatar);
                tvPlayerName = itemView.findViewById(R.id.tv_player_name);
                tvScore = itemView.findViewById(R.id.tv_score);
            }

            public void bind(PlayerRankingDTO player, int rank) {
                tvRank.setText(String.valueOf(rank));
                tvPlayerName.setText(player.getUsername());
                tvScore.setText(String.valueOf(player.getScore()));

                // Load avatar
                if (player.getAvatar() != null && !player.getAvatar().isEmpty()) {
                    ImageLoader.loadImage(
                            itemView.getContext(),
                            ivPlayerAvatar,
                            player.getAvatar(),
                            R.drawable.avatar_placeholder,
                            R.drawable.avatar_placeholder
                    );
                } else {
                    ivPlayerAvatar.setImageResource(R.drawable.avatar_placeholder);
                }
            }
        }
    }
}
