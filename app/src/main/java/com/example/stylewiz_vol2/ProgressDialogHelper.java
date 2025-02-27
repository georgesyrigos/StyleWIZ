package com.example.stylewiz_vol2;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class ProgressDialogHelper {
    private AlertDialog progressDialog;
    private final Context context;


    // Constructor accepting Context
    public ProgressDialogHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
    }

    public void showProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomProgressDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_dialog, null);
        builder.setView(view);
        builder.setCancelable(false); // Prevent dismissal on outside touch
        progressDialog = builder.create();

        // Make background transparent
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.show();

    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
