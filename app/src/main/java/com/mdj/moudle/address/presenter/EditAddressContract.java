package com.mdj.moudle.address.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.address.AddressBean;

public interface EditAddressContract {
    interface View extends BaseLoadingView<Presenter> {
        void done();//添加或编辑完成后，调用
    }

    interface Presenter extends BasePresenter {
        void addOrUpdateAddress(AddressBean bean);
    }
}