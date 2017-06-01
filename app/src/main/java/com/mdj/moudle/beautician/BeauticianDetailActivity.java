package com.mdj.moudle.beautician;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.beautician.bean.BeauticianTag;
import com.mdj.moudle.beautician.presenter.BeauticianDetailContract;
import com.mdj.moudle.beautician.presenter.BeauticianDetailPresenter;
import com.mdj.moudle.beautyParlor.BeautyParlorListActivity;
import com.mdj.moudle.order.ConfirmOrderInHomeActivity;
import com.mdj.moudle.order.ConfirmOrderToShopActivity;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.project.ProjectListAdapter;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.widget.AddAndSubWidget;
import com.mdj.moudle.widget.SelectServiceTypeWidget;
import com.mdj.moudle.widget.TabMenuWidget;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.moudle.widget.shoppingCart.GoodsBean;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;
import com.mdj.moudle.widget.shoppingCart.ShoppingCartWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.TextViewUtil;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RoundImageView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.flowlayout.FlowLayout;
import com.zhy.flowlayout.TagAdapter;
import com.zhy.flowlayout.TagFlowLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeauticianDetailActivity extends BaseActivity implements
        View.OnClickListener,AdapterView.OnItemClickListener,BeauticianDetailContract.View,
        ShoppingCartWidget.ShoppingCartWidgetSubmitListener {
    public static final String ADD_AND_SUB_USER_BEAUTICIAN_DETAIL = "beautician_detail";
    private LinearLayout llTag;
    private BeauticianBean beauticianBean;

    private TextView tvName,tvExperience,tvOrderNum,tvIntruduction,tvDepartment,tvTagNum;
    private RoundImageView ivImg;
    private TagFlowLayout mFlowLayout;
//
    private ProjectListAdapter adapter;
    private ListView lvProjectList;

    private SelectServiceTypeWidget selectServiceTypeWidget;

    private RelativeLayout rlComment;
    private String beauticianId;

    private ShoppingCartWidget shoppingCartWidget;
    private List<GoodsWrapperBean> inHomeBakGoods,toShopBakGoods;

    private TitleWidget titleWidget;
    private BeauticianDetailContract.Presenter presenter;

    private TextView tvHint;
    private TabMenuWidget tabMenuLayoutNormal,tabMenuLayoutStick;
    private OrderBean orderBean;
    private int tabIndex = 0;
/*****************************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDisconnectTipsLayout:
                presenter.getInitData(beauticianId);

                break;

            case R.id.rlComment:
                startActivity(new Intent(mContext, CommentInfoActivity.class).putExtra(BundleConstant.BEAUTICIAN_ID,beauticianId));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MobclickAgent.onPageStart(CommonUtil.generateTag());
            MobclickAgent.onResume(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPageEnd(CommonUtil.generateTag());
            MobclickAgent.onPause(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.activity_beautician_detail);
        presenter = new BeauticianDetailPresenter(this);

        if(getIntent().hasExtra(BundleConstant.BEAUTICIAN_ID)){  /*我的美容师*/
            beauticianId = getIntent().getStringExtra(BundleConstant.BEAUTICIAN_ID);
        }
        if(getIntent().hasExtra(BundleConstant.ORDER_BEAN)){    /*追单*/
            orderBean = (OrderBean) getIntent().getSerializableExtra(BundleConstant.ORDER_BEAN);
            beauticianId = orderBean.getBeauticianId();
        }

        titleWidget = (TitleWidget)findViewById(R.id.titleWidget);

        tabMenuLayoutStick = (TabMenuWidget)findViewById(R.id.tabMenuLayout);
        tabMenuLayoutStick.setCustomTabView(customTabMenuBridge);

        lvProjectList = (ListView)findViewById(R.id.lvProjectList);
/*设置emptyView*/
        View emptyView = View.inflate(mContext,R.layout.empty, null);
        tvHint = ((TextView)emptyView.findViewById(R.id.tvHint));
        tvHint.setText("此技师无上门服务项目");
        ((ViewGroup)lvProjectList.getParent()).addView(emptyView);
//        lvProjectList.setEmptyView(emptyView);
/*设置headerViews*/
        View headerView = View.inflate(this, R.layout.activity_beautician_detail_header, null);
        findListHeaderViews(headerView);

        View headerViewTab = View.inflate(this, R.layout.tab_menu_scroll, null);
        tabMenuLayoutNormal = (TabMenuWidget)headerViewTab.findViewById(R.id.tabMenuLayout);
        tabMenuLayoutNormal.setCustomTabView(customTabMenuBridge);

        lvProjectList.addHeaderView(headerView);
        lvProjectList.addHeaderView(headerViewTab);
/*设置滑动监听*/
        lvProjectList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 1) {
                    tabMenuLayoutStick.setVisibility(View.VISIBLE);
                } else {
                    tabMenuLayoutStick.setVisibility(View.GONE);
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount){
                    presenter.loadMoreData(beauticianId, selectServiceTypeWidget.getCurrentSelectedType(), tabIndex);
                }
            }
        });
/*设置adapter*/
        adapter = new ProjectListAdapter(mContext);
        adapter.setAddAndSubUser(BeauticianDetailActivity.ADD_AND_SUB_USER_BEAUTICIAN_DETAIL);
        lvProjectList.setAdapter(adapter);
/*购物车*/
        shoppingCartWidget = (ShoppingCartWidget)findViewById(R.id.shoppingCartWidget);
        shoppingCartWidget.setShoppingCartWidgetSubmitListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(AddAndSubWidget.BROADCAST_ACTION_OPERATION + ADD_AND_SUB_USER_BEAUTICIAN_DETAIL);
        mContext.registerReceiver(receiver, filter);

        inHomeBakGoods = new ArrayList<>();
        toShopBakGoods = new ArrayList<>();

        if(orderBean==null){//我的美容师，可以选择上门或到店，下单
            presenter.getInitData(beauticianId);
        }else{
            /*隐藏上门、到店选择widget*/
            selectServiceTypeWidget.setVisibility(View.GONE);
            presenter.getBeauticianDetail(beauticianId, orderBean == null ? CommonConstant.SERVICE_TYPE_IN_HOME : orderBean.getServiceType());
        }
    }

    private float getAlpha(float scrollY) {
        if (titleWidget.getHeight() != 0) {
            return scrollY / titleWidget.getHeight();
        }
        return 0;
    }

    private void findListHeaderViews(View headerView) {
        llTag = (LinearLayout) headerView.findViewById(R.id.llTag);

        tvName = (TextView) headerView.findViewById(R.id.tvName);
        tvExperience = (TextView) headerView.findViewById(R.id.tvExperience);
        tvOrderNum = (TextView) headerView.findViewById(R.id.tvOrderNum);
        tvIntruduction = (TextView) headerView.findViewById(R.id.tvIntruduction);
        tvDepartment = (TextView) headerView.findViewById(R.id.tvDepartment);
        tvTagNum = (TextView) headerView.findViewById(R.id.tvTagNum);

        ivImg = (RoundImageView) headerView.findViewById(R.id.ivImg);

        mFlowLayout = (TagFlowLayout) headerView.findViewById(R.id.id_flowlayout);
        rlComment = (RelativeLayout)headerView.findViewById(R.id.rlComment);
        rlComment.setOnClickListener(this);

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
                    tvHint.setText("此技师无上门服务项目");
                } else {
                    inHomeBakGoods.clear();
                    inHomeBakGoods.addAll(shoppingCartWidget.getCurrentGoods());
                    shoppingCartWidget.replaceAllGoods(toShopBakGoods);
                    shoppingCartWidget.setServiceTypeText("到店服务");
                    tvHint.setText("此技师无到店服务项目");
                }
                //切换之后，指向第一个
                updateSelectTabUI(0,(LinearLayout)tabMenuLayoutNormal.getLlTabMenu());
                updateSelectTabUI(0,(LinearLayout)tabMenuLayoutStick.getLlTabMenu());
                presenter.getBeauticianProjectListPre(beauticianId,serviceType,0);
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
    private TabMenuWidget.CustomTabMenuBridge customTabMenuBridge = new TabMenuWidget.CustomTabMenuBridge() {
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
            tvCategoryName.setText(CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().get(position).getName());
//                    tabView.setMinimumWidth(500);
            //如果小于5，则将菜单按屏幕宽度等分
            int categoryNum = CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().size();
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
            return CacheManager.getInstance().getInHomeDataCache().getProjectCategoryList().size();
        }


        @Override
        public void onClick(int i, ViewGroup parent) {
            updateSelectTabUI(i,(LinearLayout)tabMenuLayoutNormal.getLlTabMenu());
            updateSelectTabUI(i,(LinearLayout)tabMenuLayoutStick.getLlTabMenu());
            presenter.getBeauticianProjectListPre(beauticianId, selectServiceTypeWidget.getCurrentSelectedType(), i);
            tabIndex = i;
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String dataTag = intent.getExtras().getString(AddAndSubWidget.DATA_TAG);
                Map<String,String> tagMap = CommonUtil.transStringToMap(dataTag);
                int position = Integer.valueOf(tagMap.get("position"));
                presenter.getSelectedGoods(tabIndex,position,selectServiceTypeWidget.getCurrentSelectedType());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(context, "exception:" + e.toString());
            }
        }
    };
    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    //只更新上半部分
    @Override
    public void updateUI(Object data) {
        beauticianBean = (BeauticianBean)data;
        tvName.setText(beauticianBean.getName());
        tvExperience.setVisibility(View.VISIBLE);
        tvExperience.setText(beauticianBean.getExperience() + "年经验");
        tvOrderNum.setVisibility(View.VISIBLE);
        tvOrderNum.setText(beauticianBean.getOrderNum()+"单");
        tvIntruduction.setText(beauticianBean.getIntruduction());
        tvDepartment.setText(beauticianBean.getDepartment());

        MyApp.instance.getImageLoad().displayImage(beauticianBean.getImgUrl(), ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());

        if(beauticianBean.getTagSet() == null||beauticianBean.getTagSet().size()==0){
            llTag.setVisibility(View.GONE);
            return;
        }
        tvTagNum.setText("她收到的印象("+beauticianBean.getTagSet().size()+")");
        final LayoutInflater mInflater = LayoutInflater.from(this);

        mFlowLayout.setMaxSelectCount(0);
        List<String> tagList = new ArrayList<>();
        for(BeauticianTag tag:beauticianBean.getTagSet()){
            tagList.add(new StringBuilder(tag.getTagName()+"("+tag.getNum()+")").toString());
        }
        mFlowLayout.setAdapter(new TagAdapter<String>(tagList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv,mFlowLayout, false);
                int specialStart = s.indexOf("(");
                TextViewUtil.setSpecialTextColor(tv,
                        s,
                        mContext.getResources().getColor(R.color.text_black),
                        mContext.getResources().getColor(R.color.text_gray),
                        specialStart,
                        s.length()
                );
                return tv;
            }
        });
    }
    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void updateProjectList(List<ProjectBean> list) {
        adapter.setDataList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateShoppingCartWidget(ProjectBean projectBean) {
        GoodsBean bean = new GoodsBean.Builder(projectBean.getName())
                .id(projectBean.getId())
                .price(MdjUtils.getCurrentProjectPrice(projectBean, new StringBuilder()))
                .duration(projectBean.getDuration())
                .isExtraProject(projectBean.isExtraProject())
                .build();
        shoppingCartWidget.addGoods(bean);
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
        if(orderBean!=null){//追单
            Intent intent = new Intent(mContext,
                    orderBean.getServiceType() == CommonConstant.SERVICE_TYPE_IN_HOME ?
                            ConfirmOrderInHomeActivity.class:ConfirmOrderToShopActivity.class);
            intent.putExtra(BundleConstant.DATA_LIST, (Serializable) dataList);
            intent.putExtra(BundleConstant.ORDER_BEAN,orderBean);
            intent.putExtra(BundleConstant.ENTRY, CommonConstant.ENTRY.APPEND_ORDER.value());
            startActivity(intent);
            return;
        }
        Intent intent = null;
        if(selectServiceTypeWidget.getCurrentSelectedType()==CommonConstant.SERVICE_TYPE_IN_HOME){
            intent = new Intent(mContext, ConfirmOrderInHomeActivity.class);
        }else{
            intent = new Intent(mContext, BeautyParlorListActivity.class);
        }
        intent.putExtra(BundleConstant.DATA_LIST, (Serializable) dataList);
        intent.putExtra(BundleConstant.ENTRY, CommonConstant.ENTRY.MY_BEAUTICIAN.value());
        intent.putExtra(BundleConstant.BEAUTICIAN_BEAN,beauticianBean);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if(shoppingCartWidget!=null){
            shoppingCartWidget.unregisterReceiver();
        }
    }
}
