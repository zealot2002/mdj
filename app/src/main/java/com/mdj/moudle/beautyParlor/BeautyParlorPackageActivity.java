package com.mdj.moudle.beautyParlor;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.userPackage.OrderProjectPackageVo;
import com.mdj.moudle.userPackage.PackageBean;
import com.mdj.moudle.userPackage.PackageDetailActivity;
import com.mdj.moudle.userPackage.PackageListAdapter;
import com.mdj.moudle.userPackage.presenter.PackageContract;
import com.mdj.moudle.userPackage.presenter.PackagePresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.view.ListenedScrollView;
import com.mdj.view.MyListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class BeautyParlorPackageActivity extends BaseActivity implements PackageContract.View,
        View.OnClickListener, ListenedScrollView.OnScrollListener, AdapterView.OnItemClickListener {

    /*套餐列表*/
    private MyListView lvPackageList;
    private PackageListAdapter adapter;
    private ListenedScrollView observableScrollView;
    private String beautyParlorId;
    private List<PackageBean> packageList;

    private PackageContract.Presenter presenter;
/****************************************************************************************************/
    @Override
    public void findViews() {
        mContext = this;
        presenter = new PackagePresenter(this);

        setContentView(R.layout.beauty_parlor_package_activity);

        observableScrollView = (ListenedScrollView)findViewById(R.id.observableScrollView);
        observableScrollView.setOnScrollListener(this);

        initPackageListView();

        beautyParlorId = getIntent().getStringExtra(BundleConstant.BEAUTY_PARLOR_ID);
        presenter.getBeautyParlorPackageList(beautyParlorId);
    }

    private void initPackageListView() {
        lvPackageList = (MyListView)findViewById(R.id.lvPackageList);
        adapter = new PackageListAdapter(mContext);
        lvPackageList.setAdapter(adapter);
        lvPackageList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.ll_back:
//
//
//                break;

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
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {

    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void updateSuggestPackageList(List<PackageBean> suggestPackageList) {

    }

    @Override
    public void updateNormalPackageList(List<PackageBean> packageList) {

    }

    @Override
    public void updateBeautyParlorPackageList(List<PackageBean> packageList) {
        this.packageList = packageList;
        adapter.setDataList(packageList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateAvailablePackageList(List<OrderProjectPackageVo> dataList) {

    }

    @Override
    public void onBottomArrived() {
        presenter.loadMoreNormalPackageList();
    }

    @Override
    public void onScrollStateChanged(ListenedScrollView view, int scrollState) {

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(mContext, PackageDetailActivity.class);
        intent.putExtra(BundleConstant.PACKAGE_ID,packageList.get(i).getId());
        intent.putExtra(BundleConstant.BEAUTY_PARLOR_ID,beautyParlorId);
        startActivity(intent);
    }
}
