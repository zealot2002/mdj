package com.mdj.moudle.address.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;

public class EditAddressPresenter implements EditAddressContract.Presenter,HttpInterface.JsonParser,HttpInterface.DataCallback, HttpInterface.Validator {
    private final EditAddressContract.View view;
    private AddressBean addressBean;
    private boolean isDefault = false;

/**************************************************************************************************/
    public EditAddressPresenter(@NonNull EditAddressContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void requestCallback(int result,Object data,Object tagObj) {
        view.closeLoading();
        if(result == HttpConstant.SUCCESS){
            view.done();
        }else{
            view.showDisconnect(data.toString());
        }
    }

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

    @Override
    public void validate(Object obj) throws NetDataInvalidException {

    }

    @Override
    public void addOrUpdateAddress(AddressBean bean) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            String httpMethod = HttpConstant.HTTP_METHOD_POST;
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());

            if(!TextUtils.isEmpty(bean.getId())){//修改
                map.put("addressId",bean.getId());
                httpMethod = HttpConstant.HTTP_METHOD_PUT;
            }
            map.put("userName", bean.getUserName());
            map.put("userPhone", bean.getUserPhone());
            map.put("address", bean.getName());
            map.put("doorNumber", bean.getDoorNum());
            map.put("isDefault", bean.isDefault()?"1":"0");
            map.put("longitude", bean.getLng());
            map.put("latitude", bean.getLat());

            map.put("cityId", bean.getCityId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.ADD_OR_UPDATE_ADDRESS;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(httpMethod,url)
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
}
