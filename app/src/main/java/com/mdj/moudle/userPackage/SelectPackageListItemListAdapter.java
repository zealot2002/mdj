package com.mdj.moudle.userPackage;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.utils.TextViewUtil;

import java.util.List;


public class SelectPackageListItemListAdapter extends BaseAdapter implements OnClickListener{
	private Context context;
	private List<OrderProjectPackageVo.PackageUseForOrderProjectVo> list;
	private int list1Position;
//	private OrderSureListEventListener listener;

	public SelectPackageListItemListAdapter(Context context) {
		this.context = context;
	}

	public SelectPackageListItemListAdapter(Context context,
                                            List<OrderProjectPackageVo.PackageUseForOrderProjectVo> list, int list1Position) {
		this.list = list;
		this.context = context;
		this.list1Position = list1Position;
	}
	
	public void setDataList(List<OrderProjectPackageVo.PackageUseForOrderProjectVo> list){
		this.list = list;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.select_package_list_item_list_item, null);
			holder = new ViewHolder();
			holder.tvPackageName = (TextView) convertView.findViewById(R.id.tvPackageName);
			holder.tvValidDuration = (TextView) convertView.findViewById(R.id.tvValidDuration);
			holder.tvLeftTimes = (TextView) convertView.findViewById(R.id.tvLeftTimes);

            holder.btnSub = (ImageButton) convertView.findViewById(R.id.btnSub);
            holder.btnAdd = (ImageButton) convertView.findViewById(R.id.btnAdd);
			holder.etNum = (EditText) convertView.findViewById(R.id.etNum);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			OrderProjectPackageVo.PackageUseForOrderProjectVo packageUseForOrderProjectVo = list.get(position);
			holder.tvPackageName.setText(packageUseForOrderProjectVo.getName());
			if(packageUseForOrderProjectVo.getValidDuration()==-1){
				holder.tvValidDuration.setText("永久有效");
			}else{
                String str = "还有"+ packageUseForOrderProjectVo.getValidDuration()+"天";
                TextViewUtil.setSpecialTextColor(holder.tvValidDuration,
                        str,
                        context.getResources().getColor(R.color.text_gray),
                        context.getResources().getColor(R.color.red),
                        2,
                        str.length()-1
                );
//				holder.tvValidDuration.setText("还有"+ packageUseForOrderProjectVo.getValidDuration()+"天");
			}

            String str = "剩余"+ packageUseForOrderProjectVo.getAvailableNum()+"次";
            TextViewUtil.setSpecialTextColor(holder.tvLeftTimes,
                    str,
                    context.getResources().getColor(R.color.text_gray),
                    context.getResources().getColor(R.color.red),
                    2,
                    str.length() - 1
            );

//			holder.tvLeftTimes.setText("剩余"+ packageUseForOrderProjectVo.getAvailableNum()+"");
			
			if(packageUseForOrderProjectVo.getAllocNum()>0){
				holder.btnSub.setVisibility(View.VISIBLE);
				holder.etNum.setVisibility(View.VISIBLE);
				holder.etNum.setText(packageUseForOrderProjectVo.getAllocNum()+"");
			}else{
				holder.btnSub.setVisibility(View.INVISIBLE);
				holder.etNum.setVisibility(View.INVISIBLE);
			}
			holder.btnSub.setTag(position);
			holder.btnAdd.setTag(position);
			holder.btnSub.setOnClickListener(this);
			holder.btnAdd.setOnClickListener(this);
			
		} catch (Exception e) {
			Toast.makeText(context, "OrderSureLowerProjectListItemPackageListAdapter Exception: " +e.getMessage(), Toast.LENGTH_LONG).show();
		}
		return convertView;
	}

	class ViewHolder {
		TextView tvPackageName,tvValidDuration,tvLeftTimes;
		ImageButton btnSub,btnAdd;
		EditText etNum;
	}

	@Override
	public void onClick(View v) {
		int position = (int)v.getTag();
		switch (v.getId()) {
			case R.id.btnSub:
				((SelectPackageListItemListEventListener)context).OnEvent(list1Position, position, SelectPackageListItemListEventListener.ButtonAction.ActionSub);
				break;
			case R.id.btnAdd:
				((SelectPackageListItemListEventListener)context).OnEvent(list1Position, position, SelectPackageListItemListEventListener.ButtonAction.ActionAdd);
				break;
			default:
				break;
		}
	}
}
