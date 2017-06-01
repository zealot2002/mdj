package com.mdj.moudle.address;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.utils.MdjLog;

import java.util.List;

public class MyAddressListAdapter extends BaseAdapter{
    public interface OnClickListener{
        void onEditButtonClick(int position);
        void onDeleteButtonClick(int position);
        void onDefaultCheckBoxClick(int position);
    }
	private Context context;
	private List<AddressBean> dataList;
    private OnClickListener listener;

    /***************************************************方法区**********************************************************/

	public MyAddressListAdapter(Context context, OnClickListener listener) {
        this.context = context;
        this.listener = listener;
	}

    public void setDataList(List<AddressBean> dataList) {
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
			convertView = View.inflate(context, R.layout.my_address_list_item, null);
			holder = new ViewHolder();
			holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
			holder.tvUserPhone = (TextView) convertView.findViewById(R.id.tvUserPhone);
			holder.tvUserAddress = (TextView) convertView.findViewById(R.id.tvUserAddress);
            holder.tvSetDefault = (TextView) convertView.findViewById(R.id.tvSetDefault);

			holder.btnEdit = (ImageButton) convertView.findViewById(R.id.btnEdit);
			holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btnDelete);

			holder.cbDefault = (CheckBox) convertView.findViewById(R.id.cbDefault);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
            AddressBean addressBean = dataList.get(position);
            holder.tvUserName.setText(addressBean.getUserName());
            holder.tvUserPhone.setText(addressBean.getUserPhone());
            holder.tvUserAddress.setText(addressBean.getName() + "  " + addressBean.getDoorNum());

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEditButtonClick(position);
                }
            });
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteButtonClick(position);
                }
            });
            if(addressBean.isDefault()){
                holder.cbDefault.setChecked(true);
                holder.tvSetDefault.setText("默认地址");
            }else{
                holder.cbDefault.setChecked(false);
                holder.tvSetDefault.setText("设为默认");
            }
            holder.cbDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDefaultCheckBoxClick(position);
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
			MdjLog.logE("zzy", "MyAddressListAdapter Exception: e :" + e.toString());
		}
		return convertView;
	}

    public class ViewHolder {
		TextView tvUserName, tvUserPhone, tvUserAddress,tvSetDefault;
        ImageButton btnEdit,btnDelete;
        CheckBox cbDefault;
	}
}
