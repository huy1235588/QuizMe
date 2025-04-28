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
        if (context == null) return true;
        
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return true;

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return capabilities == null || (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) &&
                !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
                !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }
}