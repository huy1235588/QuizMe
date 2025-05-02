package com.huy.QuizMe.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter quản lý và hiển thị danh sách các tác giả trong RecyclerView
 */
public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {

    private final Context context;
    private final List<User> authors = new ArrayList<>();
    private OnAuthorClickListener listener;
    private final int[] BACKGROUND_COLORS = {
            Color.parseColor("#6A5ACD"), // Slateblue 
            Color.parseColor("#FF69B4"), // Hot pink
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#FF9800")  // Orange
    };

    public AuthorAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_author, parent, false);
        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
        User author = authors.get(position);
        holder.tvAuthorName.setText(author.getUsername());
        
        // Set background color for the author's avatar
        int colorPosition = position % BACKGROUND_COLORS.length;
        holder.ivAuthor.setCircleBackgroundColor(BACKGROUND_COLORS[colorPosition]);
        
        // Load author avatar if available, otherwise show initials
        if (author.getProfileImage() != null && !author.getProfileImage().isEmpty()) {
            Glide.with(context)
                    .load(author.getProfileImage())
                    .placeholder(R.drawable.avatar_1)
                    .error(R.drawable.avatar_1)
                    .into(holder.ivAuthor);
        } else {
            // Use default avatar
            holder.ivAuthor.setImageResource(R.drawable.avatar_1);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAuthorClick(author);
            }
        });
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    /**
     * Cập nhật danh sách tác giả và thông báo adapter cần refresh
     *
     * @param newAuthors Danh sách tác giả mới
     */
    public void updateAuthors(List<User> newAuthors) {
        if (newAuthors != null) {
            this.authors.clear();
            this.authors.addAll(newAuthors);
            notifyDataSetChanged();
        }
    }

    /**
     * Thiết lập listener cho sự kiện click vào author
     *
     * @param listener Interface xử lý sự kiện click
     */
    public void setOnAuthorClickListener(OnAuthorClickListener listener) {
        this.listener = listener;
    }

    /**
     * Interface để lắng nghe sự kiện click vào một author
     */
    public interface OnAuthorClickListener {
        void onAuthorClick(User author);
    }

    /**
     * ViewHolder chứa các view thành phần trong item author
     */
    public static class AuthorViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAuthor;
        TextView tvAuthorName;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAuthor = itemView.findViewById(R.id.iv_author);
            tvAuthorName = itemView.findViewById(R.id.tv_author_name);
        }
    }
}