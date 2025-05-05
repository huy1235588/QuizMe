package com.huy.QuizMe.ui.main.join;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Room;
import com.huy.QuizMe.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private final Context context;
    private List<Room> rooms = new ArrayList<>();
    private OnRoomClickListener listener;

    public RoomAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void updateItems(List<Room> newRooms) {
        if (newRooms != null) {
            this.rooms.clear();
            this.rooms.addAll(newRooms);
            notifyDataSetChanged();
        }
    }

    public void setOnRoomClickListener(OnRoomClickListener listener) {
        this.listener = listener;
    }

    // Interface xử lý sự kiện click phòng
    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    // ViewHolder class
    class RoomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivRoomQuizThumbnail;
        private final ImageView ivHostAvatar;
        private final TextView tvRoomName;
        private final TextView tvHostName;
        private final TextView tvQuizName;
        private final TextView tvPlayersCount;
        private final TextView tvStatus;
        private final ImageView ivLock;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomQuizThumbnail = itemView.findViewById(R.id.iv_room_quiz_thumbnail);
            ivHostAvatar = itemView.findViewById(R.id.iv_host_avatar);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvHostName = itemView.findViewById(R.id.tv_host_name);
            tvQuizName = itemView.findViewById(R.id.tv_quiz_name);
            tvPlayersCount = itemView.findViewById(R.id.tv_players_count);
            tvStatus = itemView.findViewById(R.id.tv_room_status);
            ivLock = itemView.findViewById(R.id.iv_lock);

            // Thiết lập sự kiện click
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onRoomClick(rooms.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Room room) {
            // Hiển thị dữ liệu phòng
            tvRoomName.setText(room.getName());
            tvHostName.setText(room.getHost().getUsername());
            tvQuizName.setText(room.getQuiz().getTitle());
            tvPlayersCount.setText(context.getString(R.string.player_count_format,
                    room.getCurrentPlayerCount(), room.getMaxPlayers()));
            tvStatus.setText(room.getStatus());

            // Hiển thị trạng thái phòng
            if (room.isHasPassword()) {
                ivLock.setVisibility(View.VISIBLE);
            } else {
                ivLock.setVisibility(View.GONE);
            }

            // Tải hình ảnh
            if (room.getQuiz().getQuizThumbnails() != null && !room.getQuiz().getQuizThumbnails().isEmpty()) {
                ImageLoader.loadImage(context, ivRoomQuizThumbnail,
                        room.getQuiz().getQuizThumbnails(), R.drawable.placeholder_quiz, R.drawable.placeholder_quiz);
            }

            if (room.getHost().getProfileImage() != null && !room.getHost().getProfileImage().isEmpty()) {
                ImageLoader.loadCircleImage(context, ivHostAvatar,
                        room.getHost().getProfileImage(), R.drawable.placeholder_avatar);
            }
        }
    }
}
