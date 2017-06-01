package com.mdj.moudle.pay.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface PayContract {
    interface View extends BaseLoadingView<Presenter> {
        void payDone(String str);
        void updateCountdown(long time);
    }

    interface Presenter extends BasePresenter {
        void pay(String orderId,String payType);
        void getCountdownTime(String orderId);
    }
}
