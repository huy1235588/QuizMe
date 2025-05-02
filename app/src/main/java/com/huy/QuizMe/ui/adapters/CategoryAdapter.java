package com.huy.QuizMe.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter quản lý và hiển thị danh sách các danh mục (category) trong RecyclerView
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // Danh sách các danh mục được hiển thị
    private final List<Category> categories;
    // Context để load ảnh và tài nguyên
    private final Context context;
    // Listener xử lý sự kiện click vào danh mục
    private OnCategoryClickListener listener;

    /**
     * Khởi tạo adapter với context
     *
     * @param context Context của activity hoặc fragment
     */
    public CategoryAdapter(Context context) {
        this.context = context;
        this.categories = new ArrayList<>();
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
        // Tạo view mới từ layout item_category
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());

        // Tải hình ảnh cho danh mục
        loadCategoryImage(category.getIconUrl(), holder.ivCategoryImage);

        // Xử lý sự kiện click vào item danh mục
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
        // Kiểm tra URL có hợp lệ không
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (isSvgUrl(imageUrl)) {
                // Xử lý riêng cho ảnh SVG
                loadSvgImage(imageUrl, imageView);
            } else {
                // Xử lý cho ảnh bitmap thông thường
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_category)
                        .error(R.drawable.placeholder_category)
                        .into(imageView);
            }
        } else {
            // Sử dụng ảnh mặc định khi URL trống
            imageView.setImageResource(R.drawable.placeholder_category);
        }
    }

    /**
     * Kiểm tra URL có phải là ảnh SVG hay không
     *
     * @param url URL cần kiểm tra
     * @return true nếu URL trỏ đến file SVG
     */
    private boolean isSvgUrl(String url) {
        return url != null && url.toLowerCase().endsWith(".svg");
    }

    /**
     * Tải ảnh SVG sử dụng thư viện GlideToVectorYou
     *
     * @param url       URL của ảnh SVG
     * @param imageView ImageView để hiển thị ảnh
     */
    private void loadSvgImage(String url, ImageView imageView) {
        try {
            // Chuyển đổi URL thành URI
            Uri uri = Uri.parse(url);

            // Hiển thị ảnh placeholder trong khi tải
            imageView.setImageResource(R.drawable.placeholder_category);


            // Thiết lập padding cho ImageView
            int paddingPx = (int) (30 * context.getResources().getDisplayMetrics().density);
            imageView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            // Sử dụng GlideToVectorYou để tải ảnh SVG
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
            // Sử dụng ảnh mặc định khi không thể tải SVG
            imageView.setImageResource(R.drawable.placeholder_category);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Cập nhật toàn bộ danh sách danh mục và thông báo thay đổi
     *
     * @param newCategories Danh sách danh mục mới
     */
    public void updateCategories(List<Category> newCategories) {
        categories.clear();
        if (newCategories != null) {
            categories.addAll(newCategories);
        }
        notifyDataSetChanged();
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