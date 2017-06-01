package com.mdj.moudle.order;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.address.SearchAddressActivity;
import com.mdj.moudle.address.widget.AddressWidget;
import com.mdj.moudle.login.LoginWidget;
import com.mdj.moudle.login.LoginWidgetAdapter;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.bean.ProjectBean;
import com.mdj.moudle.order.bean.ProjectBeanWrapper;
import com.mdj.moudle.order.presenter.ConfirmPackageOrderContract;
import com.mdj.moudle.order.presenter.ConfirmPackageOrderPresenter;
import com.mdj.moudle.pay.PayActivity;
import com.mdj.moudle.project.bean.ProjectWrapperBean;
import com.mdj.moudle.userPackage.MyPackageListActivity;
import com.mdj.moudle.userPackage.PackageBean;
import com.mdj.view.MyListView;

import java.util.ArrayList;
import java.util.List;

public class ConfirmOrderPackageActivity extends BaseActivity implements
        LoginWidgetAdapter.LoginWidgetEventListener,View.OnClickListener, ConfirmPackageOrderContract.View {
    private static final int REQUEST_CODE_SEARCH_ADDRESS = 4;
    private static final int REQUEST_CODE_PAY = 5;
/*login相关*/
    private LoginWidget loginWidget;
    private LoginWidgetAdapter loginWidgetAdapter;
/*address相关*/
    private AddressWidget addressWidget;
/*项目列表相关*/
    private MyListView lvProjectList;
    private ProjectListAdapter projectListAdapter;
    private List<ProjectBeanWrapper> projectBeanWrapperList;

    private PackageBean packageBean;
    private String beautyParlorId;
/*其他*/
    private Button btnBuy;
    private TextView tvName,tvValidityDays,tvTotalPrice;

    private ConfirmPackageOrderContract.Presenter presenter;
/***************************************************方法区**********************************************************/

    private void initOtherView() {
        btnBuy = (Button)findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(this);

        tvName = (TextView)findViewById(R.id.tvName);
        tvValidityDays = (TextView)findViewById(R.id.tvValidityDays);
        tvTotalPrice = (TextView)findViewById(R.id.tvTotalPrice);

        tvName.setText(packageBean.getName());
        tvValidityDays.setText("使用期限: "+packageBean.getValidityDays()+"天");
        tvTotalPrice.setText(getString(R.string.symbol_rmb) + packageBean.getPrice());
    }

    private void initProjectListView() {
        lvProjectList = (MyListView)findViewById(R.id.lvProjectList);

        projectBeanWrapperList = new ArrayList<>();
        for(ProjectWrapperBean projectBean:packageBean.getProjectList()){
            projectBeanWrapperList.add(new ProjectBeanWrapper(projectBean.getNum(),
                    new ProjectBean(projectBean.getName(),projectBean.getDuration(),projectBean.getPrice())));
        }
        projectListAdapter = new ProjectListAdapter(mContext,projectBeanWrapperList);
        lvProjectList.setAdapter(projectListAdapter);
        projectListAdapter.setIsPackageOrder(true);
        projectListAdapter.notifyDataSetChanged();
    }

    private void initAddressWidget() {
        addressWidget = (AddressWidget)findViewById(R.id.addressWidget);
        addressWidget.setAddressWidgetListener(new AddressWidget.AddressWidgetListener() {
            @Override
            public void onSearchAddress(int position) {
            /*打开搜索地址界面*/
                Intent intent = new Intent(mContext, SearchAddressActivity.class);
                intent.putExtra(BundleConstant.HIDE_RIGHT, true);
                intent.putExtra(BundleConstant.TAG_STR, position + "");
                startActivityForResult(intent, REQUEST_CODE_SEARCH_ADDRESS);
            }
        });
        addressWidget.setVisibility(View.VISIBLE);
    }

    private void initLoginWidget() {
        loginWidget = (LoginWidget)findViewById(R.id.loginWidget);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBuy:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                AddressBean addressBean = addressWidget.getSelectAddressBeanWrapper().getAddressBean();
                if (TextUtils.isEmpty(addressBean.getName())) {
                    showShortToast("地址不能为空");
                    return;
                }
                if (TextUtils.isEmpty(addressBean.getLat())||TextUtils.isEmpty(addressBean.getLng())) {
                    showShortToast("经纬度不能为空");
                    return;
                }
                if (TextUtils.isEmpty(addressBean.getUserName())) {
                    showShortToast("联系人不能为空");
                    return;
                }
                if (TextUtils.isEmpty(addressBean.getUserPhone())) {
                    showShortToast("联系电话不能为空");
                    return;
                }
                presenter.createOrder(addressBean,packageBean.getId(),beautyParlorId);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_CODE_PAY == requestCode) {
            Intent intent = new Intent(mContext, MyPackageListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(REQUEST_CODE_SEARCH_ADDRESS == requestCode&& Activity.RESULT_OK == resultCode) {
            AddressBean addressBean = (AddressBean)data.getSerializableExtra(SearchAddressActivity.RESULT_CODE_SEARCH_ADDRESS);
            int position = Integer.valueOf(data.getStringExtra("tagStr"));
            addressWidget.updateData(addressBean,position);
        }
    }

    @Override
    public void onLogin(int errorCode, String errorMessage) {
        presenter.start();
    }

    @Override
    public void findViews() {
        setContentView(R.layout.confirm_order_package);
        mContext = this;
        packageBean = (PackageBean) getIntent().getSerializableExtra(BundleConstant.PACKAGE_BEAN);
        if(getIntent().hasExtra(BundleConstant.BEAUTY_PARLOR_ID)){
            beautyParlorId = getIntent().getStringExtra(BundleConstant.BEAUTY_PARLOR_ID);
        }
        initLoginWidget();
        initAddressWidget();
        initProjectListView();
        initOtherView();
//        updateUI();
        presenter = new ConfirmPackageOrderPresenter(this);
        presenter.start();
        presenter.getPackageInfo(packageBean.getId());
    }

    @Override
    public void updateLoginWidget(boolean b) {
        if(b){
            loginWidgetAdapter = new LoginWidgetAdapter(mContext,this);
            loginWidget.setAdapter(loginWidgetAdapter);

            loginWidget.setVisibility(View.VISIBLE);
            addressWidget.setVisibility(View.GONE);
        }else{
            loginWidget.setVisibility(View.GONE);
            addressWidget.setVisibility(View.VISIBLE);
            presenter.getAddressList();
        }
    }

    @Override
    public void updateAddress(List<AddressBean> addressList) {
        addressWidget.setVisibility(View.VISIBLE);
        addressWidget.setDataList(addressList);
    }

    @Override
    public void createOrderDone(Object obj) {
        if(obj == null){
            return;
        }
        OrderBean orderBean = (OrderBean) obj;
        Intent intent = new Intent(mContext, PayActivity.class);
        intent.putExtra(BundleConstant.ORDER_BEAN,orderBean);
        startActivityForResult(intent,REQUEST_CODE_PAY);
    }

    @Override
    public void updatePackageProjectList(List<ProjectWrapperBean> projectList) {
        List<ProjectBeanWrapper> wrappers = new ArrayList<>();
        for(ProjectWrapperBean projectBean:projectList){
            ProjectBeanWrapper wrapper = new ProjectBeanWrapper();
            wrapper.setNum(projectBean.getNum());
            wrapper.setProjectBean(new ProjectBean(projectBean.getName(), projectBean.getDuration(), projectBean.getPrice()));
            wrappers.add(wrapper);
        }
        projectListAdapter.setDataList(wrappers);
        projectListAdapter.notifyDataSetChanged();
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
}
