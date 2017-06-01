package com.mdj.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ScrollView;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/13/14.
 */
public class NestScrollView extends ScrollView {
    private static final String TAG = NestScrollView.class.getSimpleName();

    public NestScrollView(Context context) {
        super(context);
    }

    public NestScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
//        if (MotionEvent.ACTION_MOVE == ev.getAction()) {
//            return true;
//        }
//        return false;
    }

    float lastDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownY = event.getY();
                parentRequestDisallowInterceptTouchEvent(false); // 保证事件可往下传递
                return true;
//                break;
            case MotionEvent.ACTION_MOVE:
                boolean isTop = event.getY() - lastDownY > 0 && this.getScrollY() == 0;
                if (isTop) { // 允许父控件拦截，即不允许父控件拦截设为false
                    parentRequestDisallowInterceptTouchEvent(false);
                    return false;
                } else { // 不允许父控件拦截，即不允许父控件拦截设为true
                    parentRequestDisallowInterceptTouchEvent(true);
                    return true;
                }
//                break;
            case MotionEvent.ACTION_UP:
                parentRequestDisallowInterceptTouchEvent(false); // 保证事件可往下传递
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    /**
     * 是否允许父控件拦截事件
     * @param disallowIntercept
     */
    private void parentRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent vp = getParent();
        if (null == vp) {
            return;
        }
        vp.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

}