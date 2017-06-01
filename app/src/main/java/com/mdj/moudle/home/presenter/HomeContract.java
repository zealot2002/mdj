package com.mdj.moudle.home.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.home.bean.BannerBean;
import com.mdj.moudle.home.bean.ProjectCategoryBean;
import com.mdj.moudle.project.bean.ProjectBean;

import java.util.List;

public interface HomeContract {
    interface View extends BaseLoadingView<Presenter> {
        /*刷新banner*/
        void updateBanner(final List<BannerBean> bannerList);
        /*刷新推荐项目列表、项目类别列表*/
        void updateUI(final List<BannerBean> bannerList
                      ,final List<ProjectBean> suggestProjectList
                ,final List<ProjectCategoryBean> projectCategoryBeanList);
        /*刷新普通项目列表，如果category组件没有画，也画上*/
        void updateNormalProjectList(final List<ProjectBean> projectList,final int tabIndex);

        void updateShoppingCartWidget(ProjectBean projectBean);
    }

    interface Presenter extends BasePresenter {
        /*loadmore后触发*/
        void loadMoreNormalProjectListPre(int tabIndex, int serviceType);
        /*切换tab后触发*/
        void getNormalProjectListPre(int tabIndex, int serviceType);
        /*切换上门、到店后触发*/
        void getServiceDataPre(int serviceType);
        /*切换城市后触发*/
        void refreshAllData();
        /*推荐项目，加号触发*/
        void getSelectedSuggestProject(int serviceType,int position);
        /*普通项目，加号触发*/
        void getSelectedNormalProject(int tabIndex, int serviceType,int position);

    }
}
