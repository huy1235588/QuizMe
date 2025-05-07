package com.huy.QuizMe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.huy.QuizMe.data.model.User;

/**
 * Quản lý lưu trữ dữ liệu người dùng và token đăng nhập sử dụng SharedPreferences
 */
public class SharedPreferencesManager {
    private static final String TAG = "SharedPrefsManager";
    private static final String PREF_NAME = "quiz_me_prefs";
    
    // Keys for SharedPreferences
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER = "user";
    
    private static SharedPreferencesManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    
    private SharedPreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    /**
     * Khởi tạo SharedPreferencesManager (phải gọi trong Application class)
     * 
     * @param context Application context
     */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (SharedPreferencesManager.class) {
                if (instance == null) {
                    instance = new SharedPreferencesManager(context);
                }
            }
        }
    }
    
    /**
     * Trả về instance của SharedPreferencesManager
     * 
     * @return SharedPreferencesManager instance
     */
    public static SharedPreferencesManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SharedPreferencesManager must be initialized with context first!");
        }
        return instance;
    }
    
    /**
     * Lưu access token vào SharedPreferences
     * 
     * @param token JWT access token
     */
    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }
    
    /**
     * Lấy access token từ SharedPreferences
     * 
     * @return JWT access token hoặc null nếu chưa đăng nhập
     */
    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }
    
    /**
     * Xóa access token khỏi SharedPreferences
     */
    public void clearAuthToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_AUTH_TOKEN);
        editor.apply();
    }
    
    /**
     * Lưu refresh token vào SharedPreferences
     * 
     * @param token JWT refresh token
     */
    public void saveRefreshToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_REFRESH_TOKEN, token);
        editor.apply();
    }
    
    /**
     * Lấy refresh token từ SharedPreferences
     * 
     * @return JWT refresh token hoặc null nếu chưa đăng nhập
     */
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }
    
    /**
     * Xóa refresh token khỏi SharedPreferences
     */
    public void clearRefreshToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_REFRESH_TOKEN);
        editor.apply();
    }
    
    /**
     * Lưu thông tin người dùng vào SharedPreferences (dạng JSON)
     * 
     * @param user Đối tượng người dùng cần lưu
     */
    public void saveUser(User user) {
        if (user == null) {
            return;
        }
        
        SharedPreferences.Editor editor = prefs.edit();
        try {
            String userJson = gson.toJson(user);
            editor.putString(KEY_USER, userJson);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user to SharedPreferences: " + e.getMessage());
        }
        editor.apply();
    }
    
    /**
     * Lấy thông tin người dùng từ SharedPreferences
     * 
     * @return Đối tượng User hoặc null nếu chưa đăng nhập
     */
    public User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson == null) {
            return null;
        }
        
        try {
            return gson.fromJson(userJson, User.class);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing user from SharedPreferences: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Xóa thông tin người dùng khỏi SharedPreferences
     */
    public void clearUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USER);
        editor.apply();
    }
    
    /**
     * Xóa tất cả dữ liệu trong SharedPreferences
     */
    public void clearAll() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}