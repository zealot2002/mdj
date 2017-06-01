package com.mdj.cache;
//缓存、持久化

import com.mdj.application.MyApp;
import com.mdj.constant.SPConstant;
import com.mdj.moudle.appUpdate.VersionBean;
import com.mdj.moudle.city.CityBean;
import com.mdj.moudle.home.bean.BannerBean;
import com.mdj.moudle.home.bean.HomeDataCache;
import com.mdj.moudle.home.bean.TabProjectListCache;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.user.UserBean;
import com.mdj.moudle.userPackage.MyPackageBean;
import com.mdj.utils.MdjLog;
import com.mdj.utils.PreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CacheManager {
	
	private static CacheManager instance = new CacheManager();
//首页数据	
	private HomeDataCache inHomeDataCache,toShopDataCache;

//banner
    private List<BannerBean> bannerList;
	
//Tab项目列表
	private TabProjectListCache inHomeTabProjectListCache,toShopTabProjectListCache;
	
//city list
	private List<CityBean> cityList;
    private CityBean globalCity;

//套餐列表
    private List<MyPackageBean> packageList;

//用户信息
    private UserBean userBean;

//统计
	private ProjectBean statisticsProjectBean = new ProjectBean();
	private MyPackageBean statisticsPackageBean = new MyPackageBean();

    private VersionBean versionBean = new VersionBean();

/********************************************方法区*******************************************************/
//构造函数
	private CacheManager(){
		init();
	}
	public static CacheManager getInstance(){
		return instance;
	}

	private void init(){
        bannerList = new ArrayList<>();
        inHomeDataCache = new HomeDataCache();
        toShopDataCache = new HomeDataCache();
        inHomeTabProjectListCache = new TabProjectListCache();
        toShopTabProjectListCache = new TabProjectListCache();
		cityList = new ArrayList<>();
        globalCity = new CityBean();
        packageList = new ArrayList<>();
        userBean = new UserBean.Builder("美道家").build();

//将持久化数据 读出来
        Object obj = readObjFromSp(SPConstant.USER_INFO);
        if(null!=obj){
            this.userBean = (UserBean)obj;
        }
//首页数据
        Object obj1 = readObjFromSp(SPConstant.INHOME_HOME_DATA);
        if(null!=obj1){
            this.inHomeDataCache = (HomeDataCache)obj1;
        }
        Object obj2 = readObjFromSp(SPConstant.TOSHOP_HOME_DATA);
        if(null!=obj2){
            this.toShopDataCache = (HomeDataCache)obj2;
        }
//banner
        Object obj3 = readObjFromSp(SPConstant.BANNER);
        if(null!=obj3){
            this.bannerList = (List<BannerBean>)obj3;
        }
//tab项目列表
        Object obj4 = readObjFromSp(SPConstant.INHOME_TAB_PROJECT_LIST);
        if(null!=obj4){
            this.inHomeTabProjectListCache = (TabProjectListCache)obj4;
        }
        Object obj5 = readObjFromSp(SPConstant.TOSHOP_TAB_PROJECT_LIST);
        if(null!=obj5){
            this.toShopTabProjectListCache = (TabProjectListCache)obj5;
        }
//当前城市
        Object obj10 = readObjFromSp(SPConstant.CURRENT_CITY);
        if(null!=obj10){
            this.globalCity = (CityBean)obj10;
        }

        readOthers();
	}

    private void readOthers() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                Object obj = readObjFromSp(SPConstant.PACKAGE_LIST);
                if(null!=obj){
                    packageList = (List<MyPackageBean>)obj;
                }
            }
        }).start();
    }

    public List<MyPackageBean> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<MyPackageBean> packageList) {
        this.packageList = packageList;
        writeObjToSp(SPConstant.PACKAGE_LIST, packageList);
    }

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
        writeObjToSp(SPConstant.BANNER, bannerList);
    }

    public HomeDataCache getInHomeDataCache() {
        return inHomeDataCache;
    }

    /*
    * 同时初始化、持久化下方的tabListCache
    * */
    public void setInHomeDataCache(HomeDataCache inHomeDataCache) {
        this.inHomeDataCache = inHomeDataCache;
        writeObjToSp(SPConstant.INHOME_HOME_DATA, inHomeDataCache);

        inHomeTabProjectListCache.getTabProjectListMap().clear();
        //初始化TabProjectListMap
        for(int i=0;i<inHomeDataCache.getProjectCategoryList().size();i++){
            inHomeTabProjectListCache.getTabProjectListMap().put(i, new TabProjectListCache.TabProjectListWrapper());
        }
        /*持久化*/
        setInHomeTabProjectListCache(inHomeTabProjectListCache);
    }

    public HomeDataCache getToShopDataCache() {
        return toShopDataCache;
    }
    /*
    * 同时初始化、持久化下方的tabListCache
    * */
    public void setToShopDataCache(HomeDataCache toShopDataCache) {
        this.toShopDataCache = toShopDataCache;
        writeObjToSp(SPConstant.TOSHOP_HOME_DATA, toShopDataCache);

        toShopTabProjectListCache.getTabProjectListMap().clear();
        //初始化TabProjectListMap
        for(int i=0;i<toShopDataCache.getProjectCategoryList().size();i++){
            toShopTabProjectListCache.getTabProjectListMap().put(i, new TabProjectListCache.TabProjectListWrapper());
        }
        /*持久化*/
        setToShopTabProjectListCache(toShopTabProjectListCache);

    }

    public TabProjectListCache getToShopTabProjectListCache() {
        return toShopTabProjectListCache;
    }

    public void setToShopTabProjectListCache(TabProjectListCache toShopTabProjectListCache) {
        this.toShopTabProjectListCache = toShopTabProjectListCache;
        writeObjToSp(SPConstant.TOSHOP_TAB_PROJECT_LIST, toShopTabProjectListCache);
    }

    public TabProjectListCache getInHomeTabProjectListCache() {
        return inHomeTabProjectListCache;
    }

    public void setInHomeTabProjectListCache(TabProjectListCache inHomeTabProjectListCache) {
        this.inHomeTabProjectListCache = inHomeTabProjectListCache;
        writeObjToSp(SPConstant.INHOME_TAB_PROJECT_LIST, inHomeTabProjectListCache);
    }

	public List<CityBean> getCityList() {
		return cityList;
	}
	public void setCityList(List<CityBean> cityList) {
		this.cityList = cityList;
	}
	public ProjectBean getStatisticsProjectBean() {
		return statisticsProjectBean;
	}
	public MyPackageBean getStatisticsPackageBean() {
		return statisticsPackageBean;
	}

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
        writeObjToSp(SPConstant.USER_INFO, userBean);
    }

    public CityBean getGlobalCity() {
        return globalCity;
    }

    public void setGlobalCity(CityBean globalCity) {
        this.globalCity = globalCity;
        writeObjToSp(SPConstant.CURRENT_CITY, globalCity);
    }

    public void clearUserCache(){
        UserBean bean = new UserBean.Builder("")
                .id("")
                .availablePackageNum(0)
                .availableCouponNum(0)
                .phone("")
                .imgUrl("")
                .build();
        CacheManager.getInstance().setUserBean(bean);
    }

    private void writeObjToSp(final String spStr,final Object obj){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    PreferencesUtils.setObject(MyApp.getInstance(), spStr,spStr,obj);
                } catch (IOException e) {
                    e.printStackTrace();
                    MdjLog.logE("zzy", "err:"+e.toString());
                }
            }
        }).start();
    }
    private Object readObjFromSp(final String spStr){
        try {
            Object obj = PreferencesUtils.getObject(MyApp.getInstance(), spStr, spStr);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public VersionBean getVersionBean() {
        return versionBean;
    }

    public void setVersionBean(VersionBean versionBean) {
        this.versionBean = versionBean;
    }
}
