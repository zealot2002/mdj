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
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.project.bean.ProjectQualityBean;
import com.mdj.moudle.project.bean.ProjectWrapperBean;
import com.mdj.moudle.userPackage.PackageBean;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;


public class PackageDetailPresenter implements PackageDetailContract.Presenter{
    private final PackageDetailContract.View view;

/**************************************************************************************************/

    public PackageDetailPresenter(@NonNull PackageDetailContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
    }


    @Override
    public void getPackageDetail(String packageId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("packageId",packageId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_PACKAGE_INFO;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getPackageDetailDataCallback)
                    .jsonParser(getPackageDetailJsonParser)
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

    HttpInterface.DataCallback getPackageDetailDataCallback = new HttpInterface.DataCallback() {
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
    HttpInterface.JsonParser getPackageDetailJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            PackageBean packageBean = new PackageBean();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject packageObj = obj.getJSONObject("data");

                packageBean.setId(packageObj.getString("id"));
                packageBean.setName(packageObj.getString("name"));
                packageBean.setPrice(packageObj.getInt("price"));
                packageBean.setOldPrice(packageObj.getInt("marketPrice"));

                packageBean.setImgUrl(packageObj.getString("image"));
                packageBean.setTotalOrderNum(packageObj.getInt("totalOrderNum"));
                packageBean.setOriginPlace(packageObj.getString("producer"));
                packageBean.setRemarksTitle(packageObj.getJSONObject("packageNotice").getString("title"));
                packageBean.setRemarks(packageObj.getJSONObject("packageNotice").getString("content"));
                packageBean.setValidityDays(packageObj.getInt("durDay") + "");
                packageBean.setSaveMoney(packageObj.getInt("saveMoney"));
                packageBean.setGuideHtml(packageObj.getString("useMethod"));

                packageBean.setType(packageObj.getInt("type"));
                /*sale info*/
                if(packageObj.has("saleInfo")){
                    packageBean.setStartTime(packageObj.getJSONObject("saleInfo").getString("startTime"));
                    packageBean.setEndTime(packageObj.getJSONObject("saleInfo").getString("endTime"));
                    packageBean.setTotal(Integer.valueOf(packageObj.getJSONObject("saleInfo").getString("total")));
                    packageBean.setSoldNum(Integer.valueOf(packageObj.getJSONObject("saleInfo").getString("soldNum")));
                    packageBean.setPrivilegePrice(Integer.valueOf(packageObj.getJSONObject("saleInfo").getString("preferentialPrice")));
                    packageBean.setPerCount(packageObj.getJSONObject("saleInfo").getInt("perCount"));
                }

                /*項目集合*/
                JSONArray projectList = packageObj.getJSONArray("projectList");
                for(int i=0;i<projectList.length();i++){
                    JSONObject projectObj = projectList.getJSONObject(i);
                    ProjectWrapperBean wrapperBean = new ProjectWrapperBean();
                    wrapperBean.setId(projectObj.getString("pid"));
                    wrapperBean.setImageUrl(projectObj.getString("image"));
                    wrapperBean.setName(projectObj.getString("name"));
                    wrapperBean.setPrice(projectObj.getInt("price"));
                    wrapperBean.setDuration(projectObj.getInt("serviceTime"));
                    wrapperBean.setNum(projectObj.getInt("num"));
                    packageBean.getProjectList().add(wrapperBean);
                }
                /*质量集合*/
                JSONArray qualityList = packageObj.getJSONArray("labelB");
                for(int i=0;i<qualityList.length();i++){
                    JSONObject qualityObj = qualityList.getJSONObject(i);
                    ProjectQualityBean qualityBean = new ProjectQualityBean();
                    qualityBean.setName(qualityObj.getString("tag_name"));
                    qualityBean.setImageUrl(qualityObj.getString("tag_icon"));
                    packageBean.getQualityList().add(qualityBean);
                }
                /*亮点*/
                JSONArray tagList = packageObj.getJSONArray("labelC");
                for(int i=0;i<tagList.length();i++){
                    JSONObject tagObj = tagList.getJSONObject(i);
                    packageBean.getTagList().add(tagObj.getString("tag_name"));
                }
                /*Extra pics*/
                JSONArray extraImgUrlList = packageObj.getJSONArray("packageImage");
                for(int i=0;i<extraImgUrlList.length();i++){
                    packageBean.getExtraImgUrlList().add(extraImgUrlList.getString(i));
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }

            return new Object[]{HttpConstant.SUCCESS,packageBean};
        }
    };
}
