package com.mdj.moudle.order;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.util.OrderHelper;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.TextViewUtil;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RoundImageView;

import java.util.List;

public class OrderListAdapter extends BaseAdapter {
    interface OrderListListener{
        void onItemClicked(int position);
        void onButtonClicked(int position);
    }
	private Context context;
	private List<OrderBean> dataList;
    private OrderListListener listener;

	public OrderListAdapter(Context context) {
		this.context = context;
	}

	public OrderListAdapter(Context context, OrderListListener listener) {
		this.context = context;
        this.listener = listener;
	}

    public void setDataList(List<OrderBean> dataList){
        this.dataList = dataList;
    }
	@Override
	public int getCount() {
		if (dataList == null) {
			return 0;
		}
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.order_list_item, null);
			holder = new ViewHolder();
            holder.tvCreateTime = (TextView) convertView.findViewById(R.id.tvCreateTime);
            holder.tvState = (TextView) convertView.findViewById(R.id.tvState);
            holder.tvBeauticianName = (TextView) convertView.findViewById(R.id.tvBeauticianName);
            holder.tvOrderId = (TextView) convertView.findViewById(R.id.tvOrderId);
            holder.tvProjectNames = (TextView) convertView.findViewById(R.id.tvProjectNames);
            holder.tvServiceTime = (TextView) convertView.findViewById(R.id.tvServiceTime);
            holder.tvServiceType = (TextView) convertView.findViewById(R.id.tvServiceType);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);

            holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);
            holder.btnOperation = (Button) convertView.findViewById(R.id.btnOperation);

            holder.rlItem = (RelativeLayout)convertView.findViewById(R.id.rlItem);

            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
            OrderBean bean = dataList.get(position);
			holder.tvCreateTime.setText(bean.getCreateTime());
            holder.tvState.setText(OrderHelper.getOrderStateStr(Integer.valueOf(bean.getState())));
            holder.tvBeauticianName.setText(bean.getBeauticianName());

            String keyword = "订单号: ";
            String str = keyword+bean.getId();
            TextViewUtil.setSpecialTextColor(holder.tvOrderId,
                    str,
                    context.getResources().getColor(R.color.text_gray),
                    context.getResources().getColor(R.color.text_black),
                    str.length() - bean.getId().length(),
                    str.length());
            if(bean.getProjectList()!=null){
                if(bean.getProjectList().size()==1){
                    holder.tvProjectNames.setText(bean.getProjectList().get(0));
                }else if(bean.getProjectList().size()>1){
                    str = bean.getProjectList().get(0)+"等";
                    TextViewUtil.setSpecialTextColor(holder.tvProjectNames,
                            str,
                            context.getResources().getColor(R.color.text_black),
                            context.getResources().getColor(R.color.red),
                            str.length()-1,
                            str.length());
                }
            }
            keyword = "服务时间: ";
            str = keyword+bean.getServiceStartTime();
            TextViewUtil.setSpecialTextColor(holder.tvServiceTime,
                    str,
                    context.getResources().getColor(R.color.text_gray),
                    context.getResources().getColor(R.color.text_black),
                    str.length() - bean.getServiceStartTime().length(),
                    str.length());

            int serviceType = Integer.valueOf(bean.getServiceType());
            String serviceTypeStr=serviceType == CommonConstant.SERVICE_TYPE_IN_HOME?"上门":"到店";
            holder.tvServiceType.setText(serviceTypeStr);
            holder.tvPrice.setText(bean.getPrice());

            MyApp.instance.getImageLoad().displayImage(bean.getBeauticianImgUrl(), holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            holder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onItemClicked(position);
                    }
                }
            });

            String operationText = OrderHelper.getOrderListStateOperationStr(Integer.valueOf(bean.getState()));
            if(operationText.isEmpty()){
                holder.btnOperation.setVisibility(View.GONE);
            }else{
                holder.btnOperation.setVisibility(View.VISIBLE);
                holder.btnOperation.setText(operationText);
                holder.btnOperation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(listener!=null){
                            listener.onButtonClicked(position);
                        }
                    }
                });
            }
		} catch (Exception e) {
			e.printStackTrace();
            ToastUtils.showLong(context,"err:"+e.toString());
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvCreateTime,tvState,tvBeauticianName,tvOrderId,tvProjectNames,tvServiceTime,tvServiceType,tvPrice;
        RoundImageView ivImg;
        Button btnOperation;
        RelativeLayout rlItem;
	}
}
