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
import com.mdj.moudle.beautician.SelectBeauticianActivity;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.beautyParlor.BeautyParlorBean;
import com.mdj.moudle.beautyParlor.BeautyParlorListActivity;
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
import com.mdj.view.RoundImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfirmOrderToShopActivity extends BaseActivity implements
        View.OnClickListener, LoginWidgetAdapter.LoginWidgetEventListener, ServiceHourWidget.ServiceHourWidgetEventListener
        ,ConfirmOrderContract.View{
    private static final int REQUEST_CODE_SELECT_BEAUTICIAN = 1;
    private static final int REQUEST_CODE_SELECT_PACKAGE = 2;
    private static final int REQUEST_CODE_SELECT_COUPON = 3;
    private static final int REQUEST_CODE_PAY = 5;

/*login相关*/
    private LoginWidget loginWidget;
    private LoginWidgetAdapter loginWidgetAdapter;
/*contact相关*/
    private LinearLayout llContact;
    private com.mdj.view.ClearEditText etUserName,etUserPhone;
/*美容院相关*/
    private RelativeLayout rlSelectBeautyParlor;
    private RoundImageView ivBeautyParlorImg;
    private TextView tvBeautyParlorAddress,tvBeautyParlorName;
    private BeautyParlorBean beautyParlorBean;
/*serviceHour相关*/
    private ServiceHourWidget serviceHourWidget;
    private RelativeLayout rlSelectServiceTime;
    private ImageView ivSelectHour;
    private TextView tvSelectHour;
    private ServiceHourBean selectedServiceHourBean = new ServiceHourBean();
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
    private ImageView ivBeautician,ivSelectBeauticianArrow,ivSelectBeautyParlorArrow;
    private EditText etRemark;

    private List<GoodsWrapperBean> goodsList;
    private String couponId,packageParams;
    private StringBuilder projectParams = new StringBuilder();
    private int totalPrice=0,totalDuration=0;

    private int entry = CommonConstant.ENTRY.NORMAL.value();

    /*追单专用*/
    private OrderBean appendOrderBean;
    private ConfirmOrderContract.Presenter presenter;
/***************************************************方法区**********************************************************/
    private void initBeautyParlorView() {
        rlSelectBeautyParlor = (RelativeLayout)findViewById(R.id.rlSelectBeautyParlor);
        ivBeautyParlorImg = (RoundImageView)findViewById(R.id.ivBeautyParlorImg);
        ivSelectBeautyParlorArrow = (ImageView)findViewById(R.id.ivSelectBeautyParlorArrow);
        tvBeautyParlorAddress = (TextView)findViewById(R.id.tvBeautyParlorAddress);
        tvBeautyParlorName = (TextView)findViewById(R.id.tvBeautyParlorName);

        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){
            beautyParlorBean = new BeautyParlorBean.Builder()
                .id(appendOrderBean.getBeautyParlorBean().getId())
                .name(appendOrderBean.getBeautyParlorBean().getName())
                .address(appendOrderBean.getBeautyParlorBean().getAddress())
                .imgUrl(appendOrderBean.getBeautyParlorBean().getImgUrl())
                .build();
            ivSelectBeautyParlorArrow.setVisibility(View.GONE);
        }else if(entry == CommonConstant.ENTRY.BEAUTY_PARLOR.value()){
            ivSelectBeautyParlorArrow.setVisibility(View.GONE);
        }else{
            rlSelectBeautyParlor.setOnClickListener(this);
        }
        if(beautyParlorBean!=null){/*从套餐下单时候，美容院对象是null*/
            MyApp.instance.getImageLoad().displayImage(beautyParlorBean.getImgUrl(),
                    ivBeautyParlorImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            tvBeautyParlorAddress.setText(beautyParlorBean.getAddress());
            tvBeautyParlorName.setText(beautyParlorBean.getName());
        }
    }

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

        tvPrice = (TextView)findViewById(R.id.tvPrice);
        tvProjectNum = (TextView)findViewById(R.id.tvProjectNum);
        tvServiceTimeLength = (TextView)findViewById(R.id.tvServiceTimeLength);
        etRemark = (EditText)findViewById(R.id.etRemark);

        /*更新ui*/
        tvProjectTotalPrice.setText(getString(R.string.symbol_rmb)+totalPrice+"");
        tvProjectNum.setText(projectBeanWrapperList.size()+"个项目");
        tvServiceTimeLength.setText(totalDuration+"分钟");

        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){
            selectedBeauticianBean = new BeauticianBean.Builder(appendOrderBean.getBeauticianName())
                    .id(appendOrderBean.getBeauticianId())
                    .imgUrl(appendOrderBean.getBeauticianImgUrl())
                    .build();

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
    /*
    * 选择美容院进入
    * 美容院详情进入
    * */
    private void getData(Intent intent){
        entry = intent.getIntExtra(BundleConstant.ENTRY, CommonConstant.ENTRY.NORMAL.value());
        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){
            appendOrderBean = (OrderBean) intent.getSerializableExtra(BundleConstant.ORDER_BEAN);
        }
        if(intent.hasExtra(BundleConstant.DATA_LIST)) {
            goodsList = (List<GoodsWrapperBean>) intent.getSerializableExtra(BundleConstant.DATA_LIST);
        }
        if(intent.hasExtra(BundleConstant.BEAUTY_PARLOR_BEAN)) {
            beautyParlorBean = (BeautyParlorBean) intent.getSerializableExtra(BundleConstant.BEAUTY_PARLOR_BEAN);
        }
        if(intent.hasExtra(BundleConstant.SERVICE_HOUR_BEAN)){
            selectedServiceHourBean = (ServiceHourBean) intent.getSerializableExtra(BundleConstant.SERVICE_HOUR_BEAN);
        }
        projectParams = new StringBuilder();
        projectBeanWrapperList = new ArrayList<>();
        for(GoodsWrapperBean bean:goodsList) {
            if (bean.getNum() > 0) {
                projectBeanWrapperList.add(new ProjectBeanWrapper(bean.getNum(), new ProjectBean(bean.getGoodsBean().getName()
                        , bean.getGoodsBean().getDuration(), bean.getGoodsBean().getPrice())));
            projectParams.append(bean.getGoodsBean().getId());
            projectParams.append("_");
            projectParams.append(bean.getNum());
            projectParams.append(",");
            }
            totalPrice+=bean.getGoodsBean().getPrice()*bean.getNum();
            totalDuration+=bean.getGoodsBean().getDuration()*bean.getNum();
        }
        projectParams.deleteCharAt(projectParams.length() - 1);
    }
    private void initProjectListView() {
        lvProjectList = (MyListView)findViewById(R.id.lvProjectList);
        projectListAdapter = new ProjectListAdapter(mContext,projectBeanWrapperList);
        lvProjectList.setAdapter(projectListAdapter);
        projectListAdapter.notifyDataSetChanged();
    }

    private void initServiceHourWidget() {
        serviceHourWidget = (ServiceHourWidget)findViewById(R.id.serviceHourWidget);
        rlSelectServiceTime = (RelativeLayout)findViewById(R.id.rlSelectServiceTime);

        ivSelectHour = (ImageView)findViewById(R.id.ivSelectHour);
        tvSelectHour = (TextView)findViewById(R.id.tvSelectHour);
        serviceHourWidget.setServiceHourWidgetEventListener(this);
        if(selectedServiceHourBean!=null&&!TextUtils.isEmpty(selectedServiceHourBean.getDate())){
            String str = selectedServiceHourBean.getDate() + " " + selectedServiceHourBean.getSelectedHour();
            updateSelectHour(str);
        }

        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){
            tvSelectHour.setText(appendOrderBean.getServiceEndTime());
            ivSelectHour.setVisibility(View.GONE);
            selectedServiceHourBean.setDate(appendOrderBean.getServiceEndTime().substring(0, 10));
            selectedServiceHourBean.setSelectedHour(appendOrderBean.getServiceEndTime().substring(11, 16));
        }else{
            rlSelectServiceTime.setOnClickListener(this);
        }
    }

    private void initContactView() {
        llContact = (LinearLayout)findViewById(R.id.llContact);
        etUserName = (com.mdj.view.ClearEditText)findViewById(R.id.etUserName);
        etUserPhone = (com.mdj.view.ClearEditText)findViewById(R.id.etUserPhone);

        if(entry == CommonConstant.ENTRY.APPEND_ORDER.value()){
            etUserName.setText(appendOrderBean.getAddressBean().getUserName());
            etUserPhone.setText(appendOrderBean.getAddressBean().getUserPhone());
        }
    }

    private void initLoginWidget() {
        loginWidget = (LoginWidget)findViewById(R.id.loginWidget);
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
                    presenter.getServiceHours(projectParams.toString(),"",
                            beautyParlorBean == null?"":beautyParlorBean.getId()==null?"":beautyParlorBean.getId(),"");
                }
                break;

            case R.id.rlSelectServiceBeautician:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                if (TextUtils.isEmpty(selectedServiceHourBean.getDate())) {
                    showShortToast("请先选择服务时间");
                    return;
                }
                Intent intent1 = new Intent(mContext, SelectBeauticianActivity.class);
                intent1.putExtra("serviceType",CommonConstant.SERVICE_TYPE_TO_SHOP);
                intent1.putExtra("beautyParlorId",beautyParlorBean == null?"":beautyParlorBean.getId()==null?"":beautyParlorBean.getId());
                intent1.putExtra("projectParams",projectParams.toString());
                intent1.putExtra("orderDate",selectedServiceHourBean.getDate());
                intent1.putExtra("startTime",selectedServiceHourBean.getSelectedHour());
                startActivityForResult(intent1, REQUEST_CODE_SELECT_BEAUTICIAN);

                break;

            case R.id.rlSelectBeautyParlor:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                Intent intent4 = new Intent(mContext, BeautyParlorListActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("dataList", (Serializable) goodsList);
                intent4.putExtras(mBundle);
                startActivity(intent4);
                break;

            case R.id.rlSelectPackage:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                Intent intent2 = new Intent(mContext, SelectPackageActivity.class);
                Bundle b = new Bundle();
                b.putString("projectParams",projectParams.toString());
                intent2.putExtras(b);
                startActivityForResult(intent2, REQUEST_CODE_SELECT_PACKAGE);
                break;

            case R.id.rlSelectCoupon:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                Intent intent3 = new Intent(mContext, SelectCouponActivity.class);
                intent3.putExtra("projectParams", projectParams.toString());
                startActivityForResult(intent3, REQUEST_CODE_SELECT_COUPON);
                break;

            case R.id.rlSubmit:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    showShortToast("请您先登录");
                    return;
                }
                if (TextUtils.isEmpty(etUserName.getText().toString())) {
                    showShortToast("请填写联系人");
                    return;
                }
                if (TextUtils.isEmpty(etUserPhone.getText().toString())) {
                    showShortToast("请填写联系电话");
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
                if(beautyParlorBean == null){
                    showShortToast("请选择美容院");
                    return;
                }

                presenter.createOrder(CommonConstant.SERVICE_TYPE_TO_SHOP
                        ,new AddressBean(etUserName.getText().toString()
                        ,etUserPhone.getText().toString()
                            ,beautyParlorBean.getAddress())
                        ,projectParams.toString()
                        ,selectedBeauticianBean.getId()
                        ,selectedServiceHourBean.getDate()
                        ,selectedServiceHourBean.getSelectedHour()
                        ,etRemark.getText().toString().trim()
                        ,beautyParlorBean.getId()
                        ,packageParams
                        ,couponId
                    ,null
                );
                break;

            default:
                break;
        }
    }

    private void clearSelectedBeautician(){
        selectedBeauticianBean = null;
        tvBeauticianHint.setText("请选择服务技师");
        tvBeauticianHint.setTextColor(getResources().getColor(R.color.text_gray));
        ivBeautician.setVisibility(View.GONE);
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
        }else if(REQUEST_CODE_PAY == requestCode) {
            //clear cache
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
                    ,packageParams, CommonConstant.SERVICE_TYPE_TO_SHOP);
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
                        s.length() - 1
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
                packageString += projectId + "_" + orderId + "_" + num + "_" + packageVo.getId() + ",";
            }
        }
        if (packageString.length() > 0)// 去掉最后的","
            packageString = packageString.substring(0, packageString.length() - 1);
        return packageString;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getData(intent);
        updateUI();
        /*美容院变了，就再次获取一下推荐的套餐和优惠券*/
        presenter.getRecommendCouponAndPackage(projectParams.toString(),
                beautyParlorBean == null?"":beautyParlorBean.getId()==null?"":beautyParlorBean.getId(),"");
        /*技师也需要重新选择*/
        clearSelectedBeautician();
    }

    @Override
    public void onLogin(int errorCode, String errorMessage) {
        presenter.start();
    }

    private void updateSelectHour(String str){
        tvSelectHour.setText(str);
        tvSelectHour.setTextColor(getResources().getColor(R.color.text_black));
        //hide widget
        serviceHourWidget.setVisibility(View.GONE);
        //update icon
        ivSelectHour.setBackgroundResource(R.mipmap.arrow_right);
        getRecommendBeautician();
    }
    @Override
    public void onSelected(ServiceHourBean serviceHourBean) {
        //更新显示
        if(serviceHourBean!=null&&!TextUtils.isEmpty(serviceHourBean.getDate())){
            String str = serviceHourBean.getDate() + " " + serviceHourBean.getSelectedHour();
            updateSelectHour(str);
        }
        //update selectedServiceHourBean
        selectedServiceHourBean.cloneObj(serviceHourBean);
    }
    private void getRecommendBeautician(){
        if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
            return;/*未登录，直接返回*/
        }
        if (TextUtils.isEmpty(selectedServiceHourBean.getDate())) {
            return;/*未选择时间，直接返回*/
        }
        if (beautyParlorBean==null) {
            return;/*未接收beautyParlorBean数据，直接返回*/
        }
        presenter.getRecommendBeautician(CommonConstant.SERVICE_TYPE_TO_SHOP,
                beautyParlorBean.getId(), "", projectParams.toString(),
                selectedServiceHourBean.getDate(), selectedServiceHourBean.getSelectedHour());
    }
    @Override
    public ServiceHourBean getLastSelectedServiceHourBean() {
        return selectedServiceHourBean;
    }
    @Override
    public void onServiceHourWidgetEvent() {

    }
    private void updateUI() {
        if (TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())) {//未登陆
            loginWidget.setVisibility(View.VISIBLE);
            llContact.setVisibility(View.GONE);
        }else{
            loginWidget.setVisibility(View.GONE);
            llContact.setVisibility(View.VISIBLE);
        }
        if(beautyParlorBean!=null){
            MyApp.instance.getImageLoad().displayImage(beautyParlorBean.getImgUrl(), ivBeautyParlorImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            tvBeautyParlorAddress.setText(beautyParlorBean.getAddress());
            tvBeautyParlorName.setText(beautyParlorBean.getName());
        }
        if(selectedServiceHourBean!=null&&!TextUtils.isEmpty(selectedServiceHourBean.getDate())){
            String str = selectedServiceHourBean.getDate() + " " + selectedServiceHourBean.getSelectedHour();
            updateSelectHour(str);
        }
    }

    @Override
    public void findViews() {
        setContentView(R.layout.confirm_order_to_shop);
        mContext = this;
        presenter = new ConfirmOrderPresenter(this);

        getData(getIntent());

        initLoginWidget();
        initContactView();
        initBeautyParlorView();
        initServiceHourWidget();
        initProjectListView();
        initOtherView();

        presenter.start();
    }

    @Override
    public void updateLoginWidget(boolean b) {
        if(b){
            loginWidgetAdapter = new LoginWidgetAdapter(mContext,this);
            loginWidget.setAdapter(loginWidgetAdapter);
            llContact.setVisibility(View.GONE);
            loginWidget.setVisibility(View.VISIBLE);
        }else{
            loginWidget.setVisibility(View.GONE);
            llContact.setVisibility(View.VISIBLE);
            presenter.getDefaultAddress();
            /*获取推荐套餐和优惠券*/
            presenter.getRecommendCouponAndPackage(projectParams.toString(),
                    beautyParlorBean == null ? "" : beautyParlorBean.getId() == null ? "":beautyParlorBean.getId(),"");

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
                ,packageParams, CommonConstant.SERVICE_TYPE_TO_SHOP);
    }

    @Override
    public void updateServiceHours(List<ServiceHourBean> serviceHourBeanList) {
        serviceHourWidget.setDataList(serviceHourBeanList);
        serviceHourWidget.setVisibility(View.VISIBLE);
        ivSelectHour.setBackgroundResource(R.mipmap.arrow_down);
        /*技师也需要重新选择*/
        clearSelectedBeautician();
    }

    @Override
    public void updateRealPrice(int realPrice) {
        tvPrice.setText(realPrice+"");
    }

    @Override
    public void updateAddress(List<AddressBean> addressList) {
        //no use
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
        etUserName.setText(addressBean.getUserName());
        etUserPhone.setText(addressBean.getUserPhone());
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
