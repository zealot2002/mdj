package com.mdj.moudle.beautyParlor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.moudle.order.serviceHour.ServiceHourBean;
import com.mdj.moudle.order.serviceHour.ServiceHourWidget;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.view.MyListView;
import com.mdj.view.RoundImageView;

import java.util.List;

public class BeautyParlorListAdapter extends BaseAdapter{
    public interface BeautyParlorListListener {
        void onSelected(int position,ServiceHourBean serviceHourBean);
        void onItemClick(int position);
    }
	private List<BeautyParlorWrapperBean> dataList;
	private Context context;
    private BeautyParlorListListener beautyParlorListListener;
    private ServiceHourWidget serviceHourWidget;
    private LinearLayout serviceHourWidgetParent = null;
    //来自ServiceHourWidget内部的事件，此事件会导致listview测量高度，但是不刷新，造成问题。
    //利用这个标志位，来判断来自外部还是内部的事件，在getView方法中使用
    private boolean isServiceHourWidgetEvent = false;

/******************************************************************************************************/
	public BeautyParlorListAdapter(Context context,BeautyParlorListListener beautyParlorListListener) {
        this.context = context;
        this.beautyParlorListListener = beautyParlorListListener;
        serviceHourWidget = new ServiceHourWidget(context);
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return position == 0 ? null : dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    public void setDataList(List<BeautyParlorWrapperBean> dataList,List<ServiceHourBean> serviceHourBeanList){
        this.dataList = dataList;
        serviceHourWidget.setDataList(serviceHourBeanList);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder = null;
//        if (convertView == null) {
            convertView = View.inflate(context, R.layout.beauty_parlor_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            holder.ivSelectHour = (ImageView) convertView.findViewById(R.id.ivSelectHour);

            holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);
            holder.rlSelectServiceTime = (RelativeLayout) convertView.findViewById(R.id.rlSelectServiceTime);
            holder.btnPhone = (ImageButton) convertView.findViewById(R.id.btnPhone);

            holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);

            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
            final BeautyParlorWrapperBean bean = dataList.get(position);

            holder.tvName.setText(bean.getBeautyParlorBean().getName());
            holder.tvDistance.setText("距离:"+bean.getBeautyParlorBean().getDistance());
            holder.tvAddress.setText(bean.getBeautyParlorBean().getAddress());

            MyApp.instance.getImageLoad().displayImage(bean.getBeautyParlorBean().getImgUrl(), holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            holder.btnPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callPhone(context, bean.getBeautyParlorBean().getTel());
                }
            });

            if (bean.isExpanded()) {
                //getView 由于父布局是wrap_content而调用多次，所以这里必须使用额外的变量serviceHourWidgetParent来保存serviceHourWidget 的parent
                //serviceHourWidget内部的viewPaper滑动会导致listview的onMeasure方法被调用，由于height没有变化，所以onLayout不调用getView，
                // 此时，需要add一个占位的view，让height被measure，来迫使onLayout重新调用getView。
                if(((MyListView) parent).isOnMeasure()) {
                    View view = new View(context);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            serviceHourWidget.getHeight()>0?serviceHourWidget.getHeight():600); //首次打开才可以获得高度，否则高度为0
                    view.setLayoutParams(lp);
                    holder.llItem.addView(view);
                }else{
                    if (serviceHourWidgetParent != null) {
                        serviceHourWidgetParent.removeView(serviceHourWidget);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(10,0,10,0);
                    serviceHourWidget.setLayoutParams(lp);
                    holder.llItem.addView(serviceHourWidget);
                    serviceHourWidgetParent = holder.llItem;
                }
                holder.ivSelectHour.setBackgroundResource(R.mipmap.arrow_down);
                serviceHourWidget.setServiceHourWidgetEventListener(new ServiceHourWidget.ServiceHourWidgetEventListener() {
                    @Override
                    public void onServiceHourWidgetEvent() {//来自ServiceHourWidget的内部事件
                        isServiceHourWidgetEvent = true;
                    }

                    @Override
                    public void onSelected(ServiceHourBean serviceHourBean) {
                        beautyParlorListListener.onSelected(position, serviceHourBean);
                    }

                    @Override
                    public ServiceHourBean getLastSelectedServiceHourBean() {
                        return null;
                    }
                });
            }
            holder.rlSelectServiceTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (serviceHourWidgetParent != null) {//点击按钮的时候，总是把HourWidget remove掉
                        serviceHourWidgetParent.removeView(serviceHourWidget);
                    }
                    isServiceHourWidgetEvent = false;
                    beautyParlorListListener.onItemClick(position);
                }
            });

        }catch (Exception e){
            Toast.makeText(context, "BeautyParlorListAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    private void callPhone(Context context,String phone) {
        Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        context.startActivity(in);
    }
    public static class ViewHolder {
		private TextView tvName,tvDistance,tvAddress;
		private RoundImageView ivImg;
		private RelativeLayout rlSelectServiceTime;
		private ImageButton btnPhone;

        private ImageView ivSelectHour;
        private LinearLayout llItem;
	}
}
