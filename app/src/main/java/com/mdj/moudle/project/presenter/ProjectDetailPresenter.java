package com.mdj.moudle.project.presenter;/*
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
import com.mdj.moudle.project.bean.CommentBean;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.project.bean.ProjectQualityBean;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class ProjectDetailPresenter implements ProjectDetailContract.Presenter{
    private final ProjectDetailContract.View view;

    private int currentPageIndex = 0;
    private boolean isNoMoreData = false;
    private List<CommentBean> projectCommentList = new ArrayList<>();
/**************************************************************************************************/

    public ProjectDetailPresenter(@NonNull ProjectDetailContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void getProjectCommentListPre(String projectId) {
        if(projectCommentList.isEmpty()&&!isNoMoreData){
            getProjectCommentList(projectId);
        } else{
            view.updateCommentList(projectCommentList);
        }
    }

    private void getProjectCommentList(String projectId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("id",projectId);
            map.put("page",++currentPageIndex+"");
            map.put("pageSize", CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_PROJECT_COMMENT_LIST;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getProjectCommentListDataCallback)
                    .jsonParser(getProjectCommentListJsonParser)
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

    HttpInterface.DataCallback getProjectCommentListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                List<CommentBean> dataList = (List<CommentBean>)data;
                if(dataList.isEmpty()){
                    isNoMoreData = true;
                }else{
                    projectCommentList.addAll(dataList);
                }
                view.updateCommentList(projectCommentList);
            }else{
                --currentPageIndex;
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getProjectCommentListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<CommentBean> commentBeanList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray commentArray = obj.getJSONArray("data");
                for(int i=0;i<commentArray.length();i++){
                    JSONObject commentObj = commentArray.getJSONObject(i);
                    commentBeanList.add(new CommentBean.Builder()
                            .beauticianImageUrl(commentObj.getString("beautyHeadImg"))
                            .beauticianName(commentObj.getString("beautyName"))
                            .content(commentObj.getString("comment"))
                            .createTime(commentObj.getString("time"))
                            .userName(commentObj.getString("userName"))
                            .userImageUrl(commentObj.getString("userHeadImg"))
                            .build());
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,commentBeanList};
        }
    };

    @Override
    public void getProjectDetail(String projectId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId",CacheManager.getInstance().getGlobalCity().getId());
            map.put("id",projectId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_PROJECT_DETAIL;

            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getProjectDetailDataCallback)
                    .jsonParser(getProjectDetailJsonParser)
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

    HttpInterface.DataCallback getProjectDetailDataCallback = new HttpInterface.DataCallback() {
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
    HttpInterface.JsonParser getProjectDetailJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            ProjectBean projectBean = new ProjectBean();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject projectObj = obj.getJSONObject("data");

                projectBean.setId(projectObj.getString("id"));
                projectBean.setName(projectObj.getString("name"));
                projectBean.setDuration(projectObj.getInt("serviceTime"));
                projectBean.setPrice(projectObj.getInt("price"));
                projectBean.setOldPrice(projectObj.getInt("marketPrice"));
                projectBean.setType(projectObj.getInt("type"));
                /*sale info*/
                if(projectObj.has("saleInfo")){
                    projectBean.setStartTime(projectObj.getJSONObject("saleInfo").getString("startTime"));
                    projectBean.setPerCount(projectObj.getJSONObject("saleInfo").getInt("perCount"));
                    projectBean.setEndTime(projectObj.getJSONObject("saleInfo").getString("endTime"));
                    projectBean.setTotal(Integer.valueOf(projectObj.getJSONObject("saleInfo").getString("total")));
                    projectBean.setSoldNum(Integer.valueOf(projectObj.getJSONObject("saleInfo").getString("soldNum")));
                    projectBean.setPrivilegePrice(Integer.valueOf(projectObj.getJSONObject("saleInfo").getString("preferentialPrice")));
                }

                projectBean.setImageUrl(projectObj.getString("image"));
                projectBean.setTotalOrderNum(projectObj.getInt("totalOrderNum"));
                projectBean.setOriginPlace(projectObj.getString("producer"));
                projectBean.setRemarksTitle(projectObj.getJSONObject("productNotice").getString("title"));
                projectBean.setRemarks(projectObj.getJSONObject("productNotice").getString("content"));
                projectBean.setAssessmentNum(projectObj.getInt("assessmentNum"));
                /*质量集合*/
                JSONArray projectQualityList = projectObj.getJSONArray("labelB");
                for(int i=0;i<projectQualityList.length();i++){
                    JSONObject qualityObj = projectQualityList.getJSONObject(i);
                    ProjectQualityBean qualityBean = new ProjectQualityBean();
                    qualityBean.setName(qualityObj.getString("tag_name"));
                    qualityBean.setImageUrl(qualityObj.getString("tag_icon"));
                    projectBean.getProjectQualityList().add(qualityBean);
                }
                /*亮点*/
                JSONArray projectTagList = projectObj.getJSONArray("labelC");
                for(int i=0;i<projectTagList.length();i++){
                    JSONObject tagObj = projectTagList.getJSONObject(i);
                    projectBean.getProjectTagList().add(tagObj.getString("tag_name"));
                }
                /*Extra pics*/
                JSONArray extraImgUrlList = projectObj.getJSONArray("productImage");
                for(int i=0;i<extraImgUrlList.length();i++){
                    projectBean.getExtraImgUrlList().add(extraImgUrlList.getString(i));
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,projectBean};
        }
    };

    @Override
    public void loadMoreData(String projectId) {
        if(!isNoMoreData) {
            getProjectCommentList(projectId);
        }
    }
}
