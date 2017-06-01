package com.mdj.moudle.project.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.project.bean.CommentBean;

import java.util.List;

public interface ProjectDetailContract {
    interface View extends BaseLoadingView<Presenter> {
        /*刷新评价列表*/
        void updateCommentList(final List<CommentBean> commentList);

    }

    interface Presenter extends BasePresenter {
        void getProjectCommentListPre(String projectId);
        void getProjectDetail(String projectId);
        void loadMoreData(String projectId);
    }
}