package com.mdj.moudle.mine.feedback.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

/**
 * @author 吴世文
 * @Description:
 */
public interface FeedBackContract {
    interface View extends BaseLoadingView<Presenter> {
//        void done(int result,String msg);
    }

    interface Presenter extends BasePresenter {
        void sendFeedBack(String uid,String feedBack);
    }
}
