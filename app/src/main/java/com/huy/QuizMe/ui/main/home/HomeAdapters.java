package com.huy.QuizMe.ui.main.home;

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

import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Category;
import com.huy.QuizMe.data.model.Quiz;
import com.huy.QuizMe.data.model.User;
import com.huy.QuizMe.utils.ImageLoader;

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

            // Tải ảnh thumbnail và avatar sử dụng ImageLoader
            ImageLoader.loadImageWithTransformations(context, holder.ivQuizImage, quiz.getQuizThumbnails(), 
                    R.drawable.placeholder_quiz, R.drawable.placeholder_quiz);
            
            // Tải ảnh avatar với hiệu ứng tròn
            ImageLoader.loadProfileImage(context, holder.ivCreatorAvatar, quiz.getCreatorAvatar(), 
                    R.drawable.placeholder_avatar);

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

            // Tải hình ảnh cho danh mục sử dụng ImageLoader với hỗ trợ SVG
            ImageLoader.loadImageWithSvgSupport(context, holder.ivCategoryImage, 
                    category.getIconUrl(), R.drawable.placeholder_category, R.drawable.placeholder_category);

            // Xử lý sự kiện click
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
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
            
            // Load author avatar với hiệu ứng tròn
            ImageLoader.loadProfileImage(context, holder.ivAuthor, author.getProfileImage(), R.drawable.avatar_1);

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
    
    /**
     * Phương thức này sẽ tải trước hình ảnh cho danh sách các quiz
     * Giúp cải thiện trải nghiệm người dùng khi cuộn danh sách
     *
     * @param context Context để tải ảnh
     * @param quizzes Danh sách quiz cần tải trước ảnh
     */
    public static void preloadQuizImages(Context context, List<Quiz> quizzes) {
        if (context == null || quizzes == null || quizzes.isEmpty()) return;
        
        for (Quiz quiz : quizzes) {
            if (quiz.getQuizThumbnails() != null && !quiz.getQuizThumbnails().isEmpty()) {
                ImageLoader.preloadImage(context, quiz.getQuizThumbnails(), 0, 0);
            }
            if (quiz.getCreatorAvatar() != null && !quiz.getCreatorAvatar().isEmpty()) {
                ImageLoader.preloadImage(context, quiz.getCreatorAvatar(), 0, 0);
            }
        }
    }
    
    /**
     * Phương thức này sẽ tải trước hình ảnh cho danh sách các category
     * Giúp cải thiện trải nghiệm người dùng khi cuộn danh sách
     *
     * @param context Context để tải ảnh
     * @param categories Danh sách category cần tải trước ảnh
     */
    public static void preloadCategoryImages(Context context, List<Category> categories) {
        if (context == null || categories == null || categories.isEmpty()) return;
        
        for (Category category : categories) {
            if (category.getIconUrl() != null && !category.getIconUrl().isEmpty()) {
                ImageLoader.preloadImage(context, category.getIconUrl(), 0, 0);
            }
        }
    }
}