package com.huy.QuizMe.ui.room.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Participant;
import com.huy.QuizMe.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private final Context context;
    private final List<Participant> participants;

    public ParticipantAdapter(Context context) {
        this.context = context;
        this.participants = new ArrayList<>();
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participant, parent, false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        Participant participant = participants.get(position);
        holder.bind(participant);
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void setParticipants(List<Participant> newParticipants) {
        participants.clear();
        if (newParticipants != null) {
            participants.addAll(newParticipants);
        }
        notifyDataSetChanged();
    }

    static class ParticipantViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivUserAvatar;
        private final TextView tvUserName;
        private final ImageView ivHostBadge;

        ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivHostBadge = itemView.findViewById(R.id.ivHostBadge);
        }

        void bind(Participant participant) {
            // Đặt tên người dùng
            String username;
            if (participant.isGuest()) {
                username = participant.getGuestName();
                ivUserAvatar.setImageResource(R.drawable.placeholder_avatar);
            } else {
                username = participant.getUser().getUsername();
                
                // Tải hình ảnh đại diện
                if (participant.getUser().getProfileImage() != null && !participant.getUser().getProfileImage().isEmpty()) {
                    ImageLoader.loadProfileImage(itemView.getContext(), ivUserAvatar, 
                            participant.getUser().getProfileImage(), R.drawable.placeholder_avatar);
                } else {
                    ivUserAvatar.setImageResource(R.drawable.placeholder_avatar);
                }
            }
            tvUserName.setText(username);
            
            // Hiển thị biểu tượng chủ phòng nếu người tham gia này là chủ phòng
            ivHostBadge.setVisibility(participant.isHost() ? View.VISIBLE : View.GONE);
        }
    }
}
