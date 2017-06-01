package com.mdj.moudle.order.serviceHour;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;

/**
 * Created by tt on 2016/6/6.
 */
public class ServiceHourFragmentAdapter extends BaseAdapter {
    public static final String[] hours = {"09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30",
                                        "13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30",
                                        "17:00","17:30","18:00","18:30","19:00","19:30","20:00"};

    private ViewHolder viewHolder;
    private Context context;
    private LayoutInflater mInflater;
    private ServiceHourBean serviceHourBean;  //一天之内的可用时段
    private ServiceHourCallbacks listener;

    public ServiceHourFragmentAdapter(Context context,ServiceHourCallbacks listener,ServiceHourBean serviceHourBean) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.serviceHourBean = serviceHourBean;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return hours.length;
    }

    @Override
    public Object getItem(int i) {
        return hours[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.service_hour_hour_item, null);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                viewHolder.ivDot = (ImageView) convertView.findViewById(R.id.ivDot);
                viewHolder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvTime.setText(hours[position]);
            viewHolder.llItem.setBackgroundResource(R.color.white);
            int color;
            if(serviceHourBean.getAvailableHours().contains(hours[position])){
                color = R.color.text_black;
                viewHolder.ivDot.setVisibility(View.INVISIBLE);
                viewHolder.llItem.setClickable(true);
                viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        serviceHourBean.setSelectedHour(hours[position]);
                        listener.onSelected(serviceHourBean);
                    }
                });
                //被选中时，背景黑色，字体白色
                ServiceHourBean lastSelectedServiceHourBean = listener.getLastSelectedServiceHourBean();
                if(lastSelectedServiceHourBean!=null
                        &&lastSelectedServiceHourBean.getDate()!=null&&lastSelectedServiceHourBean.getDate().equals(serviceHourBean.getDate())
                        &&lastSelectedServiceHourBean.getWeek()!=null&&lastSelectedServiceHourBean.getWeek().equals(serviceHourBean.getWeek())
                        &&lastSelectedServiceHourBean.getSelectedHour()!=null&&lastSelectedServiceHourBean.getSelectedHour().equals(hours[position])){
                    viewHolder.llItem.setBackgroundResource(R.drawable.black_round);
                    color = R.color.white;
                }
            }else{
                color = R.color.text_gray;
                viewHolder.ivDot.setVisibility(View.VISIBLE);
                viewHolder.llItem.setClickable(false);
            }
            viewHolder.tvTime.setTextColor(context.getResources().getColor(color));
        } catch (Exception e) {
            Toast.makeText(context, "ServiceHourFragmentAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    public class ViewHolder{
        private TextView tvTime;
        private ImageView ivDot;
        private LinearLayout llItem;
    }
}
