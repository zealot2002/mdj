package com.mdj.moudle.coupon;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.coupon.presenter.SelectCouponListContract;
import com.mdj.moudle.coupon.presenter.SelectCouponListPresenter;
import com.mdj.utils.ListViewUtils;
import com.mdj.utils.MdjLog;
import com.tjerkw.slideexpandable.library.AbstractSlideExpandableListAdapter;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

import java.util.ArrayList;
import java.util.List;

public class SelectCouponActivity extends BaseActivity implements
        AbstractSlideExpandableListAdapter.OnItemExpandCollapseListener,View.OnClickListener, SelectCouponListContract.View {
	private List<CouponBean> dataList = new ArrayList<>();
    private String projectParams;
    private ActionSlideExpandableListView lvCouponList;
    private CouponListAdapter adapter;

	private CheckBox cbNoUse;
    private RelativeLayout rlSubmit;
    private SelectCouponListContract.Presenter presenter;

/***************************************************方法区**********************************************************/
    private void initListView() {
        lvCouponList = (ActionSlideExpandableListView) findViewById(R.id.lvCouponList);
        adapter = new CouponListAdapter(mContext);
        adapter.setFromSelectCoupon(true);
        lvCouponList.setAdapter(adapter, R.id.expandable_toggle_button, R.id.expandable, this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlSubmit:
                if(cbNoUse.isChecked()){
                    //传回给调用者
                    Intent intent = new Intent();
                    intent.putExtra(BundleConstant.COUPON_BEAN,new CouponBean.Builder("").build());
                    setResult(Activity.RESULT_OK, intent);
                }else{
                    if(adapter.getCurrentSelected()==-1){
                        setResult(Activity.RESULT_CANCELED, new Intent());
                    }else{
                        CouponBean couponBean = dataList.get(adapter.getCurrentSelected());
                        Intent intent = new Intent();
                        intent.putExtra(BundleConstant.COUPON_BEAN,couponBean);
                        setResult(Activity.RESULT_OK, intent);
                    }
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void findViews() {
        this.setContentView(R.layout.select_coupon);
        mContext = this;
        projectParams = getIntent().getStringExtra(BundleConstant.PROJCET_PARAMS);

        cbNoUse = (CheckBox) findViewById(R.id.cbNoUse);
        rlSubmit = (RelativeLayout) findViewById(R.id.rlSubmit);
        rlSubmit.setOnClickListener(this);

        initListView();

        presenter = new SelectCouponListPresenter(this);
        presenter.getAvailableCouponList(projectParams);
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        dataList.addAll((List<CouponBean>) data);
        adapter.setDataList(dataList);
        ListViewUtils.fixListViewHeight(lvCouponList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(Object presenter) {

    }
}
