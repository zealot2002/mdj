package com.mdj.moudle.order.serviceHour;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.mdj.R;
import com.mdj.view.MyGridView;

/**
 * Created by tt on 2016/6/6.
 */
public class ServiceHourFragment extends Fragment {
    private View view;
    private Context context;
    private MyGridView gridView;
    private ServiceHourFragmentAdapter adapter;
    private ServiceHourBean serviceHourBean;
    private ServiceHourCallbacks listener;

    public ServiceHourFragment(){}

    @SuppressLint("ValidFragment")
    public ServiceHourFragment(ServiceHourCallbacks listener,ServiceHourBean serviceHourBean) {
        this.listener = listener;
        this.serviceHourBean = serviceHourBean;
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.service_hour_date_item, container, false);

        gridView = (MyGridView)view.findViewById(R.id.gridView);
        adapter = new ServiceHourFragmentAdapter(context,listener,serviceHourBean);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }
    public void refresh(){
        adapter.notifyDataSetChanged();
    }
}
