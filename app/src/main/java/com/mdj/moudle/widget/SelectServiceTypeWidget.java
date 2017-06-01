package com.mdj.moudle.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.constant.CommonConstant;

/**
 * Created by tt on 2016/6/27.
 */
public class SelectServiceTypeWidget extends LinearLayout implements View.OnClickListener{
    public interface OnSelectedListener {
        void onSelected(int serviceType);
    }
    private OnSelectedListener listener;
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;
    private RelativeLayout rlLeft,rlRight;
    private ImageView ivLeft,ivRight;
    private TextView tvLeft,tvRight;

    private int currentSelectedType = CommonConstant.SERVICE_TYPE_IN_HOME;
/********************************************************************************************************/
    public SelectServiceTypeWidget(Context context){
        this(context,null);
    }
    public SelectServiceTypeWidget(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public SelectServiceTypeWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.select_service_type_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);
        initView();
    }

    public void setOnSelectedListener(OnSelectedListener listener){
        this.listener = listener;
    }
    public void setServiceType(int serviceType){
        if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME) {
            rlLeft.setBackgroundResource(R.drawable.select_service_type_left_selected);
            ivLeft.setBackgroundResource(R.mipmap.location_white);
            tvLeft.setTextColor(getResources().getColor(R.color.white));

            rlRight.setBackgroundResource(R.drawable.select_service_type_right_normal);
            ivRight.setBackgroundResource(R.mipmap.address);
            tvRight.setTextColor(getResources().getColor(R.color.text_black));

            currentSelectedType = CommonConstant.SERVICE_TYPE_IN_HOME;
        }else{
            rlLeft.setBackgroundResource(R.drawable.select_service_type_left_normal);
            ivLeft.setBackgroundResource(R.mipmap.location);
            tvLeft.setTextColor(getResources().getColor(R.color.text_black));

            rlRight.setBackgroundResource(R.drawable.select_service_type_right_selected);
            ivRight.setBackgroundResource(R.mipmap.address_white);
            tvRight.setTextColor(getResources().getColor(R.color.white));

            currentSelectedType = CommonConstant.SERVICE_TYPE_TO_SHOP;
        }
    }
    private void initView(){
        rlLeft = (RelativeLayout)findViewById(R.id.rlLeft);
        rlRight = (RelativeLayout)findViewById(R.id.rlRight);

        rlLeft.setOnClickListener(this);
        rlRight.setOnClickListener(this);

        ivLeft = (ImageView)findViewById(R.id.ivLeft);
        ivRight = (ImageView)findViewById(R.id.ivRight);

        tvLeft = (TextView)findViewById(R.id.tvLeft);
        tvRight = (TextView)findViewById(R.id.tvRight);
    }

    public int getCurrentSelectedType(){
        return currentSelectedType;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlLeft:
                if(currentSelectedType == CommonConstant.SERVICE_TYPE_IN_HOME){
                    return; //do nothing
                }
                rlLeft.setBackgroundResource(R.drawable.select_service_type_left_selected);
                ivLeft.setBackgroundResource(R.mipmap.location_white);
                tvLeft.setTextColor(getResources().getColor(R.color.white));

                rlRight.setBackgroundResource(R.drawable.select_service_type_right_normal);
                ivRight.setBackgroundResource(R.mipmap.address);
                tvRight.setTextColor(getResources().getColor(R.color.text_black));

                currentSelectedType = CommonConstant.SERVICE_TYPE_IN_HOME;
                if(listener!=null)
                    listener.onSelected(currentSelectedType);
                break;

            case R.id.rlRight:
                if(currentSelectedType == CommonConstant.SERVICE_TYPE_TO_SHOP){
                    return; //do nothing
                }
                rlLeft.setBackgroundResource(R.drawable.select_service_type_left_normal);
                ivLeft.setBackgroundResource(R.mipmap.location);
                tvLeft.setTextColor(getResources().getColor(R.color.text_black));

                rlRight.setBackgroundResource(R.drawable.select_service_type_right_selected);
                ivRight.setBackgroundResource(R.mipmap.address_white);
                tvRight.setTextColor(getResources().getColor(R.color.white));

                currentSelectedType = CommonConstant.SERVICE_TYPE_TO_SHOP;
                if(listener!=null)
                    listener.onSelected(currentSelectedType);
                break;

            default:
                break;
        }
    }
}
