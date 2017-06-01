package com.mdj.moudle.beautyParlor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.address.SearchAddressActivity;
import com.mdj.moudle.beautyParlor.presenter.BeautyParlorListContract;
import com.mdj.moudle.beautyParlor.presenter.BeautyParlorListPresenter;
import com.mdj.moudle.order.ConfirmOrderToShopActivity;
import com.mdj.moudle.order.serviceHour.ServiceHourBean;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;
import com.mdj.utils.BaiduHelper;
import com.mdj.utils.ListViewUtils;
import com.mdj.view.MyListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BeautyParlorListActivity extends BaseActivity implements BeautyParlorListAdapter.BeautyParlorListListener
            ,BeautyParlorListContract.View,View.OnClickListener{
    private static final int REQUEST_CODE_SEARCH_ADDRESS = 1;
    private MyListView lvBeautyParlorList;
    private BeautyParlorListAdapter adapter;
    private List<BeautyParlorWrapperBean> dataList = new ArrayList<>();
    private List<ServiceHourBean> currentServiceHourBeanList = new ArrayList<>();

    private List<List<ServiceHourBean>> beautyParlorServiceHoursList;
    private RelativeLayout rlSearchAddress;
    private TextView tvAddress;

    private StringBuilder projectParams = new StringBuilder();
    private List<GoodsWrapperBean> goodsList;
    private BeautyParlorListContract.Presenter presenter;
/*************************************************************************************************/
    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.beauty_parlor_list);
        rlSearchAddress = (RelativeLayout)findViewById(R.id.rlSearchAddress);
        rlSearchAddress.setOnClickListener(this);
        tvAddress = (TextView)findViewById(R.id.tvAddress);

        initBeautyPorlorListView();

        getProjectParams();

        showLoading();
        BaiduHelper.getInstance().getCurrentLocation(mContext, new BaiduHelper.LocationEventListener() {
            @Override
            public void onLocationEvent(boolean b,double curLng, double curLat, String addressStr, String cityName) {
                closeLoading();
                if(b &&!TextUtils.isEmpty(cityName)&&
                        (cityName.equals(CacheManager.getInstance().getGlobalCity().getName())  //等于或包含
                        || cityName.contains(CacheManager.getInstance().getGlobalCity().getName()))){
                    /*所在位置与首页城市相同*/
                    tvAddress.setText(addressStr);
                    presenter.getBeautyParlorList(projectParams.toString(),curLng+"",curLat+"");
                }else{
                    /*定位失败、或所在位置与首页城市不同*/
                    tvAddress.setText("当前位置未知，点击进行设置");
                    presenter.getBeautyParlorList(projectParams.toString(),"","");
                }
            }
        });
        presenter = new BeautyParlorListPresenter(this);
        presenter.start();
    }

    private void getProjectParams() {
        goodsList = (List<GoodsWrapperBean>) getIntent().getSerializableExtra("dataList");
        for(GoodsWrapperBean bean:goodsList){
            if(bean.getNum()>0){
                projectParams.append(bean.getGoodsBean().getId());
                projectParams.append("_");
                projectParams.append(bean.getNum());
                projectParams.append(",");
            }
        }
        projectParams.deleteCharAt(projectParams.length() - 1);
    }

    private void initBeautyPorlorListView() {
        lvBeautyParlorList = (MyListView)findViewById(R.id.lvBeautyParlorList);
        adapter = new BeautyParlorListAdapter(mContext,this);
        lvBeautyParlorList.setAdapter(adapter);
        lvBeautyParlorList.setEmptyView(ListViewUtils.getEmptyView(mContext, "请尝试不同地址选择门店", lvBeautyParlorList));
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.rlSearchAddress:
                Intent intent = new Intent(mContext,SearchAddressActivity.class);
                intent.putExtra("hideRight",true);
                startActivityForResult(intent, REQUEST_CODE_SEARCH_ADDRESS);
                break;
	
			default:
				break;
		}
	}

    @Override
    public void onSelected(int position, ServiceHourBean serviceHourBean) {
        Intent intent = new Intent(this, ConfirmOrderToShopActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleConstant.BEAUTY_PARLOR_BEAN, dataList.get(position).getBeautyParlorBean());
        bundle.putSerializable(BundleConstant.SERVICE_HOUR_BEAN,serviceHourBean);
        bundle.putSerializable(BundleConstant.DATA_LIST, (Serializable) goodsList);
        bundle.putSerializable(BundleConstant.SERVICE_HOUR_BEAN_LIST, (Serializable)  currentServiceHourBeanList);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(int position) {
        boolean expand = dataList.get(position).isExpanded();
        for(int i=0;i<dataList.size();i++){//全部关闭
            dataList.get(i).setIsExpanded(false);
        }
        if(!expand){//如果处于关闭，则打开
            dataList.get(position).setIsExpanded(true);
            if(beautyParlorServiceHoursList.get(position).isEmpty()){
                presenter.getBeautyParlorServiceHours(projectParams.toString(),dataList.get(position).getBeautyParlorBean().getId(),position);
            }else{
                //update data
                adapter.setDataList(dataList,beautyParlorServiceHoursList.get(position));
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        List<BeautyParlorBean> beautyParlorBeans = (List<BeautyParlorBean>) data;
        for(BeautyParlorBean bean:beautyParlorBeans){
            dataList.add(new BeautyParlorWrapperBean(false,bean));
        }
        beautyParlorServiceHoursList = new ArrayList<>();
        for(int i=0;i<dataList.size();i++){
            beautyParlorServiceHoursList.add(new ArrayList<ServiceHourBean>());
        }
        adapter.setDataList(dataList, new ArrayList<ServiceHourBean>());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void updateServiceHours(int index, List<ServiceHourBean> serviceHourBeanList) {
//update data
        currentServiceHourBeanList.addAll(serviceHourBeanList);
        beautyParlorServiceHoursList.get(index).addAll(serviceHourBeanList);
        adapter.setDataList(dataList,beautyParlorServiceHoursList.get(index));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_SEARCH_ADDRESS == requestCode&& Activity.RESULT_OK == resultCode) {
            AddressBean addressBean = (AddressBean)data.getSerializableExtra(SearchAddressActivity.RESULT_CODE_SEARCH_ADDRESS);
            tvAddress.setText(addressBean.getName());
            presenter.getBeautyParlorList(projectParams.toString(), addressBean.getLat(), addressBean.getLng());
            dataList.clear();
            beautyParlorServiceHoursList.clear();
        }
    }
}