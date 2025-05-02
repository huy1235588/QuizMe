package com.huy.QuizMe.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.io.File;

/**
 * Lớp tiện ích để tải và hiển thị ảnh trong ứng dụng
 * Cung cấp các phương thức để tải ảnh từ nhiều nguồn khác nhau (URL, tài nguyên, tệp tin)
 * Hỗ trợ các tính năng nâng cao như hiệu ứng chuyển đổi, bộ nhớ đệm và biến đổi hình ảnh
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";
    
    // Thời gian chuyển đổi hiệu ứng mặc định (ms)
    private static final int DEFAULT_TRANSITION_DURATION = 300;
    
    // Các phương thức tải ảnh cơ bản
    
    /**
     * Tải ảnh từ URL sử dụng Glide
     *
     * @param context     Context để tải ảnh
     * @param imageView   ImageView để hiển thị ảnh
     * @param imageUrl    URL của ảnh cần tải
     * @param placeholder Resource ID của ảnh mặc định
     * @param errorImage  Resource ID của ảnh hiển thị khi lỗi
     */
    public static void loadImage(Context context, ImageView imageView, String imageUrl,
                                @DrawableRes int placeholder, @DrawableRes int errorImage) {
        if (context == null || imageView == null) return;
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Cải thiện: sử dụng DiskCacheStrategy.ALL để đảm bảo lưu đúng trong cache
            // Thêm skipMemoryCache(false) để đảm bảo sử dụng bộ nhớ đệm hiệu quả
            RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .error(errorImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false);
            
            Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, 
                                             Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Image load failed: " + imageUrl + 
                              (e != null ? " - " + e.getMessage() : ""));
                        
                        // Kiểm tra trạng thái mạng nếu load thất bại
                        if (NetworkUtils.isNetworkAvailable(context)) {
                            // Thử tải lại ảnh sau một khoảng thời gian nếu có mạng
                            imageView.postDelayed(() -> {
                                // Xóa cache cho URL này và thử lại
                                clearCacheForUrl(context, imageUrl);
                                loadImage(context, imageView, imageUrl, placeholder, errorImage);
                            }, 2000); // Thử lại sau 2 giây
                        }
                        return false;
                    }
                    
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, 
                                                Target<Drawable> target, DataSource dataSource, 
                                                boolean isFirstResource) {
                        Log.d(TAG, "Image loaded successfully: " + imageUrl + 
                              " (source: " + dataSource.name() + ")");
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_TRANSITION_DURATION))
                .into(imageView);
        } else {
            imageView.setImageResource(placeholder);
        }
    }
    
    /**
     * Tải ảnh từ fragment context, tránh lỗi khi fragment không còn gắn với activity
     *
     * @param fragment    Fragment để tải ảnh
     * @param imageView   ImageView để hiển thị ảnh
     * @param imageUrl    URL của ảnh cần tải
     * @param placeholder Resource ID của ảnh mặc định
     * @param errorImage  Resource ID của ảnh hiển thị khi lỗi
     */
    public static void loadImage(Fragment fragment, ImageView imageView, String imageUrl,
                               @DrawableRes int placeholder, @DrawableRes int errorImage) {
        if (fragment == null || !fragment.isAdded() || imageView == null) return;
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(fragment)
                .load(imageUrl)
                .placeholder(placeholder)
                .error(errorImage)
                .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_TRANSITION_DURATION))
                .into(imageView);
        } else {
            imageView.setImageResource(placeholder);
        }
    }
    
    /**
     * Tải ảnh có thể là SVG hoặc bitmap thông thường
     *
     * @param context     Context để tải ảnh
     * @param imageView   ImageView để hiển thị ảnh
     * @param imageUrl    URL của ảnh cần tải
     * @param placeholder Resource ID của ảnh mặc định
     * @param errorImage  Resource ID của ảnh hiển thị khi lỗi
     */
    public static void loadImageWithSvgSupport(Context context, ImageView imageView, String imageUrl,
                                             @DrawableRes int placeholder, @DrawableRes int errorImage) {
        if (context == null || imageView == null) return;
        
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(placeholder);
            return;
        }

        if (imageUrl.toLowerCase().endsWith(".svg")) {
            loadSvgImage(context, imageUrl, imageView, placeholder, errorImage);
        } else {
            loadImage(context, imageView, imageUrl, placeholder, errorImage);
        }
    }

    /**
     * Tải ảnh SVG sử dụng thư viện GlideToVectorYou
     *
     * @param context     Context để tải ảnh
     * @param url         URL của ảnh SVG
     * @param imageView   ImageView để hiển thị ảnh
     * @param placeholder Resource ID của ảnh mặc định
     * @param errorImage  Resource ID của ảnh hiển thị khi lỗi
     */
    private static void loadSvgImage(Context context, String url, ImageView imageView,
                                   @DrawableRes int placeholder, @DrawableRes int errorImage) {
        try {
            Uri uri = Uri.parse(url);
            imageView.setImageResource(placeholder);

            // Thiết lập padding cho ImageView (thường cần thiết cho SVG)
            int paddingPx = (int) (30 * context.getResources().getDisplayMetrics().density);
            imageView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            // Kiểm tra xem imageView đã được gắn vào window chưa
            if (!imageView.isAttachedToWindow()) {
                Log.w(TAG, "ImageView not attached to window, using post() to load SVG");
            }
            
            // Sửa lỗi: thêm kiểm tra trạng thái của View và cài đặt listener
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    // Kiểm tra context có hợp lệ không
                    if (context instanceof android.app.Activity) {
                        android.app.Activity activity = (android.app.Activity) context;
                        if (activity.isFinishing() || activity.isDestroyed()) {
                            Log.w(TAG, "Activity is finishing or destroyed, skipping SVG load");
                            return;
                        }
                    }
                    
                    // Kiểm tra imageView có hợp lệ không
                    if (imageView.getVisibility() != View.VISIBLE || !imageView.isShown()) {
                        Log.w(TAG, "ImageView not visible or not shown, retrying SVG load");
                        // Thử lại sau 100ms
                        imageView.postDelayed(this, 100);
                        return;
                    }
                    
                    // Tải SVG với listener
                    GlideToVectorYou
                        .init()
                        .with(context)
                        .setPlaceHolder(placeholder, errorImage)
                        .withListener(new GlideToVectorYouListener() {
                            @Override
                            public void onLoadFailed() {
                                Log.e(TAG, "SVG load failed: " + url);
                                imageView.setImageResource(errorImage);
                            }
                            
                            @Override
                            public void onResourceReady() {
                                Log.d(TAG, "SVG loaded successfully: " + url);
                            }
                        })
                        .load(uri, imageView);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading SVG image: " + e.getMessage());
            imageView.setImageResource(errorImage);
        }
    }
    
    // Phương thức tải ảnh nâng cao với các biến đổi và hiệu ứng
    
    /**
     * Tải ảnh với các biến đổi và hiệu ứng tùy chỉnh
     *
     * @param context       Context để tải ảnh
     * @param imageView     ImageView để hiển thị ảnh
     * @param imageUrl      URL của ảnh cần tải
     * @param placeholder   Resource ID của ảnh mặc định
     * @param errorImage    Resource ID của ảnh hiển thị khi lỗi
     * @param transformations Danh sách các biến đổi cần áp dụng
     */
    public static void loadImageWithTransformations(Context context, ImageView imageView, String imageUrl,
                                                 @DrawableRes int placeholder, @DrawableRes int errorImage,
                                                 BitmapTransformation... transformations) {
        if (context == null || imageView == null) return;
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions()
                .placeholder(placeholder)
                .error(errorImage)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .format(DecodeFormat.PREFER_ARGB_8888);
                
            if (transformations != null && transformations.length > 0) {
                requestOptions = requestOptions.transform(transformations);
            }
            
            Glide.with(context)
                .load(imageUrl)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_TRANSITION_DURATION))
                .into(imageView);
        } else {
            imageView.setImageResource(placeholder);
        }
    }
    
    /**
     * Tải ảnh tròn (avatar)
     *
     * @param context     Context để tải ảnh
     * @param imageView   ImageView để hiển thị ảnh
     * @param imageUrl    URL của ảnh cần tải
     * @param placeholder Resource ID của ảnh mặc định
     */
    public static void loadCircleImage(Context context, ImageView imageView, String imageUrl, @DrawableRes int placeholder) {
        loadImageWithTransformations(context, imageView, imageUrl, placeholder, placeholder, new CircleCrop());
    }
    
    /**
     * Tải ảnh với góc bo tròn
     *
     * @param context     Context để tải ảnh
     * @param imageView   ImageView để hiển thị ảnh
     * @param imageUrl    URL của ảnh cần tải
     * @param placeholder Resource ID của ảnh mặc định
     * @param cornerRadius Bán kính bo góc (đơn vị: px)
     */
    public static void loadRoundedCornerImage(Context context, ImageView imageView, String imageUrl,
                                           @DrawableRes int placeholder, int cornerRadius) {
        loadImageWithTransformations(context, imageView, imageUrl, placeholder, placeholder, 
            new CenterCrop(), new RoundedCorners(cornerRadius));
    }
    
    /**
     * Tải ảnh hồ sơ người dùng, sử dụng cho avatar
     *
     * @param context     Context để tải ảnh
     * @param imageView   ImageView để hiển thị ảnh
     * @param imageUrl    URL của ảnh hồ sơ
     * @param placeholder Resource ID của ảnh mặc định
     */
    public static void loadProfileImage(Context context, ImageView imageView, String imageUrl, @DrawableRes int placeholder) {
        loadCircleImage(context, imageView, imageUrl, placeholder);
    }
    
    /**
     * Tải ảnh từ tài nguyên (drawable)
     *
     * @param context       Context để tải ảnh
     * @param imageView     ImageView để hiển thị ảnh
     * @param resourceId    ID của tài nguyên drawable
     * @param placeholder   Resource ID của ảnh mặc định
     */
    public static void loadResourceImage(Context context, ImageView imageView, @DrawableRes int resourceId,
                                       @DrawableRes int placeholder) {
        if (context == null || imageView == null) return;
        
        Glide.with(context)
            .load(resourceId)
            .placeholder(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_TRANSITION_DURATION))
            .into(imageView);
    }
    
    /**
     * Tải ảnh từ tệp tin (file)
     *
     * @param context     Context để tải ảnh
     * @param imageView   ImageView để hiển thị ảnh
     * @param file        Tệp tin ảnh cần tải
     * @param placeholder Resource ID của ảnh mặc định
     */
    public static void loadFileImage(Context context, ImageView imageView, File file, @DrawableRes int placeholder) {
        if (context == null || imageView == null || file == null || !file.exists()) {
            if (imageView != null) {
                imageView.setImageResource(placeholder);
            }
            return;
        }
        
        Glide.with(context)
            .load(file)
            .placeholder(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_TRANSITION_DURATION))
            .into(imageView);
    }
    
    /**
     * Tải ảnh với callback để thực hiện hành động sau khi tải xong hoặc lỗi
     *
     * @param context    Context để tải ảnh
     * @param imageView  ImageView để hiển thị ảnh
     * @param imageUrl   URL của ảnh cần tải
     * @param placeholder Resource ID của ảnh mặc định
     * @param listener   Listener để nhận thông báo khi tải xong hoặc lỗi
     */
    public static void loadImageWithCallback(Context context, ImageView imageView, String imageUrl, 
                                          @DrawableRes int placeholder, ImageLoadListener listener) {
        if (context == null || imageView == null) return;
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, 
                                              Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadFailed(e != null ? e.getMessage() : "Unknown error");
                        }
                        return false;
                    }
                    
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, 
                                                Target<Drawable> target, DataSource dataSource, 
                                                boolean isFirstResource) {
                        if (listener != null) {
                            listener.onLoadSuccessful();
                        }
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade(DEFAULT_TRANSITION_DURATION))
                .into(imageView);
        } else {
            imageView.setImageResource(placeholder);
            if (listener != null) {
                listener.onLoadFailed("URL is empty or null");
            }
        }
    }
    
    /**
     * Tải ảnh với hiển thị loading view trong khi tải
     *
     * @param context      Context để tải ảnh
     * @param imageView    ImageView để hiển thị ảnh
     * @param imageUrl     URL của ảnh cần tải
     * @param placeholder  Resource ID của ảnh mặc định
     * @param loadingView  View hiển thị trong khi tải (progress bar)
     */
    public static void loadImageWithLoading(Context context, ImageView imageView, String imageUrl,
                                         @DrawableRes int placeholder, View loadingView) {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        
        loadImageWithCallback(context, imageView, imageUrl, placeholder, new ImageLoadListener() {
            @Override
            public void onLoadSuccessful() {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void onLoadFailed(String errorMessage) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
    }
    
    // Phương thức tiện ích cho bộ nhớ đệm
    
    /**
     * Xóa bộ nhớ đệm ảnh
     * Gọi phương thức này khi cần giải phóng bộ nhớ hoặc cập nhật ảnh mới
     *
     * @param context Context để xóa bộ nhớ đệm
     */
    public static void clearCache(Context context) {
        if (context == null) return;
        
        Glide.get(context).clearMemory();
        // Xóa bộ nhớ đệm trên đĩa trong luồng nền
        new Thread(() -> Glide.get(context).clearDiskCache()).start();
    }
    
    /**
     * Xóa bộ nhớ đệm ảnh cho một URL cụ thể
     *
     * @param context Context để xóa bộ nhớ đệm
     * @param url     URL của ảnh cần xóa khỏi bộ nhớ đệm
     */
    public static void clearCacheForUrl(Context context, String url) {
        if (context == null || url == null || url.isEmpty()) return;
        
        // Xóa từ bộ nhớ RAM
        Glide.with(context).clear(new Target<Drawable>() {
            @Override
            public void onStart() {}
            
            @Override
            public void onStop() {}
            
            @Override
            public void onDestroy() {}
            
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {}
            
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {}
            
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
            
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {}
            
            @Override
            public void getSize(@NonNull com.bumptech.glide.request.target.SizeReadyCallback cb) {}
            
            @Override
            public void removeCallback(@NonNull com.bumptech.glide.request.target.SizeReadyCallback cb) {}
            
            @Override
            public void setRequest(@Nullable com.bumptech.glide.request.Request request) {}
            
            @Nullable
            @Override
            public com.bumptech.glide.request.Request getRequest() {
                return null;
            }
        });
        
        // Xóa từ bộ nhớ đĩa
        new Thread(() -> {
            try {
                Glide.get(context).clearDiskCache();
            } catch (Exception e) {
                Log.e(TAG, "Error clearing disk cache for URL: " + url, e);
            }
        }).start();
    }
    
    /**
     * Tải sẵn ảnh vào bộ nhớ đệm (preloading)
     * Hữu ích để chuẩn bị trước ảnh mà người dùng có khả năng xem
     *
     * @param context   Context để tải ảnh
     * @param imageUrl  URL của ảnh cần tải sẵn
     * @param width     Chiều rộng mong muốn (px), sử dụng Target.SIZE_ORIGINAL cho kích thước gốc
     * @param height    Chiều cao mong muốn (px), sử dụng Target.SIZE_ORIGINAL cho kích thước gốc
     */
    public static void preloadImage(Context context, String imageUrl, int width, int height) {
        if (context == null || imageUrl == null || imageUrl.isEmpty()) return;
        
        RequestBuilder<Drawable> requestBuilder = Glide.with(context).load(imageUrl);
        
        if (width > 0 && height > 0) {
            requestBuilder.preload(width, height);
        } else {
            requestBuilder.preload();
        }
    }
    
    /**
     * Tải nhiều ảnh vào bộ nhớ đệm cùng lúc
     *
     * @param context   Context để tải ảnh
     * @param imageUrls Mảng các URL ảnh cần tải sẵn
     */
    public static void preloadImages(Context context, String[] imageUrls) {
        if (context == null || imageUrls == null || imageUrls.length == 0) return;
        
        for (String url : imageUrls) {
            if (url != null && !url.isEmpty()) {
                preloadImage(context, url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            }
        }
    }
    
    /**
     * Interface lắng nghe sự kiện tải ảnh
     */
    public interface ImageLoadListener {
        /**
         * Gọi khi tải ảnh thành công
         */
        void onLoadSuccessful();
        
        /**
         * Gọi khi tải ảnh thất bại
         * @param errorMessage Thông báo lỗi
         */
        void onLoadFailed(String errorMessage);
    }
}