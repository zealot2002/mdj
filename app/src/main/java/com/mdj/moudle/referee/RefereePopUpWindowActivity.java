package com.mdj.moudle.referee;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.coupon.CouponBean;
import com.mdj.moudle.coupon.MyCouponListActivity;
import com.mdj.moudle.referee.presenter.RefereeContract;
import com.mdj.moudle.referee.presenter.RefereePresenter;
import com.mdj.utils.DisplayImageOptionsUtil;

public class RefereePopUpWindowActivity extends BaseActivity implements View.OnClickListener ,RefereeContract.View{
    enum POPUP_CONTENT_ENUM{
        POPUP_CONTENT_BEAUTICIAN,
        POPUP_CONTENT_COUPON
    };

    private ImageButton btnClose;
    private RefereeBean refereeBean;
    private CouponBean couponBean;
    /*美容师*/
    private TextView tvName,tvPhone;
    private ImageView ivImg;
    private Button btnOk;
    private RelativeLayout rlBeautician;

/*优惠券*/
    private TextView tvPrice,tvCoupon;
    private Button btnView;
    private RelativeLayout rlCoupon;

    private RefereeContract.Presenter presenter;

/********************************************************************************************************/

    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.referee_popup_window);

        btnClose = (ImageButton)findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        initBeauticianView();
        initCouponView();
        initData();

        presenter = new RefereePresenter(this);
    }


    private void initCouponView() {
        rlCoupon = (RelativeLayout)findViewById(R.id.rlCoupon);

        tvPrice = (TextView)findViewById(R.id.tvPrice);
        tvCoupon = (TextView)findViewById(R.id.tvCoupon);

        btnView = (Button)findViewById(R.id.btnView);
        btnView.setOnClickListener(this);
    }

    private void initBeauticianView() {
        rlBeautician = (RelativeLayout)findViewById(R.id.rlBeautician);

        tvName = (TextView)findViewById(R.id.tvName);
        tvPhone = (TextView)findViewById(R.id.tvPhone);

        ivImg = (ImageView)findViewById(R.id.ivImg);
        btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
    }

    private void initData(){
        try{
            refereeBean = (RefereeBean)getIntent().getSerializableExtra(BundleConstant.REFEREE_BEAN);
            if(refereeBean !=null){
                updateUI(POPUP_CONTENT_ENUM.POPUP_CONTENT_BEAUTICIAN);
            }

            couponBean = (CouponBean) getIntent().getSerializableExtra(BundleConstant.COUPON_BEAN);
            if(couponBean != null){
                updateUI(POPUP_CONTENT_ENUM.POPUP_CONTENT_COUPON);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updateUI(POPUP_CONTENT_ENUM type){
        if(type.equals(POPUP_CONTENT_ENUM.POPUP_CONTENT_BEAUTICIAN)){
            rlBeautician.setVisibility(View.VISIBLE);
            rlCoupon.setVisibility(View.GONE);

            tvName.setText(refereeBean.getName());
            tvPhone.setText(refereeBean.getPhone());
            MyApp.instance.getImageLoad().displayImage(refereeBean.getImageUrl(), ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
        }else{
            //如果没有优惠券，则关闭popup
            if(TextUtils.isEmpty(couponBean.getName())){
                finish();
                return;
            }
            rlCoupon.setVisibility(View.VISIBLE);
            rlBeautician.setVisibility(View.GONE);

          /*  tvPrice.setText("50");
            tvCoupon.setText("恭喜您获得一张50元优惠券");*/
            tvPrice.setText(couponBean.getPrice());
            tvCoupon.setText(couponBean.getName());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose:
                finish();
                break;

            case R.id.btnOk:
                presenter.setReferee(refereeBean.getOrderId(),refereeBean.getId(),refereeBean.getCouponId());
                break;

            case R.id.btnView:
                Intent it = new Intent(this, MyCouponListActivity.class);
                startActivity(it);
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        updateUI(POPUP_CONTENT_ENUM.POPUP_CONTENT_COUPON);
    }

    @Override
    public void setPresenter(Object presenter) {

    }
    @Override
    public void setRefereeDone() {
        if(refereeBean.getCouponId().isEmpty()
                ||refereeBean.getCouponId().equals("0")
                ){
            setResult(Activity.RESULT_OK);
            finish();
        }else{
            presenter.getCouponDetail(refereeBean.getCouponId());
        }
    }

    @Override
    public void updateCouponDetail(CouponBean couponBean) {
        this.couponBean = couponBean;
        updateUI(POPUP_CONTENT_ENUM.POPUP_CONTENT_COUPON);
    }
}
