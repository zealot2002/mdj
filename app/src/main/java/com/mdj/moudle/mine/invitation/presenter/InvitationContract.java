package com.mdj.moudle.mine.invitation.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

import java.util.List;

/**
 * @author 吴世文
 * @Description:
 */
public interface InvitationContract {
    interface View extends BaseLoadingView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        void submitPhoneNumber(String phone,String userId);

    }
}
