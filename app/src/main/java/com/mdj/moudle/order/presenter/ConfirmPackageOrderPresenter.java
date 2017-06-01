package com.mdj.moudle.order.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.project.bean.ProjectWrapperBean;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.MdjLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ConfirmPackageOrderPresenter implements ConfirmPackageOrderContract.Presenter{
    private final ConfirmPackageOrderContract.View view;


/***************************************************************************************************/
    public ConfirmPackageOrderPresenter(@NonNull ConfirmPackageOrderContract.View view) {
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
    public void createOrder(AddressBean addressBean, String packageId, String beautyParlorId) {
        MdjLog.log("createOrder");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("uId",CacheManager.getInstance().getUserBean().getId());
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("packageId",packageId);
            map.put("homeId",beautyParlorId==null?"":beautyParlorId);

            if(!TextUtils.isEmpty(addressBean.getId())) {  //新增地址
                map.put("addressId", addressBean.getId());
            }
            map.put("location", addressBean.getLng().isEmpty() ? "" : addressBean.getLng() + "," + addressBean.getLat());
            map.put("userName", addressBean.getUserName());
            map.put("phone", addressBean.getUserPhone());
            map.put("address", addressBean.getName());
            map.put("room", addressBean.getDoorNum());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.CREATE_PACKAGE_ORDER;
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
    public void getPackageInfo(String packageId) {
        MdjLog.log("getPackageInfo");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("packageId",packageId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_PACKAGE_INFO;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getPackageInfoDataCallback)
                    .jsonParser(getPackageInfoJsonParser)
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

    HttpInterface.DataCallback getPackageInfoDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updatePackageProjectList((List<ProjectWrapperBean>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getPackageInfoJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<ProjectWrapperBean> projectBeanList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                JSONArray projcetArray = dataObj.getJSONArray("projectList");
                for(int i=0;i<projcetArray.length();i++){
                    ProjectWrapperBean projectBean = new ProjectWrapperBean();
                    projectBean.setName(projcetArray.getJSONObject(i).getString("name"));
                    projectBean.setNum(projcetArray.getJSONObject(i).getInt("num"));
                    projectBeanList.add(projectBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,projectBeanList};
        }
    };
}
