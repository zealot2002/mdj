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
import com.mdj.moudle.userPackage.OrderProjectPackageVo;
import com.mdj.moudle.userPackage.PackageBean;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class PackagePresenter implements PackageContract.Presenter{
    private final PackageContract.View view;

    private int currentPageIndex = 0;
    private boolean isNoMoreData = false;
//    private List<PackageBean> packageList = new ArrayList<>();
/**************************************************************************************************/

    public PackagePresenter(@NonNull PackageContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        getSuggestPackageList();
        getNormalPackageList();
    }

    @Override
    public void getSuggestPackageList() {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_SUGGEST_PACKAGE_LIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getSuggestPackageListDataCallback)
                    .jsonParser(getSuggestPackageListJsonParser)
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
    HttpInterface.DataCallback getSuggestPackageListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
//            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateSuggestPackageList((List<PackageBean>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getSuggestPackageListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<PackageBean> suggestPackageList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray packageArray = obj.getJSONArray("data");
                for (int i = 0; i < packageArray.length(); i++) {
                    PackageBean packageBean = new PackageBean();
                    JSONObject obj1 = packageArray.getJSONObject(i);
                    packageBean.setImgUrl(obj1.getJSONObject("pInfo").getString("image"));
                    packageBean.setId(obj1.getJSONObject("pInfo").getString("id"));
                    packageBean.setName(obj1.getJSONObject("pInfo").getString("name"));
                    packageBean.setOldPrice(obj1.getJSONObject("pInfo").getInt("marketPrice"));
                    packageBean.setPrice(obj1.getJSONObject("pInfo").getInt("price"));
                    packageBean.setValidityDays(obj1.getJSONObject("pInfo").getString("durDay"));

                    packageBean.setType(obj1.getJSONObject("pInfo").getInt("type"));

                    /*sale info*/
                    if(obj1.has("saleInfo")){
                        packageBean.setStartTime(obj1.getJSONObject("saleInfo").getString("startTime"));
                        packageBean.setEndTime(obj1.getJSONObject("saleInfo").getString("endTime"));
                        packageBean.setTotal(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("total")));
                        packageBean.setSoldNum(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("soldNum")));
                        packageBean.setPrivilegePrice(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("preferentialPrice")));

                        packageBean.setPerCount(obj1.getJSONObject("saleInfo").getInt("perCount"));
                    }

                    JSONArray effectArray = obj1.getJSONObject("pInfo").getJSONArray("tag");
                    for(int m=0;m<effectArray.length();m++){
                        packageBean.getEffectList().add(effectArray.getString(m));
                    }

                    suggestPackageList.add(packageBean);
                }
//                CacheManager.getInstance().setPackageList(packageList);
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,suggestPackageList};
        }
    };
    @Override
    public void getNormalPackageList() {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("page", ++currentPageIndex + "");
            map.put("pageSize", CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_NORMAL_PACKAGE_LIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getNormalPackageListDataCallback)
                    .jsonParser(getNormalPackageListJsonParser)
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

    HttpInterface.DataCallback getNormalPackageListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateNormalPackageList((List<PackageBean>) data);
            }else{
                --currentPageIndex;
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getNormalPackageListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<PackageBean> packageList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray packageArray = obj.getJSONArray("data");
                if(packageArray.length()>0){
                    for (int i = 0; i < packageArray.length(); i++) {
                        PackageBean packageBean = new PackageBean();
                        JSONObject obj1 = packageArray.getJSONObject(i);
                        packageBean.setImgUrl(obj1.getJSONObject("pInfo").getString("image"));
                        packageBean.setId(obj1.getJSONObject("pInfo").getString("id"));
                        packageBean.setName(obj1.getJSONObject("pInfo").getString("name"));
                        packageBean.setOldPrice(obj1.getJSONObject("pInfo").getInt("marketPrice"));
                        packageBean.setPrice(obj1.getJSONObject("pInfo").getInt("price"));
                        packageBean.setValidityDays(obj1.getJSONObject("pInfo").getString("durDay"));
                        packageBean.setType(obj1.getJSONObject("pInfo").getInt("type"));

                        packageBean.setType(obj1.getJSONObject("pInfo").getInt("type"));

                    /*sale info*/
                        if(obj1.has("saleInfo")){
                            packageBean.setStartTime(obj1.getJSONObject("saleInfo").getString("startTime"));
                            packageBean.setEndTime(obj1.getJSONObject("saleInfo").getString("endTime"));
                            packageBean.setTotal(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("total")));
                            packageBean.setSoldNum(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("soldNum")));
                            packageBean.setPrivilegePrice(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("preferentialPrice")));

                            packageBean.setPerCount(obj1.getJSONObject("saleInfo").getInt("perCount"));
                        }

                        JSONArray effectArray = obj1.getJSONObject("pInfo").getJSONArray("tag");
                        for(int m=0;m<effectArray.length();m++){
                            packageBean.getEffectList().add(effectArray.getString(m));
                        }
                        packageList.add(packageBean);
                    }
                }else{
                    isNoMoreData = true;
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,packageList};
        }
    };
    @Override
    public void loadMoreNormalPackageList() {
        if(!isNoMoreData)
            getNormalPackageList();
    }

    @Override
    public void getBeautyParlorPackageList(String beautyParlorId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("shopId", beautyParlorId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTY_PRALOR_PACKAGE_LIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeautyParlorPackageListDataCallback)
                    .jsonParser(getBeautyParlorPackageListJsonParser)
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
    HttpInterface.DataCallback getBeautyParlorPackageListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateBeautyParlorPackageList((List<PackageBean>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getBeautyParlorPackageListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<PackageBean> packageList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray packageArray = obj.getJSONArray("data");
                for (int i = 0; i < packageArray.length(); i++) {
                    PackageBean packageBean = new PackageBean();
                    JSONObject obj1 = packageArray.getJSONObject(i);
                    packageBean.setImgUrl(obj1.getJSONObject("pInfo").getString("image"));
                    packageBean.setId(obj1.getJSONObject("pInfo").getString("id"));
                    packageBean.setName(obj1.getJSONObject("pInfo").getString("name"));
                    packageBean.setOldPrice(obj1.getJSONObject("pInfo").getInt("marketPrice"));
                    packageBean.setPrice(obj1.getJSONObject("pInfo").getInt("price"));
                    packageBean.setValidityDays(obj1.getJSONObject("pInfo").getString("durDay"));
                    packageBean.setType(obj1.getJSONObject("pInfo").getInt("type"));

                    /*sale info*/
                    if(obj1.has("saleInfo")){
                        packageBean.setStartTime(obj1.getJSONObject("saleInfo").getString("startTime"));
                        packageBean.setEndTime(obj1.getJSONObject("saleInfo").getString("endTime"));
                        packageBean.setTotal(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("total")));
                        packageBean.setSoldNum(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("soldNum")));
                        packageBean.setPrivilegePrice(Integer.valueOf(obj1.getJSONObject("saleInfo").getString("preferentialPrice")));
                        if(obj1.getJSONObject("saleInfo").has("perCount")){
                            packageBean.setPerCount(obj1.getJSONObject("saleInfo").getInt("perCount"));
                        }
                    }

                    JSONArray effectArray = obj1.getJSONObject("pInfo").getJSONArray("tag");
                    for(int m=0;m<effectArray.length();m++){
                        packageBean.getEffectList().add(effectArray.getString(m));
                    }
                    packageList.add(packageBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,packageList};
        }
    };

    @Override
    public void getAvailablePackageList(String projectParams) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId", CacheManager.getInstance().getUserBean().getId());
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());
            map.put("projectParams",projectParams);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_AVAILABLE_PACKAGE_LIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getAvailablePackageListDataCallback)
                    .jsonParser(getAvailablePackageListJsonParser)
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
    HttpInterface.DataCallback getAvailablePackageListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateAvailablePackageList((List<OrderProjectPackageVo>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getAvailablePackageListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<OrderProjectPackageVo> dataList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray projectArray = obj.getJSONArray("data");
                for (int i = 0; i < projectArray.length(); i++) {
                    JSONObject obj1 = projectArray.getJSONObject(i);
                    OrderProjectPackageVo vo = new OrderProjectPackageVo();
                    vo.setBuyNum(Integer.valueOf(obj1.getString("projectNum")));
                    vo.setId(obj1.getInt("projectId")+"");
                    vo.setImgUrl(obj1.getString("projectPhoto"));
                    vo.setName(obj1.getString("projectName"));

                    JSONArray packageArray = obj1.getJSONArray("packageList");
                    List<OrderProjectPackageVo.PackageUseForOrderProjectVo> packageUseForOrderProjectVoList = new ArrayList<>();
                    int num = 0;
                    for (int j = 0; j < packageArray.length(); j++) {
                        JSONObject obj2 = packageArray.getJSONObject(j);
                        OrderProjectPackageVo.PackageUseForOrderProjectVo vo2 = vo.new PackageUseForOrderProjectVo();
                        vo2.setAllocNum(Integer.valueOf(obj2.getString("useNum")));
                        vo2.setAvailableNum(Integer.valueOf(obj2.getString("availableNum")));
                        vo2.setId(obj2.getString("packageId"));
                        vo2.setName(obj2.getString("packageName"));
                        vo2.setValidDuration(obj2.getInt("expireDay"));
                        vo2.setOrderId(obj2.getString("packageOrderSn"));
                        num += vo2.getAllocNum();

                        packageUseForOrderProjectVoList.add(vo2);
                    }
                    vo.setPackageUseForOrderProjectVoList(packageUseForOrderProjectVoList);
                    vo.setFreeNum(num);
                    dataList.add(vo);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };
}
