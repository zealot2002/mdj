package com.mdj.moudle.userPackage;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.utils.ToastUtils;
import com.mdj.view.button.NoDoubleClickListener;

import java.util.List;

public class MyPackageListItemProjectListAdapter extends BaseAdapter {
    public static final String BROADCAST_ACTION_OPERATION = "com.vmei.broadcast.MyPackageListItemAction";
    private Context context;
    private List<MyPackageBean.ProjectBean> list;
    private List<MyPackageBean.CityBean> cityList;
    private MyPackageBean.PACKAGE_STATUS status;
    private String packageId;

	public MyPackageListItemProjectListAdapter(Context context) {
		this.context = context;
	}

    public MyPackageListItemProjectListAdapter(Context context, List<MyPackageBean.ProjectBean> list, List<MyPackageBean.CityBean> cityList,
                                               MyPackageBean.PACKAGE_STATUS status, String packageId) {
		this.list = list;
		this.context = context;
		this.cityList = cityList;
		this.status = status;
		this.packageId = packageId;
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
			convertView = View.inflate(context, R.layout.mypackage_list_item_project_list_item, null);
			holder = new ViewHolder();
			holder.tvProjectName = (TextView) convertView.findViewById(R.id.tvProjectName);
			holder.tvTimes = (TextView) convertView.findViewById(R.id.tvTimes);
			holder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);

			holder.btnUse = (Button) convertView.findViewById(R.id.btnUse);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			MyPackageBean.ProjectBean projectBean = list.get(position);
			holder.tvProjectName.setText(projectBean.getName());
			holder.tvTimes.setText(projectBean.getLeftTimes() + "/" + projectBean.getTotalTimes());
			holder.tvNumber.setText(position + 1 + "");

			// 判断是否支付
			if (status.equals(MyPackageBean.PACKAGE_STATUS.NOT_PAY)) {
				holder.btnUse.setText("未支付");
				holder.btnUse.setBackgroundResource(R.drawable.round_gray_btn_normal);
			} else if (status.equals(MyPackageBean.PACKAGE_STATUS.EXPIRED)) {
				holder.btnUse.setText("已过期");
				holder.btnUse.setBackgroundResource(R.drawable.round_gray_btn_normal);
			} else if (status.equals(MyPackageBean.PACKAGE_STATUS.EXPIRED)) {
				holder.btnUse.setText("已用完");
				holder.btnUse.setBackgroundResource(R.drawable.round_gray_btn_normal);
			} else {
				if (Integer.valueOf(projectBean.getLeftTimes()) > 0) {
					holder.btnUse.setText("去使用");
					holder.btnUse.setBackgroundResource(R.drawable.round_red_deepred_selector);

					holder.btnUse.setTag(position);
                    holder.btnUse.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            /** 是否可以使用 **/
                            boolean isUseAbale = false;
                            for (int i = 0; i < cityList.size(); i++) {
                                if (cityList.get(i).getId().equals(CacheManager.getInstance().getGlobalCity().getId())) {
                                    isUseAbale = true;
                                }
                            }
                            if(!isUseAbale){
                                String cityStr = "";
                                for (MyPackageBean.CityBean b : cityList) {
                                    cityStr+=b.getName();
                                    cityStr+=" ";
                                }
                                ToastUtils.showLong(context, "该套餐在当前城市无法使用。适用：  " + cityStr + ", 请您到首页切换城市");
                                return;
                            }
                            /*将选中项目广播至我的套餐列表activity*/
                            Intent broadcast = new Intent();
                            broadcast.putExtra(BundleConstant.PROJCET_ID,list.get(position).getId());
                            broadcast.putExtra(BundleConstant.PACKAGE_ID,packageId);
                            broadcast.setAction(BROADCAST_ACTION_OPERATION);
                            context.sendBroadcast(broadcast);
                        }
                    });
				} else {
					holder.btnUse.setText("已用完");
					holder.btnUse.setBackgroundResource(R.drawable.round_gray_btn_normal);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvProjectName, tvTimes, tvNumber;
		Button btnUse;
	}
}
