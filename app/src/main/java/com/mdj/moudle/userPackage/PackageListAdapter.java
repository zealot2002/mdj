package com.mdj.moudle.userPackage;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.CommonConstant;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjUtils;
import com.mdj.view.RoundImageView;

import java.util.List;

public class PackageListAdapter extends BaseAdapter{
    private Context context;
    private List<PackageBean> dataList;

    public PackageListAdapter(Context context) {
        this.context = context;
    }
    public void setDataList(List<PackageBean> dataList){
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.package_commodity_list_item, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvSymbol = (TextView) convertView.findViewById(R.id.tvSymbol);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
                holder.tvOldPrice = (TextView) convertView.findViewById(R.id.tvOldPrice);
                holder.tvValidityDays = (TextView) convertView.findViewById(R.id.tvValidityDays);
                holder.tvEfficiency1 = (TextView) convertView.findViewById(R.id.tvEfficiency1);
                holder.tvEfficiency2 = (TextView) convertView.findViewById(R.id.tvEfficiency2);

                holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);
                holder.ivPreferential = (ImageView) convertView.findViewById(R.id.ivPreferential);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final PackageBean bean = dataList.get(position);

            holder.tvName.setText(bean.getName());

            holder.tvOldPrice.setText(context.getResources().getString(R.string.symbol_rmb)+bean.getOldPrice());
            holder.tvOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线

            holder.tvValidityDays.setText(bean.getValidityDays() + "天");

            if(bean.getEffectList().size()>0){
                holder.tvEfficiency1.setText(bean.getEffectList().get(0));
                holder.tvEfficiency1.setVisibility(View.VISIBLE);
                if(bean.getEffectList().size()>1) {
                    holder.tvEfficiency2.setText(bean.getEffectList().get(1));
                    holder.tvEfficiency2.setVisibility(View.VISIBLE);
                }
            }

            StringBuilder isNormal = new StringBuilder();
            int price = MdjUtils.getCurrentPackagePrice(bean, isNormal);
            holder.tvPrice.setText(price + "");
            if(isNormal.toString().equals("true")){
                holder.tvPrice.setTextColor(context.getResources().getColor(R.color.text_black));
                holder.tvSymbol.setTextColor(context.getResources().getColor(R.color.text_black));
            }else{
                holder.tvPrice.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvSymbol.setTextColor(context.getResources().getColor(R.color.red));
            }
            if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_NORMAL)){
                holder.ivPreferential.setVisibility(View.GONE);
            }else{
                holder.ivPreferential.setVisibility(View.VISIBLE);
            }

            MyApp.instance.getImageLoad().displayImage(bean.getImgUrl(), holder.ivImg, DisplayImageOptionsUtil.getSmallImageOptions());

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "PackageListAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    private class ViewHolder {
        private RoundImageView ivImg;
        private TextView tvName,tvSymbol,tvPrice,tvOldPrice,tvValidityDays,tvEfficiency1,tvEfficiency2;
        private ImageView ivPreferential;
    }
}
