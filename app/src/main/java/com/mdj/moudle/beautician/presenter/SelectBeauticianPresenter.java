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
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SelectBeauticianPresenter implements SelectBeauticianContract.Presenter{
    private final SelectBeauticianContract.View view;
    private String location,beautyParlorId,projectParams,orderDate,startTime;
    private int serviceType;
    private ListWrapper listWrapperAll,listWrapperOrder,listWrapperAppraise;
/**************************************************************************************************/

    public SelectBeauticianPresenter(@NonNull SelectBeauticianContract.View view) {
        this.view = view;
        this.view.setPresenter(this);

        listWrapperAll = new ListWrapper();
        listWrapperOrder = new ListWrapper();
        listWrapperAppraise = new ListWrapper();
    }

    @Override
    public void start() {
    }

    @Override
    public void getAvailableBeauticianListPre(int serviceType,String beautyParlorId,String location,String projectParams,String orderDate,String startTime,int sortType) {
        this.serviceType = serviceType;
        this.beautyParlorId = beautyParlorId;
        this.location = location;
        this.projectParams = projectParams;
        this.orderDate = orderDate;
        this.startTime = startTime;

        int pageIndex = 0;
        if(sortType == CommonConstant.BEAUTICIAN_LIST_TYPE_ALL){
            if(!listWrapperAll.dataList.isEmpty()){
                view.updateUI(listWrapperAll.dataList);
            }else{
                pageIndex = ++listWrapperAll.pageIndex;
                getAvailableBeauticianList(serviceType,beautyParlorId,location,projectParams,orderDate,startTime,sortType,pageIndex);
            }
        }else if(sortType == CommonConstant.BEAUTICIAN_LIST_TYPE_MOST_ORDER_NUM){
            if(!listWrapperOrder.dataList.isEmpty()){
                view.updateUI(listWrapperOrder.dataList);
            }else{
                pageIndex = ++listWrapperOrder.pageIndex;
                getAvailableBeauticianList(serviceType,beautyParlorId,location,projectParams,orderDate,startTime,sortType,pageIndex);
            }
        }else{
            if(!listWrapperAppraise.dataList.isEmpty()){
                view.updateUI(listWrapperAppraise.dataList);
            }else{
                pageIndex = ++listWrapperAppraise.pageIndex;
                getAvailableBeauticianList(serviceType,beautyParlorId,location,projectParams,orderDate,startTime,sortType,pageIndex);
            }
        }
    }

    @Override
    public void loadMorePre(int type) {
        if(type == CommonConstant.BEAUTICIAN_LIST_TYPE_ALL){
            if(!listWrapperAll.isNoMoreData){
                getAvailableBeauticianList(serviceType,beautyParlorId,location,projectParams,orderDate,startTime,type,++listWrapperAll.pageIndex);
            }
        }else if(type == CommonConstant.BEAUTICIAN_LIST_TYPE_MOST_ORDER_NUM){
            if(!listWrapperOrder.isNoMoreData){
                getAvailableBeauticianList(serviceType,beautyParlorId,location,projectParams,orderDate,startTime,type,++listWrapperOrder.pageIndex);
            }
        }else{
            if(!listWrapperAppraise.isNoMoreData){
                getAvailableBeauticianList(serviceType,beautyParlorId,location,projectParams,orderDate,startTime,type,++listWrapperAppraise.pageIndex);
            }
        }
    }

    private void getAvailableBeauticianList(int serviceType,String beautyParlorId,String location,String projectParams,String orderDate,String startTime,int type,int pageIndex) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId", CacheManager.getInstance().getUserBean().getId());
            if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME){
                map.put("location", location);
            }else{
                map.put("shopId", beautyParlorId);
            }
            map.put("projectParams",projectParams);
            map.put("orderDate",orderDate);
            map.put("startTime",startTime);
            map.put("cate",type+"");
            map.put("page",pageIndex+"");
            map.put("pageSize", CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_AVAILABLE_BEAUTICIAN_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getAvailableBeauticianListDataCallback)
                    .jsonParser(getAvailableBeauticianListJsonParser)
                    .tagObj(type)
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

    HttpInterface.DataCallback getAvailableBeauticianListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            int type = (int)tagObj;
            List<BeauticianBean> dataList = (List<BeauticianBean>)data;

            if(type == CommonConstant.BEAUTICIAN_LIST_TYPE_ALL){
                if(result == HttpConstant.SUCCESS){
                    if(dataList.isEmpty()){
                        listWrapperAll.isNoMoreData = true;
                    }else{
                        listWrapperAll.dataList.addAll(dataList);
                    }
                    view.updateUI(listWrapperAll.dataList);
                }else{
                    listWrapperAll.pageIndex--;
                    view.showDisconnect(data.toString());
                }
            }else if(type == CommonConstant.BEAUTICIAN_LIST_TYPE_MOST_ORDER_NUM){
                if(result == HttpConstant.SUCCESS){
                    if(dataList.isEmpty()){
                        listWrapperOrder.isNoMoreData = true;
                    }else{
                        listWrapperOrder.dataList.addAll(dataList);
                    }
                    view.updateUI(listWrapperOrder.dataList);
                }else{
                    listWrapperOrder.pageIndex--;
                    view.showDisconnect(data.toString());
                }
            }else{
                if(result == HttpConstant.SUCCESS){
                    if(dataList.isEmpty()){
                        listWrapperAppraise.isNoMoreData = true;
                    }else{
                        listWrapperAppraise.dataList.addAll(dataList);
                    }
                    view.updateUI(listWrapperAppraise.dataList);
                }else{
                    listWrapperAppraise.pageIndex--;
                    view.showDisconnect(data.toString());
                }
            }
        }
    };
    HttpInterface.JsonParser getAvailableBeauticianListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<BeauticianBean> beauticianList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                if(dataObj.has("beauticianList")){
                    JSONArray beauticianArray = dataObj.getJSONArray("beauticianList");
                    for(int i=0;i<beauticianArray.length();i++){
                        JSONObject beauticianObj = beauticianArray.getJSONObject(i);
                        BeauticianBean bean = new BeauticianBean.Builder(beauticianObj.getString("beautyName"))
                                .id(beauticianObj.getString("beautyId"))
                                .imgUrl(beauticianObj.getString("beautyPhoto"))
                                .goodAppraiseNum(beauticianObj.getInt("goodAppraiseNum")+"")
                                .orderNum(beauticianObj.getInt("orderNum")+"")
                                .intruduction(StringUtils.replaceBlank(beauticianObj.getString("introduction")))
                                .build();

                        beauticianList.add(bean);
                    }
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,beauticianList};
        }
    };

    public static class ListWrapper{
        public List<BeauticianBean> dataList = new ArrayList<>();
        public int pageIndex;
        public boolean isNoMoreData = false;
    }
}
