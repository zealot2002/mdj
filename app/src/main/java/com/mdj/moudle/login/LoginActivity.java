package com.mdj.moudle.login;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.login.presenter.LoginContract;
import com.mdj.moudle.login.presenter.LoginPresenter;
import com.mdj.moudle.webview.BaseWebviewActivity;
import com.mdj.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity implements LoginContract.View, View.OnClickListener {
    private final int COUNTDOWN_TIME = 30;
    private final int VALID_PHONE_LENGTH = 11;
    private final int VALID_SMSCODE_LENGTH = 6;

    private EditText etPhone,etSmsCode;
    private Button btnGetSmsCode,btnLogin;
    private ImageButton btnClose;
    private CheckBox cbProtocal;
    private TextView tvProtocal;

    private TextWatcher textWatcherPhone,textWatcherSmsCode;
    private LoginContract.Presenter presenter;
/***************************************************************************************************/
    @Override
    public void setPresenter(Object presenter) {

    }
    private void enableLoginButton(boolean b) {
        if(b){
            btnLogin.setBackgroundResource(R.drawable.round_red_deepred_selector);
            btnLogin.setEnabled(true);
        }else{
            btnLogin.setBackgroundResource(R.drawable.gray_round_bg);
            btnLogin.setEnabled(false);
        }
    }

    private void initTextWatcher() {
        textWatcherPhone = new TextWatcher() {
            private CharSequence temp;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if ((temp.length() == 11)
                        && (etSmsCode.getText().toString().length()==6)
                        &&cbProtocal.isChecked()
                        ){
                    enableLoginButton(true);
                } else {
                    enableLoginButton(false);
                }
            }
        };
        textWatcherSmsCode = new TextWatcher() {
            private CharSequence temp;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if ((temp.length() == 6)
                        &&(etPhone.getText().toString().length()==11)
                        &&cbProtocal.isChecked()
                        ){
                    enableLoginButton(true);
                } else {
                    enableLoginButton(false);
                }
            }
        };
    }

    private boolean isLoginReady(){
        if(etPhone.getText().toString().length() == VALID_PHONE_LENGTH
                && etSmsCode.getText().toString().length() == VALID_SMSCODE_LENGTH
                && cbProtocal.isChecked()
                ){
            return true;
        }
        return false;
    }

    @Override
    public void findViews() {
        mContext = this;
        presenter = new LoginPresenter(this);
        setContentView(R.layout.activity_login);

        etPhone = (EditText) findViewById(R.id.etPhone);
        etSmsCode = (EditText) findViewById(R.id.etSmsCode);
        cbProtocal = (CheckBox) findViewById(R.id.cbProtocal);

        btnGetSmsCode = (Button) findViewById(R.id.btnGetSmsCode);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnClose = (ImageButton) findViewById(R.id.btnClose);

        tvProtocal = (TextView) findViewById(R.id.tvProtocal);
        tvProtocal.setOnClickListener(this);

        btnGetSmsCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        cbProtocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(isLoginReady()){
                    enableLoginButton(true);
                }else{
                    enableLoginButton(false);
                }
            }
        });
        initTextWatcher();
        etPhone.addTextChangedListener(textWatcherPhone);
        etSmsCode.addTextChangedListener(textWatcherSmsCode);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnClose:
			finish();
			break;

		case R.id.tvProtocal:
            Intent it = new Intent(mContext, BaseWebviewActivity.class);
            it.putExtra("url", HttpConstant.PROTOCAL_URL);
            it.putExtra("title", "用户协议");
            startActivity(it);
			break;

		case R.id.btnGetSmsCode:
            if(btnGetSmsCode.getBackground() == getResources().getDrawable(R.drawable.gray_round_bg)){
                return;
            }
            if(!CommonUtil.isMobileNO(etPhone.getText().toString().trim())) {
                showShortToast("无效的手机号");
                return;
            }
            //开启倒计时
            new CountDownTimer(COUNTDOWN_TIME*1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    btnGetSmsCode.setText(millisUntilFinished / 1000 + "秒");
                    btnGetSmsCode.setBackgroundResource(R.drawable.gray_round_bg);
                    btnGetSmsCode.setEnabled(false);
                }
                public void onFinish() {
                    btnGetSmsCode.setText("获取验证码");
                    btnGetSmsCode.setBackgroundResource(R.drawable.black_round_bg);
                    btnGetSmsCode.setEnabled(true);
                }
            }.start();
            //发送短信验证码
            presenter.getSmsCode(etPhone.getText().toString().trim());
			break;
		case R.id.btnLogin:// 登录
            if(etSmsCode.getText().toString().length()<6){
                showShortToast("无效的验证码");
                return;
            }
            presenter.login(etPhone.getText().toString().trim(),etSmsCode.getText().toString().trim());
			break;
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

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {

    }

    @Override
    public void login(int result, String msg) {
        if(result == HttpConstant.SUCCESS){
            setResult(Activity.RESULT_OK);
            finish();
        }else{
            showShortToast(msg);
        }
    }

}
