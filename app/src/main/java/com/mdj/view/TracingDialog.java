package com.mdj.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.mdj.R;

/**
 * Created by tt on 2016/9/12.
 */
public class TracingDialog extends Dialog {
    public TracingDialog(Context mContext, int style) {
        super(mContext, style);
        setCancelable(false);
    }

    protected void onCreate(Bundle paramBundle) {
        applyCompat();
        setContentView(R.layout.loading_dialog);
    }
    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}