package com.huy.QuizMe.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    /**
     * Chuyển đổi URI thành File
     *
     * @param context Context của ứng dụng
     * @param uri     URI của file
     * @return File object hoặc null nếu có lỗi
     */
    public static File getFileFromUri(Context context, Uri uri) {
        try {
            // Lấy tên file
            String fileName = getFileName(context, uri);
            if (fileName == null) {
                fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
            }

            // Tạo file tạm thời
            File tempFile = new File(context.getCacheDir(), fileName);

            // Copy dữ liệu từ URI vào file tạm thời
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                return null;
            }

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tên file từ URI
     *
     * @param context Context của ứng dụng
     * @param uri     URI của file
     * @return Tên file hoặc null nếu không lấy được
     */
    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    /**
     * Lấy đường dẫn thực của file từ URI (dành cho Android < 10)
     *
     * @param context Context của ứng dụng
     * @param uri     URI của file
     * @return Đường dẫn file hoặc null nếu không lấy được
     */
    @Deprecated
    public static String getRealPathFromURI(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}
