package com.mdj.moudle.address;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.utils.MdjLog;
import com.mdj.utils.TextViewUtil;

import java.util.List;

public class SearchAddressListAdapter extends BaseAdapter{
	private Context context;
	private List<AddressBean> dataList;
    private String keyword;


    /***************************************************方法区**********************************************************/

	public SearchAddressListAdapter(Context context) {
        this.context = context;
	}

    public void setDataList(List<AddressBean> dataList,String keyword) {
        this.dataList = dataList;
        this.keyword = keyword;
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
			convertView = View.inflate(context, R.layout.search_address_list_item, null);
			holder = new ViewHolder();
			holder.tvAddressName = (TextView) convertView.findViewById(R.id.tvAddressName);
			holder.tvAddressAddress = (TextView) convertView.findViewById(R.id.tvAddressAddress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
            AddressBean searchAddressBean = dataList.get(position);
            if(keyword.isEmpty()){
                holder.tvAddressName.setText(searchAddressBean.getName());
            }else{
                String str = searchAddressBean.getName();
                int startP = str.indexOf(keyword);
                if(startP != -1){//找到了
                    TextViewUtil.setSpecialTextColor(holder.tvAddressName,
                            str,
                            context.getResources().getColor(R.color.text_black),
                            context.getResources().getColor(R.color.red),
                            startP,
                            startP+keyword.length());
                }else{
                    holder.tvAddressName.setText(searchAddressBean.getName());
                }
            }
            holder.tvAddressAddress.setText(searchAddressBean.getAddress());
		} catch (Exception e) {
			e.printStackTrace();
			MdjLog.logE("zzy", "SearchAddressListAdapter Exception: e :" + e.toString());
		}
		return convertView;
	}

    public class ViewHolder {
		TextView tvAddressName, tvAddressAddress;
	}
}
