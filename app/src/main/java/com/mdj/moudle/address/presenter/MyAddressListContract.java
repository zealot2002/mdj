package com.mdj.moudle.address.presenter;

import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;

public interface MyAddressListContract {
    interface View extends BaseLoadingView<Presenter> {
        void setDefaultAddressDone(boolean result);
    }

    interface Presenter extends BasePresenter {
        void getMyAddressList();
        void setDefaultAddress(String id);
        void deleteAddress(String addressId);
        void loadMoreData();
    }
}