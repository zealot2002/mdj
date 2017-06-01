package com.mdj.moudle.order;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.moudle.order.bean.ProjectBeanWrapper;

import java.util.List;

public class ProjectListAdapter extends BaseAdapter {
	private Context context;
	private List<ProjectBeanWrapper> dataList;
    private boolean isPackageOrder = false;
/*********************************************************************************************************/
	public ProjectListAdapter(Context context) {
		this.context = context;
	}

	public ProjectListAdapter(Context context, List<ProjectBeanWrapper> dataList) {
		this.dataList = dataList;
		this.context = context;

	}
    public void setDataList(List<ProjectBeanWrapper> dataList){
        this.dataList = dataList;
    }

    public void setIsPackageOrder(boolean isPackageOrder){
        this.isPackageOrder = isPackageOrder;
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
			convertView = View.inflate(context, R.layout.order_project_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvDuration = (TextView) convertView.findViewById(R.id.tvDuration);
            holder.tvNum = (TextView) convertView.findViewById(R.id.tvNum);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
            ProjectBeanWrapper projectBeanWrapper = dataList.get(position);
			holder.tvName.setText(projectBeanWrapper.getProjectBean().getName());
            holder.tvNum.setText("x" + projectBeanWrapper.getNum());

            if(!isPackageOrder){
                holder.tvDuration.setText(projectBeanWrapper.getProjectBean().getDuration()+"分钟");
                holder.tvPrice.setText(context.getResources().getString(R.string.symbol_rmb)+projectBeanWrapper.getProjectBean().getPrice());
            }else{
                holder.tvDuration.setVisibility(View.GONE);
                holder.tvPrice.setVisibility(View.GONE);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvName,tvNum,tvPrice,tvDuration;
	}
}
