package com.mdj.moudle.beautician.presenter;
import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.beautician.bean.BeauticianTag;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.MdjLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class BeauticianDetailPresenter implements BeauticianDetailContract.Presenter{
    private final BeauticianDetailContract.View view;
    private List<BeauticianProjectListWrapper> inHomeListWrapperList,toShopListWrapperList;
    private String beauticianId;
    private boolean isProcessing = false;

/********************************************************************************************************/
    public BeauticianDetailPresenter(@NonNull BeauticianDetailContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        int projectCategoryNum = CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().size();  //上门的和到店的类别都是一样的
        inHomeListWrapperList = new ArrayList<>();
        toShopListWrapperList = new ArrayList<>();
        for(int i=0;i<projectCategoryNum;i++){
            inHomeListWrapperList.add(new BeauticianProjectListWrapper());
            toShopListWrapperList.add(new BeauticianProjectListWrapper());
        }
    }

    @Override
    public void start() {

    }

    private void getBeauticianDetail(String beauticianId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("beauticianId",beauticianId);
            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTICIAN_DETAIL;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeauticianDetailDataCallback)
                    .jsonParser(getBeauticianDetailJsonParser)
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
    HttpInterface.DataCallback getBeauticianDetailDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
        if(result == HttpConstant.SUCCESS){
            view.updateUI(data);
        }else{
            view.showDisconnect(data.toString());
        }
        }
    };
    HttpInterface.JsonParser getBeauticianDetailJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            BeauticianBean beauticianBean;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                //tag
                Set<BeauticianTag> tagSet = new HashSet<>();
                JSONArray tagList = dataObj.getJSONArray("tagList");
                for(int i=0;i<tagList.length();i++){
                    JSONObject tagObj = tagList.getJSONObject(i);
                    tagSet.add(new BeauticianTag(tagObj.getString("tagName"),tagObj.getString("tagNum")));
                }
                //beauticianInfo
                JSONObject beauticianInfo = dataObj.getJSONObject("beauticianInfo");
                beauticianBean = new BeauticianBean.Builder(beauticianInfo.getString("name"))
                        .id(beauticianInfo.getString("id"))
                        .imgUrl(beauticianInfo.getString("headImg"))
                        .experience(beauticianInfo.getString("experience"))
                        .intruduction(beauticianInfo.getString("introduction"))
                        .orderNum(beauticianInfo.getString("orderNum"))
                        .tagSet(tagSet)
                        .department(dataObj.getJSONObject("shopInfo").getString("home_name"))
                        .build();

            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,beauticianBean};
        }
    };
    @Override
    public void getInitData(String beauticianId) {
        this.beauticianId = beauticianId;
        getBeauticianDetail(beauticianId);
        getBeauticianProjectList(beauticianId,++inHomeListWrapperList.get(0).pageIndex,CommonConstant.SERVICE_TYPE_IN_HOME,
                0);
    }

    @Override
    public void loadMoreData(String beauticianId,int serviceType,int tabIndex) {
        if(isProcessing){
            return;
        }
        if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME){
            if(!inHomeListWrapperList.get(tabIndex).isNoMoreData){
                getBeauticianProjectList(beauticianId,++inHomeListWrapperList.get(tabIndex).pageIndex,serviceType,tabIndex);
            }
        }else{  //到店
            if(!toShopListWrapperList.get(tabIndex).isNoMoreData){
                getBeauticianProjectList(beauticianId,++toShopListWrapperList.get(tabIndex).pageIndex,serviceType,tabIndex);
            }
        }
    }

    @Override
    public void getSelectedGoods(int tabIndex,int position,int serviceType) {
        if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME){
            view.updateShoppingCartWidget(inHomeListWrapperList.get(tabIndex).projectList.get(position));
        }else{
            view.updateShoppingCartWidget(toShopListWrapperList.get(tabIndex).projectList.get(position));
        }
    }
    @Override
    public void getBeauticianProjectListPre(String beauticianId, int serviceType,int tabIndex) {
        if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME){
            //更新ui
            if(inHomeListWrapperList.get(tabIndex).projectList.size()>0
                    ||inHomeListWrapperList.get(tabIndex).isNoMoreData
                    ){
                view.updateProjectList(inHomeListWrapperList.get(tabIndex).projectList); //直接返回
            }else{
                getBeauticianProjectList(beauticianId,
                        ++inHomeListWrapperList.get(tabIndex).pageIndex,
                        serviceType,
                        tabIndex);
            }
        }else{  //到店
            if(toShopListWrapperList.get(tabIndex).projectList.size()>0
                    ||toShopListWrapperList.get(tabIndex).isNoMoreData
                    ){
                view.updateProjectList(toShopListWrapperList.get(tabIndex).projectList); //直接返回
            }else{
                getBeauticianProjectList(beauticianId,
                        ++toShopListWrapperList.get(tabIndex).pageIndex,
                        serviceType,
                        tabIndex
                );
            }
        }
    }
    public void getBeauticianProjectList(String beauticianId, int pageIndex,int serviceType,int tabIndex) {
        isProcessing = true;
        MdjLog.log("getBeauticianProjectList");
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("beauticianId",beauticianId);
            map.put("userId",CacheManager.getInstance().getUserBean().getId());
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());
            map.put("serviceType",serviceType+"");
            String projectCategory = CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().get(tabIndex).getId();
            map.put("kind",projectCategory);
            map.put("page",pageIndex+"");
            map.put("pageSize",CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTICIAN_PROJECT_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeauticianProjectListDataCallback)
                    .jsonParser(getBeauticianProjectListJsonParser)
                    .tagObj(new Object[]{tabIndex,serviceType})
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

    HttpInterface.DataCallback getBeauticianProjectListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            Object[] objs = (Object[])tagObj;
            int tabIndex = (int)objs[0];
            int serviceType = (int)objs[1];

            if(result == HttpConstant.SUCCESS){
                List<ProjectBean> projectList = (List<ProjectBean>)data;
                if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME){
                    if(projectList.isEmpty()){
                        inHomeListWrapperList.get(tabIndex).isNoMoreData = true;
                    }
                    inHomeListWrapperList.get(tabIndex).projectList.addAll(projectList);
                    view.updateProjectList(inHomeListWrapperList.get(tabIndex).projectList);
                }else{
                    if(projectList.isEmpty()){
                        toShopListWrapperList.get(tabIndex).isNoMoreData = true;
                    }
                    toShopListWrapperList.get(tabIndex).projectList.addAll(projectList);
                    view.updateProjectList(toShopListWrapperList.get(tabIndex).projectList);
                }
            }else{
                if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME){
                    if(inHomeListWrapperList.get(tabIndex).pageIndex>0){
                        inHomeListWrapperList.get(tabIndex).pageIndex--;
                    }
                }else{  //到店
                    if(toShopListWrapperList.get(tabIndex).pageIndex>0){
                        toShopListWrapperList.get(tabIndex).pageIndex--;
                    }
                }
                view.showDisconnect(data.toString());
            }
            isProcessing = false;
        }
    };
    HttpInterface.JsonParser getBeauticianProjectListJsonParser = new HttpInterface.JsonParser() {
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
                    projectBean.setIsExtraProject(projectObj.getJSONObject("pInfo").getInt("kind")==CommonConstant.PROJECT_TYPE_EXTRA?true:false);
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
    public void getBeauticianDetail(String beauticianId, int serviceType) {
        this.beauticianId = beauticianId;
        getBeauticianDetail(beauticianId);
        int pageIndex = 1;
        if(CommonConstant.SERVICE_TYPE_IN_HOME == serviceType){
            pageIndex = ++inHomeListWrapperList.get(0).pageIndex;
        }else{
            pageIndex = ++toShopListWrapperList.get(0).pageIndex;
        }
        getBeauticianProjectList(beauticianId,pageIndex,serviceType,0);
    }

    private class BeauticianProjectListWrapper{
        private List<ProjectBean> projectList = new ArrayList<>();
        private int pageIndex;
        private boolean isNoMoreData = false;
    }
}
