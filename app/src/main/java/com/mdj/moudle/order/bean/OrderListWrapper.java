package com.mdj.moudle.order.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2016/6/12.
 */
public class OrderListWrapper {
    public List<OrderBean> dataList = new ArrayList<>();
    public int pageIndex;
    public boolean isNoMoreData = false;

    public void clear(){
        dataList.clear();
        pageIndex = 0;
        isNoMoreData = false;
    }
}
