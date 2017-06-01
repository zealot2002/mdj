package com.mdj.moudle.mine.exchange.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

/**
 * @author 吴世文
 * @Description:
 */
public interface ExchangeContract {
    interface View extends BaseLoadingView<Presenter> {
//        void done(int result,String msg);
    }

    interface Presenter extends BasePresenter {
        void exchange(String phone,String redeemCode);
    }
}
