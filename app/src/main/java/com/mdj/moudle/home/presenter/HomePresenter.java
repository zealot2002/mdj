package com.mdj.moudle.home.presenter;

import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.home.bean.BannerBean;
import com.mdj.moudle.home.bean.HomeDataCache;
import com.mdj.moudle.home.bean.ProjectCategoryBean;
import com.mdj.moudle.home.bean.TabProjectListCache;
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
import java.util.LinkedHashMap;
import java.util.List;
/*
*
*
*
*
*
*
* */
public class HomePresenter implements HomeContract.Presenter{
    private final HomeContract.View view;


/****************************************************************************************************/
    public HomePresenter(@NonNull HomeContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        getBannerList();
        getSuggestProjectList(CommonConstant.SERVICE_TYPE_IN_HOME);
    }

    private void getSuggestProjectList(int serviceType) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
//            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());
            map.put("serviceType",serviceType+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_SUGGEST_PROJECT_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getSuggestProjectListDataCallback)
                    .jsonParser(getSuggestProjectListJsonParser)
                    .tagObj(serviceType)
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

    private void getBannerList() {
        MdjLog.log("getBannerList");
        if(!CacheManager.getInstance().getBannerList().isEmpty()){
//            view.updateBanner(CacheManager.getInstance().getBannerList());
            return;
        }
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
//            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BANNER_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBannerListDataCallback)
                    .jsonParser(getBannerListJsonParser)
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
    /*如果cache不空，则不刷新，因为ui已经画出来了*/
    @Override
    public void getNormalProjectListPre(int tabIndex, int serviceType) {
        MdjLog.log("getNormalProjectListPre");
        TabProjectListCache.TabProjectListWrapper wrapper;
        if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
            wrapper = CacheManager.getInstance().getInHomeTabProjectListCache().getTabProjectListMap().get(tabIndex);
        }else{
            wrapper = CacheManager.getInstance().getToShopTabProjectListCache().getTabProjectListMap().get(tabIndex);
        }
        /*只有当list为空，并且不是nomoredata时候，才去获取数据*/
        if(wrapper != null) {
            if(wrapper.projectList.isEmpty()&&!wrapper.isNoMoreData){
                getNormalProjectList(tabIndex, serviceType);
            }else{
                view.updateNormalProjectList(wrapper.projectList, tabIndex);
            }
        }
    }
    @Override
    public void loadMoreNormalProjectListPre(int tabIndex, int serviceType) {
        TabProjectListCache.TabProjectListWrapper wrapper;
        if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
            wrapper = CacheManager.getInstance().getInHomeTabProjectListCache().getTabProjectListMap().get(tabIndex);
        }else{
            wrapper = CacheManager.getInstance().getToShopTabProjectListCache().getTabProjectListMap().get(tabIndex);
        }
        /*只有当不是nomoredata时候，才去获取数据*/
        if(wrapper != null &&!wrapper.isNoMoreData) {
            getNormalProjectList(tabIndex, serviceType);
        }
    }
    /*上门、到店按钮切换*/
    @Override
    public void getServiceDataPre(int serviceType) {
        getSuggestProjectList(serviceType);
    }
/*切换城市后触发*/
    @Override
    public void refreshAllData() {
        /*清除所有首页cache*/
        CacheManager.getInstance().getBannerList().clear();
        CacheManager.getInstance().getInHomeTabProjectListCache().clear();
        CacheManager.getInstance().getInHomeDataCache().clear();
        CacheManager.getInstance().getToShopDataCache().clear();
        CacheManager.getInstance().getToShopTabProjectListCache().clear();

        /*从头再来*/
        start();
    }

    @Override
    public void getSelectedSuggestProject(int serviceType, int position) {
        if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME) {
            view.updateShoppingCartWidget(CacheManager.getInstance().getInHomeDataCache().getSuggestProjectList().get(position));
        } else {
            view.updateShoppingCartWidget(CacheManager.getInstance().getToShopDataCache().getSuggestProjectList().get(position));
        }
    }

    @Override
    public void getSelectedNormalProject(int tabIndex, int serviceType, int position) {
        if(serviceType==CommonConstant.SERVICE_TYPE_IN_HOME){
            view.updateShoppingCartWidget(CacheManager.getInstance().getInHomeTabProjectListCache()
                    .getTabProjectListMap().get(tabIndex).projectList.get(position));
        }else{
            view.updateShoppingCartWidget(CacheManager.getInstance().getToShopTabProjectListCache()
                    .getTabProjectListMap().get(tabIndex).projectList.get(position));
        }
    }

    private String getProjectCategoryStr(int tabIndex, int serviceType){
        if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
            return CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().get(tabIndex).getId();
        }else{
            return CacheManager.getInstance().getToShopDataCache().getProjectCategoryList().get(tabIndex).getId();
        }
    }
    /*注意：调用一次 pageIndex自增1*/
    private int getProjectPageIndex(int tabIndex, int serviceType){
        if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
            return ++CacheManager.getInstance().getInHomeTabProjectListCache().getTabProjectListMap().get(tabIndex).pageIndex;
        }else{
            return ++CacheManager.getInstance().getToShopTabProjectListCache().getTabProjectListMap().get(tabIndex).pageIndex;
        }
    }
/*只有当对应tabIndex的cache为空时，或是loadmore时，才应该调用此函数，注意pageIndex自增长的！！*/
    private void getNormalProjectList(int tabIndex, int serviceType) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());
            map.put("kind", getProjectCategoryStr(tabIndex,serviceType));
            map.put("serviceType",serviceType+"");
            map.put("page",getProjectPageIndex(tabIndex,serviceType)+"");
            map.put("pageSize",CommonConstant.PAGE_SIZE+"");

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_HOME_PROJECT_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getNormalProjectListDataCallback)
                    .jsonParser(getNormalProjectListJsonParser)
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

    HttpInterface.DataCallback getNormalProjectListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result, Object data,Object tagObj) {
            view.closeLoading();
            Object[] objs = (Object[])tagObj;
            int tabIndex = (int)objs[0];
            int serviceType = (int)objs[1];

            if(result == HttpConstant.SUCCESS){
                List<ProjectBean> dataList = (List<ProjectBean>)data;
                if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
                    if(dataList.isEmpty()){
                        CacheManager.getInstance().getInHomeTabProjectListCache().getTabProjectListMap().get(tabIndex).isNoMoreData = true;
                    }else{
                        CacheManager.getInstance().getInHomeTabProjectListCache().getTabProjectListMap().get(tabIndex).projectList.addAll(dataList);
                    /*持久化一下*/
                        CacheManager.getInstance().setInHomeTabProjectListCache(CacheManager.getInstance().getInHomeTabProjectListCache());
                    }
                    view.updateNormalProjectList(CacheManager.getInstance().getInHomeTabProjectListCache().getTabProjectListMap().get(tabIndex).projectList, tabIndex);
                }else{
                    if(dataList.isEmpty()){
                        CacheManager.getInstance().getToShopTabProjectListCache().getTabProjectListMap().get(tabIndex).isNoMoreData = true;
                    }else{
                        CacheManager.getInstance().getToShopTabProjectListCache().getTabProjectListMap().get(tabIndex).projectList.addAll(dataList);
                    /*持久化一下*/
                        CacheManager.getInstance().setToShopTabProjectListCache(CacheManager.getInstance().getToShopTabProjectListCache());
                    }
                    view.updateNormalProjectList(CacheManager.getInstance().getToShopTabProjectListCache()
                            .getTabProjectListMap().get(tabIndex).projectList, tabIndex);
                }
            }else{
                if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
                    --CacheManager.getInstance().getInHomeTabProjectListCache().getTabProjectListMap().get(tabIndex).pageIndex;
                }else{
                    --CacheManager.getInstance().getToShopTabProjectListCache().getTabProjectListMap().get(tabIndex).pageIndex;
                }
                view.showDisconnect(data.toString());
                view.updateNormalProjectList(new ArrayList<ProjectBean>(), tabIndex);
            }
        }
    };
    HttpInterface.JsonParser getNormalProjectListJsonParser = new HttpInterface.JsonParser() {
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
                    projectBean.setIsExtraProject(projectObj.getJSONObject("pInfo").getInt("kind")==5?true:false);
                    dataList.add(projectBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };

    HttpInterface.DataCallback getBannerListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result, Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                CacheManager.getInstance().setBannerList((List<BannerBean>) data);
//                view.updateBanner((List<BannerBean>) data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getBannerListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<BannerBean> dataList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray bannerList = obj.getJSONArray("data");
                for (int i = 0; i < bannerList.length(); i++) {
                    JSONObject bannerObj = (JSONObject) bannerList.get(i);
                    BannerBean bannerBean = new BannerBean();
                    bannerBean.setTitle(bannerObj.getString("title"));
                    bannerBean.setImgUrl(bannerObj.getString("img_url"));
                    bannerBean.setLinkUrl(bannerObj.getString("link_url"));
                    bannerBean.setShareContent(bannerObj.getString("share_content"));
                    bannerBean.setShareImageUrl(bannerObj.getString("share_image_url"));
                    bannerBean.setShareTargetUrl(bannerObj.getString("share_target_url"));
                    dataList.add(bannerBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };

    /*
    * 注意：此回调所做事情如下：
    * 1，根据serviceType，填充推荐项目列表、项目类别（homeDataCache）、并持久化
    * 2，根据serviceType，初始化TabProjectListMap、并持久化
    * */
    HttpInterface.DataCallback getSuggestProjectListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result, Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                //更新缓存
                int serviceType = (int)tagObj;
                if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
                    CacheManager.getInstance().setInHomeDataCache((HomeDataCache) data);
                    view.updateUI(CacheManager.getInstance().getBannerList()
                            ,CacheManager.getInstance().getInHomeDataCache().getSuggestProjectList()
                            ,CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList());
                }else{
                    CacheManager.getInstance().setToShopDataCache((HomeDataCache) data);
                    view.updateUI(CacheManager.getInstance().getBannerList()
                            , CacheManager.getInstance().getToShopDataCache().getSuggestProjectList()
                            , CacheManager.getInstance().getToShopDataCache().getProjectCategoryList());
                }
                /*继续请求第一页数据*/
                getNormalProjectListPre(0, serviceType);
            } else {
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getSuggestProjectListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            HomeDataCache homeDataCache = new HomeDataCache();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                JSONArray projectList = dataObj.getJSONArray("projectList");
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
                        projectBean.setPerCount(projectObj.getJSONObject("saleInfo").getInt("perCount"));
                    }
                    projectBean.setIsExtraProject(projectObj.getJSONObject("pInfo").getInt("kind")==CommonConstant.PROJECT_TYPE_EXTRA?
                            true:false);/*5是附加项目*/
                    homeDataCache.getSuggestProjectList().add(projectBean);
                }
                /*category*/
                JSONArray categoryList = dataObj.getJSONArray("kindList");
                for (int m = 0; m < categoryList.length(); m++) {
                    JSONObject categoryObj = (JSONObject) categoryList.get(m);
                    ProjectCategoryBean projectCategoryBean = new ProjectCategoryBean();
                    projectCategoryBean.setId(categoryObj.getString("type"));
                    projectCategoryBean.setName(categoryObj.getString("name"));
                    homeDataCache.getProjectCategoryList().add(projectCategoryBean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,homeDataCache};
        }
    };
}
