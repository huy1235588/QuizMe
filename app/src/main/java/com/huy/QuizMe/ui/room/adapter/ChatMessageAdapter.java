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
import com.huy.QuizMe.data.model.ChatMessage;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.utils.ImageLoader;
import com.huy.QuizMe.utils.SharedPreferencesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 0;
    private static final int VIEW_TYPE_RECEIVED = 1;
    private static final int VIEW_TYPE_SYSTEM = 2;

    private final Context context;
    private final List<ChatMessage> messages;
    private final SharedPreferencesManager preferencesManager;
    private final SimpleDateFormat apiDateFormat;
    private final SimpleDateFormat displayDateFormat;

    public ChatMessageAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
        this.preferencesManager = SharedPreferencesManager.getInstance();
        this.apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        this.displayDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);

        // Check if this is a system message
        if (message.getUser() == null && (message.getGuest() == null || !message.getGuest()) &&
                message.getMessage() != null && message.getMessage().startsWith("SYSTEM_MESSAGE:")) {
            return VIEW_TYPE_SYSTEM;
        }

        User currentUser = preferencesManager.getUser();

        if (isCurrentUser(message, currentUser)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    private boolean isCurrentUser(ChatMessage message, User currentUser) {
        if (message.getUser() == null && message.getGuest() != null && message.getGuest() && currentUser == null) {
            return true;
        }
        return currentUser != null && message.getUser() != null &&
                currentUser.getId().equals(message.getUser().getId());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_SYSTEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_system, parent, false);
            return new SystemMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (viewType == VIEW_TYPE_SYSTEM) {
            ((SystemMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void setMessages(List<ChatMessage> newMessages) {
        messages.clear();
        if (newMessages != null) {
            messages.addAll(newMessages);
        }
        notifyDataSetChanged();
    }

    private String formatTime(String sentAt) {
        try {
            Date date = apiDateFormat.parse(sentAt);
            if (date != null) {
                return displayDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    // ViewHolder cho tin nhắn đã gửi
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvMessage, tvTimestamp;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        void bind(ChatMessage message) {
            tvSenderName.setText("You");
            tvMessage.setText(message.getMessage());
            tvTimestamp.setText(message.getSentAt());
        }
    }

    // ViewHolder cho tin nhắn đã nhận
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvMessage, tvTimestamp;
        ImageView ivUserAvatar;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        void bind(ChatMessage message) {
            String senderName;
            if (message.getUser() != null) {
                senderName = message.getUser().getUsername();

                // Tải avatar
                if (message.getUser().getProfileImage() != null && !message.getUser().getProfileImage().isEmpty()) {
                    ImageLoader.loadProfileImage(itemView.getContext(), ivUserAvatar,
                            message.getUser().getProfileImage(), R.drawable.placeholder_avatar);
                }
            } else {
                senderName = message.getGuestName() != null ? message.getGuestName() : "Guest";
                ivUserAvatar.setImageResource(R.drawable.placeholder_avatar);
            }

            tvSenderName.setText(senderName);
            tvMessage.setText(message.getMessage());
            tvTimestamp.setText(message.getSentAt());
        }
    }

    // ViewHolder cho tin nhắn hệ thống
    static class SystemMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSystemMessage;

        SystemMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSystemMessage = itemView.findViewById(R.id.tvSystemMessage);
        }

        void bind(ChatMessage message) {
            // Loại bỏ prefix "SYSTEM_MESSAGE:" khỏi nội dung hiển thị
            String systemMessage = message.getMessage();
            if (systemMessage != null && systemMessage.startsWith("SYSTEM_MESSAGE:")) {
                systemMessage = systemMessage.substring("SYSTEM_MESSAGE:".length()).trim();
            }

            tvSystemMessage.setText(systemMessage);
        }
    }
}
