package com.mdj.moudle.mine.exchange;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.coupon.CouponBean;
import com.mdj.moudle.mine.exchange.presenter.ExchangeContract;
import com.mdj.moudle.mine.exchange.presenter.ExchangePresenter;
import com.mdj.moudle.referee.RefereePopUpWindowActivity;
import com.mdj.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;


/**
 * @author 吴世文
 * @Description: 兑换界面
 */
public class ExchangeActivity extends BaseActivity implements View.OnClickListener, ExchangeContract.View {
    private EditText etRedeemCode;
    private Button btnRedeem;
    private boolean iskitkat;
    private LinearLayout ll_status;
    private ExchangeContract.Presenter presenter;

    @Override
    public void findViews() {
        setContentView(R.layout.exchange);
        mContext = this;
        presenter = new ExchangePresenter(this);
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_status.setVisibility(View.VISIBLE);
        etRedeemCode = (EditText) findViewById(R.id.etRedeemCode);
        btnRedeem = (Button) findViewById(R.id.btnRedeem);
        btnRedeem.setOnClickListener(this);
        etRedeemCode.addTextChangedListener(new MyTextWatcher());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRedeem:
                sendRedeemCode();
                break;
        }
    }

    private void sendRedeemCode() {

        if (TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())) {
            showShortToast("请登录");
            return;
        }

        String redeemCode = etRedeemCode.getText().toString().trim();

        presenter.exchange(CacheManager.getInstance().getUserBean().getPhone(), redeemCode);
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        Intent intent = new Intent(mContext, RefereePopUpWindowActivity.class);
        intent.putExtra(BundleConstant.COUPON_BEAN, (CouponBean) data);
        startActivity(intent);
    }

    @Override
    public void setPresenter(Object presenter) {
    }

    private class MyTextWatcher implements TextWatcher {
        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() > 7) {
                enableLoginButton(true);
            } else {
                enableLoginButton(false);
            }
        }
    }

    private void enableLoginButton(boolean b) {
        if (b) {
            btnRedeem.setBackgroundResource(R.drawable.round_red_deepred_selector);
            btnRedeem.setEnabled(true);
        } else {
            btnRedeem.setBackgroundResource(R.drawable.gray_round_bg);
            btnRedeem.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
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

}
