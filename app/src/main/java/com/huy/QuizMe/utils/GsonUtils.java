package com.huy.QuizMe.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Lớp tiện ích để xử lý chuyển đổi JSON với Gson
 */
public class GsonUtils {
    private static final String TAG = "GsonUtils";
    private static final Gson gson;
    private static final Gson prettyGson;

    static {
        // Cấu hình Gson với các adapter tùy chỉnh
        gson = new GsonBuilder().create();

        // Cấu hình Gson với chức năng in đẹp
        prettyGson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Chuyển đối tượng thành chuỗi JSON
     *
     * @param object Đối tượng cần chuyển đổi
     * @return Chuỗi JSON hoặc null nếu có lỗi
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return gson.toJson(object);
        } catch (Exception e) {
            Log.e(TAG, "Error converting object to JSON", e);
            return null;
        }
    }

    /**
     * Chuyển đối tượng thành chuỗi JSON có định dạng đẹp
     *
     * @param object Đối tượng cần chuyển đổi
     * @return Chuỗi JSON có định dạng đẹp hoặc null nếu có lỗi
     */
    public static String toJsonPretty(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return prettyGson.toJson(object);
        } catch (Exception e) {
            Log.e(TAG, "Error converting object to pretty JSON", e);
            return null;
        }
    }

    /**
     * Chuyển chuỗi JSON thành đối tượng Java
     *
     * @param json     Chuỗi JSON
     * @param classOfT Lớp của đối tượng cần chuyển đổi
     * @return Đối tượng Java hoặc null nếu có lỗi
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (json == null || json.isEmpty() || classOfT == null) {
            return null;
        }

        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Invalid JSON syntax: " + json, e);
            return null;
        } catch (JsonParseException e) {
            Log.e(TAG, "Error parsing JSON: " + json, e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error parsing JSON", e);
            return null;
        }
    }

    /**
     * Chuyển chuỗi JSON thành đối tượng Java với Type cụ thể
     * (Hữu ích cho các generic type như List<T>)
     *
     * @param json    Chuỗi JSON
     * @param typeOfT Type của đối tượng cần chuyển đổi
     * @return Đối tượng Java hoặc null nếu có lỗi
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        if (json == null || json.isEmpty() || typeOfT == null) {
            return null;
        }

        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Invalid JSON syntax for type: " + typeOfT, e);
            return null;
        } catch (JsonParseException e) {
            Log.e(TAG, "Error parsing JSON for type: " + typeOfT, e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error parsing JSON for type: " + typeOfT, e);
            return null;
        }
    }

    /**
     * Kiểm tra xem một chuỗi có phải là JSON hợp lệ hay không
     *
     * @param json Chuỗi cần kiểm tra
     * @return true nếu là JSON hợp lệ, false nếu không phải
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }

        try {
            gson.fromJson(json, Object.class);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }
}
