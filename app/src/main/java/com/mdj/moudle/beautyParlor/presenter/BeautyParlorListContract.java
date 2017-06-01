package com.mdj.moudle.beautyParlor.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.order.serviceHour.ServiceHourBean;

import java.util.List;

public interface BeautyParlorListContract {
    interface View extends BaseLoadingView<Presenter> {
        void updateServiceHours(int index,List<ServiceHourBean> serviceHourBeanList);
    }

    interface Presenter extends BasePresenter {
        void getBeautyParlorList(String projectParams,String lat,String lng);
        void getBeautyParlorServiceHours(String projectParams,String beautyParlorId,int index);
    }
}
