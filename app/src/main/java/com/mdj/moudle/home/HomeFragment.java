package com.mdj.moudle.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.OnItemClickListener;
import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.common.CommonWebviewActivity;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseFragment;
import com.mdj.moudle.beautyParlor.BeautyParlorListActivity;
import com.mdj.moudle.city.CityBean;
import com.mdj.moudle.city.CityListActivity;
import com.mdj.moudle.home.bean.BannerBean;
import com.mdj.moudle.home.bean.ProjectCategoryBean;
import com.mdj.moudle.home.bean.TabProjectListCache;
import com.mdj.moudle.home.presenter.HomeContract;
import com.mdj.moudle.home.presenter.HomePresenter;
import com.mdj.moudle.order.ConfirmOrderInHomeActivity;
import com.mdj.moudle.project.ProjectDetailActivity;
import com.mdj.moudle.project.ProjectListAdapter;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.userPackage.PackageMainActivity;
import com.mdj.moudle.widget.AddAndSubWidget;
import com.mdj.moudle.widget.SelectServiceTypeWidget;
import com.mdj.moudle.widget.TabMenuWidget;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.moudle.widget.shoppingCart.GoodsBean;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;
import com.mdj.moudle.widget.shoppingCart.ShoppingCartWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.ListViewUtils;
import com.mdj.utils.MdjLog;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RefreshableView;
import com.mdj.view.WrapContentHeightViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseFragment implements
        HomeContract.View, View.OnClickListener, ShoppingCartWidget.ShoppingCartWidgetSubmitListener,
        ProjectListAdapter.OnItemClickedListener, RefreshableView.RefreshListener {
    public static final String ADD_AND_SUB_USER_HOME_SUGGEST_LIST = "suggest_project_list";
    public static final String ADD_AND_SUB_USER_HOME_NORMAL_LIST = "normal_project_list";
    public static final String BROCAST_UPDATE_SHOPPING_CART = "update_shopping_cart";

    private static final int REQUEST_CODE_SELECT_CITY = 1;

    // banner
    private ConvenientBanner<String> convenientBanner;

    /*购买套餐*/
    private RelativeLayout rlBuyPackage;
    /*选择上门、到店*/
    private SelectServiceTypeWidget selectServiceTypeWidget;

    /*推荐项目paper*/
    private WrapContentHeightViewPager vpSuggestProjectList;
    private SuggestProjectPagerAdapter suggestProjectPagerAdapter;

    /*项目分类、项目列表*/
    private TabMenuWidget tabMenuLayoutNormal,tabMenuLayoutStick;
    private ListView lvProjectList;
    private ProjectListAdapter adapter;

    /*other*/
    private ImageButton ibtnGoToTop;
    private TitleWidget titleWidget;

    private ShoppingCartWidget shoppingCartWidget;
    private List<GoodsWrapperBean> inHomeBakGoods,toShopBakGoods;//全局购物车
    private IntentFilter suggestFilter,normalFilter,shoppingCartFilter;

    private int tabIndex;
    private HomeContract.Presenter presenter;

    private View headerView;
    private TabMenuWidget.CustomTabMenuBridge customTabMenuBridge;
    /*临时buffer*/
//    private List<BannerBean> bannerList;
    private List<ProjectBean> suggestProjectList = new ArrayList<>();
    private List<ProjectCategoryBean> projectCategoryBeanList = new ArrayList<>();
    private TabProjectListCache tabProjectListCache;
    private List<ProjectBean> projectList = new ArrayList<>();

    private RefreshableView refreshableView;// 下拉刷新
/*****************************************************方法区*******************************************************/
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate(R.layout.home_fragment, container, false);
        initBuffer();

        shoppingCartWidget = (ShoppingCartWidget)view.findViewById(R.id.shoppingCartWidget);
        shoppingCartWidget.setShoppingCartWidgetSubmitListener(this);
        registerReceiver();

        ibtnGoToTop = (ImageButton)view.findViewById(R.id.ibtnGoToTop);
        ibtnGoToTop.setOnClickListener(this);

        titleWidget = (TitleWidget)view.findViewById(R.id.titleWidget);
        titleWidget.setFooterDividersEnabled(false);
        titleWidget.setCurrentScreen(TitleWidget.SCREEN_ID.SCREEN_MAIN);
        titleWidget.setBackgroundResource(getResources().getColor(R.color.transparent));
        titleWidget.setCustomLeftBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到城市列表选择
                startActivityForResult(new Intent(getActivity(),
                        CityListActivity.class), REQUEST_CODE_SELECT_CITY);
            }
        });
        titleWidget.setCustomLeftBtnBackground(R.mipmap.down_gray);
        titleWidget.setCustomLeftBtnText(CacheManager.getInstance().getGlobalCity().getName());

//        findViews();
        inHomeBakGoods = new ArrayList<>();
        toShopBakGoods = new ArrayList<>();

        refreshableView = (RefreshableView) view.findViewById(R.id.refresh_root);
        refreshableView.setRefreshListener(this);

        presenter = new HomePresenter(this);
        presenter.start();
        return view;
    }

    private void initBuffer() {
        tabProjectListCache = new TabProjectListCache();
    }

    public void findViews() {
        tabMenuLayoutStick = (TabMenuWidget)view.findViewById(R.id.tabMenuLayout);
        tabMenuLayoutStick.setCustomTabView(customTabMenuBridge);

        lvProjectList = (ListView)view.findViewById(R.id.lvProjectList);
        headerView = View.inflate(mContext, R.layout.home_fragment_header, null);
        findListHeaderViews(headerView);
        View headerViewTab = View.inflate(mContext, R.layout.tab_menu_scroll, null);
        tabMenuLayoutNormal = (TabMenuWidget)headerViewTab.findViewById(R.id.tabMenuLayout);
        tabMenuLayoutNormal.setCustomTabView(customTabMenuBridge);

        lvProjectList.addHeaderView(headerView);
        lvProjectList.addHeaderView(headerViewTab);

        /*设置footerView*/
        View footerView = View.inflate(mContext, R.layout.slot_20_gray, null);
        lvProjectList.addFooterView(footerView);

        /*设置滑动监听*/
        lvProjectList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 3) {
                    ibtnGoToTop.setVisibility(View.VISIBLE);
                } else {
                    ibtnGoToTop.setVisibility(View.GONE);
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    presenter.loadMoreNormalProjectListPre(tabIndex,selectServiceTypeWidget.getCurrentSelectedType());
                }
                int scrollY = ListViewUtils.getScrollY(lvProjectList,headerView.getHeight());
                int transfY = headerView.getHeight()-titleWidget.getHeight();
                if (scrollY > transfY) {
                    tabMenuLayoutStick.setVisibility(View.VISIBLE);
                }else{
                    tabMenuLayoutStick.setVisibility(View.INVISIBLE);
                }
                /*Title 渐变*/
                if (scrollY <= titleWidget.getHeight()) {
                    if (scrollY > 0) {
                        float alpha = getAlpha(scrollY);
                        titleWidget.setBackgroundColor(Color.argb((int) (alpha * 255), 255, 255, 255));
                    } else {
                        titleWidget.setBackgroundColor(Color.argb((int) (0 * 255), 255, 255, 255));
                    }
                } else {
                    titleWidget.setBackgroundColor(Color.argb((int) (1 * 255), 255, 255, 255));
                }
            }
        });
/*设置adapter*/
        adapter = new ProjectListAdapter(mContext);
        adapter.setAddAndSubUser(HomeFragment.ADD_AND_SUB_USER_HOME_NORMAL_LIST);
        adapter.setOnItemClickedListener(this);
        lvProjectList.setAdapter(adapter);

    }

    private void findListHeaderViews(View headerView) {
        initBanner(headerView);
        initSuggestProjectPaper(headerView);
        initOtherView(headerView);
        selectServiceTypeWidget = (SelectServiceTypeWidget)headerView.findViewById(R.id.selectServiceTypeWidget);
        selectServiceTypeWidget.setOnSelectedListener(new SelectServiceTypeWidget.OnSelectedListener() {
            @Override
            public void onSelected(int serviceType) {
                //保存购物车数据
                if (serviceType == CommonConstant.SERVICE_TYPE_IN_HOME) {
                    toShopBakGoods.clear();
                    toShopBakGoods.addAll(shoppingCartWidget.getCurrentGoods());
                    shoppingCartWidget.replaceAllGoods(inHomeBakGoods);
                    shoppingCartWidget.setServiceTypeText("上门服务");
                } else {
                    inHomeBakGoods.clear();
                    inHomeBakGoods.addAll(shoppingCartWidget.getCurrentGoods());
                    shoppingCartWidget.replaceAllGoods(toShopBakGoods);
                    shoppingCartWidget.setServiceTypeText("到店服务");
                }
                //切换之后，指向第一个
                updateSelectTabUI(0, (LinearLayout) tabMenuLayoutNormal.getLlTabMenu());
                updateSelectTabUI(0, (LinearLayout) tabMenuLayoutStick.getLlTabMenu());
                presenter.getServiceDataPre(serviceType);
            }
        });
    }
    private void updateSelectTabUI(int tabIndex,LinearLayout parent){
        for(int m=0;m<parent.getChildCount();m++){
            View tabView = parent.getChildAt(m);
            TextView tvCategoryName = (TextView) tabView.findViewById(R.id.tvCategoryName);
            View vLine = (View) tabView.findViewById(R.id.vLine);
            if(m == tabIndex){
                tvCategoryName.setTextColor(getResources().getColor(R.color.text_black));
                vLine.setVisibility(View.VISIBLE);
            }else{
                tvCategoryName.setTextColor(getResources().getColor(R.color.text_gray));
                vLine.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void updateUI(final List<BannerBean> bannerList,
                         final List<ProjectBean> suggestProjectList,
                         final List<ProjectCategoryBean> projectCategoryBeanList) {
        refreshableView.finishRefresh();
        /*copy buffer*/
        this.suggestProjectList.clear();
        this.suggestProjectList.addAll(suggestProjectList);

        this.projectCategoryBeanList.clear();
        this.projectCategoryBeanList.addAll(projectCategoryBeanList);

        this.tabProjectListCache.deepCloneMap(tabProjectListCache.getTabProjectListMap());
        if(customTabMenuBridge == null){
            customTabMenuBridge = new TabMenuWidget.CustomTabMenuBridge() {
                @Override
                public View getView(int position, ViewGroup parent) {
                    View tabView;
                    TextView tvCategoryName;

                    tabView = LayoutInflater.from(mContext).inflate(R.layout.project_category_tab_indicator, parent, false);
                    tvCategoryName = (TextView) tabView.findViewById(R.id.tvCategoryName);
                    View vLine = tabView.findViewById(R.id.vLine);

                    if(position == 0){
                        tvCategoryName.setTextColor(getResources().getColor(R.color.text_black));
                        vLine.setVisibility(View.VISIBLE);
                    }else{
                        tvCategoryName.setTextColor(getResources().getColor(R.color.text_gray));
                        vLine.setVisibility(View.GONE);
                    }
                    tvCategoryName.setText(projectCategoryBeanList.get(position).getName());
//                    tabView.setMinimumWidth(500);
                    //如果小于5，则将菜单按屏幕宽度等分
                    int categoryNum = projectCategoryBeanList.size();
                    if(categoryNum<5){
                        int width = CommonUtil.getScreenWidth(mContext)/categoryNum;
                        ViewGroup.LayoutParams lp;
                        lp=tabView.getLayoutParams();
                        lp.width=width;
                        tabView.setLayoutParams(lp);
                    }
                    return tabView;
                }

                @Override
                public int getCount() {
                    return projectCategoryBeanList.size();
                }

                @Override
                public void onClick(int i, ViewGroup parent) {
                    updateSelectTabUI(i,(LinearLayout)tabMenuLayoutNormal.getLlTabMenu());
                    updateSelectTabUI(i,(LinearLayout)tabMenuLayoutStick.getLlTabMenu());
                    tabIndex = i;
                    presenter.getNormalProjectListPre(tabIndex, selectServiceTypeWidget.getCurrentSelectedType());
                }
            };
            findViews();
        }
        /*update ui*/
        updateBanner(bannerList);
        suggestProjectPagerAdapter.setOnItemClickedListener(new SuggestProjectPagerAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(String projectId) {
                Intent intent = new Intent(mContext, ProjectDetailActivity.class);
                intent.putExtra(BundleConstant.PROJCET_ID, projectId);
                intent.putExtra(BundleConstant.SERVICE_TYPE, selectServiceTypeWidget.getCurrentSelectedType());
                intent.putExtra(BundleConstant.DATA_LIST, (Serializable) shoppingCartWidget.getCurrentGoods());
                startActivity(intent);
            }
        });
        suggestProjectPagerAdapter.setDataList(this.suggestProjectList);
        suggestProjectPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateNormalProjectList(List<ProjectBean> projectList, int tabIndex) {
        MdjLog.log("updateNormalProjectList");
        if(projectList == null){
            return;
        }
        this.projectList.clear();
        this.projectList.addAll(projectList);

        adapter.setTabIndex(tabIndex);
        adapter.setDataList(projectList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateShoppingCartWidget(ProjectBean projectBean) {
        GoodsBean bean = new GoodsBean.Builder(projectBean.getName())
                .id(projectBean.getId())
                .price(MdjUtils.getCurrentProjectPrice(projectBean,new StringBuilder()))
                .duration(projectBean.getDuration())
                .isExtraProject(projectBean.isExtraProject())
                .build();
        shoppingCartWidget.addGoods(bean);
    }

    private void initSuggestProjectPaper(View headerView) {
        vpSuggestProjectList = (WrapContentHeightViewPager)headerView.findViewById(R.id.vpSuggestProjectList);

        suggestProjectPagerAdapter = new SuggestProjectPagerAdapter(mContext);
        vpSuggestProjectList.setAdapter(suggestProjectPagerAdapter);
    }

    private void initBanner(View headerView) {
        convenientBanner = (ConvenientBanner)headerView.findViewById(R.id.convenientBanner);
    }

    private void initOtherView(View headerView) {
        rlBuyPackage = (RelativeLayout)headerView.findViewById(R.id.rlBuyPackage);
        rlBuyPackage.setOnClickListener(this);
    }
    private void unRegisterReceiver(){
        if(shoppingCartWidget!=null){
            shoppingCartWidget.unregisterReceiver();
        }
        mContext.unregisterReceiver(suggestReceiver);
        mContext.unregisterReceiver(normalReceiver);
//        mContext.unregisterReceiver(shoppingCartReceiver);
    }
    private void registerReceiver(){
        if(suggestFilter == null){
            suggestFilter = new IntentFilter();
            suggestFilter.addAction(AddAndSubWidget.BROADCAST_ACTION_OPERATION+ADD_AND_SUB_USER_HOME_SUGGEST_LIST);
            normalFilter = new IntentFilter();
            normalFilter.addAction(AddAndSubWidget.BROADCAST_ACTION_OPERATION+ADD_AND_SUB_USER_HOME_NORMAL_LIST);
            shoppingCartFilter = new IntentFilter();
            shoppingCartFilter.addAction(HomeFragment.BROCAST_UPDATE_SHOPPING_CART);
        }
        mContext.registerReceiver(suggestReceiver, suggestFilter);
        mContext.registerReceiver(normalReceiver, normalFilter);
        mContext.registerReceiver(shoppingCartReceiver, shoppingCartFilter);
        shoppingCartWidget.registerReceiver();
    }
    private BroadcastReceiver shoppingCartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int serviceType = intent.getExtras().getInt(BundleConstant.SERVICE_TYPE);
                List<GoodsWrapperBean> wrapperBeanList = (List<GoodsWrapperBean>) intent.getSerializableExtra(BundleConstant.DATA_LIST);
                if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
                    inHomeBakGoods.clear();
                    inHomeBakGoods.addAll(wrapperBeanList);
                    shoppingCartWidget.replaceAllGoods(inHomeBakGoods);
                }else{
                    toShopBakGoods.clear();
                    toShopBakGoods.addAll(wrapperBeanList);
                    shoppingCartWidget.replaceAllGoods(toShopBakGoods);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(context, "exception:" + e.toString());
            }
        }
    };
    private BroadcastReceiver suggestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String dataTag = intent.getExtras().getString(AddAndSubWidget.DATA_TAG);
                Map<String,String> tagMap = CommonUtil.transStringToMap(dataTag);
                String position = tagMap.get("position");
                presenter.getSelectedSuggestProject(selectServiceTypeWidget.getCurrentSelectedType(), Integer.valueOf(position));

            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(context, "exception:" + e.toString());
            }
        }
    };
    private BroadcastReceiver normalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String dataTag = intent.getExtras().getString(AddAndSubWidget.DATA_TAG);
                Map<String,String> tagMap = CommonUtil.transStringToMap(dataTag);
                String position = tagMap.get("position");
                String tabIndex = tagMap.get("tabIndex");
                presenter.getSelectedNormalProject(Integer.valueOf(tabIndex),selectServiceTypeWidget.getCurrentSelectedType(), Integer.valueOf(position));
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(context, "exception:" + e.toString());
            }
        }
    };


    @Override
    public void updateBanner(final List<BannerBean> bannerList) {
        List<String> networkImages = new ArrayList<>();
        for (BannerBean banner:bannerList) {
            networkImages.add(banner.getImgUrl());
        }
        convenientBanner
                .setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView(new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
//                                ToastUtils.showShort(mContext,"打开活动WebActivity");
                                if(bannerList.size()>0){
                                    Intent intent = new Intent(mContext,CommonWebviewActivity.class);
                                    intent.putExtra(CommonConstant.FLAG,"0");
                                    intent.putExtra(BundleConstant.LINK_URL,bannerList.get(position).getLinkUrl());
                                    intent.putExtra(BundleConstant.IMAGE_URL,bannerList.get(position).getShareImageUrl());
                                    intent.putExtra(BundleConstant.TARGET_URl,bannerList.get(position).getShareTargetUrl());
                                    intent.putExtra(BundleConstant.TITLE,bannerList.get(position).getTitle());
                                    intent.putExtra(BundleConstant.CONTENT,bannerList.get(position).getShareContent());
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }, networkImages)
                .setPageIndicator(
                        new int[] { R.mipmap.ic_page_indicator,
                                R.mipmap.ic_page_indicator_focused })
                        // 设置指示器的方向
                        // .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                        // 设置翻页的效果，不需要翻页效果可用不设
                .setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer)// 可以更换效果
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                });
        convenientBanner.startTurning(5000);
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            // 开始自动翻页
            if(convenientBanner!=null){
                convenientBanner.startTurning(5000);// 此值不能小于1200（即ViewPagerScroller的mScrollDuration的值），否则最后一页翻页效果会出问题。如果硬要兼容1200以下，那么请修改ViewPagerScroller的mScrollDuration的值，不过修改后，3d效果就没那么明显了。
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            // 停止翻页
            if(convenientBanner!=null){
                convenientBanner.stopTurning();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unRegisterReceiver();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBuyPackage:
                startActivity(new Intent(mContext, PackageMainActivity.class));
                break;

            case R.id.ibtnGoToTop:
                ibtnGoToTop.setVisibility(View.GONE);
                lvProjectList.setSelection(0);
                break;
        }
    }
    /**
     * 根据滑动Y值计算alpha值
     *
     * @param scrollY
     * @return
     */
    private float getAlpha(float scrollY) {
        if (titleWidget.getHeight() != 0) {
            return scrollY / titleWidget.getHeight();
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MdjLog.logE("zzy", "HomeFragment onActivityResult() in");
        if (requestCode == REQUEST_CODE_SELECT_CITY) {
            if (resultCode == Activity.RESULT_OK) {// 切换城市，一切缓存清除
                CityBean cityBean = (CityBean)data.getSerializableExtra(BundleConstant.CITY_BEAN);
                CacheManager.getInstance().setGlobalCity(cityBean);
                titleWidget.setCustomLeftBtnText(CacheManager.getInstance().getGlobalCity().getName());
                refreshAllDataAndUI();

            }
        }
    }

    @Override
    public void showDisconnect(String msg) {
        refreshableView.finishRefresh();
        ToastUtils.showLong(mContext, msg);
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void updateUI(Object data) {

    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void onSubmit(List<GoodsWrapperBean> dataList) {
        if(dataList==null||dataList.isEmpty())
            return;
        if(MdjUtils.isOnlyExtraProject(shoppingCartWidget.getCurrentGoods())){
            ToastUtils.showLong(mContext,getString(R.string.can_not_create_order_with_one_extra_project));
            return;
        }
        if (MdjUtils.isOver6hours(shoppingCartWidget.getCurrentGoods())){
            ToastUtils.showLong(mContext,getString(R.string.can_not_more_than_6_hours));
            return;
        }
        Intent intent = new Intent(mContext,
                selectServiceTypeWidget.getCurrentSelectedType()==CommonConstant.SERVICE_TYPE_IN_HOME?
                ConfirmOrderInHomeActivity.class : BeautyParlorListActivity.class);
        intent.putExtra(BundleConstant.DATA_LIST, (Serializable) dataList);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(String projectId) {
        Intent intent = new Intent(mContext,ProjectDetailActivity.class);
        intent.putExtra(BundleConstant.PROJCET_ID,projectId);
        intent.putExtra(BundleConstant.SERVICE_TYPE,selectServiceTypeWidget.getCurrentSelectedType());
        intent.putExtra(BundleConstant.DATA_LIST, (Serializable)shoppingCartWidget.getCurrentGoods());
        startActivity(intent);
    }

    private void refreshAllDataAndUI(){
        presenter.refreshAllData();
        if(selectServiceTypeWidget!=null){
            selectServiceTypeWidget.setServiceType(CommonConstant.SERVICE_TYPE_IN_HOME);
        }
        if(tabMenuLayoutNormal!=null){
            //切换之后，指向第一个
            updateSelectTabUI(0, (LinearLayout) tabMenuLayoutNormal.getLlTabMenu());
        }
        if(tabMenuLayoutStick!=null){
            //切换之后，指向第一个
            updateSelectTabUI(0, (LinearLayout) tabMenuLayoutStick.getLlTabMenu());
        }
        vpSuggestProjectList.setCurrentItem(0);
        suggestProjectPagerAdapter = new SuggestProjectPagerAdapter(mContext);
        vpSuggestProjectList.setAdapter(suggestProjectPagerAdapter);
    }
    @Override
    public void onRefresh(RefreshableView view) {
        refreshAllDataAndUI();
    }

    @Override
    public boolean canPullDownRefresh() {
        try{
            if(projectList.isEmpty()){
                return true;/*可以刷新*/
            }
            if(lvProjectList==null){
                return false;
            }
            int top = lvProjectList.getChildAt(0).getTop();
            int pad = lvProjectList.getListPaddingTop();
            if ((Math.abs(top - pad)) < 3
                    && lvProjectList.getFirstVisiblePosition() == 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
