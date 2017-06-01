package com.mdj.moudle.login;

import android.content.Context;

import com.mdj.moudle.login.presenter.LoginContract;
import com.mdj.moudle.login.presenter.LoginPresenter;
import com.mdj.utils.ToastUtils;

/*
* 登录小组件适配器
* 它负责与activity通信（双向），接收LoginWidget的事件消息
*
* */
public class LoginWidgetAdapter implements LoginContract.View{
    public interface LoginWidgetEventListener {
        void onLogin(int errorCode, String errorMessage);
    }
    private LoginWidgetEventListener loginWidgetEventListener;
    private Context context;
    private LoginContract.Presenter presenter;
/**************************************************方法区**********************************************************/
    public LoginWidgetAdapter(Context context,LoginWidgetEventListener loginWidgetEventListener){
        this.context = context;
        this.loginWidgetEventListener = loginWidgetEventListener;
        presenter = new LoginPresenter(this);
    }
    public void sendSmsCode(String phone) {
        presenter.getSmsCode(phone);
    }


    public void login(String phone,String smsCode) {
        presenter.login(phone,smsCode);
    }

    @Override
    public void login(int result, String msg) {
        loginWidgetEventListener.onLogin(result, msg);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void closeLoading() {

    }

    @Override
    public void showDisconnect(String msg) {
        ToastUtils.showLong(context,msg);
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void updateUI(Object data) {

    }

    @Override
    public void setPresenter(Object presenter) {

    }
}
