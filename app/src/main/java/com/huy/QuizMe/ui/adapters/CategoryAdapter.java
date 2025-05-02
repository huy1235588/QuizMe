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
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.huy.QuizMe.R;
import com.huy.QuizMe.data.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final Context context;
    private OnCategoryClickListener listener;

    public CategoryAdapter(Context context) {
        this.context = context;
        this.categories = new ArrayList<>();
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

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
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());

        // Load image from category
        if (category.getIconUrl() != null && !category.getIconUrl().isEmpty()) {
            if (isSvgUrl(category.getIconUrl())) {
                loadSvgImage(category.getIconUrl(), holder.ivCategoryImage);
            } else {
                // Regular image handling with Glide
                Glide.with(context)
                        .load(category.getIconUrl())
                        .placeholder(R.drawable.placeholder_category)
                        .error(R.drawable.placeholder_category)
                        .into(holder.ivCategoryImage);
            }
        } else {
            holder.ivCategoryImage.setImageResource(R.drawable.placeholder_category);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    // Check if URL points to an SVG image
    private boolean isSvgUrl(String url) {
        return url != null && url.toLowerCase().endsWith(".svg");
    }

    // Load SVG image using GlideToVectorYou library
    private void loadSvgImage(String url, ImageView imageView) {
        try {
            // Parse the URL to URI
            Uri uri = Uri.parse(url);

            // Set placeholder while loading
            imageView.setImageResource(R.drawable.placeholder_category);

            // Use GlideToVectorYou to load SVG with a listener for better error handling
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
            Log.e("CategoryAdapter", "Error loading SVG: " + e.getMessage(), e);
            // Fallback to default image if SVG loading fails
            imageView.setImageResource(R.drawable.placeholder_category);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<Category> newCategories) {
        categories.clear();
        if (newCategories != null) {
            categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}