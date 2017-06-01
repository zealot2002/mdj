package com.mdj.moudle.order.serviceHour;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdj.R;

import java.util.List;

/*
* 服务时间选择小组件
* 它只与它的AddressWidgetAdapter通信（单向），不直接与activity通信
* 倒计时逻辑、手机号和验证码的基本容错，都在组件内部处理
* */
public class ServiceHourWidget extends LinearLayout implements View.OnClickListener, ServiceHourCallbacks {
    public interface ServiceHourWidgetEventListener extends ServiceHourCallbacks{
        void onServiceHourWidgetEvent();
    }
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;

    //hour
    private static final int HOUR_TAB_NUM = 9;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private ImageView ivLeft,ivRight;
    private List<ServiceHourBean> serviceHourBeanList;
    private ServiceHourWidgetEventListener serviceHourWidgetEventListener;

/***************************************************方法区**********************************************************/
    public ServiceHourWidget(Context context) {this(context, null);}
    public ServiceHourWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.service_hour_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);
    }
    public void setDataList(List<ServiceHourBean> serviceHourBeanList){
        this.serviceHourBeanList = serviceHourBeanList;
        initHourView();
    }

    public void setServiceHourWidgetEventListener(ServiceHourWidgetEventListener serviceHourWidgetEventListener){
        this.serviceHourWidgetEventListener = serviceHourWidgetEventListener;
    }
    private void refresh(){

    }
    private void initHourView(){
        ivLeft = (ImageView)findViewById(R.id.ivLeft);
        ivRight = (ImageView)findViewById(R.id.ivRight);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);

        mPagerAdapter = new NavigationAdapter(((FragmentActivity)context).getSupportFragmentManager(), this,serviceHourBeanList);
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(new SlidingTabLayout.CustomTabViewAdapter() {
            @Override
            public View getView(int position, ViewGroup parent) {
                View tabView;
                TextView tvWeek, tvDate;

                tabView = LayoutInflater.from(context).inflate(R.layout.hour_tab_indicator, parent, false);
                tvWeek = (TextView) tabView.findViewById(R.id.tvWeek);
                tvDate = (TextView) tabView.findViewById(R.id.tvDate);

                tvWeek.setText(serviceHourBeanList.get(position).getWeek());
                tvWeek.setTextColor(getResources().getColorStateList(R.color.black_red_selector));

                String date = serviceHourBeanList.get(position).getDate();
                tvDate.setText(date.substring(5,date.length()));
                tvDate.setTextColor(getResources().getColorStateList(R.color.black_red_selector));
                return tabView;
            }
        });

        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.red));
        slidingTabLayout.setViewPager(mPager);
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.transparent));
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                if (i > 1) {
                    ivLeft.setVisibility(View.VISIBLE);
                } else {
                    ivLeft.setVisibility(View.GONE);
                }
                if (i > HOUR_TAB_NUM - 5) {//一共9个，一屏幕显示5个，所以4
                    ivRight.setVisibility(View.GONE);
                } else {
                    ivRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int i) {
                serviceHourWidgetEventListener.onServiceHourWidgetEvent();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                int currentPosition = mPager.getCurrentItem();
                if(currentPosition>0)
                    mPager.setCurrentItem(--currentPosition);
                break;

            case R.id.ivRight:
                int currentPosition2 = mPager.getCurrentItem();
                if(currentPosition2<HOUR_TAB_NUM-1)//一共9个
                    mPager.setCurrentItem(++currentPosition2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSelected(ServiceHourBean serviceHourBean) {
        serviceHourWidgetEventListener.onSelected(serviceHourBean);
    }

    @Override
    public ServiceHourBean getLastSelectedServiceHourBean() {
        return serviceHourWidgetEventListener.getLastSelectedServiceHourBean();
    }
}
