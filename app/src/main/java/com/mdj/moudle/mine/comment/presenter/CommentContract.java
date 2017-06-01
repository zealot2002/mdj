package com.mdj.moudle.mine.comment.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

import java.util.List;


/**
 * @author 吴世文
 * @Description:
 */
public interface CommentContract {
    interface View extends BaseLoadingView<Presenter> {
        void setBeautyInfo(Object object);
        void submitCommentCallBack(); //只有成功时候调用
    }

    interface Presenter extends BasePresenter {
        /**
         * @param type 0评价初始化  1查看评价详情
         * @param orderSn 订单号
         */
        void evaluateInitialize(String type, String orderSn);
        void submitComment(String orderSn, String serviceType, String isAnonymous,
                           String serviceLeve, String shopScore, String content, List<String> impressionList);
        void getBeautyInfo(String beautyId);
    }
}
