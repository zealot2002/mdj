package com.mdj.moudle.widget.sale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.moudle.widget.TextCountDownWidget;
import com.mdj.utils.DateUtil;
import com.mdj.utils.ToastUtils;

/**
 * 特惠商品倒计时widget
 */
public class SaleCountDownWidget extends LinearLayout{
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;

    private TextView tvPerCount;
    /*not ongoing*/
    private LinearLayout llNotSaling;
    private TextView tvPrivilegePrice,tvNote;

    /*ongoing*/
    private LinearLayout llOnSaling;
    private TextView tvSalePercent,tvSaleNum;
    private ProgressBar progressBar;
    private TextCountDownWidget textCountDownWidget;


/********************************************************************************************************/
    public SaleCountDownWidget(Context context){
        this(context, null);
    }
    public SaleCountDownWidget(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public SaleCountDownWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.sale_count_down_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);
        initView();
    }

    private void initView(){
        llNotSaling = (LinearLayout)findViewById(R.id.llNotSaling);
        tvPrivilegePrice = (TextView)findViewById(R.id.tvPrivilegePrice);
        tvNote = (TextView)findViewById(R.id.tvNote);

        llOnSaling = (LinearLayout)findViewById(R.id.llOnSaling);
        tvSalePercent = (TextView)findViewById(R.id.tvSalePercent);
        tvSaleNum = (TextView)findViewById(R.id.tvSaleNum);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        textCountDownWidget = (TextCountDownWidget)findViewById(R.id.textCountDownWidget);

        tvPerCount = (TextView)findViewById(R.id.tvPerCount);
    }

    public void show(SaleBean bean){
        try{
            String now = DateUtil.getCurrentTime();
            if(DateUtil.compareDate(now, bean.getStartTime())==-1){//当前时间小于开始时间、未开始
                setNotOnSalingText(bean.getPrivilegePrice() + "元特惠",
                        bean.getStartTime() + "开抢", context.getResources().getColor(R.color.text_black));
            }else if(DateUtil.compareDate(now,bean.getEndTime())==1){//当前时间大于结束时间、已结束
                setNotOnSalingText(bean.getPrivilegePrice() + "元特惠",
                        bean.getStartTime() + "  已结束  ", context.getResources().getColor(R.color.white));
            }else{
                int soldNum = Integer.valueOf(bean.getSoldNum());
                int totalNum = Integer.valueOf(bean.getTotal());
                if(soldNum<totalNum){//没有抢完
                    int salePercent = 100*soldNum/totalNum;
                    String salePercentStr = "已抢"+salePercent+"%";
                    String saleNumStr = "已抢"+bean.getSoldNum()+"份";

                    setOnSalingText(salePercentStr, saleNumStr,
                            salePercent, bean.getEndTime());
                }else{//倒计时中，但是已经抢完，所以显示已抢完，并隐藏进度条
                    setNotOnSalingText(bean.getPrivilegePrice() + "元特惠",
                            "  已抢光  ", context.getResources().getColor(R.color.white));
                }
            }
            tvPerCount.setText("(每人限购"+bean.getPerCount()+"份)");
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showLong(context,"SaleCountDownWidget err:"+e.toString());
        }
    }
    /*not onSaling*/
    public void setNotOnSalingText(String privilegePrice,String note,int noteColor){
        llOnSaling.setVisibility(GONE);
        llNotSaling.setVisibility(VISIBLE);
        tvPrivilegePrice.setText(privilegePrice);
        tvNote.setText(note);
        tvNote.setTextColor(noteColor);
    }

    /*onSaling*/
    public void setOnSalingText(String salePercentStr,String saleNum,int salePercent,String endTime){
        llNotSaling.setVisibility(GONE);
        llOnSaling.setVisibility(VISIBLE);
        tvSalePercent.setText(salePercentStr);
        tvSaleNum.setText(saleNum);
        progressBar.setProgress(salePercent);
        textCountDownWidget.setEndTime(endTime);
        textCountDownWidget.start();
    }
}
