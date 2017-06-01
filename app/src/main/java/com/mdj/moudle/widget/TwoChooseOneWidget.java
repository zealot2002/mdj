package com.mdj.moudle.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;

/**
 * 二选一widget
 */
public class TwoChooseOneWidget extends LinearLayout implements View.OnClickListener{
    public interface OnSelectedListener {
        void onSelected(int position);
    }
    private OnSelectedListener listener;
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;
    private RelativeLayout rlLeft,rlRight;
    private TextView tvLeft,tvRight;

    private int currentSelected = 0;
/********************************************************************************************************/
    public TwoChooseOneWidget(Context context){
        this(context,null);
    }
    public TwoChooseOneWidget(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public TwoChooseOneWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.two_choose_one_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);
        initView();
    }
    public void setCustomerText(String leftStr,String rightStr){
        tvLeft.setText(leftStr);
        tvRight.setText(rightStr);
    }
    public void setOnSelectedListener(OnSelectedListener listener){
        this.listener = listener;
    }
    public void setServiceType(int position){
        if(position == 0) {
            rlLeft.setBackgroundResource(R.drawable.select_service_type_left_selected);
            tvLeft.setTextColor(getResources().getColor(R.color.white));
            rlRight.setBackgroundResource(R.drawable.select_service_type_right_normal);
            tvRight.setTextColor(getResources().getColor(R.color.text_gray));
        }else{
            rlLeft.setBackgroundResource(R.drawable.select_service_type_left_normal);
            tvLeft.setTextColor(getResources().getColor(R.color.text_black));
            rlRight.setBackgroundResource(R.drawable.select_service_type_right_selected);
            tvRight.setTextColor(getResources().getColor(R.color.white));
        }
        currentSelected = position;
    }
    private void initView(){
        rlLeft = (RelativeLayout)findViewById(R.id.rlLeft);
        rlRight = (RelativeLayout)findViewById(R.id.rlRight);

        rlLeft.setOnClickListener(this);
        rlRight.setOnClickListener(this);

        tvLeft = (TextView)findViewById(R.id.tvLeft);
        tvRight = (TextView)findViewById(R.id.tvRight);
    }

    public int getCurrentSelected(){
        return currentSelected;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlLeft:
                if(currentSelected == 0){
                    return; //do nothing
                }
                currentSelected = 0;
                rlLeft.setBackgroundResource(R.drawable.select_service_type_left_selected);
                tvLeft.setTextColor(getResources().getColor(R.color.white));
                rlRight.setBackgroundResource(R.drawable.select_service_type_right_normal);
                tvRight.setTextColor(getResources().getColor(R.color.text_gray));
                break;
            case R.id.rlRight:
                if(currentSelected == 1){
                    return; //do nothing
                }
                currentSelected = 1;
                rlLeft.setBackgroundResource(R.drawable.select_service_type_left_normal);
                tvLeft.setTextColor(getResources().getColor(R.color.text_black));
                rlRight.setBackgroundResource(R.drawable.select_service_type_right_selected);
                tvRight.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
        if(listener!=null)
            listener.onSelected(currentSelected);
    }
}
