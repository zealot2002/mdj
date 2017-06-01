package com.mdj.moudle.userPackage.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.userPackage.OrderProjectPackageVo;
import com.mdj.moudle.userPackage.PackageBean;

import java.util.List;

public interface PackageContract {
    interface View extends BaseLoadingView<Presenter> {
        /*刷新推荐套餐列表*/
        void updateSuggestPackageList(final List<PackageBean> suggestPackageList);
        /*刷新普通套餐列表*/
        void updateNormalPackageList(final List<PackageBean> packageList);

        /*刷新美容院套餐列表*/
        void updateBeautyParlorPackageList(final List<PackageBean> packageList);

        /*刷新选择套餐*/
        void updateAvailablePackageList(List<OrderProjectPackageVo> dataList);
    }

    interface Presenter extends BasePresenter {
        void getSuggestPackageList();
        void getNormalPackageList();
        /*loadmore后触发*/
        void loadMoreNormalPackageList();

        /*美容院套餐列表*/
        void getBeautyParlorPackageList(String beautyParlorId);

        /*获取可用套餐列表  下单时*/
        void getAvailablePackageList(String projectParams);

    }
}