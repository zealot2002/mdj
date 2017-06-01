package com.mdj.moudle.beautician.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.beautician.bean.CommentBean;

import java.util.List;

public interface CommentInfoContract {
    interface View extends BaseLoadingView<Presenter> {
        void refreshCommentList(int commentType,List<CommentBean> data);  //增加commentType参数，是为了防止过快的切换tab，导致数据错乱
    }

    interface Presenter extends BasePresenter {
        void getAllData(String beauticianId);
        void getBeauticianCommentListPre(int type);
        void loadMoreData(int commentType);
    }
}