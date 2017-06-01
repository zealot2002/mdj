package com.mdj.moudle.order.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.order.bean.RecommendCouponAndPackageBean;
import com.mdj.moudle.order.serviceHour.ServiceHourBean;

import java.util.List;

public interface ConfirmOrderContract {
    interface View extends BaseLoadingView<Presenter> {
        /*是否显示登录widget*/
        void updateLoginWidget(boolean b);
        /*更新套餐和优惠券*/
        void updatePackageAndCoupon(RecommendCouponAndPackageBean recommendCouponAndPackageBean);
        /*更新服务时间*/
        void updateServiceHours(List<ServiceHourBean> serviceHourBeanList);
        /*更新实付金额*/
        void updateRealPrice(int realPrice);
        /*更新地址*/
        void updateAddress(List<AddressBean> addressList);
        /*下单完成*/
        void createOrderDone(Object obj);

        /*更新默认地址*/
        void updateDefaultAddress(AddressBean addressBean);

        /*更新推荐美容师*/
        void updateRecommendBeautician(BeauticianBean beauticianBean);
    }

    interface Presenter extends BasePresenter {
        void getServiceHours(String projectParams,String location, String beautyParlorId,String beauticianId);
        /*获取推荐的套餐和优惠券  -- 上门*/
        void getRecommendCouponAndPackage(String projectParams);
        /*获取推荐的套餐和优惠券  -- 到店*/
        void getRecommendCouponAndPackage(String projectParams,String beautyParlorId,String packageParams);
        void calRealPrice(String projectParams,String couponId,String packageParams,int serviceType);
        void getAddressList();
        void getDefaultAddress();
        void getRecommendBeautician(int serviceType,String beautyParlorId,String location,
                                                        String projectParams,String orderDate,String startTime);
        /*下单
        * ServiceType 上门、到店
        * beautyParlorId 到店专用
        * usePackageParams|couponId 可选
        * */
        void createOrder(int ServiceType,AddressBean addressBean,String projectParams,String beauticianId,String date,String time,String remarks,
                         String beautyParlorId,String usePackageParams,String couponId,String orderId);
    }
}
