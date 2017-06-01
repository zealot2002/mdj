package com.mdj.moudle.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.cache.RefreshManager;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseFragment;
import com.mdj.moudle.mine.comment.InHomeCommentActivity;
import com.mdj.moudle.mine.comment.ToShopCommentActivity;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.presenter.OrderContract;
import com.mdj.moudle.order.presenter.OrderPresenter;
import com.mdj.moudle.pay.PayActivity;
import com.mdj.moudle.project.ProjectDetailActivity;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.utils.ListViewUtils;
import com.mdj.utils.MdjLog;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RefreshableView;

import java.util.ArrayList;
import java.util.List;

public class MyOrderListFragment extends BaseFragment implements
        OrderListAdapter.OrderListListener , OrderContract.View, RefreshableView.RefreshListener{
    private RadioGroup rgNav;
    private ImageView ivNav;

    private static String[] TAB_TITLE = {"进行中", "已完成", "已取消"}; // 标题
    private int cardinality; // 将屏幕按宽分成的份数
    private int currentNavItemWidth, currentIndicatorLeft = 0;

    private TitleWidget titleWidget;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_COMMENT = 3;//评价

    private ListView lvOrderList;
    private OrderListAdapter adapter;
    private int orderType = CommonConstant.ORDER_TYPE_ONGOING;
    private List<OrderBean> dataList = new ArrayList<>();
    private RefreshableView refreshableView;// 下拉刷新

    private OrderPresenter presenter;

    /****************************************************方法区*****************************************************/
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_order_list, container, false);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        findViews();
        presenter = new OrderPresenter(this);
        RefreshManager.setNeedRefreshFlag();
        return view;
    }

    public void findViews() {
        mContext = getActivity();

        initTitleView();

        lvOrderList = (ListView)view.findViewById(R.id.lvOrderList);
/*设置headerViews*/
        findListHeaderViews(view);

        refreshableView = (RefreshableView) view.findViewById(R.id.refresh_root);
        refreshableView.setRefreshListener(this);
    }

    private void findListHeaderViews(View headerView) {
        initOrderTypeTabView(headerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(RefreshManager.needRefresh()){
            RefreshManager.clearNeedRefreshFlag();
            presenter.clearOrderList();
            rgNav.check(0);
            orderType = CommonConstant.ORDER_TYPE_ONGOING;
            presenter.getMyOrderList(orderType);
        }
    }

    private void initTitleView() {
        titleWidget = (TitleWidget)view.findViewById(R.id.titleWidget);
        titleWidget.setCurrentScreen(TitleWidget.SCREEN_ID.SCREEN_ORDER);
        titleWidget.hideLeftBtn();
    }

    private void initOrderTypeTabView(final View view) {
        rgNav = (RadioGroup) view.findViewById(R.id.rgNav);
        ivNav = (ImageView) view.findViewById(R.id.ivNav);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        cardinality = 3;
        if (TAB_TITLE.length < 3) {
            cardinality = TAB_TITLE.length;
        }
        currentNavItemWidth = (dm.widthPixels-40)/ cardinality; //减掉两边的margin
        int marginWidth = (currentNavItemWidth-100)/2;  //使游标居中显示
        // indicator
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100,4);//宽度:100px；高度:4px
        lp.setMargins(marginWidth, 0, 0, 0);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ivNav.setLayoutParams(lp);

        // RadioGroup
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rgNav.removeAllViews();
        for (int i = 0; i < TAB_TITLE.length; i++) {
            RadioButton rb = (RadioButton) inflater.inflate(
                    R.layout.nav_rg_item, null);
            rb.setLayoutParams(new LinearLayout.LayoutParams(
                    currentNavItemWidth, LinearLayout.LayoutParams.MATCH_PARENT));
            rb.setId(i);
            rb.setText(TAB_TITLE[i]);
            rgNav.addView(rb);
        }
        rgNav.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View checkView = view.findViewById(checkedId);
                if (!checkView.isPressed()){
                    return;
                }
                // RadioGroup
                rgNav.check(checkedId);

                // indicator
                TranslateAnimation animation = new TranslateAnimation(
                        currentIndicatorLeft, ((RadioButton) rgNav
                        .getChildAt(checkedId)).getLeft(), 0f, 0f);
                animation.setInterpolator(new LinearInterpolator());
                animation.setDuration(200);
                animation.setFillAfter(true);

                ivNav.startAnimation(animation);// 执行位移动画

                currentIndicatorLeft = rgNav.getChildAt(checkedId).getLeft();// 记录当前
                orderType = checkedId+1;
                presenter.getMyOrderList(orderType);
                // 下标的距最左侧的
            }
        });
    }

    @Override
    public void refreshList(final int orderType,List<OrderBean> dataList) {
        refreshableView.finishRefresh();
        this.dataList.clear();
        this.dataList.addAll(dataList);

        if(this.orderType == orderType){
            if(dataList.isEmpty()&&lvOrderList.getEmptyView()==null){
                lvOrderList.setEmptyView(ListViewUtils.getEmptyView(mContext,"没有数据",lvOrderList));
            }
            if(adapter == null){
                adapter = new OrderListAdapter(mContext,this);
                lvOrderList.setAdapter(adapter);
                /*设置滑动监听*/
                lvOrderList.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }
                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                        MdjLog.log("firstVisibleItem:"+firstVisibleItem +" visibleItemCount:"+visibleItemCount+" totalItemCount:"+totalItemCount);
                        if ((firstVisibleItem + visibleItemCount) == totalItemCount){
                            if(!TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                                presenter.loadMoreData(orderType);
                            }
                        }
                    }
                });
            }
            adapter.setDataList(this.dataList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finishOrderDone() {
        presenter.clearOrderList();
        presenter.getMyOrderList(orderType);
    }

    @Override
    public void cancelOrderDone() {

    }

    @Override
    public void updateOrderDetail(OrderBean orderBean) {

    }

    @Override
    public void showDisconnect(String msg) {
        ToastUtils.showLong(mContext, msg);
        refreshableView.finishRefresh();
        this.dataList.clear();
        if(adapter!=null){
            adapter.setDataList(dataList);
            adapter.notifyDataSetChanged();
        }
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
    public void onItemClicked(int position) {
        Intent it = new Intent(mContext, OrderDetailActivity.class);
        it.putExtra(BundleConstant.ORDER_ID, dataList.get(position).getId());
        startActivity(it);
    }

    @Override
    public void onButtonClicked(int position) {
        entryScreenByOrderState(Integer.valueOf(dataList.get(position).getState()), position);
    }

    @Override
    public void onRefresh(RefreshableView view) {
        presenter.refreshMyOrderList(orderType);
    }

    @Override
    public boolean canPullDownRefresh() {
        if (lvOrderList == null) {
            return false;
        }
        try{
            if(dataList.isEmpty()){
                return true;/*可以刷新*/
            }
            int top = lvOrderList.getChildAt(0).getTop();
            int pad = lvOrderList.getListPaddingTop();
            if ((Math.abs(top - pad)) < 3 && (lvOrderList).getFirstVisiblePosition() == 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){

        }
        return false;
    }

    private void entryScreenByOrderState(int state,int position){
        switch (state){
            case CommonConstant.ORDER_STATUS_WAIT_TO_CHARGE:
            {
                //去支付
                Intent intent = new Intent(mContext, PayActivity.class);
                intent.putExtra(BundleConstant.ORDER_BEAN,dataList.get(position));
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }
            break;
            case CommonConstant.ORDER_STATUS_CHARGE_SUCCESS:
            case CommonConstant.ORDER_STATUS_BEAUTY_ALREADY_GO:
            case CommonConstant.ORDER_STATUS_BEAUTY_ARRIVED:
                //do nothing
                break;
            case CommonConstant.ORDER_STATUS_SERVICE_OVER:
                //确认
                presenter.finishOrder(dataList.get(position).getId());
                break;
            case CommonConstant.ORDER_STATUS_WAIT_TO_COMMENT:
                //评价
                Intent intent1;
                if(dataList.get(position).getServiceType() == CommonConstant.SERVICE_TYPE_IN_HOME){
                    intent1  = new Intent(mContext, InHomeCommentActivity.class);
                }else{
                    intent1 = new Intent(mContext, ToShopCommentActivity.class);
                }
                intent1.putExtra(BundleConstant.ORDER_ID,dataList.get(position).getId());
                intent1.putExtra(BundleConstant.BEAUTICIAN_ID,dataList.get(position).getBeauticianId());
                intent1.putExtra(BundleConstant.PROJCET_NAME,dataList.get(position).getProjectListStr());
                startActivityForResult(intent1,REQUEST_CODE_COMMENT);
                break;

            case CommonConstant.ORDER_STATUS_COMMENT_OVER:
            case CommonConstant.ORDER_STATUS_FAIL_TO_CHARGE:
            case CommonConstant.ORDER_STATUS_CANCELED_BY_CUSTOMER:
            case CommonConstant.ORDER_STATUS_DRAWBACK_SUCCESS:
                //再次预约、进入项目详情
            {
                Intent intent = new Intent(mContext,ProjectDetailActivity.class);
                intent.putExtra(BundleConstant.PROJCET_ID, dataList.get(position).getMainProjectId());
                intent.putExtra(BundleConstant.SERVICE_TYPE,dataList.get(position).getServiceType()+"");
                startActivity(intent);
            }
            break;
            case CommonConstant.ORDER_STATUS_ERROR:
            default:
                //do nothing
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 支付页面返回处理
        if ((requestCode == REQUEST_CODE_PAYMENT&&resultCode == Activity.RESULT_OK)
                ||(requestCode == REQUEST_CODE_COMMENT&&resultCode == Activity.RESULT_OK)
                ) {
            presenter.clearOrderList();
            presenter.getMyOrderList(orderType);
        }
    }
}
