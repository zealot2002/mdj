package com.mdj.moudle.userPackage;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.moudle.userPackage.MyPackageBean.PACKAGE_STATUS;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.ListViewUtils;
import com.mdj.utils.MdjLog;
import com.mdj.utils.StringUtils;
import com.mdj.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;

public class MyPackageListAdapter extends BaseAdapter {
	public interface OnPackageListEventListener {
		void onPayEvent(String orderId, String price,String payType);
        void onSetRefereeEvent(int position);//关联推荐人
        void onViewShopListEvent(int position);//查看体验店
	}

	private Context context;
	private List<MyPackageBean> list;
	private List<MyPackageListItemProjectListAdapter> PackageListItemProjectListAdapterList;
	private List<Integer> toggleStateList = new ArrayList<>();// 记录扩展按钮的状态
	private PACKAGE_STATUS packageStatus;

	public MyPackageListAdapter(Context context) {
		this.context = context;
	}

	public void setDataList(List<MyPackageBean> list) {
		if (PackageListItemProjectListAdapterList != null) {
			PackageListItemProjectListAdapterList.clear();
		}
		this.list = list;
	}

	public MyPackageListAdapter(Context context, List<MyPackageBean> list) {
		this.list = list;
		this.context = context;
		PackageListItemProjectListAdapterList = new ArrayList<>();
		// 初始化状态
		toggleStateList.clear();
		for (int i = 0; i < list.size(); i++) {
			toggleStateList.add(0);// 初始化为0
		}
	}

	public void setToggleState(int position, int state) {
		for (int i = 0; i < toggleStateList.size(); i++) {
			toggleStateList.set(i, 0);
		}
		toggleStateList.set(position, state);
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.mypackage_list_item, null);
			holder = new ViewHolder();
			holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
			holder.tvPackageName = (TextView) convertView.findViewById(R.id.tvPackageName);
			holder.tvLeftDays = (TextView) convertView.findViewById(R.id.tvLeftDays);
			holder.tvLeftTimes = (TextView) convertView.findViewById(R.id.tvLeftTimes);
			holder.tvOrderId = (TextView) convertView.findViewById(R.id.tvOrderId);
			holder.tvOrderCreateTime = (TextView) convertView.findViewById(R.id.tvOrderCreateTime);
            holder.tvRefereeName = (TextView) convertView.findViewById(R.id.tvRefereeName);
            holder.tvRefereePhone = (TextView) convertView.findViewById(R.id.tvRefereePhone);

			holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);

			holder.vShade = (View) convertView.findViewById(R.id.vShade);
			holder.vLine1 = (View) convertView.findViewById(R.id.vLine1);
			holder.ivPackageStatus = (ImageView) convertView.findViewById(R.id.ivPackageStatus);
			holder.item = (RelativeLayout) convertView.findViewById(R.id.item);
            holder.btnSetReferee = (Button)convertView.findViewById(R.id.btnSetReferee);
            holder.btnViewShopList = (Button)convertView.findViewById(R.id.btnViewShopList);

            holder.rlSetReferee = (RelativeLayout)convertView.findViewById(R.id.rlSetReferee);
            holder.rlShowReferee = (RelativeLayout)convertView.findViewById(R.id.rlShowReferee);

			holder.listView = (ListView) convertView.findViewById(R.id.listView);
			holder.expandable_toggle_button = (ImageButton) convertView.findViewById(R.id.expandable_toggle_button);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			MyPackageBean packageBean = list.get(position);
			packageStatus = PACKAGE_STATUS.NORMAL;
			holder.tvPrice.setText(context.getResources().getString(R.string.symbol_rmb) + StringUtils.getPriceInt(packageBean.getPrice()));

			holder.tvPrice.setText(context.getResources().getString(R.string.symbol_rmb) + StringUtils.getPriceInt(packageBean.getPrice()));
			holder.tvPackageName.setText(packageBean.getName());
			// 过期时间
            holder.tvLeftDays.setText(packageBean.getExpireTime());
			// 支付状态
			if (packageBean.getPayStatus().equals("1")) {//已经支付
                holder.ivPackageStatus.setVisibility(View.GONE);
                holder.item.setClickable(false);
			} else if(packageBean.getPayStatus().equals("0")){//待支付
                holder.item.setTag(position);
                holder.item.setClickable(true);
                holder.item.setTag(position);
                holder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        MyPackageBean item = list.get(position);
                        ((OnPackageListEventListener) context).onPayEvent(item.getOrderSn(), item.getPrice(), item.getPayType());
                    }
                });
                packageStatus = PACKAGE_STATUS.NOT_PAY;// 未支付
                holder.ivPackageStatus.setVisibility(View.VISIBLE);
                holder.ivPackageStatus.setImageResource(R.mipmap.package_to_pay);
			}else if(packageBean.getPayStatus().equals("2")){//已取消
                packageStatus = PACKAGE_STATUS.NOT_PAY;// 未支付
                holder.ivPackageStatus.setVisibility(View.VISIBLE);
                holder.item.setClickable(false);
                holder.ivPackageStatus.setImageResource(R.mipmap.package_canceled);
            }
/*判断是否显示关联推荐人按钮*/
            if(packageBean.getShowSetReferee()==0){
                holder.rlSetReferee.setVisibility(View.GONE);
            }else{
                holder.rlSetReferee.setVisibility(View.VISIBLE);
            }
/*判断是否显示推荐人*/
            if(TextUtils.isEmpty(packageBean.getRefereeBean().getName())){
                holder.rlShowReferee.setVisibility(View.GONE);
            }else{
                holder.rlShowReferee.setVisibility(View.VISIBLE);
                holder.tvRefereeName.setText(packageBean.getRefereeBean().getName());
                holder.tvRefereePhone.setText(packageBean.getRefereeBean().getPhone());
            }
            holder.btnSetReferee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((OnPackageListEventListener) context).onSetRefereeEvent(position);
                }
            });

            if(packageBean.isLimitBeautyParlor()){
                holder.btnViewShopList.setVisibility(View.VISIBLE);
                holder.btnViewShopList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((OnPackageListEventListener) context).onViewShopListEvent(position);
                    }
                });
            }else{
                holder.btnViewShopList.setVisibility(View.GONE);
            }
			// 剩余次数
			int leftTimes = 0;
			int totalTimes = 0;
			for (MyPackageBean.ProjectBean bean : list.get(position).getProjectList()) {
				leftTimes += Integer.valueOf(bean.getLeftTimes());
				totalTimes += Integer.valueOf(bean.getTotalTimes());
			}
			holder.tvLeftTimes.setText("可用次数: "+leftTimes + "/" + totalTimes+"次");
			// 全部用完/已过期logo、遮罩的显示与否
			holder.vShade.setVisibility(View.GONE);

			if (packageStatus != PACKAGE_STATUS.NOT_PAY) {
				if (packageBean.getLeftDays()==0) {// 先判断已过期，
//					holder.vShade.setVisibility(View.VISIBLE);
					packageStatus = PACKAGE_STATUS.EXPIRED;
                    holder.ivPackageStatus.setImageResource(R.mipmap.package_expired);
                    holder.ivPackageStatus.setVisibility(View.VISIBLE);
				}else if (leftTimes == 0) {// 再判断已用完。总的分子为0，并且是已支付状态
//					holder.vShade.setVisibility(View.VISIBLE);
                    packageStatus = PACKAGE_STATUS.ALL_USED;
                    holder.ivPackageStatus.setVisibility(View.VISIBLE);
                    holder.ivPackageStatus.setImageResource(R.mipmap.package_all_used);
                }
			}

			// 订单号、下单时间
			holder.tvOrderId.setText("订单号: "+packageBean.getOrderSn());
			holder.tvOrderCreateTime.setText("下单时间: "+packageBean.getOrderCreateTime());
			// 子列表
			if (PackageListItemProjectListAdapterList.size() == position) {// 首次填充
				MyPackageListItemProjectListAdapter adapter = new MyPackageListItemProjectListAdapter(
                        context, list.get(position).getProjectList(), list.get(
						position).getCityList(), packageStatus,list.get(position).getId());
				PackageListItemProjectListAdapterList.add(adapter);
			}
			holder.listView.setAdapter(PackageListItemProjectListAdapterList.get(position));
			if (list.get(position).getProjectList().size() == 0) {
				holder.vLine1.setVisibility(View.GONE);
			} else {
				holder.vLine1.setVisibility(View.VISIBLE);
			}
			ListViewUtils.fixListViewHeight(holder.listView);
			MyApp.instance.getImageLoad().displayImage(packageBean.getImgUrl(), holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());

			if (toggleStateList.get(position).equals(1)) {
				holder.expandable_toggle_button.setImageResource(R.mipmap.arrow_up);
			} else {
				holder.expandable_toggle_button.setImageResource(R.mipmap.arrow_down);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MdjLog.logE("zzy", "PackageListAdapter Exception: e :" + e.getMessage());
		}
		return convertView;
	}

	public class ViewHolder {
		TextView tvPrice, tvPackageName, tvLeftDays, tvLeftTimes, tvOrderId, tvOrderCreateTime,tvRefereeName,tvRefereePhone;
		RoundImageView ivImg;
		RelativeLayout item;
		View vShade, vLine1;
		ListView listView;
		ImageButton expandable_toggle_button;
        ImageView ivPackageStatus;
        Button btnSetReferee,btnViewShopList;
        RelativeLayout rlSetReferee,rlShowReferee;

	}
}
