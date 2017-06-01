package com.mdj.moudle.project;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautyParlor.BeautyParlorListActivity;
import com.mdj.moudle.home.HomeFragment;
import com.mdj.moudle.order.ConfirmOrderInHomeActivity;
import com.mdj.moudle.project.bean.CommentBean;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.project.presenter.ProjectDetailContract;
import com.mdj.moudle.project.presenter.ProjectDetailPresenter;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.moudle.widget.TwoChooseOneWidget;
import com.mdj.moudle.widget.sale.SaleBean;
import com.mdj.moudle.widget.sale.SaleCountDownWidget;
import com.mdj.moudle.widget.shoppingCart.GoodsBean;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;
import com.mdj.moudle.widget.shoppingCart.ShoppingCartWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.ImageUtils;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.ToastUtils;
import com.mdj.utils.ViewUtils;
import com.umeng.analytics.MobclickAgent;
import com.zhy.flowlayout.FlowLayout;
import com.zhy.flowlayout.TagAdapter;
import com.zhy.flowlayout.TagFlowLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectDetailActivity extends BaseActivity implements
        View.OnClickListener,AdapterView.OnItemClickListener,ProjectDetailContract.View,
        ShoppingCartWidget.ShoppingCartWidgetSubmitListener {
    private ProjectBean projectBean;

    private SaleCountDownWidget saleCountDownWidget;
    private TextView tvSoldNum,tvDuration,tvOriginPlace,tvPrice,tvOldPrice,tvRemarksTitle,tvRemarks,
                                                        tvQuality1,tvQuality2,tvQuality3;
    private ImageView ivPic1,ivPic2,ivQuality1,ivQuality2,ivQuality3;
    private TagFlowLayout mFlowLayout;
//
    private ProjectDetailListAdapter adapter;
    private ListView lvProjectDetailList;
    private View footerView;

    private TwoChooseOneWidget twoChooseOneWidget;

    private ShoppingCartWidget shoppingCartWidget;
    private List<GoodsWrapperBean> goodsWrapperBeanList;
    private ImageButton btnAdd;
    private RelativeLayout rlBuy;

    private ProjectDetailContract.Presenter presenter;
    private String projectId;
    private int serviceType;
    private boolean readOnly = false;

    /*flat*/
    private TitleWidget titleWidget;
    private LinearLayout llDisconnectTipsLayout;

/*****************************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDisconnectTipsLayout:
                presenter.getProjectDetail(projectId);
                break;

            case R.id.btnAdd:
                try {
                    GoodsBean bean = new GoodsBean.Builder(projectBean.getName())
                            .id(projectBean.getId())
                            .price(MdjUtils.getCurrentProjectPrice(projectBean,new StringBuilder()))
                            .duration(projectBean.getDuration())
                            .isExtraProject(projectBean.isExtraProject())
                            .build();
                    shoppingCartWidget.addGoods(bean);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showLong(mContext, "exception:" + e.toString());
                }
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
            notifyHomeFragmentShoppingCartChanged();
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
        setContentView(R.layout.activity_flat);
        titleWidget = (TitleWidget)findViewById(R.id.titleWidget);
        titleWidget.setTitle("项目详情");
        llDisconnectTipsLayout = (LinearLayout)findViewById(R.id.llDisconnectTipsLayout);
        llDisconnectTipsLayout.setOnClickListener(this);

        getData(getIntent());
        presenter = new ProjectDetailPresenter(this);
        presenter.getProjectDetail(projectId);
    }
    private void getData(Intent intent){
        projectId = intent.getStringExtra(BundleConstant.PROJCET_ID);
        serviceType = intent.getIntExtra(BundleConstant.SERVICE_TYPE, CommonConstant.SERVICE_TYPE_IN_HOME);
        readOnly = intent.getBooleanExtra(BundleConstant.READ_ONLY, false);
        goodsWrapperBeanList = (List<GoodsWrapperBean>)intent.getSerializableExtra(BundleConstant.DATA_LIST);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getData(intent);
        presenter.getProjectDetail(projectId);
    }

    public void findAllViews() {
        setContentView(R.layout.activity_project_detail);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        if(readOnly){
            btnAdd.setVisibility(View.GONE);
        }else{
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(this);
        }

        lvProjectDetailList = (ListView)findViewById(R.id.lvProjectDetailList);
/*设置headerView*/
        View headerView = View.inflate(this, R.layout.activity_project_detail_header, null);
        findListHeaderViews(headerView);
        lvProjectDetailList.addHeaderView(headerView);

/*设置footerView*/
        footerView = View.inflate(this, R.layout.activity_project_detail_footer, null);
        tvRemarks = (TextView) footerView.findViewById(R.id.tvRemarks);
        tvRemarksTitle = (TextView) footerView.findViewById(R.id.tvRemarksTitle);
        lvProjectDetailList.addFooterView(footerView);
/*设置滑动监听*/
        lvProjectDetailList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (((firstVisibleItem + visibleItemCount) == totalItemCount)
                        && (twoChooseOneWidget.getCurrentSelected() == 1)) { //评论列表
                    presenter.loadMoreData(projectId);
                }
            }
        });
/*设置adapter*/
        adapter = new ProjectDetailListAdapter(mContext);
        lvProjectDetailList.setAdapter(adapter);
/*购物车*/
        shoppingCartWidget = (ShoppingCartWidget)findViewById(R.id.shoppingCartWidget);
        shoppingCartWidget.setShoppingCartWidgetSubmitListener(this);
        shoppingCartWidget.replaceAllGoods(goodsWrapperBeanList);

        if(readOnly){
            shoppingCartWidget.setVisibility(View.GONE);
            ViewUtils.setMargins(lvProjectDetailList,0,0,0,0);
        }else{
            shoppingCartWidget.setVisibility(View.VISIBLE);
            ViewUtils.setMargins(lvProjectDetailList,0,0,0,50);
        }
    }

    private void findListHeaderViews(View headerView) {
        saleCountDownWidget = (SaleCountDownWidget)headerView.findViewById(R.id.saleCountDownWidget);

        tvSoldNum = (TextView) headerView.findViewById(R.id.tvSoldNum);
        tvDuration = (TextView) headerView.findViewById(R.id.tvDuration);
        tvOriginPlace = (TextView) headerView.findViewById(R.id.tvOriginPlace);
        tvPrice = (TextView) headerView.findViewById(R.id.tvPrice);
        tvOldPrice = (TextView) headerView.findViewById(R.id.tvOldPrice);
        tvQuality1 = (TextView) headerView.findViewById(R.id.tvQuality1);
        tvQuality2 = (TextView) headerView.findViewById(R.id.tvQuality2);
        tvQuality3 = (TextView) headerView.findViewById(R.id.tvQuality3);

        ivPic1 = (ImageView) headerView.findViewById(R.id.ivPic1);
        ivPic2 = (ImageView) headerView.findViewById(R.id.ivPic2);
        ivQuality1 = (ImageView) headerView.findViewById(R.id.ivQuality1);
        ivQuality2 = (ImageView) headerView.findViewById(R.id.ivQuality2);
        ivQuality3 = (ImageView) headerView.findViewById(R.id.ivQuality3);

        mFlowLayout = (TagFlowLayout) headerView.findViewById(R.id.id_flowlayout);
        twoChooseOneWidget = (TwoChooseOneWidget)headerView.findViewById(R.id.twoChooseOneWidget);
    }

    @Override
    public void showDisconnect(String msg) {
        llDisconnectTipsLayout.setVisibility(View.VISIBLE);
        showShortToast(msg);
    }

    //只更新上半部分
    @Override
    public void updateUI(Object data) {
        try{
            projectBean = (ProjectBean)data;
            findAllViews();
            twoChooseOneWidget.setCustomerText("项目介绍", "评价(" + projectBean.getAssessmentNum() + ")");
            twoChooseOneWidget.setOnSelectedListener(new TwoChooseOneWidget.OnSelectedListener() {
                @Override
                public void onSelected(int position) {
                    if (position == 0) {/*显示图片list*/
                        /*更新list*/
                        List<ProjectDetailDataWrapperBean> wrapperList = new ArrayList<>();
                        for (int i = 1; i < projectBean.getExtraImgUrlList().size(); i++) {
                            wrapperList.add(new ProjectDetailDataWrapperBean(0, projectBean.getExtraImgUrlList().get(i)));
                        }
                        adapter.setDataList(wrapperList);
                        adapter.notifyDataSetChanged();
                        lvProjectDetailList.addFooterView(footerView);
                        lvProjectDetailList.setDivider(getResources().getDrawable(R.color.dividing_line_gray));
                    } else {/*显示评论list*/
                        presenter.getProjectCommentListPre(projectId);
                        lvProjectDetailList.setDivider(getResources().getDrawable(R.color.transparent));
                        lvProjectDetailList.removeFooterView(footerView);
                    }
                }
            });
    //        showActivityBody();

            tvSoldNum.setText("总销量:"+projectBean.getTotalOrderNum());
            tvDuration.setText(projectBean.getDuration()+"分钟");
            tvOriginPlace.setText(projectBean.getOriginPlace());
            tvPrice.setText(MdjUtils.getCurrentProjectPrice(projectBean, new StringBuilder()) + "");

            tvOldPrice.setText(getString(R.string.symbol_rmb) + projectBean.getOldPrice());
            tvOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线

            tvRemarks.setText(projectBean.getRemarks());
            tvRemarksTitle.setText("一 " + projectBean.getRemarksTitle() + " 一");

            ImageUtils.scopImage(mContext, ivPic1);
            MyApp.instance.getImageLoad().displayImage(projectBean.getImageUrl()
                    , ivPic1, DisplayImageOptionsUtil.getCommonCacheOptions());
            ImageUtils.scopImage(mContext, ivPic2);
            MyApp.instance.getImageLoad().displayImage(projectBean.getExtraImgUrlList().size() > 0 ? projectBean.getExtraImgUrlList().get(0) : ""
                    , ivPic2, DisplayImageOptionsUtil.getCommonCacheOptions());

            final LayoutInflater mInflater = LayoutInflater.from(this);

            mFlowLayout.setMaxSelectCount(0);
            mFlowLayout.setAdapter(new TagAdapter<String>(projectBean.getProjectTagList()) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv_for_project_detail, mFlowLayout, false);
                    tv.setText(s);
                    return tv;
                }
            });

            /*质量三包*/
            if(projectBean.getProjectQualityList().size()>2){
                tvQuality1.setText(projectBean.getProjectQualityList().get(0).getName());
                tvQuality2.setText(projectBean.getProjectQualityList().get(1).getName());
                tvQuality3.setText(projectBean.getProjectQualityList().get(2).getName());
                MyApp.instance.getImageLoad().displayImage(projectBean.getProjectQualityList().get(0).getImageUrl()
                        , ivQuality1, DisplayImageOptionsUtil.getCommonCacheOptions());
                MyApp.instance.getImageLoad().displayImage(projectBean.getProjectQualityList().get(1).getImageUrl()
                        , ivQuality2, DisplayImageOptionsUtil.getCommonCacheOptions());
                MyApp.instance.getImageLoad().displayImage(projectBean.getProjectQualityList().get(2).getImageUrl()
                        , ivQuality3, DisplayImageOptionsUtil.getCommonCacheOptions());
            }

            /*倒计时*/
            if(projectBean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_LIMIT_PRIVILEGE)) {//限时限量
                saleCountDownWidget.setVisibility(View.VISIBLE);
                saleCountDownWidget.show(new SaleBean.Builder()
                        .startTime(projectBean.getStartTime())
                        .endTime(projectBean.getEndTime())
                        .total(projectBean.getTotal())
                        .soldNum(projectBean.getSoldNum())
                        .perCount(projectBean.getPerCount())
                        .privilegePrice(projectBean.getPrivilegePrice())
                        .build());
            }else{
                saleCountDownWidget.setVisibility(View.GONE);
            }

            /*更新list*/
            List<ProjectDetailDataWrapperBean> wrapperList = new ArrayList<>();
            for(int i=1;i<projectBean.getExtraImgUrlList().size();i++){
                wrapperList.add(new ProjectDetailDataWrapperBean(0,projectBean.getExtraImgUrlList().get(i)));
            }
            adapter.setDataList(wrapperList);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        Intent intent = new Intent(mContext,serviceType==CommonConstant.SERVICE_TYPE_IN_HOME?
                        ConfirmOrderInHomeActivity.class : BeautyParlorListActivity.class);
        intent.putExtra(BundleConstant.DATA_LIST, (Serializable) dataList);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(shoppingCartWidget!=null){
            shoppingCartWidget.unregisterReceiver();
        }
    }

    @Override
    public void updateCommentList(List<CommentBean> commentList) {
        if(twoChooseOneWidget.getCurrentSelected() == 0){
            return;
        }
        /*更新list*/
        List<ProjectDetailDataWrapperBean> wrapperList = new ArrayList<>();
        for(CommentBean bean:commentList){
            ProjectDetailDataWrapperBean wrapperBean = new ProjectDetailDataWrapperBean();
            wrapperBean.setDataType(1);
            wrapperBean.setCommentBean(bean);
            wrapperList.add(wrapperBean);
        }
        adapter.setDataList(wrapperList);
        adapter.notifyDataSetChanged();
    }

    private void notifyHomeFragmentShoppingCartChanged() {
        Intent broadcast = new Intent();
        broadcast.putExtra(BundleConstant.SERVICE_TYPE, serviceType);
        broadcast.putExtra(BundleConstant.DATA_LIST, (Serializable) shoppingCartWidget.getCurrentGoods());
        broadcast.setAction(HomeFragment.BROCAST_UPDATE_SHOPPING_CART);
        sendBroadcast(broadcast);
    }
}
