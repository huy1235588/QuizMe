package com.huy.QuizMe.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Lớp tiện ích cho các chức năng liên quan đến bàn phím và điều chỉnh khu vực trò chuyện.
 * Có thể được tái sử dụng trong các activity khác nhau có chức năng trò chuyện.
 */
public class KeyboardUtils {

    /**
     * Thiết lập điều chỉnh bàn phím cho một activity có khu vực trò chuyện
     * @param activity Activity chứa giao diện trò chuyện
     */
    public static void setupKeyboardAdjustResize(Activity activity) {
        if (activity != null) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    /**
     * Thiết lập trình lắng nghe hiển thị bàn phím để điều chỉnh khu vực trò chuyện khi bàn phím hiển thị/ẩn đi
     * @param rootView View chính của activity để theo dõi thay đổi bố cục
     * @param chatRecyclerView RecyclerView cần điều chỉnh
     */
    public static void setupChatAreaKeyboardListener(final View rootView, final RecyclerView chatRecyclerView) {
        if (rootView == null || chatRecyclerView == null) return;

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

            // Nếu chênh lệch chiều cao lớn hơn 200dp, bàn phím có thể đang hiển thị
            if (heightDiff > convertDpToPx(rootView.getContext(), 200)) {
                // Bàn phím đang hiển thị, điều chỉnh khu vực trò chuyện
                chatRecyclerView.setPadding(0, 0, 0, 0);

                // Cuộn xuống dưới nếu có các mục
                if (chatRecyclerView.getAdapter() != null &&
                    chatRecyclerView.getAdapter().getItemCount() > 0) {
                    chatRecyclerView.smoothScrollToPosition(
                        chatRecyclerView.getAdapter().getItemCount() - 1);
                }
            } else {
                // Bàn phím đã ẩn, đặt lại padding
                chatRecyclerView.setPadding(0, 0, 0, convertDpToPx(rootView.getContext(), 0));
            }
        });
    }

    /**
     * Chuyển đổi từ density-independent pixels (dp) sang pixels (px)
     * @param context Context để lấy thông số hiển thị
     * @param dp Giá trị dp cần chuyển đổi
     * @return Giá trị tương đương theo pixels
     */
    public static int convertDpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
