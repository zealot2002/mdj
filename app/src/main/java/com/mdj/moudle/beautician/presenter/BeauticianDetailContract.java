package com.mdj.moudle.beautician.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.project.bean.ProjectBean;

import java.util.List;

public interface BeauticianDetailContract {
    interface View extends BaseLoadingView<Presenter> {
        void updateProjectList(List<ProjectBean> list);
        void updateShoppingCartWidget(ProjectBean projectBean);
    }

    interface Presenter extends BasePresenter {
        //init方法
        void getInitData(String beauticianId);
        //加载更多
        void loadMoreData(String beauticianId,int serviceType,int tabIndex);
        //获取项目列表
        void getBeauticianProjectListPre(String beauticianId,int serviceType,int tabIndex);

        //处理加减号事件
        void getSelectedGoods(int tabIndex,int position,int serviceType);

        void getBeauticianDetail(String beauticianId,int serviceType);

    }
}