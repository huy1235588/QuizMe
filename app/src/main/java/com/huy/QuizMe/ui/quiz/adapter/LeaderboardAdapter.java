package com.huy.QuizMe.ui.quiz.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.game.LeaderboardDTO.PlayerRankingDTO;
import com.huy.QuizMe.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter để hiển thị bảng xếp hạng trong trò chơi trắc nghiệm nhiều người chơi theo thời gian thực
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final List<PlayerRankingDTO> players = new ArrayList<>();
    private final Context context;
    private final Long currentUserId;

    public LeaderboardAdapter(Context context, Long currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLeaderboard(List<PlayerRankingDTO> newPlayers) {
        this.players.clear();
        if (newPlayers != null) {
            this.players.addAll(newPlayers);
        }
        notifyDataSetChanged();
    }

    /**
     * Cập nhật leaderboard với animation
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateLeaderboardWithAnimation(List<PlayerRankingDTO> newPlayers) {
        if (newPlayers == null) {
            players.clear();
            notifyDataSetChanged();
            return;
        }

        // Đơn giản hóa: sử dụng notifyDataSetChanged() với animation
        this.players.clear();
        this.players.addAll(newPlayers);
        notifyDataSetChanged();

        // Có thể implement DiffUtil sau này để có animation tốt hơn
    }

    /**
     * Lấy thông tin player tại vị trí cụ thể
     */
    public PlayerRankingDTO getPlayerAt(int position) {
        if (position >= 0 && position < players.size()) {
            return players.get(position);
        }
        return null;
    }

    /**
     * Tìm vị trí của người chơi hiện tại
     */
    public int getCurrentUserPosition() {
        if (currentUserId == null) return -1;

        for (int i = 0; i < players.size(); i++) {
            PlayerRankingDTO player = players.get(i);
            if (player.getUserId() != null && player.getUserId().equals(currentUserId)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard_player, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        PlayerRankingDTO player = players.get(position);
        holder.bind(player, position + 1);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        final private TextView tvRank;
        final private CircleImageView ivPlayerAvatar;
        final private TextView tvPlayerName;
        final private TextView tvScore;
        final private ImageView ivCrown;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            ivPlayerAvatar = itemView.findViewById(R.id.iv_player_avatar);
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            ivCrown = itemView.findViewById(R.id.iv_crown);
        }

        public void bind(PlayerRankingDTO player, int rank) {
            // Đặt thứ hạng
            tvRank.setText(String.valueOf(rank));

            // Đặt màu huy hiệu thứ hạng
            int rankColor = getRankColor(rank);
            tvRank.setBackgroundTintList(ContextCompat.getColorStateList(context, rankColor));

            // Hiển thị vương miện cho vị trí đầu tiên
            if (rank == 1) {
                ivCrown.setVisibility(View.VISIBLE);
                tvRank.setVisibility(View.GONE);
            } else {
                ivCrown.setVisibility(View.GONE);
                tvRank.setVisibility(View.VISIBLE);
            }

            // Đặt tên người chơi
            String displayName = player.getUsername();

            tvPlayerName.setText(displayName);

            // Đánh dấu người dùng hiện tại
            boolean isCurrentUser = false;
            isCurrentUser = currentUserId != null && currentUserId.equals(player.getUserId());

//            if (isCurrentUser) {
//                tvPlayerName.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
//                tvPlayerName.setTextSize(16);
//                itemView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
//            } else {
//                tvPlayerName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
//                tvPlayerName.setTextSize(14);
//                itemView.setBackgroundTintList(null);
//            }

            // Đặt điểm số
            String scoreText = String.valueOf(player.getScore() != null ? player.getScore() : 0);
            tvScore.setText(scoreText);

            // Tải avatar nếu có sẵn
            if (player.getAvatar() != null && !player.getAvatar().isEmpty()) {
                // Sử dụng ImageLoader để tải avatar của user
                ImageLoader.loadImage(
                        context,
                        ivPlayerAvatar,
                        player.getAvatar(),
                        R.drawable.avatar_placeholder,
                        R.drawable.avatar_placeholder
                );
            } else {
                // User không có avatar
                ivPlayerAvatar.setImageResource(R.drawable.avatar_placeholder);
            }
        }

        private int getRankColor(int rank) {
            switch (rank) {
                case 1:
                    return R.color.colorGold;
                case 2:
                    return R.color.colorSilver;
                case 3:
                    return R.color.colorBronze;
                default:
                    return R.color.colorPrimary;
            }
        }
    }
}
