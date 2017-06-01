package com.mdj.moudle.order.presenter;
import com.mdj.base.BaseLoadingView;
import com.mdj.base.BasePresenter;
import com.mdj.moudle.order.bean.OrderBean;

import java.util.List;

public interface OrderContract {
    interface View extends BaseLoadingView<Presenter> {
        void refreshList(int orderType,List<OrderBean> dataList);

        void finishOrderDone();
        void cancelOrderDone();
        /*更新订单详情页面*/
        void updateOrderDetail(OrderBean orderBean);
    }

    interface Presenter extends BasePresenter {
        void clearOrderList();
        void getMyOrderList(int orderType);
        void refreshMyOrderList(int orderType);
        //加载更多
        void loadMoreData(int orderType);

        /*确认订单*/
        void finishOrder(String orderId);
        /*获得订单详情*/
        void getOrderDetail(String orderId);
        /*取消订单*/
        void cancelOrder(String orderId, String reason);
    }
}
