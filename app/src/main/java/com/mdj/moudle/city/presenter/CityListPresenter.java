package com.mdj.moudle.city.presenter;/*
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
import com.mdj.moudle.city.CityBean;
import com.mdj.moudle.city.MapPoint;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CharacterParser;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class CityListPresenter implements CityListContract.Presenter,HttpInterface.JsonParser,HttpInterface.DataCallback, HttpInterface.Validator {
    private final CityListContract.View mCityListView;

    public CityListPresenter(@NonNull CityListContract.View cityListView) {
        mCityListView = cityListView;
        mCityListView.setPresenter(this);
    }

    @Override
    public void start() {
        if (CacheManager.getInstance().getCityList().size() > 0) {// cityList只获取一次：程序启动后、首次使用时，加载一次
			mCityListView.updateUI(CacheManager.getInstance().getCityList());
			return;
		}
        getCityList();
    }

    private void getCityList() {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            mCityListView.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            String url = HttpConstant.NEW_SERVER_URL + Constant.GET_CITY_lIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET,url)
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
            mCityListView.showDisconnect("联网失败，请检查网络后重试");
        }
    }

    @Override
    public void requestCallback(int result,Object data,Object tagObj) {
        mCityListView.closeLoading();
        if(result == HttpConstant.SUCCESS){
            mCityListView.updateUI(CacheManager.getInstance().getCityList());
        }else{
            mCityListView.showDisconnect(data.toString());
        }
    }

    @Override
    public Object[] parse(String str) throws JSONException {
        JSONTokener jsonParser = new JSONTokener(str);
        JSONObject obj = (JSONObject) jsonParser.nextValue();
        int err = obj.getInt("err");
        if (err == 0) {
            JSONArray cityList = obj.getJSONArray("data");
            CacheManager.getInstance().getCityList().clear();
            for (int i = 0; i < cityList.length(); i++) {
                JSONObject obj2 = (JSONObject) cityList.get(i);
                CityBean bean = new CityBean();
                bean.setId(obj2.getString("id"));
                bean.setImageUrl(obj2.getString("iconUrl"));
                bean.setName(obj2.getString("name"));
                bean.setServiceScope(obj2.getString("description"));
                bean.setIsHot(Integer.valueOf(obj2.getString("isHot")));
                String pinyin = CharacterParser.getInstance().getSelling(bean.getName());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                bean.setPys(sortString);

                JSONArray serviceArea = obj2.getJSONArray("serviceArea");
                for (int m = 0; m < serviceArea.length(); m++) {//有m个区域
                    JSONArray pointList = (JSONArray) serviceArea.get(m);
                    List<MapPoint> list = new ArrayList<>();
                    for(int n = 0; n < pointList.length(); n++){//每个区域有n个坐标
                        JSONObject pointObj = (JSONObject) pointList.get(n);
                        MapPoint point = new MapPoint();
                        point.setLat(pointObj.getString("la"));
                        point.setLng(pointObj.getString("ln"));
                        list.add(point);
                    }
                    bean.getAreaPointList().add(list);
                }
                CacheManager.getInstance().getCityList().add(bean);
            }
        } else {
            String msg = obj.getString("msg");
            return new Object[]{HttpConstant.FAIL,msg};
        }
        return new Object[]{HttpConstant.SUCCESS,CacheManager.getInstance().getCityList()};
    }

    @Override
    public void validate(Object obj) throws NetDataInvalidException {
        try {
            for(CityBean b : CacheManager.getInstance().getCityList()){
                if(new BigInteger(b.getId()).compareTo(BigInteger.ZERO)!=1){//城市的id应该大于等于0
                    throw new NetDataInvalidException("网络数据校验异常-->checkCityListData 城市的id : "
                            + b.getId() +" 不是整数");
                }
            }
        } catch (Exception e) {
            throw new NetDataInvalidException("CityListPresenter validater: err:"+e.toString());
        }
    }

    @Override
    public void updateGlobalCityBean(CityBean cityBean) {
        CacheManager.getInstance().setGlobalCity(cityBean);
    }
}
