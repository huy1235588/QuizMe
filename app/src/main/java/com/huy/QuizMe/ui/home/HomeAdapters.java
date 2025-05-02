package com.huy.QuizMe.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Category;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * - QuizAdapter: Hiển thị danh sách quiz
 * - CategoryAdapter: Hiển thị danh sách category
 * - AuthorAdapter: Hiển thị danh sách tác giả
 */
public class HomeAdapters {

    /**
     * Lớp trừu tượng cơ sở cho các Adapter để tránh mã lặp lại
     */
    private abstract static class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        protected final Context context;
        protected final List<T> items = new ArrayList<>();

        protected BaseAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         * Cập nhật danh sách items và thông báo thay đổi
         * 
         * @param newItems Danh sách items mới
         */
        public void updateItems(List<T> newItems) {
            if (newItems != null) {
                items.clear();
                items.addAll(newItems);
                notifyDataSetChanged();
            }
        }

        /**
         * Tải ảnh từ URL sử dụng Glide
         * 
         * @param imageView View để hiển thị ảnh
         * @param imageUrl URL ảnh cần tải
         * @param placeholder Resource ID của ảnh mặc định
         * @param errorImage Resource ID của ảnh hiển thị khi lỗi
         */
        protected void loadImage(ImageView imageView, String imageUrl, @DrawableRes int placeholder, @DrawableRes int errorImage) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .placeholder(placeholder)
                    .error(errorImage)
                    .into(imageView);
            } else {
                imageView.setImageResource(placeholder);
            }
        }
    }

    /**
     * QuizAdapter - Adapter quản lý và hiển thị danh sách các quiz trong RecyclerView
     */
    public static class QuizAdapter extends BaseAdapter<Quiz, QuizAdapter.QuizViewHolder> {
        private OnQuizClickListener listener;

        /**
         * Khởi tạo adapter với context
         *
         * @param context Context của activity hoặc fragment
         */
        public QuizAdapter(Context context) {
            super(context);
        }

        /**
         * Interface xử lý sự kiện khi người dùng click vào một quiz
         */
        public interface OnQuizClickListener {
            void onQuizClick(Quiz quiz);
        }

        /**
         * Thiết lập listener cho sự kiện click vào quiz
         *
         * @param listener Listener xử lý sự kiện
         */
        public void setOnQuizClickListener(OnQuizClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
            return new QuizViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
            Quiz quiz = items.get(position);

            holder.tvQuizTitle.setText(quiz.getTitle());
            holder.tvQuestionCount.setText(String.format("%d Questions", quiz.getQuestionCount()));
            holder.tvCreatorName.setText(quiz.getCreatorName());

            // Tải ảnh thumbnail và avatar
            loadImage(holder.ivQuizImage, quiz.getQuizThumbnails(), 
                    R.drawable.placeholder_quiz, R.drawable.placeholder_quiz);
            loadImage(holder.ivCreatorAvatar, quiz.getCreatorAvatar(), 
                    R.drawable.placeholder_avatar, R.drawable.placeholder_avatar);

            // Xử lý sự kiện click
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuizClick(quiz);
                }
            });
        }

        /**
         * Thêm quiz vào danh sách hiện tại (sử dụng cho tính năng tải thêm)
         *
         * @param moreQuizzes Danh sách quiz bổ sung
         */
        public void addQuizzes(List<Quiz> moreQuizzes) {
            if (moreQuizzes != null && !moreQuizzes.isEmpty()) {
                int startPosition = items.size();
                items.addAll(moreQuizzes);
                notifyItemRangeInserted(startPosition, moreQuizzes.size());
            }
        }

        /**
         * ViewHolder chứa các view thành phần trong item quiz
         */
        public static class QuizViewHolder extends RecyclerView.ViewHolder {
            ImageView ivQuizImage;
            TextView tvQuizTitle, tvQuestionCount, tvCreatorName;
            CircleImageView ivCreatorAvatar;

            public QuizViewHolder(@NonNull View itemView) {
                super(itemView);
                ivQuizImage = itemView.findViewById(R.id.iv_quiz_image);
                tvQuizTitle = itemView.findViewById(R.id.tv_quiz_title);
                tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
                tvCreatorName = itemView.findViewById(R.id.tv_author_name);
                ivCreatorAvatar = itemView.findViewById(R.id.iv_author_avatar);
            }
        }
    }

    /**
     * CategoryAdapter - Adapter quản lý và hiển thị danh sách các danh mục (category) trong RecyclerView
     */
    public static class CategoryAdapter extends BaseAdapter<Category, CategoryAdapter.CategoryViewHolder> {
        private OnCategoryClickListener listener;

        /**
         * Khởi tạo adapter với context
         *
         * @param context Context của activity hoặc fragment
         */
        public CategoryAdapter(Context context) {
            super(context);
        }

        /**
         * Interface xử lý sự kiện khi người dùng click vào một danh mục
         */
        public interface OnCategoryClickListener {
            void onCategoryClick(Category category);
        }

        /**
         * Thiết lập listener cho sự kiện click vào danh mục
         *
         * @param listener Listener xử lý sự kiện
         */
        public void setOnCategoryClickListener(OnCategoryClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = items.get(position);
            holder.tvCategoryName.setText(category.getName());

            // Tải hình ảnh cho danh mục
            loadCategoryImage(category.getIconUrl(), holder.ivCategoryImage);

            // Xử lý sự kiện click
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }

        /**
         * Tải hình ảnh cho danh mục, xử lý cả định dạng SVG và bitmap thông thường
         *
         * @param imageUrl  URL của hình ảnh cần tải
         * @param imageView ImageView để hiển thị hình ảnh
         */
        private void loadCategoryImage(String imageUrl, ImageView imageView) {
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageView.setImageResource(R.drawable.placeholder_category);
                return;
            }
            
            if (imageUrl.toLowerCase().endsWith(".svg")) {
                loadSvgImage(imageUrl, imageView);
            } else {
                loadImage(imageView, imageUrl, 
                        R.drawable.placeholder_category, R.drawable.placeholder_category);
            }
        }

        /**
         * Tải ảnh SVG sử dụng thư viện GlideToVectorYou
         *
         * @param url       URL của ảnh SVG
         * @param imageView ImageView để hiển thị ảnh
         */
        private void loadSvgImage(String url, ImageView imageView) {
            try {
                Uri uri = Uri.parse(url);
                imageView.setImageResource(R.drawable.placeholder_category);

                int paddingPx = (int) (30 * context.getResources().getDisplayMetrics().density);
                imageView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

                imageView.post(() -> {
                    if (context instanceof android.app.Activity && !((android.app.Activity) context).isDestroyed()) {
                        GlideToVectorYou
                            .init()
                            .with(context)
                            .setPlaceHolder(R.drawable.placeholder_category, R.drawable.placeholder_category)
                            .load(uri, imageView);
                    }
                });
            } catch (Exception e) {
                Log.e("CategoryAdapter", "Error loading SVG image: " + e.getMessage());
                imageView.setImageResource(R.drawable.placeholder_category);
            }
        }

        /**
         * ViewHolder chứa các view thành phần trong item danh mục
         */
        public static class CategoryViewHolder extends RecyclerView.ViewHolder {
            ImageView ivCategoryImage;
            TextView tvCategoryName;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
                tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            }
        }
    }

    /**
     * AuthorAdapter - Adapter quản lý và hiển thị danh sách các tác giả trong RecyclerView
     */
    public static class AuthorAdapter extends BaseAdapter<User, AuthorAdapter.AuthorViewHolder> {
        private OnAuthorClickListener listener;
        private static final int[] BACKGROUND_COLORS = {
            Color.parseColor("#6A5ACD"), // Slateblue 
            Color.parseColor("#FF69B4"), // Hot pink
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#FF9800")  // Orange
        };

        public AuthorAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_author, parent, false);
            return new AuthorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
            User author = items.get(position);
            holder.tvAuthorName.setText(author.getUsername());
            
            // Set background color for the author's avatar
            int colorPosition = position % BACKGROUND_COLORS.length;
            holder.ivAuthor.setCircleBackgroundColor(BACKGROUND_COLORS[colorPosition]);
            
            // Load author avatar
            loadImage(holder.ivAuthor, author.getProfileImage(), 
                    R.drawable.avatar_1, R.drawable.avatar_1);

            // Set click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAuthorClick(author);
                }
            });
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
}