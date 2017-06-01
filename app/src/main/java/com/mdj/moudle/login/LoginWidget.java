package com.mdj.moudle.login;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.utils.CommonUtil;

/*
* 登录小组件
* 它只与它的LoginWidgetAdapter通信（单向），不直接与activity通信
* 倒计时逻辑、手机号和验证码的基本容错，都在组件内部处理
* */
public class LoginWidget extends LinearLayout implements View.OnClickListener{
    private Context context;
    private LayoutInflater inflater;
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout main;
    private Button btnGetSmsCode,btnLogin;
    private EditText etPhone,etSmsCode;
    private TextView tvWarning;
    private LoginWidgetAdapter adapter;
    private final int COUNTDOWN_TIME = 30;

    public LoginWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.login_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main,layoutParams);

        btnGetSmsCode = (Button)main.findViewById(R.id.btnGetSmsCode);
        btnLogin = (Button)main.findViewById(R.id.btnLogin);
        etPhone = (EditText)main.findViewById(R.id.etPhone);
        etSmsCode = (EditText)main.findViewById(R.id.etSmsCode);
        tvWarning = (TextView)main.findViewById(R.id.tvWarning);

        btnGetSmsCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    public void setAdapter(LoginWidgetAdapter adapter){
        this.adapter = adapter;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGetSmsCode:
                if(btnGetSmsCode.getBackground() == getResources().getDrawable(R.drawable.gray_round_bg)){
                    return;
                }
                if(!CommonUtil.isMobileNO(etPhone.getText().toString())){
                    startTextViewAnim(tvWarning,"无效的手机号");
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
                adapter.sendSmsCode(etPhone.getText().toString());
                break;
            case R.id.btnLogin:
                if(!CommonUtil.isMobileNO(etPhone.getText().toString())){
                    startTextViewAnim(tvWarning,"无效的手机号");
                    return;
                }
                if(etSmsCode.getText().toString().length()<6){
                    startTextViewAnim(tvWarning,"无效的验证码");
                    return;
                }
                adapter.login(etPhone.getText().toString(),etSmsCode.getText().toString());
                break;
            default:
                break;
        }
    }

    private void startTextViewAnim(final TextView textView,String str){
        textView.setText(str);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_alpha);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText("");
            }
        });
        textView.startAnimation(anim);
    }
}
