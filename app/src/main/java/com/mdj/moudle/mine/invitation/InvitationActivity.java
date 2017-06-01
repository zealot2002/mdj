package com.mdj.moudle.mine.invitation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.mine.invitation.presenter.InvitationContract;
import com.mdj.moudle.mine.invitation.presenter.InvitationPresenter;
import com.mdj.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("HandlerLeak")
public class InvitationActivity extends BaseActivity implements View.OnClickListener, InvitationContract.View {
    private EditText etPhone;
    private Button btnOk;
    private boolean iskitkat;
    private LinearLayout ll_status;
    private InvitationContract.Presenter presenter;

    @Override
    public void findViews() {
        setContentView(R.layout.invitation);
        mContext = this;
        presenter = new InvitationPresenter(this);
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        etPhone = (EditText) findViewById(R.id.etPhone);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                String phone = etPhone.getText().toString().trim();
                String id = CacheManager.getInstance().getUserBean().getId();
                if(TextUtils.isEmpty(id)){
                    showShortToast("请登录");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    showShortToast("手机号为空，请重新输入");
                    return;
                }
                if(!CommonUtil.isMobileNO(phone)) {
                    showShortToast("无效的手机号");
                    return;
                }
                presenter.submitPhoneNumber(phone, id);
                break;
        }
    }

    @Override
    public void showDisconnect(String msg) {
        Intent intent = new Intent(mContext,ErrorPromptBoxActivity.class);
        intent.putExtra("ErrMsg",msg);
        startActivity(intent);
    }

    @Override
    public void updateUI(Object data) {
        showShortToast("邀请成功");
        finish();
    }

    @Override
    public void setPresenter(Object presenter) {

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
