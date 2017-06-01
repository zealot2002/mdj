package com.mdj.moudle.login.presenter;/*
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
import com.mdj.constant.SPConstant;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.user.UserBean;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;


public class LoginPresenter implements LoginContract.Presenter,HttpInterface.JsonParser,HttpInterface.DataCallback, HttpInterface.Validator {
    private final LoginContract.View view;

    private String smsCode;
/**************************************************************************************************/

    public LoginPresenter(@NonNull LoginContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
//
    }

    @Override
    public void requestCallback(int result,Object data,Object tagObj) {
        view.closeLoading();
        if(result == HttpConstant.SUCCESS){
            view.updateUI("");
        }else{
            view.showDisconnect(data.toString());
        }
    }

    @Override
    public Object[] parse(String str) throws JSONException {
        JSONTokener jsonParser = new JSONTokener(str);
        JSONObject obj = (JSONObject) jsonParser.nextValue();
        int err = obj.getInt("err");
        if (err == 0) {
            return new Object[]{HttpConstant.SUCCESS,""};
        } else {
            String msg = obj.getString("msg");
            return new Object[]{HttpConstant.FAIL,msg};
        }

    }

    @Override
    public void validate(Object obj) throws NetDataInvalidException {

    }

    @Override
    public void getSmsCode(String phone) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
//            view.showLoadingDialog();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("phone", phone);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_SMS_CODE;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_POST, url)
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
    public void login(String phone, String smsCode) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("code", smsCode);
            map.put("phone", phone);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.LOGIN;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_POST, url)
                    .callback(loginDataCallback)
                    .jsonParser(loginJsonParser)
                    .validator(loginValidator)
                    .build();
            try {
                HttpUtil.getInstance().request(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            view.showDisconnect("请检查您的网络后再试");
        }
    }
    HttpInterface.DataCallback loginDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.login(HttpConstant.SUCCESS,"");
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser loginJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            String token;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                token = dataObj.getString("token");
                SPUtils.putString(MyApp.getInstance(), SPConstant.TOKEN, token);
                JSONObject userInfo = dataObj.getJSONObject("userInfo");

                UserBean bean = new UserBean.Builder(userInfo.getString("name"))
                        .id(userInfo.getString("id"))
                        .build();
                CacheManager.getInstance().setUserBean(bean);
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,token};
        }
    };
    HttpInterface.Validator loginValidator = new HttpInterface.Validator() {
        @Override
        public void validate(Object obj) throws NetDataInvalidException {
            try {
                String token = (String) obj;
                if(TextUtils.isEmpty(token)){
                    throw new NetDataInvalidException("网络数据校验异常-->loginValidator token 是空的");
                }
            } catch (Exception e) {
                throw new NetDataInvalidException("CityListPresenter validater: err:"+e.toString());
            }
        }
    };
}
