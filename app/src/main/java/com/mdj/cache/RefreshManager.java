package com.mdj.cache;

import com.lidroid.xutils.util.OtherUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tt on 2016/9/1.
 */
public class RefreshManager {
    //需要刷新的界面
    public static final String ORDER_LIST_FRAGMENT = "com.mdj.moudle.order.MyOrderListFragment";
    public static final String ORDER_DETAIL_ACTIVITY = "com.mdj.moudle.order.OrderDetailActivity";
//    public static final String ORDER_LIST_FRAGMENT = "com.mdj.moudle.order.MyOrderListFragment";
//    public static final String ORDER_LIST_FRAGMENT = "com.mdj.moudle.order.MyOrderListFragment";
//    public static final String ORDER_LIST_FRAGMENT = "com.mdj.moudle.order.MyOrderListFragment";
//    public static final String ORDER_LIST_FRAGMENT = "com.mdj.moudle.order.MyOrderListFragment";

    private static Map<String ,Boolean> refreshMap = new HashMap<>();
    public static boolean needRefresh(){
        String callerClazzName = OtherUtils.getCallerStackTraceElement().getClassName();
        return refreshMap.containsKey(callerClazzName)?refreshMap.get(callerClazzName):false;
    }
    public static void setNeedRefreshFlag(){
        String callerClazzName = OtherUtils.getCallerStackTraceElement().getClassName();
        refreshMap.put(callerClazzName,true);
    }
    public static void setNeedRefreshFlag(String screen){
        refreshMap.put(screen,true);
    }
    public static void clearNeedRefreshFlag(){
        String callerClazzName = OtherUtils.getCallerStackTraceElement().getClassName();
        refreshMap.put(callerClazzName,false);
    }

}
