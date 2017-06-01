package com.mdj.moudle.order.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.bean.RecommendCouponAndPackageBean;
import com.mdj.moudle.order.serviceHour.ServiceHourBean;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.MdjLog;
import com.mdj.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class ConfirmOrderPresenter implements ConfirmOrderContract.Presenter{
    private final ConfirmOrderContract.View view;
/***************************************************************************************************/
    public ConfirmOrderPresenter(@NonNull ConfirmOrderContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        view.updateLoginWidget(needLogin());
    }

    private boolean needLogin(){
        return TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId());
    }

    @Override
    public void getServiceHours(String projectParams, String location, String beautyParlorId,String beauticianId) {
        MdjLog.log("getServiceHours");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            if(!TextUtils.isEmpty(location)){
                map.put("location",location);
            }
            if(!TextUtils.isEmpty(beautyParlorId)){
                map.put("shopId",beautyParlorId);
            }
            if(!TextUtils.isEmpty(beauticianId)){
                map.put("bid",beauticianId);
            }
            map.put("projectParams",projectParams);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_AVAILABLE_SERVICE_HOURS;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getServiceHoursDataCallback)
                    .jsonParser(getServiceHoursJsonParser)
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

    HttpInterface.DataCallback getServiceHoursDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateServiceHours((List<ServiceHourBean>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getServiceHoursJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<ServiceHourBean> serviceHourBeanList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray hourList = obj.getJSONArray("data");
                for(int i=0;i<hourList.length();i++){
                    JSONObject hourObj = hourList.getJSONObject(i);
                    ServiceHourBean serviceHourBean = new ServiceHourBean();
                    serviceHourBean.setWeek(hourObj.getString("week"));
                    serviceHourBean.setDate(hourObj.getString("date"));
                    JSONArray hours = hourObj.getJSONArray("availableHours");
                    HashSet<String> availableHour = new HashSet<>();
                    for(int j=0;j<hours.length();j++){
                        availableHour.add(hours.getString(j));
                    }
                    serviceHourBean.setAvailableHours(availableHour);
                    serviceHourBeanList.add(serviceHourBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,serviceHourBeanList};
        }
    };

    @Override
    public void getRecommendCouponAndPackage(String projectParams) {
        getRecommendCouponAndPackage(projectParams,"","");
    }

    @Override
    public void getRecommendCouponAndPackage(String projectParams,String beautyParlorId,String packageParams) {
        MdjLog.log("getRecommendCouponAndPackage");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("projectParams",projectParams);
            if(!TextUtils.isEmpty(beautyParlorId)){
                map.put("shopId",beautyParlorId);
            }
            if(!TextUtils.isEmpty(packageParams)){
                map.put("usePackageParams",packageParams);
            }

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_RECOMMAND_COUPON_AND_PACKAGE;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getRecommendCouponAndPackageDataCallback)
                    .jsonParser(getRecommendCouponAndPackageJsonParser)
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

    HttpInterface.DataCallback getRecommendCouponAndPackageDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updatePackageAndCoupon((RecommendCouponAndPackageBean) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getRecommendCouponAndPackageJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            RecommendCouponAndPackageBean recommendCouponAndPackageBean = new RecommendCouponAndPackageBean();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                JSONObject couponInfo = dataObj.getJSONObject("couponInfo");
                JSONObject packageInfo = dataObj.getJSONObject("packageInfo");
                if(couponInfo.has("name")) {
                    recommendCouponAndPackageBean.getCouponInfo().setName(couponInfo.getString("name"));
                    recommendCouponAndPackageBean.getCouponInfo().setId(couponInfo.getString("id"));
                }
                if(packageInfo.has("detail")) {
                    recommendCouponAndPackageBean.getPackageInfo().setDetail(packageInfo.getString("detail"));
                    recommendCouponAndPackageBean.getPackageInfo().setNum(packageInfo.getInt("num"));
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,recommendCouponAndPackageBean};
        }
    };

    @Override
    public void calRealPrice(String projectParams,String couponId,String packageParams,int serviceType) {
        MdjLog.log("calRealPrice");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("projectParams",TextUtils.isEmpty(projectParams)?"":projectParams);
            map.put("couponId", TextUtils.isEmpty(couponId) ? "" : couponId);
            map.put("packageParams", TextUtils.isEmpty(packageParams)?"":packageParams);
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("serviceType",serviceType+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.CAL_REAL_PRICE;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(calRealPriceDataCallback)
                    .jsonParser(calRealPriceJsonParser)
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



    HttpInterface.DataCallback calRealPriceDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateRealPrice((Integer) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser calRealPriceJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            int realPrice = 0;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                JSONObject realPriceObj = dataObj.getJSONObject("realPrice");
                realPrice = realPriceObj.getInt("price");
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,realPrice};
        }
    };
    @Override
    public void getAddressList() {
        MdjLog.log("getAddressList");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("pageSize", 10 + "");
            map.put("pageStart", 1 + "");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_MY_ADDRESS_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getAddressListDataCallback)
                    .jsonParser(getAddressListJsonParser)
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

    HttpInterface.DataCallback getAddressListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateAddress((List<AddressBean>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getAddressListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<AddressBean> dataList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray addressList = obj.getJSONArray("data");
                for(int i=0;i<addressList.length();i++){
                    JSONObject obj2 = addressList.getJSONObject(i);
                    AddressBean bean = new AddressBean();
                    bean.setName(obj2.getString("address"));
                    bean.setLat(obj2.getString("latitude"));
                    bean.setLng(obj2.getString("longitude"));
                    bean.setCityId(obj2.getString("cityId"));
                    bean.setDoorNum(obj2.getString("doorNumber"));
                    bean.setUserName(obj2.getString("userName"));
                    bean.setUserPhone(obj2.getString("userPhone"));
                    bean.setId(obj2.getString("addressId"));

                    bean.setIsDefault(obj2.getInt("isDefault") == 1 ? true : false);
                    /*只取当前城市的address*/
                    if(bean.getCityId().equals(CacheManager.getInstance().getGlobalCity().getId())){
                        dataList.add(bean);
                    }
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };

    @Override
    public void createOrder(int serviceType, AddressBean addressBean, String projectParams,
                            String beauticianId, String date, String time, String remarks,
                            String beautyParlorId, String usePackageParams, String couponId,
                            String orderId) {
        MdjLog.log("createOrder");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("serviceType",serviceType+"");
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("projectParams",projectParams);
            map.put("beauticianId",beauticianId);
            map.put("orderDate",date);
            map.put("startTime",time);
            map.put("remarks",remarks);
            map.put("usePackageParams",usePackageParams==null?"":usePackageParams);
            map.put("couponId",couponId==null?"":couponId);
            map.put("shopId",beautyParlorId);

            /*追单 start*/
            if(!TextUtils.isEmpty(orderId)){
                map.put("appendOrderSn", orderId);
            }
            /*追单 end*/
            if(!TextUtils.isEmpty(addressBean.getId())) {  //新增地址
                map.put("addressId", addressBean.getId());
            }

            if(TextUtils.isEmpty(addressBean.getId()) /*addressId为空的时候，传这些过去!!*/
                    ){
                map.put("location", addressBean.getLng().isEmpty() ? "" : addressBean.getLng() + "," + addressBean.getLat());
                map.put("userName", addressBean.getUserName());
                map.put("phone", addressBean.getUserPhone());
                map.put("address", addressBean.getName());
                map.put("room", addressBean.getDoorNum());
            }

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.CREATE_PROJECT_ORDER;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_POST, url)
                    .callback(createOrderDataCallback)
                    .jsonParser(createOrderJsonParser)
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

    HttpInterface.DataCallback createOrderDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.createOrderDone(data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser createOrderJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            OrderBean orderBean = null;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                orderBean = new OrderBean.Builder(dataObj.getString("orderSn"))
                        .price(dataObj.getInt("price")+"")
                        .build();
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,orderBean};
        }
    };

    @Override
    public void getDefaultAddress() {
        MdjLog.log("getDefaultAddress");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_DEFAULT_ADDRESS;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getDefaultAddressDataCallback)
                    .jsonParser(getDefaultAddressJsonParser)
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

    HttpInterface.DataCallback getDefaultAddressDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateDefaultAddress((AddressBean) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getDefaultAddressJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            AddressBean addressBean = new AddressBean();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                if(dataObj.has("addressInfo")){
                    JSONObject addressObj = dataObj.getJSONObject("addressInfo");
                    if(addressObj.has("addressId")){
                        addressBean.setId(addressObj.getString("addressId"));
                    }
                    if(addressObj.has("userName")){
                        addressBean.setUserName(addressObj.getString("userName"));
                    }
                    if(addressObj.has("userPhone")){
                        addressBean.setUserPhone(addressObj.getString("userPhone"));
                    }
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,addressBean};
        }
    };
    @Override
    public void getRecommendBeautician(int serviceType, String beautyParlorId, String location, String projectParams, String orderDate, String startTime) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId", CacheManager.getInstance().getUserBean().getId());
            if(serviceType== CommonConstant.SERVICE_TYPE_IN_HOME){
                map.put("location", location);
            }else{
                map.put("shopId", beautyParlorId);
            }
            map.put("projectParams",projectParams);
            map.put("orderDate",orderDate);
            map.put("startTime",startTime);
            map.put("cate",CommonConstant.BEAUTICIAN_LIST_TYPE_ALL+"");
            map.put("page", 1 + "");
            map.put("pageSize", CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_AVAILABLE_BEAUTICIAN_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getRecommendBeauticianDataCallback)
                    .jsonParser(getRecommendBeauticianJsonParser)
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
    HttpInterface.DataCallback getRecommendBeauticianDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateRecommendBeautician((BeauticianBean) data);
            }else{
//                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getRecommendBeauticianJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            BeauticianBean beauticianBean = null;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                if(dataObj.has("beauticianList")){
                    JSONArray beauticianArray = dataObj.getJSONArray("beauticianList");
                    for(int i=0;i<beauticianArray.length();i++){
                        JSONObject beauticianObj = beauticianArray.getJSONObject(i);
                        beauticianBean = new BeauticianBean.Builder(beauticianObj.getString("beautyName"))
                                .id(beauticianObj.getString("beautyId"))
                                .imgUrl(beauticianObj.getString("beautyPhoto"))
                                .goodAppraiseNum(beauticianObj.getInt("goodAppraiseNum")+"")
                                .orderNum(beauticianObj.getInt("orderNum")+"")
                                .intruduction(StringUtils.replaceBlank(beauticianObj.getString("introduction")))
                                .build();
                        break;
                    }
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,beauticianBean == null?new BeauticianBean.Builder("").build():beauticianBean};
        }
    };
}
