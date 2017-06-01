package com.mdj.moudle.order.util;

import com.mdj.constant.CommonConstant;

/**
 * Created by tt on 2016/8/15.
 */
public class OrderHelper {
    /*
    * 给出状态文案
    * */
    public static String getOrderStateStr(int state){
        switch (state){
            case CommonConstant.ORDER_STATUS_WAIT_TO_CHARGE:return "待支付";
            case CommonConstant.ORDER_STATUS_CHARGE_SUCCESS:return "支付成功";
            case CommonConstant.ORDER_STATUS_BEAUTY_ALREADY_GO:return "美容师已出发";
            case CommonConstant.ORDER_STATUS_BEAUTY_ARRIVED:return "美容师已到达";
            case CommonConstant.ORDER_STATUS_SERVICE_OVER:return "待用户确认";
            case CommonConstant.ORDER_STATUS_WAIT_TO_COMMENT:return "待评价";
            case CommonConstant.ORDER_STATUS_COMMENT_OVER:return "已评价";
            case CommonConstant.ORDER_STATUS_FAIL_TO_CHARGE:return "支付失败";
            case CommonConstant.ORDER_STATUS_CANCELED_BY_CUSTOMER:return "待退款";
            case CommonConstant.ORDER_STATUS_DRAWBACK_SUCCESS:return "退款成功";
            case CommonConstant.ORDER_STATUS_ERROR:
            default:return "异常状态:"+state;
        }
    }
    /*
        * 给出operation button文案
        *
        * 订单列表专用
        * */
    public static String getOrderListStateOperationStr(int state){
        switch (state){
            case CommonConstant.ORDER_STATUS_WAIT_TO_CHARGE:return "去支付";
            case CommonConstant.ORDER_STATUS_CHARGE_SUCCESS:return "";
            case CommonConstant.ORDER_STATUS_BEAUTY_ALREADY_GO:return "";
            case CommonConstant.ORDER_STATUS_BEAUTY_ARRIVED:return "";
            case CommonConstant.ORDER_STATUS_SERVICE_OVER:return "确认";
            case CommonConstant.ORDER_STATUS_WAIT_TO_COMMENT:return "评价";

            case CommonConstant.ORDER_STATUS_COMMENT_OVER:
            case CommonConstant.ORDER_STATUS_FAIL_TO_CHARGE:
            case CommonConstant.ORDER_STATUS_CANCELED_BY_CUSTOMER:
            case CommonConstant.ORDER_STATUS_DRAWBACK_SUCCESS:return "再次预约";

            case CommonConstant.ORDER_STATUS_ERROR:
            default:return "异常状态:"+state;
        }
    }
    /*
    * 给出operation button文案
    *
    * 详情专用
    * */
    public static String getOrderDetailStateOperationStr(int state){
        switch (state){
            case CommonConstant.ORDER_STATUS_WAIT_TO_CHARGE:return "去支付";

            case CommonConstant.ORDER_STATUS_CHARGE_SUCCESS:
            case CommonConstant.ORDER_STATUS_BEAUTY_ALREADY_GO:
            case CommonConstant.ORDER_STATUS_BEAUTY_ARRIVED:return "进行中";

            case CommonConstant.ORDER_STATUS_SERVICE_OVER:return "确认";
            case CommonConstant.ORDER_STATUS_WAIT_TO_COMMENT:return "评价";

            case CommonConstant.ORDER_STATUS_COMMENT_OVER:
            case CommonConstant.ORDER_STATUS_FAIL_TO_CHARGE:
            case CommonConstant.ORDER_STATUS_CANCELED_BY_CUSTOMER:
            case CommonConstant.ORDER_STATUS_DRAWBACK_SUCCESS:return "再次预约";

            case CommonConstant.ORDER_STATUS_ERROR:
            default:return "异常状态:"+state;
        }
    }
}
