package com.mdj.utils;

import android.view.View;
import android.view.ViewGroup;

import com.mdj.application.MyApp;

/**
 * Created by tt on 2016/9/22.
 */
public class ViewUtils {
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(CommonUtil.dip2px(MyApp.getInstance(),l),
                    CommonUtil.dip2px(MyApp.getInstance(),t),
                    CommonUtil.dip2px(MyApp.getInstance(),r),
                    CommonUtil.dip2px(MyApp.getInstance(),b));
            v.requestLayout();
        }
    }
}
