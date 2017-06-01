package com.mdj.moudle.beautician.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface MyBeauticianListContract {
    interface View extends BaseLoadingView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void getMyBeauticianList();
    }
}