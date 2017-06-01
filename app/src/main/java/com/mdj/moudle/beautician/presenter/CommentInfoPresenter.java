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
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.beautician.bean.BeauticianCommentNumBean;
import com.mdj.moudle.beautician.bean.CommentBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class CommentInfoPresenter implements CommentInfoContract.Presenter{
    private final CommentInfoContract.View view;
    private String beauticianId;
    private CommmentListWrapper allWrapper,goodWrapper,middleWrapper,badWrapper;
/**************************************************************************************************/

    public CommentInfoPresenter(@NonNull CommentInfoContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        allWrapper = new CommmentListWrapper();
        goodWrapper = new CommmentListWrapper();
        middleWrapper = new CommmentListWrapper();
        badWrapper = new CommmentListWrapper();
    }

    @Override
    public void start() {

    }
    @Override
    public void getBeauticianCommentListPre(int commentType) {
        switch (commentType){
            case CommonConstant.COMMENT_TYPE_ALL:
                if(allWrapper.commentList.size()>0
                        ||allWrapper.isNoMoreData
                        ){
                    view.refreshCommentList(commentType,allWrapper.commentList);
                    return; //直接返回
                }
                break;

            case CommonConstant.COMMENT_TYPE_GOOD:
                if(goodWrapper.commentList.size()>0
                        ||goodWrapper.isNoMoreData
                        ){
                    view.refreshCommentList(commentType,goodWrapper.commentList);
                    return;
                }
                break;

            case CommonConstant.COMMENT_TYPE_MIDDLE:
                if(middleWrapper.commentList.size()>0
                        ||middleWrapper.isNoMoreData
                        ){
                    view.refreshCommentList(commentType,middleWrapper.commentList);
                    return;
                }
                break;

            case CommonConstant.COMMENT_TYPE_BAD:
                if(badWrapper.commentList.size()>0
                        ||badWrapper.isNoMoreData
                        ){
                    view.refreshCommentList(commentType,badWrapper.commentList);
                    return;
                }
                break;
        }
        getBeauticianCommentList(commentType);
    }
    private void getBeauticianCommentList(int commentType) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            int pageIndex = 1;
            switch (commentType){
                case CommonConstant.COMMENT_TYPE_GOOD:
                    pageIndex = ++goodWrapper.pageIndex;
                    break;
                case CommonConstant.COMMENT_TYPE_MIDDLE:
                    pageIndex = ++middleWrapper.pageIndex;
                    break;
                case CommonConstant.COMMENT_TYPE_BAD:
                    pageIndex = ++badWrapper.pageIndex;
                    break;
                case CommonConstant.COMMENT_TYPE_ALL:
                    pageIndex = ++allWrapper.pageIndex;
                    break;
            }
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("beauticianId",beauticianId);
            map.put("type",commentType+"");
            map.put("pageStart", "" + pageIndex);
            map.put("pageSize", CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTICIAN_COMMENT_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeauticianCommentListDataCallback)
                    .jsonParser(getBeauticianCommentListJsonParser)
                    .tagObj(commentType)
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

    HttpInterface.DataCallback getBeauticianCommentListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            int commentType = (int)tagObj;
            if(result == HttpConstant.SUCCESS){
                List<CommentBean> dataList = (List<CommentBean>)data;
                if(dataList.isEmpty()){
                    setCurrentListNoMoreData(commentType);
                }
                view.refreshCommentList(commentType,getCurrentList(commentType, dataList));
            }else{
                view.showDisconnect(data.toString());
                rollbackPageIndex(commentType);
            }
        }
    };
    HttpInterface.JsonParser getBeauticianCommentListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<CommentBean> commentList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                if(dataObj.has("appriaseList")){
                    JSONArray commentArray = dataObj.getJSONArray("appriaseList");
                    for(int i=0;i<commentArray.length();i++) {
                        JSONObject o = commentArray.getJSONObject(i);
                        CommentBean bean = new CommentBean.Builder("")
                                .content(o.getString("appriseContent"))
                                .projectName(o.getString("projectName"))
                                .time(o.getString("createTime"))
                                .userHeaderUrl(o.getString("userPhoto"))
                                .userName(o.getString("userName"))
                                .build();
                        commentList.add(bean);
                    }
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,commentList};
        }
    };

    private void getBeauticianCommentNum(String beauticianId) {
        this.beauticianId = beauticianId;
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("beauticianId",beauticianId);
            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTICIAN_COMMENT_NUM_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeauticianCommentNumDataCallback)
                    .jsonParser(getBeauticianCommentNumJsonParser)
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

    HttpInterface.DataCallback getBeauticianCommentNumDataCallback = new HttpInterface.DataCallback() {
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
    HttpInterface.JsonParser getBeauticianCommentNumJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            BeauticianCommentNumBean beauticianCommentNumBean;
            List<ProjectBean> projectList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                beauticianCommentNumBean = new BeauticianCommentNumBean.Builder()
                        .goodNum(dataObj.getInt("good"))
                        .middleNum(dataObj.getInt("middle"))
                        .badNum(dataObj.getInt("bad"))
                        .build();
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,beauticianCommentNumBean};
        }
    };

    private void setCurrentListNoMoreData(int commentType){
        switch (commentType){
            case CommonConstant.COMMENT_TYPE_ALL:
                allWrapper.isNoMoreData = true;
                break;
            case CommonConstant.COMMENT_TYPE_GOOD:
                goodWrapper.isNoMoreData = true;
                break;
            case CommonConstant.COMMENT_TYPE_MIDDLE:
                middleWrapper.isNoMoreData = true;
                break;
            case CommonConstant.COMMENT_TYPE_BAD:
                badWrapper.isNoMoreData = true;
                break;
            default:
                break;
        }
    }
    private List<CommentBean> getCurrentList(int commentType,List<CommentBean> list){
        switch (commentType){
            case CommonConstant.COMMENT_TYPE_ALL:
                allWrapper.commentList.addAll(list);
                return allWrapper.commentList;

            case CommonConstant.COMMENT_TYPE_GOOD:
                goodWrapper.commentList.addAll(list);
                return goodWrapper.commentList;

            case CommonConstant.COMMENT_TYPE_MIDDLE:
                middleWrapper.commentList.addAll(list);
                return middleWrapper.commentList;

            case CommonConstant.COMMENT_TYPE_BAD:
                badWrapper.commentList.addAll(list);
                return badWrapper.commentList;
            default:
                return allWrapper.commentList;
        }
    }
    private void rollbackPageIndex(int commentType){
        switch (commentType){
            case CommonConstant.COMMENT_TYPE_ALL:
                if(allWrapper.pageIndex>0)
                    allWrapper.pageIndex--;
                break;
            case CommonConstant.COMMENT_TYPE_GOOD:
                if(goodWrapper.pageIndex>0)
                    goodWrapper.pageIndex--;
                break;
            case CommonConstant.COMMENT_TYPE_MIDDLE:
                if(middleWrapper.pageIndex>0)
                    middleWrapper.pageIndex--;
                break;
            case CommonConstant.COMMENT_TYPE_BAD:
                if(badWrapper.pageIndex>0)
                    badWrapper.pageIndex--;
                break;
        }
    }
    @Override
    public void getAllData(String beauticianId) {
        getBeauticianCommentNum(beauticianId);
        getBeauticianCommentList(CommonConstant.COMMENT_TYPE_ALL);
    }
    @Override
    public void loadMoreData(int commentType) {
        switch (commentType){
            case CommonConstant.COMMENT_TYPE_ALL:
                if(allWrapper.isNoMoreData)
                    return;
                break;
            case CommonConstant.COMMENT_TYPE_GOOD:
                if(goodWrapper.isNoMoreData)
                    return;
                break;
            case CommonConstant.COMMENT_TYPE_MIDDLE:
                if(middleWrapper.isNoMoreData)
                    return;
                break;
            case CommonConstant.COMMENT_TYPE_BAD:
                if(badWrapper.isNoMoreData)
                    return;
                break;
        }
        getBeauticianCommentList(commentType);
    }

    private class CommmentListWrapper{
        public List<CommentBean> commentList = new ArrayList<>();
        public int pageIndex;
        public boolean isNoMoreData = false;

    }
}
