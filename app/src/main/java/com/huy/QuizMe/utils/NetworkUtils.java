package com.huy.QuizMe.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

/**
 * Lớp tiện ích cho các hoạt động liên quan đến mạng
 */
public class NetworkUtils {

    /**
     * Kiểm tra thiết bị có kết nối internet hay không
     * @param context Context của ứng dụng
     * @return true nếu thiết bị có kết nối internet, false nếu không
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (capabilities == null) return false;
        
        // Thiết bị có kết nối nếu có ít nhất một trong các loại kết nối sau
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
               capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }
}