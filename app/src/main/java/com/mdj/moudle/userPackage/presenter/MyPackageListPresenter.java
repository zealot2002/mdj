package com.mdj.moudle.userPackage.presenter;/*
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
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.userPackage.MyPackageBean;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class MyPackageListPresenter implements MyPackageListContract.Presenter{
    private final MyPackageListContract.View view;

/**************************************************************************************************/

    public MyPackageListPresenter(@NonNull MyPackageListContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        getMyPackageList(true);
    }

    HttpInterface.DataCallback packageListDataCallback = new HttpInterface.DataCallback() {
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
    HttpInterface.JsonParser packageListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                List<MyPackageBean> packageList = new ArrayList<>();
                JSONObject dataObj = obj.getJSONObject("data");
                JSONArray packageArray = dataObj.getJSONArray("packageList");
                for (int i = 0; i < packageArray.length(); i++) {
                    MyPackageBean packageBean = new MyPackageBean();
                    JSONObject obj1 = packageArray.getJSONObject(i);
                    packageBean.setHomeRefereeId(obj1.getString("id"));
                    packageBean.setImgUrl(obj1.getString("imgUrl"));
                    packageBean.setId(obj1.getString("packageId"));
                    packageBean.setName(obj1.getString("packageName"));
                    packageBean.setOrderCreateTime(obj1.getString("createTime"));
                    packageBean.setOrderSn(obj1.getString("orderSn"));
                    packageBean.setPayStatus(obj1.getString("payStatus"));
                    packageBean.setPrice(obj1.getString("price"));
                    packageBean.setLeftDays(obj1.getInt("leftDays"));
                    packageBean.setExpireTime(obj1.getString("expireTime"));

                    packageBean.setLimitBeautyParlor(!obj1.getString("homeId").equals("0"));
                    String payType = obj1.getString("payType");
                    if(payType.equals("1")){
                        payType = CommonConstant.PAY_TYPE.alipay.value();
                    }else if(payType.equals("2")){
                        payType = CommonConstant.PAY_TYPE.wx.value();
                    }else if(payType.equals("3")){
                        payType = CommonConstant.PAY_TYPE.bfb.value();
                    }
                    packageBean.setPayType(payType);
                    packageBean.setShowSetReferee(obj1.getInt("isRecommend"));
                    JSONObject recommender = obj1.getJSONObject("recommender");
                    if(recommender.has("name")){
                        packageBean.getRefereeBean().setName(recommender.getString("name"));
                        packageBean.getRefereeBean().setPhone(recommender.getString("phone"));
                    }

                    JSONArray projectArray = obj1.getJSONArray("projectList");
                    for (int j = 0; j < projectArray.length(); j++) {
                        JSONObject obj2 = projectArray.getJSONObject(j);
                        packageBean.getProjectList().add(
                                packageBean.new ProjectBean(
                                        obj2.getString("projectId"),
                                        obj2.getString("projectName"),
                                        obj2.getString("projectLeftNum"),
                                        obj2.getString("projectTotalNum"),
                                        /*obj2.getString("serviceType")*/"1",0,0
//                                        obj2.getInt("serviceTime"),
//                                        obj2.getInt("price")
                                ));
                    }
                    // 城市列表
                    JSONArray cityArray = obj1.getJSONArray("cityList");
                    for (int j = 0; j < cityArray.length(); j++) {
                        MyPackageBean.CityBean bean = packageBean.new CityBean();
                        JSONObject obj3 = cityArray.getJSONObject(j);
                        bean.setId(obj3.getString("cityId"));
                        bean.setName(obj3.getString("cityName"));
                        packageBean.getCityList().add(bean);
                    }
                    packageList.add(packageBean);
                }
                CacheManager.getInstance().setPackageList(packageList);
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,CacheManager.getInstance().getPackageList()};
        }
    };

    @Override
    public void getMyPackageList(boolean showLoading) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            if(showLoading){
                view.showLoading();
            }
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_MY_PACKAGE_LIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(packageListDataCallback)
                    .jsonParser(packageListJsonParser)
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
    public void getProjectInfo(String projectId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("id",projectId);
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_PROJECT_INFO;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getProjectInfoDataCallback)
                    .jsonParser(getProjectInfoJsonParser)
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



    HttpInterface.DataCallback getProjectInfoDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateProjectInfo((ProjectBean) data);
            }else{
                view.showError(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getProjectInfoJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            ProjectBean projectBean = new ProjectBean();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                projectBean.setId(dataObj.getString("id"));
                projectBean.setName(dataObj.getString("name"));
                projectBean.setDuration(dataObj.getInt("serviceTime"));
                projectBean.setPrice(dataObj.getInt("price"));
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,projectBean};
        }
    };

    @Override
    public void getFitableBeautyParlorList(String packageId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("packageOrderId",packageId);
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_FITABLE_BEAUTY_PARLOR_LIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getFitableBeautyParlorListDataCallback)
                    .jsonParser(getFitableBeautyParlorListJsonParser)
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

    HttpInterface.DataCallback getFitableBeautyParlorListDataCallback = new HttpInterface.DataCallback() {
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
    HttpInterface.JsonParser getFitableBeautyParlorListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<String> beautyParlorList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray array = obj.getJSONArray("data");
                for(int i=0;i<array.length();i++){
                    beautyParlorList.add(array.getJSONObject(i).getString("shopName"));
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,beautyParlorList};
        }
    };
}
