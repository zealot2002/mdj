package com.mdj.moudle.order.presenter;

import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.beautyParlor.BeautyParlorBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.bean.OrderListWrapper;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.MdjLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderPresenter implements OrderContract.Presenter{
    private final OrderContract.View view;
    private Map<Integer,OrderListWrapper> orderListWrapperMap = new HashMap<>();
    private boolean isProcessing = false;

/****************************************************************************************************/
    public OrderPresenter(@NonNull OrderContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }
    @Override
    public void clearOrderList() {
        orderListWrapperMap.clear();
    }
    @Override
    public void refreshMyOrderList(int orderType) {
        if(orderListWrapperMap.containsKey(orderType)){
            orderListWrapperMap.get(orderType).clear();
        }else{
            orderListWrapperMap.put(orderType,new OrderListWrapper());
        }
        getMyOrderList(orderType, ++orderListWrapperMap.get(orderType).pageIndex, false);
    }
    @Override
    public void getMyOrderList(int orderType) {
        getMyOrderList(orderType,true);
    }

    private void getMyOrderList(int orderType,boolean showLoading) {
        OrderListWrapper orderListWrapper;
        if(orderListWrapperMap.containsKey(orderType)){
            orderListWrapper = orderListWrapperMap.get(orderType);
        }else{
            orderListWrapper = new OrderListWrapper();
            orderListWrapperMap.put(orderType,orderListWrapper);
        }
        if (orderListWrapper.dataList.size()>0
                ||orderListWrapper.isNoMoreData
                ){
            view.refreshList(orderType,orderListWrapper.dataList); //直接返回
        }else{
            getMyOrderList(orderType,++orderListWrapper.pageIndex,showLoading);
        }
    }

    private void getMyOrderList(int orderType, int pageIndex,boolean showLoading) {
        MdjLog.log("getMyOrderList");
        isProcessing = true;
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            if(showLoading){
                view.showLoading();
            }
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("orderType", orderType+"");
            map.put("uId", CacheManager.getInstance().getUserBean().getId());

            map.put("pageStart",pageIndex+"");
            map.put("pageSize", CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_MY_ORDER_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getMyOrderListDataCallback)
                    .jsonParser(getMyOrderListJsonParser)
                    .tagObj(orderType)
                    .build();
            try {
                HttpUtil.getInstance().request(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            view.showDisconnect("请检查您的网络再试");
        }
    }

    HttpInterface.DataCallback getMyOrderListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            int orderType = (int)tagObj;
            if(result == HttpConstant.SUCCESS){
                List<OrderBean> dataList = (List<OrderBean>)data;
                if(dataList.isEmpty()||dataList.size()<CommonConstant.PAGE_SIZE){
                    orderListWrapperMap.get(orderType).isNoMoreData = true;
                }
                orderListWrapperMap.get(orderType).dataList.addAll((List<OrderBean>)data);
                view.refreshList(orderType, orderListWrapperMap.get(orderType).dataList);
            }else{
                if(orderListWrapperMap.get(orderType).pageIndex>0){
                    orderListWrapperMap.get(orderType).pageIndex--;
                }
                view.showDisconnect(data.toString());
                /*也要刷新一下*/
                view.refreshList(orderType,orderListWrapperMap.get(orderType).dataList);
            }
            isProcessing = false;
        }
    };
    HttpInterface.JsonParser getMyOrderListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<OrderBean> dataList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray orderArray = obj.getJSONArray("data");

                for(int i=0;i<orderArray.length();i++){
                    JSONObject object = orderArray.getJSONObject(i);
                    JSONArray projectArray = object.getJSONArray("projectList");

                    List<String> projcetList = new ArrayList<>();
                    for(int j=0;j<projectArray.length();j++){
                        projcetList.add(projectArray.getJSONObject(j).getString("service_name"));
                    }
                    OrderBean bean = new OrderBean.Builder(object.getString("orderSn"))
                            .serviceStartTime(object.getString("serviceStartTime"))
                            .price(object.getString("realIncome"))
                            .serviceType(Integer.valueOf(object.getString("orderType")))
                            .state(Integer.valueOf(object.getString("orderState")))
                            .beauticianImgUrl(
                                    HttpConstant.FILE_SERVER_URL +
                                            object.getString("beautyPhoto"))
                            .createTime(object.getString("addTime"))
                            .beauticianName(object.getString("beautyName"))
                            .beauticianId(object.getString("beautyId"))
                            .payType(object.getString("payType"))
                            .projectListStr(object.getString("projectName"))
                            .projectList(projcetList)
                            .mainProjectId(object.getString("projectId"))
                            .build();
                    dataList.add(bean);
                }
            } else {
                String msg = obj.getString("msg");
                if(errno == 10005){
                    msg = "请先登录";
                }
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };

    @Override
    public void loadMoreData(int orderType) {
        if(isProcessing){
            return;
        }
        if(!orderListWrapperMap.get(orderType).isNoMoreData){
            getMyOrderList(orderType, ++orderListWrapperMap.get(orderType).pageIndex,true);
        }
    }

    @Override
    public void finishOrder(String orderId) {
        MdjLog.log("finishOrder");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("orderSn",orderId);
            map.put("userId", CacheManager.getInstance().getUserBean().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.FINISH_ORDER;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_PUT, url)
                    .callback(finishOrderDataCallback)
                    .jsonParser(finishOrderJsonParser)
                    .build();
            try {
                HttpUtil.getInstance().request(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            view.showDisconnect("请检查您的网络再试");
        }
    }
    HttpInterface.DataCallback finishOrderDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
//                //clear cache
//                CacheManager.getInstance().getOrderListWrapperMap().clear();
//                //再次获取
//                getMyOrderList(orderListWrapper.orderType);
                view.finishOrderDone();
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser finishOrderJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {

            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,"errno:"+errno+" msg:"+msg};
            }
            return new Object[]{HttpConstant.SUCCESS,""};
        }
    };

    @Override
    public void getOrderDetail(String orderId) {
        MdjLog.log("getOrderDetail");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("orderSn",orderId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.ORDER_DETAIL;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getOrderDetailDataCallback)
                    .jsonParser(getOrderDetailJsonParser)
                    .build();
            try {
                HttpUtil.getInstance().request(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            view.showDisconnect("请检查您的网络再试");
        }
    }



    HttpInterface.DataCallback getOrderDetailDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateOrderDetail((OrderBean) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getOrderDetailJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            OrderBean orderBean = null;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                BeautyParlorBean beautyParlorBean = null;
                if(dataObj.has("experienceShopInfo")&&(dataObj.getJSONObject("experienceShopInfo").has("name"))){
                    beautyParlorBean = new BeautyParlorBean.Builder()
                            .id(dataObj.getJSONObject("experienceShopInfo").getString("id"))
                            .name(dataObj.getJSONObject("experienceShopInfo").getString("name"))
                            .imgUrl(dataObj.getJSONObject("experienceShopInfo").getString("logo"))
                            .address(dataObj.getJSONObject("experienceShopInfo").getString("address"))
                            .build();;
                }
                AddressBean addressBean = null;
                if(dataObj.has("addressInfo")&&(dataObj.getJSONObject("addressInfo").has("userName"))){  /*上门*/
                    addressBean = new AddressBean(
                            dataObj.getJSONObject("addressInfo").getString("addressId"),
                            dataObj.getJSONObject("addressInfo").getString("userName"),
                            dataObj.getJSONObject("addressInfo").getString("userPhone"),
                            dataObj.getJSONObject("addressInfo").getString("address"),
                            dataObj.getJSONObject("addressInfo").getString("latitude"),
                            dataObj.getJSONObject("addressInfo").getString("longitude"),
                            dataObj.getJSONObject("addressInfo").getString("cityId")
                    );
                }else {
                    addressBean = new AddressBean(dataObj.getString("acceptServicerName"),
                            dataObj.getString("acceptServicerPhone"),
                            dataObj.getString("serviceAddress")
                            );
                }
                orderBean = new OrderBean.Builder(dataObj.getString("orderSn"))
                        .state(dataObj.getInt("orderState"))
                        .serviceType(Integer.valueOf(dataObj.getString("orderType")))
                        .createTime(dataObj.getString("addTime"))
                        .isShowReferee(Integer.valueOf(dataObj.getString("isRecommend")))
                        .beauticianId(dataObj.getString("beautyId"))
                        .beauticianName(dataObj.getString("beautyName"))
                        .beauticianImgUrl(HttpConstant.FILE_SERVER_URL + dataObj.getString("beautyPhoto"))
                        .price(dataObj.getString("realIncome"))
                        .cancelAdd(dataObj.getInt("cancelAdd"))
                        .projectListStr(dataObj.getString("projectName"))
                        .serviceStartTime(dataObj.getString("serviceStartTime"))
                        .serviceEndTime(dataObj.getString("serviceEndTime"))
                        .remarks(dataObj.getString("postscript"))
                        .addressBean(addressBean)
                        .beautyParlorBean(beautyParlorBean)
                        .orderStatusRemark(dataObj.getString("orderStatusRemark"))
                        .mainProjectId(dataObj.getString("projectId"))
                        .build();
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,"errno:"+errno+" msg:"+msg};
            }
            return new Object[]{HttpConstant.SUCCESS,orderBean};
        }
    };

    @Override
    public void cancelOrder(String orderId, String reason) {
        MdjLog.log("cancelOrder");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("uId",CacheManager.getInstance().getUserBean().getId());
            map.put("orderSn", orderId);
            map.put("cancelReason",reason);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.CANCEL_ORDER;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_PUT, url)
                    .callback(cancelOrderDataCallback)
                    .jsonParser(cancelOrderJsonParser)
                    .build();
            try {
                HttpUtil.getInstance().request(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            view.showDisconnect("请检查您的网络再试");
        }
    }

    HttpInterface.DataCallback cancelOrderDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.cancelOrderDone();
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser cancelOrderJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {

            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,"errno:"+errno+" msg:"+msg};
            }
            return new Object[]{HttpConstant.SUCCESS,""};
        }
    };
}
