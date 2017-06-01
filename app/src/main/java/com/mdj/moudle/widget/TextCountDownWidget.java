package com.mdj.moudle.widget;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.utils.DateUtil;

/**
 * 倒计时textview
 */
public class TextCountDownWidget extends LinearLayout{
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;

    private boolean mTickerStopped;
    private Runnable mTicker;
    private Handler mHandler;
    private String endTime;
    public static long distanceTime;

    private TextView tvDay,tvHour,tvMinute,tvSecond;
/*****************************************************************************************************/
    public TextCountDownWidget(Context context){
        this(context,null);
    }
    public TextCountDownWidget(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public TextCountDownWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        mTickerStopped = false;
        mHandler = new Handler();

        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.text_count_down_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);
        initView();
    }
    private void initView(){
        tvDay = (TextView)findViewById(R.id.tvDay);
        tvHour = (TextView)findViewById(R.id.tvHour);
        tvMinute = (TextView)findViewById(R.id.tvMinute);
        tvSecond = (TextView)findViewById(R.id.tvSecond);
    }
    private void setTime(String day,String hour,String minute,String second){
        tvDay.setText(day);
        tvHour.setText(hour);
        tvMinute.setText(minute);
        tvSecond.setText(second);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public void start(){
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                String currentTime = DateUtil.getCurrentTime();
                try {
                    distanceTime = DateUtil.getTimeDiffer(currentTime, endTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (distanceTime > 0) {
                    dealTime(distanceTime);
                }else {
                    setTime("0","00","00","00");
                }
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }
    private void dealTime(long between) {
        StringBuffer returnString = new StringBuffer();
        long days = between / (24 * 3600);
        long hours = between % (24 * 3600) / 3600;
        long minutes = between % 3600 / 60;
        long seconds = between % 60;

        String hoursStr = timeStrFormat(String.valueOf(hours));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(seconds));
        returnString.append(hoursStr).append(":").append(minutesStr).append(":").append(secondStr);
        setTime(days+"",hoursStr,minutesStr,secondStr);
    }
    private String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }
}
