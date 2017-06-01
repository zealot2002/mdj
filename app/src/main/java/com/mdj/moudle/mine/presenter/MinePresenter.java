package com.mdj.moudle.mine.presenter;/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.user.UserBean;
import com.mdj.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;


public class MinePresenter implements MineContract.Presenter,HttpInterface.JsonParser,HttpInterface.DataCallback, HttpInterface.Validator {
    private final MineContract.View view;

/**************************************************************************************************/

    public MinePresenter(@NonNull MineContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){//未登陆
            view.showLogin();
        }else{
            getUserInfo(CacheManager.getInstance().getUserBean().getId(),true);
        }
    }

    private void getUserInfo(String userId,boolean showLoading) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            if(showLoading)
                view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("id",userId);
            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_USER_INFO;
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
    public void requestCallback(int result,Object data,Object tagObj) {
        view.closeLoading();
        if(result == HttpConstant.SUCCESS){
            view.updateUI(CacheManager.getInstance().getUserBean());
        }else{
            view.showDisconnect(data.toString());
        }
    }

    @Override
    public Object[] parse(String str) throws JSONException {
        JSONTokener jsonParser = new JSONTokener(str);
        JSONObject obj = (JSONObject) jsonParser.nextValue();
        int errno = obj.getInt("err");
        if (errno == 0) {
            JSONObject dataObj = obj.getJSONObject("data");
            JSONObject userInfo = dataObj.getJSONObject("userInfo");
            UserBean bean = new UserBean.Builder(userInfo.getString("name"))
                    .id(userInfo.getString("id"))
                    .phone(userInfo.getString("phone"))
                    .imgUrl(userInfo.getString("headImg"))
                    .availablePackageNum(userInfo.getInt("availablePackageNum"))
                    .availableCouponNum(userInfo.getInt("availableCouponNum"))
                    .build();
            CacheManager.getInstance().setUserBean(bean);
        } else {
            String msg = obj.getString("msg");
            return new Object[]{HttpConstant.FAIL,msg};
        }
        return new Object[]{HttpConstant.SUCCESS,""};

    }

    @Override
    public void validate(Object obj) throws NetDataInvalidException {

    }

    @Override
    public void getUserInfo(boolean showLoading) {
        getUserInfo(CacheManager.getInstance().getUserBean().getId(),showLoading);
    }
}
