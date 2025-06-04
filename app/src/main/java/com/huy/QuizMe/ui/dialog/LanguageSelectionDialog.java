package com.huy.QuizMe.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.huy.QuizMe.R;
import com.huy.QuizMe.utils.LanguageUtils;

/**
 * Dialog chọn ngôn ngữ cho ứng dụng
 */
public class LanguageSelectionDialog {

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(String languageCode);
    }

    private Context context;
    private OnLanguageSelectedListener listener;
    private Dialog dialog;

    public LanguageSelectionDialog(Context context, OnLanguageSelectedListener listener) {
        this.context = context;
        this.listener = listener;
        createDialog();
    }

    private void createDialog() {
        String[] languages = LanguageUtils.getSupportedLanguages();
        String[] languageNames = LanguageUtils.getSupportedLanguageNames(context);
        String currentLanguage = LanguageUtils.getCurrentLanguage();

        // Tìm vị trí của ngôn ngữ hiện tại
        int checkedItem = 0;
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(currentLanguage)) {
                checkedItem = i;
                break;
            }
        }

        dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.language)
                .setSingleChoiceItems(languageNames, checkedItem, (dialogInterface, which) -> {
                    String selectedLanguage = languages[which];
                    if (listener != null) {
                        listener.onLanguageSelected(selectedLanguage);
                    }
                    dialogInterface.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .create();
    }

    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
