package com.mdj.moudle.referee.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.coupon.CouponBean;

public interface RefereeContract {
    interface View extends BaseLoadingView<Presenter> {
        void setRefereeDone();
        void updateCouponDetail(CouponBean couponBean);
    }

    interface Presenter extends BasePresenter {
        void setReferee(String orderId, String beauticianId, String couponId);
        void getCouponDetail(String couponId);
    }
}
