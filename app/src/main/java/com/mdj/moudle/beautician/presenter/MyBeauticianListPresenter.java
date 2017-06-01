package com.mdj.moudle.beautician.presenter;/*
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

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class MyBeauticianListPresenter implements MyBeauticianListContract.Presenter,HttpInterface.JsonParser,HttpInterface.DataCallback, HttpInterface.Validator {
    private final MyBeauticianListContract.View view;
    private List<BeauticianBean> dataList;

/**************************************************************************************************/

    public MyBeauticianListPresenter(@NonNull MyBeauticianListContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        getMyBeauticianList();
    }

    @Override
    public void requestCallback(int result,Object data,Object tagObj) {
        view.closeLoading();
        if(result == HttpConstant.SUCCESS){
            view.updateUI(dataList);
        }else{
            view.showDisconnect(data.toString());
        }
    }

    @Override
    public Object[] parse(String str) throws JSONException {
        if(dataList==null){
            dataList = new ArrayList<>();
        }else{
            dataList.clear();
        }
        JSONTokener jsonParser = new JSONTokener(str);
        JSONObject obj = (JSONObject) jsonParser.nextValue();
        int errno = obj.getInt("err");
        if (errno == 0) {
            JSONObject dataObj = obj.getJSONObject("data");
            JSONArray beauticianList = dataObj.getJSONArray("beauticianList");
            for(int i=0;i<beauticianList.length();i++){
                JSONObject beauticianObj = beauticianList.getJSONObject(i);
                BeauticianBean bean = new BeauticianBean.Builder(beauticianObj.getString("beautyName"))
                        .id(beauticianObj.getString("beautyId"))
                        .imgUrl(HttpConstant.FILE_SERVER_URL + beauticianObj.getString("beautyPhoto"))
                        .intruduction(beauticianObj.getString("introduction"))
                        .orderNum(beauticianObj.getInt("monthOrderNum")+"")
                        .goodAppraiseNum(beauticianObj.getString("goodAppraiseNum"))
                        .build();
                dataList.add(bean);
            }
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
    public void getMyBeauticianList() {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_MY_BEAUTICIAN_LIST;
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
}
