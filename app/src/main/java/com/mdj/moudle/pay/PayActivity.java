package com.mdj.moudle.pay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.pay.presenter.PayContract;
import com.mdj.moudle.pay.presenter.PayPresenter;
import com.pingplusplus.android.PaymentActivity;

public class PayActivity extends BaseActivity implements PayContract.View,View.OnClickListener{
    private static final int REQUEST_CODE_PAYMENT = 1;
    private TextView tvOrderId,tvPrice;
    private RadioGroup rgPayWay;
    private RadioButton rbAlipay,rbWx;

    private CheckBox cbProtocal;
    private CustomDigitalClock clockCountDown;
    private Button btnPay;
    private String payType;
    private OrderBean orderBean;
    private PayContract.Presenter presenter;

/*****************************************************************************************************/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.btnPay:
                presenter.pay(orderBean.getId(),getPayType(rgPayWay.getCheckedRadioButtonId()));
                break;
			default:
				break;
		}
	}

    private String getPayType(int resourceId){
        switch (resourceId){
            case R.id.rbAlipay:return CommonConstant.PAY_TYPE.alipay.value();
            case R.id.rbWx:return CommonConstant.PAY_TYPE.wx.value();
            default:
                return "";
        }
    }

    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.pay_activity);
        getData(getIntent());

        tvOrderId = (TextView) findViewById(R.id.tvOrderId);
        tvPrice = (TextView) findViewById(R.id.tvPrice);

        rgPayWay = (RadioGroup) findViewById(R.id.rgPayWay);
        rbAlipay = (RadioButton) findViewById(R.id.rbAlipay);
        rbWx = (RadioButton) findViewById(R.id.rbWx);

        if(payType!=null){//继续支付，选中支付方式，不能修改
            rbAlipay.setEnabled(false);
            rbWx.setEnabled(false);
            if(payType.equals(CommonConstant.PAY_TYPE.alipay.value())){
                rbAlipay.setChecked(true);
            }else if(payType.equals(CommonConstant.PAY_TYPE.wx.value())){
                rbWx.setChecked(true);
            }
        }
        cbProtocal = (CheckBox) findViewById(R.id.cbProtocal);
        cbProtocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    btnPay.setBackgroundResource(R.drawable.red_deep_red_selector);
                    btnPay.setEnabled(true);
                }else{
                    btnPay.setBackgroundResource(R.color.mdj_gray);
                    btnPay.setEnabled(false);
                }
            }
        });

        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(this);

        tvOrderId.setText(orderBean.getId());
        tvPrice.setText(getString(R.string.symbol_rmb) + orderBean.getPrice());

        presenter = new PayPresenter(this);
        presenter.getCountdownTime(orderBean.getId());
    }

    private void initCountDown(long duration) {
        long endTime = System.currentTimeMillis()+duration;
        clockCountDown = (CustomDigitalClock) findViewById(R.id.clockCountDown);
        clockCountDown.setEndTime(endTime);
        clockCountDown.setClockListener(new CustomDigitalClock.ClockListener() {
            @Override
            public void countDownFinsh() {
                btnPay.setBackgroundResource(R.color.mdj_gray);
                btnPay.setEnabled(false);
            }
        });
        clockCountDown.start();
    }

    private void getData(Intent intent) {
        orderBean = (OrderBean) intent.getSerializableExtra(BundleConstant.ORDER_BEAN);
        if(intent.hasExtra(BundleConstant.PAY_TYPE)){
            payType = getIntent().getStringExtra(BundleConstant.PAY_TYPE);
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
    public void payDone(String str) {
        Intent intent = new Intent();
        String packageName = getPackageName();
        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, str);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    public void updateCountdown(long time) {
        if(time>0){
        }else{
            btnPay.setBackgroundResource(R.color.mdj_gray);
            btnPay.setEnabled(false);
        }
        initCountDown(time*1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                String errorMsg = data.getExtras().getString("error_msg");
                if(result != null ){
                    if (result.equals("success")) {
                        setResult(Activity.RESULT_OK);
                    }else if(result.equals("cancel")){
                        setResult(Activity.RESULT_CANCELED);
                    }else if(result.equals("fail")){
                        setResult(Activity.RESULT_CANCELED);
                        showShortToast("支付失败" + errorMsg);
                    }else{
                        setResult(Activity.RESULT_CANCELED);
                        showShortToast(errorMsg);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setResult(Activity.RESULT_CANCELED);
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                showShortToast("无效的凭证提交");
                setResult(Activity.RESULT_CANCELED);
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}