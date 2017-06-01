package com.mdj.moudle.beautyParlor.presenter;
import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.beautyParlor.BeautyParlorBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.order.serviceHour.ServiceHourBean;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class BeautyParlorListPresenter implements BeautyParlorListContract.Presenter{
    private final BeautyParlorListContract.View view;

    public BeautyParlorListPresenter(@NonNull BeautyParlorListContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void getBeautyParlorList(String projectParams, String lat, String lng) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());
            map.put("projectParams",projectParams);
            if(!(lat.isEmpty()||lng.isEmpty())){
                map.put("location",lng+","+lat);
            }

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_AVAILABLE_BEAUTY_PARLOR_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeautyParlorListDataCallback)
                    .jsonParser(getBeautyParlorListJsonParser)
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

    HttpInterface.DataCallback getBeautyParlorListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateUI(data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getBeautyParlorListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<BeautyParlorBean> dataList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray beautyParlorArray = obj.getJSONArray("data");
                for(int i=0;i<beautyParlorArray.length();i++){
                    JSONObject beautyParlorObj = beautyParlorArray.getJSONObject(i);
                    BeautyParlorBean beautyParlorBean = new BeautyParlorBean.Builder()
                            .id(beautyParlorObj.getString("storeId"))
                            .name(beautyParlorObj.getString("storeName"))
                            .address(beautyParlorObj.getString("storeAddress"))
                            .distance(beautyParlorObj.getString("storeDistance"))
                            .tel(beautyParlorObj.getString("storeTel"))
                            .imgUrl(beautyParlorObj.getString("storeImg"))
                            .build();
                    dataList.add(beautyParlorBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };

    @Override
    public void getBeautyParlorServiceHours(String projectParams, String beautyParlorId,int index) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());
            map.put("projectParams",projectParams);
            map.put("shopId",beautyParlorId);
            map.put("serviceType", CommonConstant.SERVICE_TYPE_TO_SHOP+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_AVAILABLE_SERVICE_HOURS;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeautyParlorServiceHoursDataCallback)
                    .jsonParser(getBeautyParlorServiceHoursJsonParser)
                    .tagObj(index)
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

    HttpInterface.DataCallback getBeautyParlorServiceHoursDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            int index = (int)tagObj;
            if(result == HttpConstant.SUCCESS){
                view.updateServiceHours(index, (List<ServiceHourBean>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getBeautyParlorServiceHoursJsonParser = new HttpInterface.JsonParser() {
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
}
