package com.mdj.moudle.order.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.address.AddressBean;
import com.mdj.moudle.project.bean.ProjectWrapperBean;

import java.util.List;

public interface ConfirmPackageOrderContract {
    interface View extends BaseLoadingView<Presenter> {
        /*是否显示登录widget*/
        void updateLoginWidget(boolean b);
        /*更新地址*/
        void updateAddress(List<AddressBean> addressList);
        /*下单完成*/
        void createOrderDone(Object obj);
        /*更新套餐项目列表*/
        void updatePackageProjectList(List<ProjectWrapperBean> projectList);
    }

    interface Presenter extends BasePresenter {
        void getAddressList();
        /*下单*/
        void createOrder(AddressBean addressBean, String packageId, String beautyParlorId);
        /*getPackageInfo*/
        void getPackageInfo(String packageId);
    }
}
