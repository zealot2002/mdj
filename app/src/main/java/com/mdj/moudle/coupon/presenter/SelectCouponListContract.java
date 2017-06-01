package com.mdj.moudle.coupon.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface SelectCouponListContract {
    interface View extends BaseLoadingView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void getAvailableCouponList(String projectParams);
    }
}
