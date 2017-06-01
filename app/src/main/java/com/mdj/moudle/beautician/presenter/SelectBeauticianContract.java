package com.mdj.moudle.beautician.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface SelectBeauticianContract {
    interface View extends BaseLoadingView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void getAvailableBeauticianListPre(int serviceType,String beautyParlorId,String location,String projectParams,String orderDate,String startTime,int sortType);
        /*loadmore后触发*/
        void loadMorePre(int type);
    }
}