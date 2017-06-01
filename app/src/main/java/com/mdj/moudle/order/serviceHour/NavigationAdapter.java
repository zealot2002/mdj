package com.mdj.moudle.order.serviceHour;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class NavigationAdapter extends CacheFragmentStatePagerAdapter {
	private List<ServiceHourBean> dataList;
    private ServiceHourCallbacks listener;
    private List<ServiceHourFragment> ServiceHourFragmentList;
    public NavigationAdapter(FragmentManager fm,ServiceHourCallbacks listener,List<ServiceHourBean> dataList) {
        super(fm);
        this.listener = listener;
        this.dataList = dataList;
        ServiceHourFragmentList = new ArrayList<>();
    }

    @Override
    protected Fragment createItem(int position) {
        ServiceHourFragment f = new ServiceHourFragment(listener,dataList.get(position));
        ServiceHourFragmentList.add(f);
        return f;
    }

    @Override
    public void refresh() {
        for(ServiceHourFragment f:ServiceHourFragmentList){
            f.refresh();
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
    }
}
