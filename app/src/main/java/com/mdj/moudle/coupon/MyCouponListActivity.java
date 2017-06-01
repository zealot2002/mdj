package com.mdj.moudle.coupon;

import android.os.Bundle;
import android.view.View;

import com.mdj.R;
import com.mdj.moudle.BaseFlatActivity;
import com.mdj.moudle.coupon.presenter.MyCouponListContract;
import com.mdj.moudle.coupon.presenter.MyCouponListPresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.ListViewUtils;
import com.mdj.utils.MdjLog;
import com.mdj.view.RefreshableView;
import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyCouponListActivity extends BaseFlatActivity implements AbstractSlideExpandableListAdapter.OnItemExpandCollapseListener
                                ,MyCouponListContract.View, View.OnClickListener, RefreshableView.RefreshListener {
    private ActionSlideExpandableListView lvCouponList;
    private CouponListAdapter adapter;
    private RefreshableView refreshableView;// 下拉刷新

    private List<CouponBean> dataList = new ArrayList<>();
    private MyCouponListContract.Presenter presenter;
/**********************************************************************************************************/
    @Override
    public void findViews() {
        mContext = this;
        setTitle("我的优惠券");

        presenter = new MyCouponListPresenter(this);
        presenter.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            default:
                break;
        }
        super.onClick(v);
    }

    @Override
    public void onExpand(View itemView, int position) {
        try {
            adapter.setToggleState(position, 1);
            View view = lvCouponList.getChildAt(position - lvCouponList.getFirstVisiblePosition());
            if (view != null) {
                CouponListAdapter.ViewHolder holder = (CouponListAdapter.ViewHolder) view.getTag();
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
            View view = lvCouponList.getChildAt(position - lvCouponList.getFirstVisiblePosition());
            if (view != null) {
                CouponListAdapter.ViewHolder holder = (CouponListAdapter.ViewHolder) view.getTag();
                holder.expandable_toggle_button.setImageResource(R.mipmap.arrow_down);
            }
        } catch (Exception e) {
            MdjLog.logD("zzy", "onCollapse e :" + e.getMessage());
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
    public void showDisconnect(String msg) {
        super.showDisconnect();
        if(refreshableView!=null){
            refreshableView.finishRefresh();
        }
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        if (refreshableView == null) {
            setContentView(R.layout.activity_my_coupon);
            refreshableView = (RefreshableView) findViewById(R.id.refresh_root);
            refreshableView.setRefreshListener(this);
            adapter = new CouponListAdapter(this);
            lvCouponList = (ActionSlideExpandableListView) this.findViewById(R.id.list);
            lvCouponList.setEmptyView(ListViewUtils.getEmptyView(mContext, "您还没有优惠券", lvCouponList));
            lvCouponList.setAdapter(adapter, R.id.expandable_toggle_button, R.id.expandable, this);
        }
        refreshableView.finishRefresh();
        dataList = (List<CouponBean>) data;
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void refreshAllData(){
        presenter.start();
    }
    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void onRefresh(RefreshableView view) {
        presenter.getMyCouponList(false);
    }

    @Override
    public boolean canPullDownRefresh() {
        if (lvCouponList == null) {
            return false;
        }
        if(dataList.isEmpty()){
            return true;
        }
        try{
            int top = lvCouponList.getChildAt(0).getTop();
            int pad = lvCouponList.getListPaddingTop();
            if ((Math.abs(top - pad)) < 3 && (lvCouponList).getFirstVisiblePosition() == 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){

        }
        return false;
    }
}
