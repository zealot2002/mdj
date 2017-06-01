package com.mdj.moudle.beautyParlor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseFlatActivity;
import com.mdj.moudle.beautyParlor.presenter.BeautyParlorDetailContract;
import com.mdj.moudle.beautyParlor.presenter.BeautyParlorDetailPresenter;
import com.mdj.moudle.order.ConfirmOrderToShopActivity;
import com.mdj.moudle.project.ProjectListAdapter;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.widget.AddAndSubWidget;
import com.mdj.moudle.widget.TabMenuWidget;
import com.mdj.moudle.widget.shoppingCart.GoodsBean;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;
import com.mdj.moudle.widget.shoppingCart.ShoppingCartWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RoundImageView;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BeautyParlorDetailActivity extends BaseFlatActivity implements BeautyParlorDetailContract.View,View.OnClickListener,
        AdapterView.OnItemClickListener,ShoppingCartWidget.ShoppingCartWidgetSubmitListener {
    public static final String ADD_AND_SUB_USER_BEAUTY_PARLOR_DETAIL = "beauty_parlor_detail";

    private List<ProjectBean> dataList;
    private ProjectListAdapter adapter;
    private ListView lvProjectList;

    private RoundImageView ivImg;
    private TextView tvName,tvOrderNum,tvIntruduction,tvAddress;
    private ImageButton btnPhone,btnPackage;

    private BeautyParlorBean beautyParlorBean;
    private String beautyParlorId;
    private ShoppingCartWidget shoppingCartWidget;

    private TabMenuWidget tabMenuLayoutNormal,tabMenuLayoutStick;
    private int tabIndex = 0;
    private BeautyParlorDetailContract.Presenter presenter;
/********************************************************************************************************/
    @Override
    public void findViews() {
        mContext = this;
        setTitle("体验店");
        beautyParlorId = getIntent().getStringExtra(BundleConstant.BEAUTY_PARLOR_ID);
        presenter = new BeautyParlorDetailPresenter(this);
        presenter.initData(beautyParlorId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        beautyParlorId = intent.getStringExtra(BundleConstant.BEAUTY_PARLOR_ID);
        presenter.initData(beautyParlorId);
    }

    @Override
    protected void refreshAllData(){
        presenter.initData(beautyParlorId);
    }
    private void initBeautyPorlorDetailView() {
        tabMenuLayoutStick = (TabMenuWidget)findViewById(R.id.tabMenuLayout);
        tabMenuLayoutStick.setCustomTabView(customTabMenuBridge);

        lvProjectList = (ListView)findViewById(R.id.lvProjectList);

/*设置headerViews*/
        View headerView = View.inflate(this, R.layout.beauty_parlor_detail_header, null);
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
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 1) {
                    tabMenuLayoutStick.setVisibility(View.VISIBLE);
                } else {
                    tabMenuLayoutStick.setVisibility(View.GONE);
                }
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
//                    presenter.loadMoreData(beautyParlorId, tabIndex);
                }
            }
        });
/*设置adapter*/
        adapter = new ProjectListAdapter(mContext);
        adapter.setAddAndSubUser(BeautyParlorDetailActivity.ADD_AND_SUB_USER_BEAUTY_PARLOR_DETAIL);
        lvProjectList.setAdapter(adapter);
        lvProjectList.setOnItemClickListener(this);

        shoppingCartWidget = (ShoppingCartWidget)findViewById(R.id.shoppingCartWidget);
        shoppingCartWidget.setShoppingCartWidgetSubmitListener(this);
        shoppingCartWidget.setServiceTypeText("到店服务");

        IntentFilter filter = new IntentFilter();
        filter.addAction(AddAndSubWidget.BROADCAST_ACTION_OPERATION + ADD_AND_SUB_USER_BEAUTY_PARLOR_DETAIL);
        mContext.registerReceiver(receiver, filter);
    }

    private void findListHeaderViews(View headerView) {
        tvName = (TextView)headerView.findViewById(R.id.tvName);
        tvOrderNum = (TextView)headerView.findViewById(R.id.tvOrderNum);
        tvIntruduction = (TextView)headerView.findViewById(R.id.tvIntruduction);
        tvAddress = (TextView)headerView.findViewById(R.id.tvAddress);
        ivImg = (RoundImageView)headerView.findViewById(R.id.ivImg);

        btnPhone = (ImageButton)headerView.findViewById(R.id.btnPhone);
        btnPackage = (ImageButton) headerView.findViewById(R.id.btnPackage);
        btnPhone.setOnClickListener(this);
        btnPackage.setOnClickListener(this);

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
            presenter.getBeautyParlorProjectListPre(beautyParlorId,i);
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
                ProjectBean projectBean = dataList.get(position);
                GoodsBean bean = new GoodsBean.Builder(projectBean.getName())
                        .id(projectBean.getId())
                        .price(MdjUtils.getCurrentProjectPrice(projectBean, new StringBuilder()))
                        .duration(projectBean.getDuration())
                        .isExtraProject(projectBean.isExtraProject())
                        .build();
                shoppingCartWidget.addGoods(bean);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(context, "exception:" + e.toString());
            }
        }
    };

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.btnPhone:
                mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + beautyParlorBean.getTel())));
                break;

            case R.id.btnPackage:
                Intent intent = new Intent(mContext,BeautyParlorPackageActivity.class);
                intent.putExtra(BundleConstant.BEAUTY_PARLOR_ID,beautyParlorId);
                startActivity(intent);
                break;

	
			default:
				break;
		}
        super.onClick(v);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if(shoppingCartWidget!=null){
            shoppingCartWidget.unregisterReceiver();
        }
    }

    @Override
    public void showDisconnect(String msg) {
        super.showDisconnect();
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        beautyParlorBean = (BeautyParlorBean) data;
        setContentView(R.layout.beauty_parlor_detail);
        initBeautyPorlorDetailView();

        MyApp.instance.getImageLoad().displayImage(beautyParlorBean.getImgUrl(), ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
        tvName.setText(beautyParlorBean.getName());
        tvOrderNum.setText(beautyParlorBean.getOrderNum()+"个订单");
        tvIntruduction.setText("简介: "+beautyParlorBean.getIntruduction());
        tvAddress.setText("地址: "+beautyParlorBean.getAddress());
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void refreshProjectList(List<ProjectBean> dataList) {
        this.dataList = dataList;
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        showShortToast("项目详情");
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
        Intent intent = new Intent(this, ConfirmOrderToShopActivity.class);
        intent.putExtra(BundleConstant.BEAUTY_PARLOR_BEAN, beautyParlorBean);
        intent.putExtra(BundleConstant.DATA_LIST, (Serializable)shoppingCartWidget.getCurrentGoods());
        intent.putExtra(BundleConstant.ENTRY, CommonConstant.ENTRY.BEAUTY_PARLOR.value());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}