package com.mdj.moudle.login.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface LoginContract {
    interface View extends BaseLoadingView<Presenter> {
        void login(int result,String msg);
    }

    interface Presenter extends BasePresenter {
        void getSmsCode(String phone);
        void login(String phone,String smsCode);
    }
}