package com.mdj.moudle.coupon.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface MyCouponListContract {
    interface View extends BaseLoadingView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void getMyCouponList(boolean showLoading);
    }
}
