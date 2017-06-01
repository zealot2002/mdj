package com.mdj.moudle.mine.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface MineContract {
    interface View extends BaseLoadingView<Presenter> {
        void showLogin();
    }

    interface Presenter extends BasePresenter {
        void getUserInfo(boolean showLoading);
    }
}