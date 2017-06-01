package com.mdj.view.button;

import android.view.View;

import java.util.Calendar;

/**
 * Created by tt on 2016/9/28.
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    public abstract void onNoDoubleClick(View v);
    /**************************************************************************************************/
    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }
}