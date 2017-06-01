package com.mdj.moudle.beautyParlor.presenter;
import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.beautyParlor.BeautyParlorBean;
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

public class BeautyParlorDetailPresenter implements BeautyParlorDetailContract.Presenter{
    private final BeautyParlorDetailContract.View view;
    private List<BeautyParlorProjectListWrapper> ListWrapperList;
/**************************************************************************************************/
    public BeautyParlorDetailPresenter(@NonNull BeautyParlorDetailContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        int projectCategoryNum = CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().size();  //上门的和到店的类别都是一样的
        ListWrapperList = new ArrayList<>();
        for(int i=0;i<projectCategoryNum;i++){
            ListWrapperList.add(new BeautyParlorProjectListWrapper());
        }
    }

    @Override
    public void start() {

    }

    private void getBeautyParlorDetail(String beautyParlorId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("shopId",beautyParlorId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTY_PARLOR_DETAIL;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeautyParlorDetailDataCallback)
                    .jsonParser(getBeautyParlorDetailJsonParser)
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

    HttpInterface.DataCallback getBeautyParlorDetailDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
//            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateUI(data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getBeautyParlorDetailJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            BeautyParlorBean beautyParlorBean;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                beautyParlorBean = new BeautyParlorBean.Builder()
                        .id(dataObj.getJSONObject("shopInfo").getString("id"))
                        .name(dataObj.getJSONObject("shopInfo").getString("name"))
                        .address(dataObj.getJSONObject("shopInfo").getString("address"))
                        .tel(dataObj.getJSONObject("shopInfo").getString("tel"))
                        .imgUrl(dataObj.getJSONObject("shopInfo").getString("image"))
                        .intruduction(dataObj.getJSONObject("shopInfo").getString("introduction"))
                        .orderNum(dataObj.getJSONObject("shopInfo").getInt("orderNum")+"")
                        .build();
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,beautyParlorBean};
        }
    };

    private void getBeautyParlorProjectList(String beautyParlorId,int tabIndex) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("shopId", beautyParlorId);
            String projectCategory = CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().get(tabIndex).getId();
            map.put("kind",projectCategory);
            map.put("userId", CacheManager.getInstance().getUserBean().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTY_PARLOR_PROJECT_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeautyParlorProjectListDataCallback)
                    .jsonParser(getBeautyParlorProjectListJsonParser)
                    .tagObj(tabIndex)
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

    HttpInterface.DataCallback getBeautyParlorProjectListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            int tabIndex = (int)tagObj;
            if(result == HttpConstant.SUCCESS){
                List<ProjectBean> projectList = (List<ProjectBean>)data;
                if(projectList.isEmpty()){
                    ListWrapperList.get(tabIndex).isNoMoreData = true;
                }
                ListWrapperList.get(tabIndex).projectList.addAll(projectList);
                view.refreshProjectList(ListWrapperList.get(tabIndex).projectList);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getBeautyParlorProjectListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<ProjectBean> dataList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray projectList = obj.getJSONArray("data");
                for (int i = 0; i < projectList.length(); i++) {
                    JSONObject projectObj = (JSONObject) projectList.get(i);
                    ProjectBean projectBean = new ProjectBean();
                    projectBean.setName(projectObj.getJSONObject("pInfo").getString("name"));
                    projectBean.setId(projectObj.getJSONObject("pInfo").getString("id"));
                    projectBean.setOldPrice(projectObj.getJSONObject("pInfo").getInt("marketPrice"));
                    projectBean.setPrice(projectObj.getJSONObject("pInfo").getInt("price"));
                    JSONArray tagArray = projectObj.getJSONObject("pInfo").getJSONArray("tag");
                    for(int j = 0; j < tagArray.length(); j++){
                        projectBean.getEfficiencySet().add(tagArray.getString(j));
                    }
                    projectBean.setType(projectObj.getJSONObject("pInfo").getInt("type"));
                    projectBean.setDuration(projectObj.getJSONObject("pInfo").getInt("serviceTime"));
                    projectBean.setRecommendNum(projectObj.getJSONObject("pInfo").getInt("serviceCount"));
                    projectBean.setImageUrl(projectObj.getJSONObject("pInfo").getString("image"));
                    /*sale info*/
                    if(projectObj.has("saleInfo")){
                        projectBean.setStartTime(projectObj.getJSONObject("saleInfo").getString("startTime"));
                        projectBean.setEndTime(projectObj.getJSONObject("saleInfo").getString("endTime"));
                        projectBean.setTotal(Integer.valueOf(projectObj.getJSONObject("saleInfo").getString("total")));
                        projectBean.setSoldNum(Integer.valueOf(projectObj.getJSONObject("saleInfo").getString("soldNum")));
                        projectBean.setPrivilegePrice(Integer.valueOf(projectObj.getJSONObject("saleInfo").getString("preferentialPrice")));
                    }
                    projectBean.setIsExtraProject(projectObj.getJSONObject("pInfo").getInt("kind")== CommonConstant.PROJECT_TYPE_EXTRA?true:false);
                    dataList.add(projectBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };
    @Override
    public void initData(String beautyParlorId) {
        getBeautyParlorDetail(beautyParlorId);
        getBeautyParlorProjectList(beautyParlorId,0);
    }

    @Override
    public void loadMoreData(String beautyParlorId, int tabIndex) {
        if(!ListWrapperList.get(tabIndex).isNoMoreData){
            getBeautyParlorProjectList(beautyParlorId,tabIndex);
        }
    }

    @Override
    public void getBeautyParlorProjectListPre(String beautyParlorId, int tabIndex) {
        if(ListWrapperList.get(tabIndex).projectList.size()>0
                ||ListWrapperList.get(tabIndex).isNoMoreData
                ){
            view.refreshProjectList(ListWrapperList.get(tabIndex).projectList); //直接返回
        }else{
            getBeautyParlorProjectList(beautyParlorId, tabIndex);
        }
    }

    private class BeautyParlorProjectListWrapper{
        private List<ProjectBean> projectList = new ArrayList<>();
        private int pageIndex;
        private boolean isNoMoreData = false;

    }
}
