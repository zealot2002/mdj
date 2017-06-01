package com.mdj.moudle.address.presenter;

import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.MdjLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
public class MyAddressListPresenter implements MyAddressListContract.Presenter,HttpInterface.JsonParser,HttpInterface.DataCallback, HttpInterface.Validator {
    private final MyAddressListContract.View view;
    private List<AddressBean> dataList = new ArrayList<>();
    private int currentPageIndex = 0;
    private boolean isNoMoreData = false;

/**************************************************************************************************/
public MyAddressListPresenter(@NonNull MyAddressListContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        getMyAddressList();
    }

    @Override
    public void requestCallback(int result,Object data,Object tagObj) {
        view.closeLoading();
        if(result == HttpConstant.SUCCESS){
            List<AddressBean> list = (List<AddressBean>)data;
            if(list.isEmpty()){
                isNoMoreData = true;
            }else{
                if(list.size()<CommonConstant.PAGE_SIZE){
                    isNoMoreData = true;
                }
                dataList.addAll(list);
            }
            view.updateUI(dataList);
        }else{
            view.showDisconnect(data.toString());
            currentPageIndex--;
        }
    }

    @Override
    public Object[] parse(String str) throws JSONException {
        List<AddressBean> addressBeanList = new ArrayList<>();
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
                bean.setIsDefault(obj2.getInt("isDefault")==1?true:false);
                addressBeanList.add(bean);
            }
        } else {
            String msg = obj.getString("msg");
            return new Object[]{HttpConstant.FAIL,msg};
        }
        return new Object[]{HttpConstant.SUCCESS,addressBeanList};

    }

    @Override
    public void validate(Object obj) throws NetDataInvalidException {

    }
    @Override
    public void loadMoreData() {
        getMyAddressList(++currentPageIndex);
    }

    public void getMyAddressList(int pageIndex) {
        if(isNoMoreData){
            return;
        }
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("pageSize", CommonConstant.PAGE_SIZE + "");
            map.put("pageStart", pageIndex + "");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_MY_ADDRESS_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(this)
                    .jsonParser(this)
                    .validator(this)
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

    @Override
    public void deleteAddress(String addressId) {
        MdjLog.log("deleteAddress");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("addressId",addressId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.ADD_OR_UPDATE_ADDRESS;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_DEL, url)
                    .callback(deleteAddressDataCallback)
                    .jsonParser(deleteAddressJsonParser)
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



    HttpInterface.DataCallback deleteAddressDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                getMyAddressList();
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser deleteAddressJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno != 0) {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,""};
        }
    };

    @Override
    public void getMyAddressList() {
        currentPageIndex = 1;
        isNoMoreData = false;
        dataList.clear();
        getMyAddressList(currentPageIndex);
    }

    @Override
    public void setDefaultAddress(String addressId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("addressId",addressId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.SET_DEFAULT_ADDRESS;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_POST, url)
                    .callback(setDefaultAddressDataCallback)
                    .jsonParser(setDefaultAddressJsonParser)
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
    HttpInterface.DataCallback setDefaultAddressDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
//            view.setDefaultAddressDone(result==HttpConstant.SUCCESS?true:false);
            if(result == HttpConstant.SUCCESS){
//                view.setDefaultAddressDone(true);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser setDefaultAddressJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno != 0) {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,""};
        }
    };
}
