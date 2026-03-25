package com.example.homesweethome.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.example.homesweethome.R;

public class UiUtils {

    public static void showError(View root, String message) {
        Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(root.getContext().getColor(R.color.error));
        snackbar.setTextColor(root.getContext().getColor(R.color.white));
        snackbar.show();
    }

    public static void showSuccess(View root, String message) {
        Snackbar snackbar = Snackbar.make(root, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(root.getContext().getColor(R.color.success));
        snackbar.setTextColor(root.getContext().getColor(R.color.white));
        snackbar.show();
    }

    public static void showLoading(ProgressBar progressBar, MaterialButton button) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (button != null) button.setEnabled(false);
    }

    public static void hideLoading(ProgressBar progressBar, MaterialButton button) {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (button != null) button.setEnabled(true);
    }

    public static void showDialog(Context context, String title, String message) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void showConfirmDialog(Context context, String title, String message,
                                         Runnable onConfirm) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Cancel", null)
                .show();
    }
}
