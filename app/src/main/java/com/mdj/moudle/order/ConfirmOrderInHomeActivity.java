package com.mdj.moudle.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.cache.RefreshManager;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.address.SearchAddressActivity;
import com.mdj.moudle.address.widget.AddressWidget;
import com.mdj.moudle.beautician.SelectBeauticianActivity;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.coupon.CouponBean;
import com.mdj.moudle.coupon.SelectCouponActivity;
import com.mdj.moudle.home.MainActivity;
import com.mdj.moudle.login.LoginWidget;
import com.mdj.moudle.login.LoginWidgetAdapter;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.bean.ProjectBean;
import com.mdj.moudle.order.bean.ProjectBeanWrapper;
import com.mdj.moudle.order.bean.RecommendCouponAndPackageBean;
import com.mdj.moudle.order.presenter.ConfirmOrderContract;
import com.mdj.moudle.order.presenter.ConfirmOrderPresenter;
import com.mdj.moudle.order.serviceHour.ServiceHourBean;
import com.mdj.moudle.order.serviceHour.ServiceHourWidget;
import com.mdj.moudle.pay.PayActivity;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;
import com.mdj.moudle.userPackage.OrderProjectPackageVo;
import com.mdj.moudle.userPackage.SelectPackageActivity;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.TextViewUtil;
import com.mdj.view.MyListView;

import java.util.ArrayList;
import java.util.List;

public class ConfirmOrderInHomeActivity extends BaseActivity implements View.OnClickListener,
        LoginWidgetAdapter.LoginWidgetEventListener,
        ServiceHourWidget.ServiceHourWidgetEventListener,
        ConfirmOrderContract.View
{
    private static final int REQUEST_CODE_SELECT_BEAUTICIAN = 1;
    private static final int REQUEST_CODE_SELECT_PACKAGE = 2;
    private static final int REQUEST_CODE_SELECT_COUPON = 3;
    private static final int REQUEST_CODE_SEARCH_ADDRESS = 4;
    private static final int REQUEST_CODE_PAY = 5;

    /*login相关*/
    private LoginWidget loginWidget;
    private LoginWidgetAdapter loginWidgetAdapter;
    /*address相关*/
    private AddressWidget addressWidget;
    /*serviceHour相关*/
    private ServiceHourWidget serviceHourWidget;
    private RelativeLayout rlSelectServiceTime;
    private ImageView ivSelectHour;
    private TextView tvSelectHour;
    private List<ServiceHourBean> serviceHourBeanList;
    private ServiceHourBean selectedServiceHourBean;
    /*项目列表相关*/
    private MyListView lvProjectList;
    private ProjectListAdapter projectListAdapter;
    private List<ProjectBeanWrapper> projectBeanWrapperList;
    /*选择美容师*/
    private BeauticianBean selectedBeauticianBean;

    /*选择优惠券*/
    private CouponBean selectedCouponBean;

    /*选择套餐*/
    private List<OrderProjectPackageVo> dataListFromRecommendPackage = null;// zzy
    // 来自项目推荐套餐页面的数据


    /*其他*/
    private RelativeLayout rlSubmit,rlSelectServiceBeautician,rlSelectPackage,rlSelectCoupon;
    private TextView tvProjectTotalPrice,tvPackageHint,tvCouponHint,tvBeauticianHint,tvPrice,tvProjectNum,tvServiceTimeLength;
    private ImageView ivBeautician,ivSelectBeauticianArrow;
    private EditText etRemark;

    private StringBuilder projectParams = new StringBuilder();
    private String packageParams;
    private int entry = CommonConstant.ENTRY.NORMAL.value();
    /*追单专用 start*/
    private OrderBean appendOrderBean;
    private LinearLayout llContact;
    private TextView tvUserName,tvUserPhone,tvAddress;
    /*追单专用 end*/
    private ConfirmOrderContract.Presenter presenter;

    private List<AddressBean> oldAddressList = new ArrayList<>();
    /***************************************************方法区**********************************************************/

    private void initOtherView() {
        rlSubmit = (RelativeLayout)findViewById(R.id.rlSubmit);
        rlSubmit.setOnClickListener(this);

        rlSelectServiceBeautician = (RelativeLayout)findViewById(R.id.rlSelectServiceBeautician);

        rlSelectPackage = (RelativeLayout)findViewById(R.id.rlSelectPackage);
        rlSelectPackage.setOnClickListener(this);

        rlSelectCoupon = (RelativeLayout)findViewById(R.id.rlSelectCoupon);
        rlSelectCoupon.setOnClickListener(this);

        tvProjectTotalPrice = (TextView)findViewById(R.id.tvProjectTotalPrice);
        tvPackageHint = (TextView)findViewById(R.id.tvPackageHint);
        tvCouponHint = (TextView)findViewById(R.id.tvCouponHint);
        tvBeauticianHint = (TextView)findViewById(R.id.tvBeauticianHint);

        ivBeautician = (ImageView)findViewById(R.id.ivBeautician);
        ivSelectBeauticianArrow = (ImageView)findViewById(R.id.ivSelectBeauticianArrow);
        etRemark = (EditText)findViewById(R.id.etRemark);

        tvPrice = (TextView)findViewById(R.id.tvPrice);
        tvProjectNum = (TextView)findViewById(R.id.tvProjectNum);
        tvServiceTimeLength = (TextView)findViewById(R.id.tvServiceTimeLength);

        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value() //追单
                ||entry == CommonConstant.ENTRY.MY_BEAUTICIAN.value()//从美容师进入
                ){
            if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){ //追单
                selectedBeauticianBean = new BeauticianBean.Builder(appendOrderBean.getBeauticianName())
                        .id(appendOrderBean.getBeauticianId())
                        .imgUrl(appendOrderBean.getBeauticianImgUrl())
                        .build();
            }
            tvBeauticianHint.setText(selectedBeauticianBean.getName());
            tvBeauticianHint.setTextColor(getResources().getColor(R.color.text_black));
            ivBeautician.setVisibility(View.VISIBLE);
            ivSelectBeauticianArrow.setVisibility(View.GONE);
            MyApp.instance.getImageLoad().displayImage(
                    selectedBeauticianBean.getImgUrl(),ivBeautician,DisplayImageOptionsUtil.getCommonCacheOptions());

        }else{
            rlSelectServiceBeautician.setOnClickListener(this);
        }
    }
    /*从上一个页面拿到数据*/
    private void initProjectListView() {
        lvProjectList = (MyListView)findViewById(R.id.lvProjectList);
        getProjectList();
        projectListAdapter = new ProjectListAdapter(mContext,projectBeanWrapperList);
        lvProjectList.setAdapter(projectListAdapter);
        projectListAdapter.notifyDataSetChanged();
    }

    private void getProjectList() {
        int totalPrice=0,projectNum=0,totalDuration=0;
        List<GoodsWrapperBean> dataList = (List<GoodsWrapperBean>) getIntent().getSerializableExtra(BundleConstant.DATA_LIST);
        projectBeanWrapperList = new ArrayList<>();
        for(GoodsWrapperBean bean:dataList){
            if(bean.getNum()>0){
                projectBeanWrapperList.add(new ProjectBeanWrapper(bean.getNum(), new ProjectBean(bean.getGoodsBean().getName()
                        , bean.getGoodsBean().getDuration(), bean.getGoodsBean().getPrice())));
                projectParams.append(bean.getGoodsBean().getId());
                projectParams.append("_");
                projectParams.append(bean.getNum());
                projectParams.append(",");
                totalPrice+=bean.getGoodsBean().getPrice()*bean.getNum();
                totalDuration+=bean.getGoodsBean().getDuration()*bean.getNum();
                projectNum++;
            }
        }
        projectParams.deleteCharAt(projectParams.length() - 1);

        /*更新ui*/
        tvProjectTotalPrice.setText(getString(R.string.symbol_rmb)+totalPrice+"");
        tvProjectNum.setText(projectNum + "个项目");
        tvServiceTimeLength.setText(totalDuration + "分钟");
    }

    private void initServiceHourWidget() {
        serviceHourWidget = (ServiceHourWidget)findViewById(R.id.serviceHourWidget);
        rlSelectServiceTime = (RelativeLayout)findViewById(R.id.rlSelectServiceTime);

        ivSelectHour = (ImageView)findViewById(R.id.ivSelectHour);
        tvSelectHour = (TextView)findViewById(R.id.tvSelectHour);
        serviceHourWidget.setServiceHourWidgetEventListener(this);
        selectedServiceHourBean = new ServiceHourBean();

        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){ //追单
            tvSelectHour.setText(appendOrderBean.getServiceEndTime());
            ivSelectHour.setVisibility(View.GONE);
            selectedServiceHourBean.setDate(appendOrderBean.getServiceEndTime().substring(0, 10));
            selectedServiceHourBean.setSelectedHour(appendOrderBean.getServiceEndTime().substring(11, 16));
        }else{
            rlSelectServiceTime.setOnClickListener(this);
        }
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

        llContact = (LinearLayout)findViewById(R.id.llContact);
        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){ //追单
            llContact.setVisibility(View.VISIBLE);
            addressWidget.setVisibility(View.GONE);
            tvUserName = (TextView)findViewById(R.id.tvUserName);
            tvUserPhone = (TextView)findViewById(R.id.tvUserPhone);
            tvAddress = (TextView)findViewById(R.id.tvAddress);

            tvUserName.setText(appendOrderBean.getAddressBean().getUserName());
            tvUserPhone.setText(appendOrderBean.getAddressBean().getUserPhone());
            tvAddress.setText(appendOrderBean.getAddressBean().getName());
        }else{
            llContact.setVisibility(View.GONE);
            addressWidget.setVisibility(View.VISIBLE);
        }
    }

    private void initLoginWidget() {
        loginWidget = (LoginWidget)findViewById(R.id.loginWidget);
    }

    private void getRecommendBeautician(){
        if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
            return;/*未登录，直接返回*/
        }
        String lat = addressWidget.getSelectAddressBeanWrapper().getAddressBean().getLat();
        String lng = addressWidget.getSelectAddressBeanWrapper().getAddressBean().getLng();
        if(TextUtils.isEmpty(lat)||TextUtils.isEmpty(lng)){
            return;/*未设置地址，直接返回*/
        }
        String location = lng +"," +lat;
        if (TextUtils.isEmpty(selectedServiceHourBean.getDate())) {
            return;/*未选择时间，直接返回*/
        }
        presenter.getRecommendBeautician(CommonConstant.SERVICE_TYPE_IN_HOME,
                "", location, projectParams.toString(), selectedServiceHourBean.getDate(), selectedServiceHourBean.getSelectedHour());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlSelectServiceTime:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                if(serviceHourWidget.getVisibility() == View.VISIBLE){
                    serviceHourWidget.setVisibility(View.GONE);
                    ivSelectHour.setBackgroundResource(R.mipmap.arrow_right);
                }else{
                    String lat = addressWidget.getSelectAddressBeanWrapper().getAddressBean().getLat();
                    String lng = addressWidget.getSelectAddressBeanWrapper().getAddressBean().getLng();
                    if(TextUtils.isEmpty(lat)||TextUtils.isEmpty(lng)){
                        showShortToast("请您先设置地址");
                        return;
                    }
                    String location = lng +"," +lat;
                    presenter.getServiceHours(projectParams.toString(), location, "",
                            selectedBeauticianBean == null ? "" : selectedBeauticianBean.getId() == null ? "" : selectedBeauticianBean.getId());
                }
                break;

            case R.id.rlSelectServiceBeautician:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                String lat = addressWidget.getSelectAddressBeanWrapper().getAddressBean().getLat();
                String lng = addressWidget.getSelectAddressBeanWrapper().getAddressBean().getLng();
                if(TextUtils.isEmpty(lat)||TextUtils.isEmpty(lng)){
                    showShortToast("请您先设置地址");
                    return;
                }
                String location = lng +"," +lat;

                if (TextUtils.isEmpty(selectedServiceHourBean.getDate())) {
                    showShortToast("请先选择服务时间");
                    return;
                }
                Intent intent1 = new Intent(mContext, SelectBeauticianActivity.class);
                intent1.putExtra("serviceType",CommonConstant.SERVICE_TYPE_IN_HOME);
                intent1.putExtra("location",location);
                intent1.putExtra("projectParams",projectParams.toString());
                intent1.putExtra("orderDate",selectedServiceHourBean.getDate());
                intent1.putExtra("startTime",selectedServiceHourBean.getSelectedHour());
                startActivityForResult(intent1, REQUEST_CODE_SELECT_BEAUTICIAN);

                break;

            case R.id.rlSelectPackage:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                Intent intent2 = new Intent(mContext, SelectPackageActivity.class);
                Bundle b = new Bundle();
                b.putString(BundleConstant.PROJCET_PARAMS,projectParams.toString());
                intent2.putExtras(b);
                startActivityForResult(intent2, REQUEST_CODE_SELECT_PACKAGE);
                break;

            case R.id.rlSelectCoupon:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                Intent intent3 = new Intent(mContext, SelectCouponActivity.class);
                intent3.putExtra(BundleConstant.PROJCET_PARAMS,projectParams.toString());
                startActivityForResult(intent3, REQUEST_CODE_SELECT_COUPON);
                break;

            case R.id.rlSubmit:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                AddressBean addressBean = null;
                if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){ //追单
                    addressBean = appendOrderBean.getAddressBean();
                }else{
                    addressBean = addressWidget.getSelectAddressBeanWrapper().getAddressBean();
                }
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
                if (TextUtils.isEmpty(selectedServiceHourBean.getDate())) {
                    showShortToast("请选择服务时间");
                    return;
                }
                if (selectedBeauticianBean == null) {
                    showShortToast("请选择服务技师");
                    return;
                }

                presenter.createOrder(CommonConstant.SERVICE_TYPE_IN_HOME
                        , fixAddressBean(addressBean)
                        , projectParams.toString()
                        , selectedBeauticianBean.getId()
                        , selectedServiceHourBean.getDate()
                        , selectedServiceHourBean.getSelectedHour()
                        , etRemark.getText().toString().trim()
                        , ""
                        , packageParams
                        , selectedCouponBean==null?"":selectedCouponBean.getId()==null?"":selectedCouponBean.getId()
                        , appendOrderBean==null?"":appendOrderBean.getId()==null?"":appendOrderBean.getId()
                );
                break;

            default:
                break;
        }
    }

    private AddressBean fixAddressBean(AddressBean addressBean){
        if(TextUtils.isEmpty(addressBean.getId())){ /*id空，说明是新增的*/
            return addressBean;
        }
        for(AddressBean bean:oldAddressList){
            if(bean.getId().equals(addressBean.getId())){/*id不空，并且有内容改变，则将此种情况视为新增的对象*/
                if(!bean.getLng().equals(addressBean.getLng())
                        ||!bean.getLat().equals(addressBean.getLat())
                        ||!bean.getUserName().equals(addressBean.getUserName())
                        ||!bean.getUserPhone().equals(addressBean.getUserPhone())
                        ||!bean.getName().equals(addressBean.getName())
                        ||!bean.getDoorNum().equals(addressBean.getDoorNum())
                        ){
                    addressBean.setId("");
                    return addressBean;
                }
            }
        }
        return addressBean;/*id不空，内容无改变*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_CODE_SELECT_BEAUTICIAN == requestCode&& Activity.RESULT_OK == resultCode) {
            selectedBeauticianBean = (BeauticianBean)data.getSerializableExtra(SelectBeauticianActivity.RESULT_CODE_SELECT_BEAUTICIAN);
            //恢复数据
            tvBeauticianHint.setText(selectedBeauticianBean.getName());
            tvBeauticianHint.setTextColor(getResources().getColor(R.color.text_black));
            ivBeautician.setVisibility(View.VISIBLE);
            MyApp.instance.getImageLoad().displayImage(
                    selectedBeauticianBean.getImgUrl(),ivBeautician,DisplayImageOptionsUtil.getCommonCacheOptions());
        }else if(REQUEST_CODE_SEARCH_ADDRESS == requestCode&& Activity.RESULT_OK == resultCode) {
            AddressBean addressBean = (AddressBean)data.getSerializableExtra(SearchAddressActivity.RESULT_CODE_SEARCH_ADDRESS);
            int position = Integer.valueOf(data.getStringExtra("tagStr"));
            addressWidget.updateData(addressBean,position);
        }else if(REQUEST_CODE_PAY == requestCode) {
            RefreshManager.setNeedRefreshFlag(RefreshManager.ORDER_LIST_FRAGMENT);

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra(MainActivity.MAIN_FRAGMENT_INDEX, 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(REQUEST_CODE_SELECT_COUPON == requestCode&& Activity.RESULT_OK == resultCode) {
            selectedCouponBean = (CouponBean) data.getSerializableExtra(BundleConstant.COUPON_BEAN);
            //恢复数据
            if(TextUtils.isEmpty(selectedCouponBean.getName())){
                selectedCouponBean = null;
                tvCouponHint.setText("不使用优惠券");
            } else {
                tvCouponHint.setText(selectedCouponBean.getPrice()+"元 "+selectedCouponBean.getName());
            }
            tvCouponHint.setTextColor(getResources().getColor(R.color.text_black));
            presenter.calRealPrice(projectParams.toString(),
                    selectedCouponBean == null?"":selectedCouponBean.getId()==null?"":selectedCouponBean.getId()
                    ,packageParams, CommonConstant.SERVICE_TYPE_IN_HOME);
        }else if(REQUEST_CODE_SELECT_PACKAGE == requestCode&& Activity.RESULT_OK == resultCode) {
            boolean use = data.getExtras().getBoolean(BundleConstant.USE_PACKAGE);
            if (use) {
                dataListFromRecommendPackage = (List<OrderProjectPackageVo>) data.getExtras().getSerializable(BundleConstant.DATA_LIST);
                packageParams = getPackageParams();
                String s = "套餐抵用" + getFreeNum() + "次";
                tvPackageHint.setTextColor(getResources().getColor(R.color.text_black));
                TextViewUtil.setSpecialTextColor(tvPackageHint,
                        s,
                        mContext.getResources().getColor(R.color.text_black),
                        mContext.getResources().getColor(R.color.red),
                        4,
                        s.length()-1
                );
            } else {// 用户选择不使用套餐
                packageParams = "0_0";
                tvPackageHint.setText("不使用套餐");
            }
            presenter.getRecommendCouponAndPackage(projectParams.toString(), "", packageParams);
        }
    }
    /** 获取总抵用次数 **/
    private int getFreeNum() {
        int num = 0;
        for (OrderProjectPackageVo projectVo : dataListFromRecommendPackage) {
            for (OrderProjectPackageVo.PackageUseForOrderProjectVo packageVo : projectVo.getPackageUseForOrderProjectVoList()) {
                num += packageVo.getAllocNum();
            }
        }
        return num;
    }

    /** 14_991438339307457_2：项目id+套餐的订单号+套餐的使用次数**/
    private String getPackageParamsForCreateOrder() {
        if (dataListFromRecommendPackage == null) {
            return "";
        }
        String packageString = "";
        for (OrderProjectPackageVo projectVo : dataListFromRecommendPackage) {
            String projectId = projectVo.getId();
            String orderId = "";
            int num = 0;
            for (OrderProjectPackageVo.PackageUseForOrderProjectVo packageVo : projectVo.getPackageUseForOrderProjectVoList()) {
                orderId = packageVo.getOrderId();
                num = packageVo.getAllocNum();
                if(num == 0){
                    continue;
                }
                packageString += projectId + "_" + orderId + "_" + num + ",";
            }
        }
        if (packageString.length() > 0)// 去掉最后的","
            packageString = packageString.substring(0, packageString.length() - 1);
        return packageString;
    }

    /** 14_991438339307457_2_12：项目id+套餐的订单号+套餐的使用次数+套餐id **/
    private String getPackageParams() {
        if (dataListFromRecommendPackage == null) {
            return "";
        }
        String packageString = "";
        for (OrderProjectPackageVo projectVo : dataListFromRecommendPackage) {
            String projectId = projectVo.getId();
            String orderId = "";
            int num = 0;
            for (OrderProjectPackageVo.PackageUseForOrderProjectVo packageVo : projectVo.getPackageUseForOrderProjectVoList()) {
                orderId = packageVo.getOrderId();
                num = packageVo.getAllocNum();
                if(num == 0){
                    continue;
                }
                packageString += projectId + "_" + orderId + "_" + num + "_" + packageVo.getId() + ",";
            }
        }
        if (packageString.length() > 0)// 去掉最后的","
            packageString = packageString.substring(0, packageString.length() - 1);
        return packageString;
    }

    @Override
    public void onLogin(int errorCode, String errorMessage) {
        presenter.start();
    }

    @Override
    public void onSelected(ServiceHourBean serviceHourBean) {
        //更新显示
        String str = serviceHourBean.getDate() + " " + serviceHourBean.getSelectedHour();
        tvSelectHour.setText(str);
        tvSelectHour.setTextColor(getResources().getColor(R.color.text_black));
        //hide widget
        serviceHourWidget.setVisibility(View.GONE);
        //update icon
        ivSelectHour.setBackgroundResource(R.mipmap.arrow_right);
        //update selectedServiceHourBean
        selectedServiceHourBean.cloneObj(serviceHourBean);

        getRecommendBeautician();
    }

    @Override
    public ServiceHourBean getLastSelectedServiceHourBean() {
        return selectedServiceHourBean;
    }
    @Override
    public void onServiceHourWidgetEvent() {

    }

    @Override
    public void findViews() {
        setContentView(R.layout.confirm_order_in_home);
        mContext = this;
        entry = getIntent().getIntExtra(BundleConstant.ENTRY, CommonConstant.ENTRY.NORMAL.value());
//        isAppendOrder = getIntent().getBooleanExtra(BundleConstant.IS_APPEND_ORDER,false);
        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){ //追单
            appendOrderBean = (OrderBean) getIntent().getSerializableExtra(BundleConstant.ORDER_BEAN);
        }else if(entry == CommonConstant.ENTRY.MY_BEAUTICIAN.value()){ //从美容师进来
            selectedBeauticianBean = (BeauticianBean) getIntent().getSerializableExtra(BundleConstant.BEAUTICIAN_BEAN);
        }
        initLoginWidget();
        initAddressWidget();
        initServiceHourWidget();
        initOtherView();
        initProjectListView();

        presenter = new ConfirmOrderPresenter(this);
        presenter.start();
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
    public void updateLoginWidget(boolean b) {
        if(b){
            loginWidgetAdapter = new LoginWidgetAdapter(mContext,this);
            loginWidget.setAdapter(loginWidgetAdapter);

            loginWidget.setVisibility(View.VISIBLE);
            if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){ //追单
                llContact.setVisibility(View.GONE);
            }else{
                addressWidget.setVisibility(View.GONE);
            }
        }else{
            loginWidget.setVisibility(View.GONE);
            if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){ //追单
                llContact.setVisibility(View.VISIBLE);
                /*获取推荐套餐和优惠券*/
                presenter.getRecommendCouponAndPackage(projectParams.toString());
            }else{
                addressWidget.setVisibility(View.VISIBLE);
                presenter.getAddressList();
            }
        }
    }

    @Override
    public void updatePackageAndCoupon(RecommendCouponAndPackageBean recommendCouponAndPackageBean) {
        RecommendCouponAndPackageBean.CouponInfo couponInfo = recommendCouponAndPackageBean.getCouponInfo();
        if (!TextUtils.isEmpty(couponInfo.getName())) {
            selectedCouponBean = new CouponBean.Builder(couponInfo.getName())
                    .id(couponInfo.getId())
                    .build();
            tvCouponHint.setText(selectedCouponBean.getName());
            tvCouponHint.setTextColor(getResources().getColor(R.color.text_black));
            rlSelectCoupon.setEnabled(true);
        } else {
            selectedCouponBean = null;
            tvCouponHint.setTextColor(getResources().getColor(R.color.text_gray));
            tvCouponHint.setText("无可用优惠券");
            rlSelectCoupon.setEnabled(false);
        }

        RecommendCouponAndPackageBean.PackageInfo packageInfo = recommendCouponAndPackageBean.getPackageInfo();
        if (!TextUtils.isEmpty(packageInfo.getDetail())) {
            tvPackageHint.setTextColor(getResources().getColor(R.color.text_black));
            String s = "套餐抵用" + packageInfo.getNum() + "次";
            TextViewUtil.setSpecialTextColor(tvPackageHint,
                    s,
                    mContext.getResources().getColor(R.color.text_black),
                    mContext.getResources().getColor(R.color.red),
                    4,
                    s.length() - 1
            );
            rlSelectPackage.setEnabled(true);
            packageParams = packageInfo.getDetail();
        } else {
            if(!tvPackageHint.getText().toString().equals("无可用套餐")){
                packageParams = "0_0";
                tvPackageHint.setText("不使用套餐");
            }else{
                rlSelectPackage.setEnabled(false);
            }
        }
        presenter.calRealPrice(projectParams.toString(),selectedCouponBean == null?"":selectedCouponBean.getId()
                ,packageParams, CommonConstant.SERVICE_TYPE_IN_HOME);
    }

    @Override
    public void updateServiceHours(List<ServiceHourBean> serviceHourBeanList) {
        serviceHourWidget.setDataList(serviceHourBeanList);
        serviceHourWidget.setVisibility(View.VISIBLE);
        ivSelectHour.setBackgroundResource(R.mipmap.arrow_down);

        /*技师也需要重新选择*/
        if(entry != CommonConstant.ENTRY.MY_BEAUTICIAN.value()){
            clearSelectedBeautician();
        }
//        getRecommendBeautician();
    }

    private void clearSelectedBeautician(){
        selectedBeauticianBean = null;
        tvBeauticianHint.setText("请选择服务技师");
        tvBeauticianHint.setTextColor(getResources().getColor(R.color.text_gray));
        ivBeautician.setVisibility(View.GONE);
    }
    @Override
    public void updateRealPrice(int realPrice) {
        tvPrice.setText(realPrice + "");
    }

    @Override
    public void updateAddress(List<AddressBean> addressList) {
        oldAddressList.clear();
        oldAddressList.addAll(addressList);

        addressWidget.setVisibility(View.VISIBLE);
        addressWidget.setDataList(addressList);
        /*获取推荐套餐和优惠券*/
        presenter.getRecommendCouponAndPackage(projectParams.toString());
    }

    @Override
    public void createOrderDone(Object obj) {
        if(obj == null){
            return;
        }
        OrderBean orderBean = (OrderBean) obj;
        if(orderBean.getPrice().equals("0")){
            RefreshManager.setNeedRefreshFlag(RefreshManager.ORDER_LIST_FRAGMENT);
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra(MainActivity.MAIN_FRAGMENT_INDEX, 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        Intent intent = new Intent(mContext, PayActivity.class);
        intent.putExtra(BundleConstant.ORDER_BEAN,orderBean);
        startActivityForResult(intent,REQUEST_CODE_PAY);
    }

    @Override
    public void updateDefaultAddress(AddressBean addressBean) {

    }

    @Override
    public void updateRecommendBeautician(BeauticianBean beauticianBean) {
        selectedBeauticianBean = beauticianBean;
        //恢复数据
        tvBeauticianHint.setText(selectedBeauticianBean.getName());
        tvBeauticianHint.setTextColor(getResources().getColor(R.color.text_black));
        ivBeautician.setVisibility(View.VISIBLE);
        MyApp.instance.getImageLoad().displayImage(
                selectedBeauticianBean.getImgUrl(),ivBeautician,DisplayImageOptionsUtil.getCommonCacheOptions());
    }
}
