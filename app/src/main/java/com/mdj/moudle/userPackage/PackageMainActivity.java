package com.mdj.moudle.userPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.common.AbstractPagerListAdapter;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.userPackage.presenter.PackageContract;
import com.mdj.moudle.userPackage.presenter.PackagePresenter;
import com.mdj.moudle.widget.sale.SaleBean;
import com.mdj.moudle.widget.sale.SaleCountDownWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjUtils;
import com.mdj.view.ListenedScrollView;
import com.mdj.view.MyListView;
import com.mdj.view.WrapContentHeightViewPager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class PackageMainActivity extends BaseActivity implements PackageContract.View,View.OnClickListener,
        ListenedScrollView.OnScrollListener,AdapterView.OnItemClickListener {
    /*推荐项目paper*/
    private WrapContentHeightViewPager vpSuggestPackageList;
    private SuggestPackagePagerAdapter suggestPackagePagerAdapter;
    private List<PackageBean> suggestPackageList;

    /*套餐列表*/
    private MyListView lvPackageList;
    private List<PackageBean> dataList = new ArrayList<>();
    private PackageListAdapter adapter;
    private ListenedScrollView observableScrollView;

    private PackageContract.Presenter presenter;
/****************************************************************************************************/
    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.package_main);

        observableScrollView = (ListenedScrollView)findViewById(R.id.observableScrollView);
        observableScrollView.setOnScrollListener(this);

        initSuggestProjectPaper();
        initPackageListView();

        presenter = new PackagePresenter(this);
        presenter.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initPackageListView() {
        lvPackageList = (MyListView)findViewById(R.id.lvPackageList);
        adapter = new PackageListAdapter(mContext);
        lvPackageList.setAdapter(adapter);
        lvPackageList.setOnItemClickListener(this);
    }

    private void initSuggestProjectPaper() {
        vpSuggestPackageList = (WrapContentHeightViewPager)findViewById(R.id.vpSuggestPackageList);
        suggestPackagePagerAdapter = new SuggestPackagePagerAdapter(mContext);
        vpSuggestPackageList.setAdapter(suggestPackagePagerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:


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
        suggestPackagePagerAdapter.setDataList(suggestPackageList);
        suggestPackagePagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateNormalPackageList(List<PackageBean> packageList) {
        dataList.addAll(packageList);
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateBeautyParlorPackageList(List<PackageBean> packageList) {

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
        intent.putExtra(BundleConstant.PACKAGE_ID,dataList.get(i).getId());
        startActivity(intent);
    }

    public class SuggestPackagePagerAdapter extends AbstractPagerListAdapter<PackageBean> {
        private Context context;
        public SuggestPackagePagerAdapter(Context context) {
            this.context = context;
        }

        public void setDataList(List<PackageBean> dataList){
            super.setDataList(dataList);
        }
        @Override
        public View newView(final int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.suggest_package_paper_item,null);
            try{
                ViewHolder holder = new ViewHolder();

                holder.ivPic = (ImageView)view.findViewById(R.id.ivPic);
                holder.tvCurrentPage = (TextView)view.findViewById(R.id.tvCurrentPage);
                holder.tvValidityDays = (TextView)view.findViewById(R.id.tvValidityDays);
                holder.tvName = (TextView)view.findViewById(R.id.tvName);
                holder.tvPrice = (TextView)view.findViewById(R.id.tvPrice);
                holder.tvSymbol = (TextView)view.findViewById(R.id.tvSymbol);
                holder.tvOldPrice = (TextView)view.findViewById(R.id.tvOldPrice);
                holder.tvEfficiency1 = (TextView)view.findViewById(R.id.tvEfficiency1);
                holder.tvEfficiency2 = (TextView)view.findViewById(R.id.tvEfficiency2);
                holder.tvEfficiency3 = (TextView)view.findViewById(R.id.tvEfficiency3);
                holder.saleCountDownWidget = (SaleCountDownWidget)view.findViewById(R.id.saleCountDownWidget);

                holder.rlItem = (RelativeLayout)view.findViewById(R.id.rlItem);
                /**************************************************************************************************************************/
                PackageBean bean = dataList.get(position);
                MyApp.instance.getImageLoad().displayImage(bean.getImgUrl(), holder.ivPic, DisplayImageOptionsUtil.getCommonCacheOptions());

                holder.tvCurrentPage.setText((position + 1) + "/" + dataList.size());

                Typeface newWeiTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/balzac.TTF");
                holder.tvCurrentPage.setTypeface(newWeiTypeFace);
                holder.tvCurrentPage.setText((position+1) + "/" + dataList.size());

                holder.tvValidityDays.setText(bean.getValidityDays()+"天");
                holder.tvName.setText(bean.getName());

                StringBuilder isNormal = new StringBuilder();
                int price = MdjUtils.getCurrentPackagePrice(bean, isNormal);
                holder.tvPrice.setText(price + "");
                if(isNormal.toString().equals("true")){
                    holder.tvPrice.setTextColor(context.getResources().getColor(R.color.text_black));
                    holder.tvSymbol.setTextColor(context.getResources().getColor(R.color.text_black));
                }else{
                    holder.tvPrice.setTextColor(context.getResources().getColor(R.color.red));
                    holder.tvSymbol.setTextColor(context.getResources().getColor(R.color.red));
                }

                holder.tvOldPrice.setText(context.getResources().getString(R.string.symbol_rmb) + bean.getOldPrice());
                holder.tvOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线

                if(bean.getEffectList().size()>0){
                    holder.tvEfficiency1.setText(bean.getEffectList().get(0));
                    holder.tvEfficiency1.setVisibility(View.VISIBLE);
                    if(bean.getEffectList().size()>1) {
                        holder.tvEfficiency2.setText(bean.getEffectList().get(1));
                        holder.tvEfficiency2.setVisibility(View.VISIBLE);
                        if(bean.getEffectList().size()>2) {
                            holder.tvEfficiency3.setText(bean.getEffectList().get(2));
                            holder.tvEfficiency3.setVisibility(View.VISIBLE);
                        }
                    }
                }
                /*倒计时*/
                if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_LIMIT_PRIVILEGE)) {//限时限量
                    holder.saleCountDownWidget.setVisibility(View.VISIBLE);
                    holder.saleCountDownWidget.show(new SaleBean.Builder()
                            .startTime(bean.getStartTime())
                            .endTime(bean.getEndTime())
                            .total(bean.getTotal())
                            .soldNum(bean.getSoldNum())
                            .perCount(bean.getPerCount())
                            .privilegePrice(bean.getPrivilegePrice())
                            .build());
                }else{
                    holder.saleCountDownWidget.setVisibility(View.GONE);
                }
                holder.rlItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext,PackageDetailActivity.class);
                        intent.putExtra(BundleConstant.PACKAGE_ID,dataList.get(position).getId());
                        startActivity(intent);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            return view;
        }

        public class ViewHolder {
            private ImageView ivPic;
            private SaleCountDownWidget saleCountDownWidget;
            private TextView tvCurrentPage,tvValidityDays,tvName,
                    tvSymbol,tvPrice,tvOldPrice,tvEfficiency1,tvEfficiency2,tvEfficiency3;
            private RelativeLayout rlItem;
        }
    }
}
