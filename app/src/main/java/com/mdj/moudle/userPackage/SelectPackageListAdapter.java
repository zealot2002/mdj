package com.mdj.moudle.userPackage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.TextViewUtil;
import com.mdj.utils.ToastUtils;
import com.mdj.view.MyListView;
import com.mdj.view.RoundImageView;

import java.util.ArrayList;
import java.util.List;


public class SelectPackageListAdapter extends BaseAdapter {
	private Context context;
	private List<OrderProjectPackageVo> dataList;
	private List<SelectPackageListItemListAdapter> adapterList;

/******************************************************************************************************/
	public SelectPackageListAdapter(Context context, List<OrderProjectPackageVo> dataList) {
        this.dataList = dataList;
		this.context = context;
		adapterList = new ArrayList<>(dataList.size());
	}

    public void setDataList(List<OrderProjectPackageVo> dataList){
        this.dataList = dataList;
    }
	public void setInnerDataList(List<OrderProjectPackageVo.PackageUseForOrderProjectVo> list,int refreshPosition){
		adapterList.get(refreshPosition).setDataList(list);
		adapterList.get(refreshPosition).notifyDataSetChanged();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.select_package_list_item, null);
			holder = new ViewHolder();
			holder.tvProjectName = (TextView) convertView.findViewById(R.id.tvProjectName);
			holder.tvProjectNum = (TextView) convertView.findViewById(R.id.tvProjectNum);
            holder.tvPackageUse = (TextView) convertView.findViewById(R.id.tvPackageUse);

            holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);
			holder.lvPackageList = (MyListView) convertView.findViewById(R.id.lvPackageProjectList);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			OrderProjectPackageVo projectForOrderVo = dataList.get(position);
			holder.tvProjectName.setText(projectForOrderVo.getName());
			holder.tvProjectNum.setText("数量:" + projectForOrderVo.getBuyNum());
            String str = "套餐抵用" + projectForOrderVo.getFreeNum() + "次";
            TextViewUtil.setSpecialTextColor(holder.tvPackageUse,
                    str,
                    context.getResources().getColor(R.color.text_gray),
                    context.getResources().getColor(R.color.red),
                    4,
                    str.length()-1
            );
//			holder.tvPackageUse.setText("套餐抵用" + projectForOrderVo.getFreeNum() + "次");

            MyApp.instance.getImageLoad().displayImage(projectForOrderVo.getImgUrl(), holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
			adapterList.add(position,
                    new SelectPackageListItemListAdapter(context,
                            dataList.get(position).getPackageUseForOrderProjectVoList(), position));
			holder.lvPackageList.setAdapter(adapterList.get(position));
            adapterList.get(position).notifyDataSetChanged();
        } catch (Exception e) {
			e.printStackTrace();
            ToastUtils.showLong(context,"SelectPackageListAdapter exception:"+e.toString());
		}
		return convertView;
	}
	
	class ViewHolder {
		TextView tvProjectName,tvProjectNum,tvPackageUse;
        RoundImageView ivImg;
        MyListView lvPackageList;
	}
}
