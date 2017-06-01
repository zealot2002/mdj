package com.mdj.moudle.pay;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.DigitalClock;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/6/6.
 */
public class CustomDigitalClock extends DigitalClock {
    Calendar mCalendar;
    private final static String m12 = "h:mm aa";
    private final static String m24 = "k:mm";
    private FormatChangeObserver mFormatChangeObserver;
    private Runnable mTicker;
    private Handler mHandler;
    private long endTime = 0;
    public static long distanceTime;
    private ClockListener mClockListener;
    private static boolean isFirst;
    private boolean mTickerStopped;
    @SuppressWarnings("unused")
    private String mFormat;
    public CustomDigitalClock(Context context) {
        super(context);
        initClock(context);
    }
    public CustomDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }
    private void initClock(Context context) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);
        setFormat();
        setText("");
    }
    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
//        super.onAttachedToWindow();

        mHandler = new Handler();
/**
 * requests a tick on the next hard-second boundary
 */
//        start();
    }
    /*
    * 通过获取剩余时间， 更新倒计时；使之不依赖
    *
    * */
    public void start(){
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                long currentTime = System.currentTimeMillis();
                distanceTime = endTime - currentTime;
                distanceTime /= 1000;
                if (distanceTime == 0) {
                    setText("00:00:00");
                    onDetachedFromWindow();
                    /**倒计时结束时的回调**/
                    mClockListener.countDownFinsh();
                } else if (distanceTime < 0) {
                    setText("00:00:00");
                    onDetachedFromWindow();
                } else {
                    setText(dealTime(distanceTime));
                }
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }
    /**
     * deal time string
     * @param time
     * @return
     */

    public static String dealTime(long time) {
        StringBuffer returnString = new StringBuffer();
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        String hoursStr = timeStrFormat(String.valueOf(hours));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));
        returnString.append(hoursStr).append(":").append(minutesStr).append(":").append(secondStr);
        return returnString.toString();
    }
    /**
     * format time
     * @param timeStr
     * @return
     */
    private static String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }
    @Override
    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
        mTickerStopped = true;
    }
    /**
     * Clock end time from now on.
     * @param endTime
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }
    private void setFormat() {
//        if (get24HourMode()) {
            mFormat = m24;
//        } else {
//            mFormat = m12;
//        }
    }
    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }
        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }
    public void setClockListener(ClockListener clockListener) {
        this.mClockListener = clockListener;
    }
    public interface ClockListener {
        void countDownFinsh();
    }
}

