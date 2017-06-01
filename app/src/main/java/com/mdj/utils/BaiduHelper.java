package com.mdj.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by tt on 2016/8/24.
 */
public class BaiduHelper{
    public interface LocationEventListener{
        void onLocationEvent(boolean result,double curLng,double curLat,String addressStr,String cityName);
    }
    private static BaiduHelper instance = new BaiduHelper();
    private LocationClient mLocClient;
//    private LocationEventListener locationEventListener;
/*****************************************************************************************************/
    private BaiduHelper(){
//        init();
    }
    public static BaiduHelper getInstance(){
        return instance;
    }

    /*获取当前坐标点经纬度和名称*/
    public void getCurrentLocation(Context context,final LocationEventListener locationEventListener){
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                //反注册位置信息监听
                if (mLocClient != null) {
                    mLocClient.stop();
                }
                mLocClient.unRegisterLocationListener(this);
                //
                if (location == null) {
                    locationEventListener.onLocationEvent(false,0,0,"","");
                    return;
                }
                if(locationEventListener!=null){
                    locationEventListener.onLocationEvent(true,location.getLatitude(),location.getLongitude(),location.getAddrStr(),location.getCity());
                }
            }
        });

        //注册定位监听接口
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll"); //设置坐标类型
        option.setScanSpan(1000);
        option.setAddrType("all");
        option.setIsNeedAddress(true);
        option.setTimeOut(5);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }
    public void start(){

    }
}
