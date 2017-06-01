package com.mdj.moudle.userPackage.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.project.bean.ProjectBean;

public interface MyPackageListContract {
    interface View extends BaseLoadingView<Presenter> {
        void updateProjectInfo(ProjectBean projectBean);
        void showError(String msg);
    }

    interface Presenter extends BasePresenter {
        void getMyPackageList(boolean showLoading);
        void getProjectInfo(String projectId);
        void getFitableBeautyParlorList(String packageId);
    }
}