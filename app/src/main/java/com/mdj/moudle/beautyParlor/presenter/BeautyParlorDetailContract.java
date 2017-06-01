package com.mdj.moudle.beautyParlor.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.project.bean.ProjectBean;

import java.util.List;

public interface BeautyParlorDetailContract {
    interface View extends BaseLoadingView<Presenter> {
        void refreshProjectList(List<ProjectBean> dataList);
    }

    interface Presenter extends BasePresenter {
        void initData(String shopId);
        //加载更多
        void loadMoreData(String beautyParlorId,int tabIndex);
        //获取项目列表
        void getBeautyParlorProjectListPre(String beautyParlorId,int tabIndex);
    }
}
