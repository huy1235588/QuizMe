package com.huy.QuizMe.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Lớp tiện ích quản lý ngôn ngữ ứng dụng
 * Hỗ trợ chuyển đổi giữa tiếng Anh và tiếng Việt
 */
public class LanguageUtils {
    private static SharedPreferencesManager prefsManager = null;

    // Constants cho các ngôn ngữ được hỗ trợ
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_VIETNAMESE = "vi";

    // Key để lưu trữ ngôn ngữ trong SharedPreferences
    private static final String PREF_KEY_LANGUAGE = "app_language";

    public LanguageUtils(SharedPreferencesManager prefsManager) {
        LanguageUtils.prefsManager = prefsManager;
    }

    /**
     * Lấy ngôn ngữ hiện tại từ SharedPreferences
     * Mặc định là tiếng Anh nếu chưa được thiết lập
     *
     * @return Mã ngôn ngữ hiện tại
     */
    public static String getCurrentLanguage() {
        return prefsManager.getLanguage();
    }

    /**
     * Lưu ngôn ngữ được chọn vào SharedPreferences
     *
     * @param languageCode Mã ngôn ngữ cần lưu
     */
    public static void saveLanguage(String languageCode) {
        if (isSupportedLanguage(languageCode)) {
            prefsManager.saveLanguage(languageCode);
        } else {
            throw new IllegalArgumentException("Unsupported language code: " + languageCode);
        }
    }

    /**
     * Áp dụng ngôn ngữ cho Context
     *
     * @param context      Context cần áp dụng ngôn ngữ
     * @param languageCode Mã ngôn ngữ
     * @return Context đã được cập nhật ngôn ngữ
     */
    public static Context setAppLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(configuration);
        } else {
            Resources resources = context.getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, displayMetrics);
            return context;
        }
    }

    /**
     * Áp dụng ngôn ngữ đã được lưu cho Context
     *
     * @param context Context cần áp dụng ngôn ngữ
     * @return Context đã được cập nhật ngôn ngữ
     */
    public static Context setAppLanguage(Context context) {
        String languageCode = getCurrentLanguage();
        return setAppLanguage(context, languageCode);
    }

    /**
     * Kiểm tra xem ngôn ngữ có được hỗ trợ hay không
     *
     * @param languageCode Mã ngôn ngữ cần kiểm tra
     * @return true nếu được hỗ trợ, false nếu không
     */
    public static boolean isSupportedLanguage(String languageCode) {
        return LANGUAGE_ENGLISH.equals(languageCode) ||
                LANGUAGE_VIETNAMESE.equals(languageCode);
    }

    /**
     * Lấy tên hiển thị của ngôn ngữ
     *
     * @param context      Context để lấy string resource
     * @param languageCode Mã ngôn ngữ
     * @return Tên hiển thị của ngôn ngữ
     */
    public static String getLanguageDisplayName(Context context, String languageCode) {
        switch (languageCode) {
            case LANGUAGE_ENGLISH:
                return "English";
            case LANGUAGE_VIETNAMESE:
                return "Tiếng Việt";
            default:
                return "English";
        }
    }

    /**
     * Chuyển đổi ngôn ngữ và lưu vào SharedPreferences
     *
     * @param context      Context hiện tại
     * @param languageCode Mã ngôn ngữ mới
     */
    public static void changeLanguage(Context context, String languageCode) {
        if (isSupportedLanguage(languageCode)) {
            saveLanguage(languageCode);
            setAppLanguage(context, languageCode);
        }
    }

    /**
     * Lấy danh sách các ngôn ngữ được hỗ trợ
     *
     * @return Mảng các mã ngôn ngữ được hỗ trợ
     */
    public static String[] getSupportedLanguages() {
        return new String[]{LANGUAGE_ENGLISH, LANGUAGE_VIETNAMESE};
    }

    /**
     * Lấy danh sách tên hiển thị của các ngôn ngữ được hỗ trợ
     *
     * @param context Context để lấy string resource
     * @return Mảng tên hiển thị của các ngôn ngữ
     */
    public static String[] getSupportedLanguageNames(Context context) {
        return new String[]{
                getLanguageDisplayName(context, LANGUAGE_ENGLISH),
                getLanguageDisplayName(context, LANGUAGE_VIETNAMESE)
        };
    }

    public void applyLanguage(Context context, String languageCode) {
        if (isSupportedLanguage(languageCode)) {
            saveLanguage(languageCode);
            setAppLanguage(context, languageCode);
        }
    }
}
