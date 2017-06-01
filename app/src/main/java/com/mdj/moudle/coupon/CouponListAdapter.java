package com.mdj.moudle.coupon;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.constant.CommonConstant;
import com.mdj.utils.MdjLog;

import java.util.ArrayList;
import java.util.List;

public class CouponListAdapter extends BaseAdapter {
	private Context context;
	private List<CouponBean> dataList;
	private List<Integer> toggleStateList = new ArrayList<>();// 记录扩展按钮的状态

    /*选择优惠券界面专用*/
    private boolean fromSelectCoupon = false;//是否为选择优惠券界面
    private List<Boolean> selectStateList = new ArrayList<>();
    private int currentSelected = -1;

/****************************************************************************************************/

    public void setFromSelectCoupon(boolean fromSelectCoupon) {
        this.fromSelectCoupon = fromSelectCoupon;
    }

    public int getCurrentSelected() {
        return currentSelected;
    }

    public void setDataList(List<CouponBean> dataList) {
		this.dataList = dataList;
        // 初始化状态
        toggleStateList.clear();
        for (int i = 0; i < dataList.size(); i++) {
            toggleStateList.add(0);// 初始化为0

            /*选择优惠券专用*/
            selectStateList.add(false);//初始化为未选中
            currentSelected = -1;
        }
	}

	public CouponListAdapter(Context context) {
		this.context = context;

	}

	public void setToggleState(int position, int state) {
		for (int i = 0; i < toggleStateList.size(); i++) {
			toggleStateList.set(i, 0);
		}
		toggleStateList.set(position, state);
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
			convertView = View.inflate(context, R.layout.mycoupon_list_item, null);
			holder = new ViewHolder();
			holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
			holder.tvYuanOrDiscount = (TextView) convertView.findViewById(R.id.tvYuanOrDiscount);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvFitableProject = (TextView) convertView.findViewById(R.id.tvFitableProject);
			holder.tvExpireTime = (TextView) convertView.findViewById(R.id.tvExpireTime);
			holder.tvService1 = (TextView) convertView.findViewById(R.id.tvService1);
            holder.tvService2 = (TextView) convertView.findViewById(R.id.tvService2);
            holder.tvFitableProjectList = (TextView) convertView.findViewById(R.id.tvFitableProjectList);
            holder.tvFitableCityList = (TextView) convertView.findViewById(R.id.tvFitableCityList);
            holder.tvRemark = (TextView) convertView.findViewById(R.id.tvRemark);

			holder.rlShade = (RelativeLayout) convertView.findViewById(R.id.rlShade);
            holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);

            holder.btnSelect = (ImageButton) convertView.findViewById(R.id.btnSelect);

			holder.expandable_toggle_button = (ImageButton) convertView.findViewById(R.id.expandable_toggle_button);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			CouponBean bean = dataList.get(position);
			holder.tvPrice.setText(bean.getPrice()+"");


            if(bean.getType()==CommonConstant.COUPON_TYPE_DISCOUNT){//折扣券
                holder.tvYuanOrDiscount.setText("折");
            }else{
                holder.tvYuanOrDiscount.setText("元");
            }

            holder.tvName.setText(bean.getName());
            if(bean.getUseScope()==CommonConstant.COUPON_USER_SCOPE_LIMIT){
                holder.tvFitableProject.setText("限项目使用");
            }else{
                holder.tvFitableProject.setText("通用项目");
            }

            holder.tvExpireTime.setText(bean.getExpiredTime());
            if(bean.getServiceType()== CommonConstant.SERVICE_TYPE_IN_HOME_AND_TO_SHOP){//上门、到店
                holder.tvService1.setVisibility(View.VISIBLE);
                holder.tvService2.setVisibility(View.VISIBLE);
                holder.tvService1.setText("上门");
            }else{
                holder.tvService1.setVisibility(View.VISIBLE);
                holder.tvService2.setVisibility(View.GONE);
                if(bean.getServiceType()==CommonConstant.SERVICE_TYPE_IN_HOME){
                    holder.tvService1.setText("上门");
                }else{
                    holder.tvService1.setText("到店");
                }
            }

            if(!TextUtils.isEmpty(bean.getFitableProject())){
                StringBuilder sb = new StringBuilder();
                sb.append("适用项目:");
                sb.append(bean.getFitableProject());
                holder.tvFitableProjectList.setText(sb.toString());
            }else{
                holder.tvFitableProjectList.setText("适用项目:通用项目");
            }

            if(!TextUtils.isEmpty(bean.getFitableCity())){
                StringBuilder sb = new StringBuilder();
                sb.append("适用城市:");
                sb.append(bean.getFitableCity());
                holder.tvFitableCityList.setText(sb.toString());
            }else{
                holder.tvFitableCityList.setText("适用城市:全国");
            }
            holder.tvRemark.setText(bean.getRemark());

            holder.rlShade.setVisibility(View.GONE);
            holder.ivStatus.setVisibility(View.GONE);

            if(bean.getState()==CommonConstant.COUPON_STATE_COMING){
                holder.rlShade.setVisibility(View.VISIBLE);
            }
            if(bean.getState()==CommonConstant.COUPON_STATE_EXPIRING){
                holder.ivStatus.setVisibility(View.VISIBLE);
            }

			if (toggleStateList.get(position).equals(1)) {
				holder.expandable_toggle_button.setImageResource(R.mipmap.arrow_up);
			} else {
				holder.expandable_toggle_button.setImageResource(R.mipmap.arrow_down);
			}

            /*已过期、已使用  将所有颜色变灰色*/
            if(bean.getState()==CommonConstant.COUPON_STATE_EXPIRED
                    ||bean.getState()==CommonConstant.COUPON_STATE_USED
                    ){
                holder.tvPrice.setTextColor(context.getResources().getColor(R.color.text_gray));
                holder.tvYuanOrDiscount.setTextColor(context.getResources().getColor(R.color.text_gray));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.text_gray));
                holder.tvService1.setBackgroundResource(R.drawable.gray_round_bg);
                holder.tvService2.setBackgroundResource(R.drawable.gray_round_bg);
            }else{
                holder.tvPrice.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvYuanOrDiscount.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvName.setTextColor(context.getResources().getColor(R.color.text_black));
                holder.tvService1.setBackgroundResource(R.drawable.black_round_bg);
                holder.tvService2.setBackgroundResource(R.drawable.black_round_bg);
            }

            /*选择优惠券专用*/
            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < selectStateList.size(); i++) {
                        selectStateList.set(i, false);
                    }
                    selectStateList.set(position, true);
                    currentSelected = position;
                    notifyDataSetChanged();
                }
            });

            if(fromSelectCoupon){
                holder.btnSelect.setVisibility(View.VISIBLE);
                holder.btnSelect.setImageResource(selectStateList.get(position) ? R.mipmap.radio_selected:R.mipmap.radio_unselected);
            }
		} catch (Exception e) {
			e.printStackTrace();
			MdjLog.log("PackageListAdapter Exception: e :" + e.toString());
		}
		return convertView;
	}


	public class ViewHolder {
		TextView tvPrice, tvYuanOrDiscount, tvName, tvFitableProject, tvExpireTime,
                tvService1,tvService2,tvFitableProjectList,tvFitableCityList,tvRemark;
		RelativeLayout rlShade;
        ImageView ivStatus;
        ImageButton expandable_toggle_button,btnSelect;
	}
}
