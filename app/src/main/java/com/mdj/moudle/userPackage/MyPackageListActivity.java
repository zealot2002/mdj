package com.mdj.moudle.userPackage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautyParlor.PackageBeautyParlorListPopUpWindowActivity;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.pay.PayActivity;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.qrcode.QrCodeHelper;
import com.mdj.moudle.qrcode.ScanQRCodeActivity;
import com.mdj.moudle.referee.RefereeBean;
import com.mdj.moudle.referee.RefereePopUpWindowActivity;
import com.mdj.moudle.widget.shoppingCart.GoodsBean;
import com.mdj.moudle.userPackage.MyPackageListAdapter.OnPackageListEventListener;
import com.mdj.moudle.userPackage.MyPackageListAdapter.ViewHolder;
import com.mdj.moudle.userPackage.presenter.MyPackageListContract;
import com.mdj.moudle.userPackage.presenter.MyPackageListPresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.ListViewUtils;
import com.mdj.utils.MdjLog;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RefreshableView;
import com.mdj.view.RefreshableView.RefreshListener;
import com.pingplusplus.android.PaymentActivity;
import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class MyPackageListActivity extends BaseActivity implements OnPackageListEventListener,View.OnClickListener,
        AbstractSlideExpandableListAdapter.OnItemExpandCollapseListener, RefreshListener ,MyPackageListContract.View{
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_SCAN_QRCODE = 2;
    private static final int REQUEST_CODE_SET_REFEREE = 3;

    private ActionSlideExpandableListView actionSlideExpandableListView;
    private MyPackageListAdapter adapter;
    private LinearLayout llDisconnectTipsLayout, llBody;
    private RefreshableView refreshableView;// 下拉刷新

    private RefereeBean refereeBean;
    private String selectedPackageId;

    private List<MyPackageBean> dataList;
    private MyPackageListContract.Presenter presenter;
    /*************************************************************************************************************/
    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.activity_my_package);

        llDisconnectTipsLayout = (LinearLayout) findViewById(R.id.llDisconnectTipsLayout);
        llBody = (LinearLayout) findViewById(R.id.llBody);
        llDisconnectTipsLayout.setOnClickListener(this);

        refreshableView = (RefreshableView) findViewById(R.id.refresh_root);
        refreshableView.setRefreshListener(this);

        presenter = new MyPackageListPresenter(this);
        presenter.start();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyPackageListItemProjectListAdapter.BROADCAST_ACTION_OPERATION);
        mContext.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String projectId = intent.getExtras().getString(BundleConstant.PROJCET_ID);
                selectedPackageId = intent.getExtras().getString(BundleConstant.PACKAGE_ID);
                presenter.getProjectInfo(projectId);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(context, "exception:" + e.toString());
            }
        }
    };

    private void showActivityBody() {
        llBody.setVisibility(View.VISIBLE);
        llDisconnectTipsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDisconnectTipsLayout:
                presenter.getMyPackageList(true);
                break;

            default:
                break;
        }
    }

    @Override
    public void onPayEvent(String orderId,String price,String payType) {
        Intent intent = new Intent(mContext, PayActivity.class);
        OrderBean orderBean = new OrderBean.Builder(orderId)
                .price(price)
                .build();
        intent.putExtra(BundleConstant.ORDER_BEAN,orderBean);
        intent.putExtra(BundleConstant.PAY_TYPE,payType);
        startActivityForResult(intent,REQUEST_CODE_PAYMENT);
    }

    @Override
    public void onSetRefereeEvent(int position) {
        Intent intent = new Intent(mContext, ScanQRCodeActivity.class);
        intent.putExtra(BundleConstant.TAG_STR,dataList.get(position).getOrderSn());
        startActivityForResult(intent, REQUEST_CODE_SCAN_QRCODE);
    }

    @Override
    public void onViewShopListEvent(int position) {
        Intent it = new Intent(mContext, PackageBeautyParlorListPopUpWindowActivity.class);
        Bundle b = new Bundle();
        b.putString(BundleConstant.PACKAGE_ID,dataList.get(position).getHomeRefereeId());
        it.putExtras(b);
        startActivity(it);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        closeLoading();
        super.onActivityResult(requestCode, resultCode, data);
        // 支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                String errorMsg = data.getExtras().getString("error_msg"); //
                if (result != null && result.equals("success")) {// 支付成功 刷新界面
                    presenter.getMyPackageList(true);
//                    showShortToast("支付成功");
                } else if (result != null && result.equals("fail")) {
                    showShortToast("支付失败" + errorMsg);
                } else if (result != null && result.equals("cancel")) {
//                    showShortToast("用户已取消");
                } else if (result != null && result.equals("invalid")) {
                    showShortToast("请安装微信");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
//                showShortToast("用户已取消");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                showShortToast("无效的凭证提交");
            }
        }else if ((REQUEST_CODE_SCAN_QRCODE == requestCode) && (resultCode == Activity.RESULT_OK)){
            try {
                String result = data.getExtras().getString(BundleConstant.RESULT);
                String orderId = data.getStringExtra(BundleConstant.TAG_STR);
                refereeBean = (RefereeBean) QrCodeHelper.parseQrCode(QrCodeHelper.QRCODE_TYPE_BEAUTICIAN, result);
                refereeBean.setOrderId(orderId);
                if(refereeBean.getId().isEmpty()){
                    showShortToast("请扫描推荐人二维码");
                    return;
                }
                Intent intent = new Intent(mContext, RefereePopUpWindowActivity.class);
                intent.putExtra(BundleConstant.REFEREE_BEAN, refereeBean);
                startActivityForResult(intent,REQUEST_CODE_SET_REFEREE);
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showLong(mContext, "UnsupportedEncodingException:" + e.toString());
            }
        }else if ((REQUEST_CODE_SET_REFEREE == requestCode) && (resultCode == Activity.RESULT_OK)){
            presenter.getMyPackageList(true);
        }
    }

    @Override
    public void onExpand(View itemView, int position) {
        try {
            adapter.setToggleState(position, 1);
            View view = actionSlideExpandableListView.getChildAt(position - actionSlideExpandableListView.getFirstVisiblePosition());
            if (view != null) {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.expandable_toggle_button.setImageResource(R.mipmap.arrow_up);
            }
        } catch (Exception e) {
            MdjLog.logD("zzy", "onExpand e :" + e.getMessage());
        }
    }

    @Override
    public void onCollapse(View itemView, int position) {
        try {
            adapter.setToggleState(position, 0);
            View view = actionSlideExpandableListView.getChildAt(position - actionSlideExpandableListView.getFirstVisiblePosition());
            if (view != null) {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.expandable_toggle_button.setImageResource(R.mipmap.arrow_down);
            }
        } catch (Exception e) {
            MdjLog.logD("zzy", "onCollapse e :" + e.getMessage());
        }
    }

    @Override
    public void onRefresh(RefreshableView view) {
        presenter.getMyPackageList(false);
    }

    @Override
    public boolean canPullDownRefresh() {
        if (actionSlideExpandableListView == null) {
            return false;
        }
        try{
            int top = actionSlideExpandableListView.getChildAt(0).getTop();
            int pad = actionSlideExpandableListView.getListPaddingTop();
            if ((Math.abs(top - pad)) < 3 && (actionSlideExpandableListView).getFirstVisiblePosition() == 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){

        }
        return false;
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
    public void showDisconnect(String msg) {
        refreshableView.finishRefresh();
        showShortToast(msg);
        llBody.setVisibility(View.INVISIBLE);
        llDisconnectTipsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateUI(Object data) {
        refreshableView.finishRefresh();
        dataList = (List<MyPackageBean>)data;
        showActivityBody();
        if (adapter == null) {
            adapter = new MyPackageListAdapter(this, dataList);
            actionSlideExpandableListView = (ActionSlideExpandableListView) this.findViewById(R.id.list);
            actionSlideExpandableListView.setEmptyView(ListViewUtils.getEmptyView(mContext,"您还没有套餐",actionSlideExpandableListView));
            actionSlideExpandableListView.setAdapter(adapter, R.id.expandable_toggle_button, R.id.expandable, this);
        }
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void updateProjectInfo(ProjectBean projectBean) {
        Intent intent = new Intent(mContext, SelectServiceTypePopUpWindowActivity.class);
        intent.putExtra(BundleConstant.GOODS_BEAN, new GoodsBean
                .Builder(projectBean.getName())
                .id(projectBean.getId())
                .duration(projectBean.getDuration())
                .price(projectBean.getPrice())
                .build());
        intent.putExtra(BundleConstant.PACKAGE_ID,selectedPackageId);
        startActivity(intent);
    }

    @Override
    public void showError(String msg) {
        refreshableView.finishRefresh();
        showShortToast(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
