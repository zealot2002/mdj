package com.mdj.moudle.userPackage.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface PackageDetailContract {
    interface View extends BaseLoadingView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        void getPackageDetail(String packageId);
    }
}