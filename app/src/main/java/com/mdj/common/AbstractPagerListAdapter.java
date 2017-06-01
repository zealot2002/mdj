package com.mdj.common;


import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class AbstractPagerListAdapter<T> extends PagerAdapter {
    protected List<T> dataList;
    protected SparseArray<View> viewArray;

/**************************************************************************************************/
    public AbstractPagerListAdapter() {
        viewArray = new SparseArray<>();
    }

    public void setDataList(List<T> dataList){
        this.dataList = dataList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return dataList==null?0:dataList.size();
    }

//    public abstract View newView(int position);

    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewArray.get(position);
        if (view == null) {
            view = newView(position);
            viewArray.put(position, view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public abstract View newView(int position);
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewArray.get(position));
    }
}