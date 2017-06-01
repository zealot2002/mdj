package com.mdj.moudle.order.serviceHour;

/**
 * Created by tt on 2016/6/6.
 */
public interface ServiceHourCallbacks {
    void onSelected(ServiceHourBean serviceHourBean);
    ServiceHourBean getLastSelectedServiceHourBean();
}
