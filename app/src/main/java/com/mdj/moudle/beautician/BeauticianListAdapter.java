package com.mdj.moudle.beautician;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjLog;
import com.mdj.utils.TextViewUtil;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RoundImageView;

import java.util.List;

public class BeauticianListAdapter extends BaseAdapter {
	private Context context;
	private List<BeauticianBean> dataList;

	public void setDataList(List<BeauticianBean> dataList) {
		this.dataList = dataList;
	}

	public BeauticianListAdapter(Context context, List<BeauticianBean> dataList) {
		this.dataList = dataList;
		this.context = context;
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
			convertView = View.inflate(context, R.layout.beautician_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvOrderNum = (TextView) convertView.findViewById(R.id.tvOrderNum);
			holder.tvGoodAppriaseNum = (TextView) convertView.findViewById(R.id.tvGoodAppriaseNum);
            holder.tvIntruduction = (TextView) convertView.findViewById(R.id.tvIntruduction);
			holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
            BeauticianBean bean = dataList.get(position);
			holder.tvName.setText(bean.getName());

            String keyword = "月订单";
            String str = keyword+bean.getOrderNum();
            int startP = 3;
            TextViewUtil.setSpecialTextColor(holder.tvOrderNum,
                    str,
                    context.getResources().getColor(R.color.text_black),
                    context.getResources().getColor(R.color.red),
                    startP,
                    str.length());

            keyword = "月好评";
            str = keyword+bean.getGoodAppraiseNum();
            startP = 3;
            TextViewUtil.setSpecialTextColor(holder.tvGoodAppriaseNum,
                    str,
                    context.getResources().getColor(R.color.text_black),
                    context.getResources().getColor(R.color.red),
                    startP,
                    str.length());

            holder.tvIntruduction.setText(bean.getIntruduction());

            MyApp.instance.getImageLoad().displayImage(bean.getImgUrl(), holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());

		} catch (Exception e) {
			e.printStackTrace();
			MdjLog.log("BeauticianListAdapter Exception: e :" + e.toString());
            ToastUtils.showLong(context,"BeauticianListAdapter Exception: e :" + e.toString());
		}
		return convertView;
	}

	public class ViewHolder {
		TextView tvName,tvOrderNum, tvGoodAppriaseNum,tvIntruduction;
        RoundImageView ivImg;
	}
}
