package com.mdj.moudle.userPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.ImageUtils;

import java.util.List;

public class PackageDetailListAdapter extends BaseAdapter {
    public static interface OnItemClickedListener{
        void onItemClicked(int position);
    }
    private Context context;
    private List<PackageDetailDataWrapperBean> dataList;
    private LayoutInflater mInflater;
    private OnItemClickedListener onItemClickedListener;
/***********************************************************************************************/
    public PackageDetailListAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener){
        this.onItemClickedListener = onItemClickedListener;
    }
    public void setDataList(List<PackageDetailDataWrapperBean> dataList){
        this.dataList = dataList;
    }
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList == null? PackageDetailDataWrapperBean.TYPE_PROJECT:dataList.get(position).getDataType();
    }

    @Override
    public int getViewTypeCount() {
        return PackageDetailDataWrapperBean.TYPE_MAX_COUNT;
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
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case PackageDetailDataWrapperBean.TYPE_PIC:
                        convertView = mInflater.inflate(R.layout.project_detail_data_item_1, null);
                        holder.ivImg = (ImageView)convertView.findViewById(R.id.ivImg);
                        break;
                    case PackageDetailDataWrapperBean.TYPE_PROJECT:
                        convertView = mInflater.inflate(R.layout.package_detail_data_item_2, null);
                        holder.ivProjcetImg = (ImageView)convertView.findViewById(R.id.ivProjcetImg);

                        holder.tvNumber = (TextView)convertView.findViewById(R.id.tvNumber);
                        holder.tvProjectName = (TextView)convertView.findViewById(R.id.tvProjectName);
                        holder.tvProjectPrice = (TextView)convertView.findViewById(R.id.tvProjectPrice);
                        holder.tvProjectDuration = (TextView)convertView.findViewById(R.id.tvProjectDuration);
                        holder.tvProjectNum = (TextView)convertView.findViewById(R.id.tvProjectNum);

                        holder.rlItem = (RelativeLayout)convertView.findViewById(R.id.rlItem);
                        holder.rlItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(onItemClickedListener!=null){
                                    onItemClickedListener.onItemClicked(position);
                                }
                            }
                        });
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            PackageDetailDataWrapperBean packageDetailDataWrapperBean = dataList.get(position);
            if(type == PackageDetailDataWrapperBean.TYPE_PIC){
                ImageUtils.scopImage(context, holder.ivImg);
                MyApp.instance.getImageLoad().displayImage(packageDetailDataWrapperBean.getImageUrl(),
                        holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            }else{
                holder.tvNumber.setText((position+1)+"");
                holder.tvProjectName.setText(packageDetailDataWrapperBean.getProjectWapperBean().getName());
                holder.tvProjectPrice.setText(packageDetailDataWrapperBean.getProjectWapperBean().getPrice()+"");
                holder.tvProjectDuration.setText(packageDetailDataWrapperBean.getProjectWapperBean().getDuration()+"分钟");
                holder.tvProjectNum.setText("x"+packageDetailDataWrapperBean.getProjectWapperBean().getNum());

                MyApp.instance.getImageLoad().displayImage(packageDetailDataWrapperBean.getProjectWapperBean().getImageUrl(),
                        holder.ivProjcetImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            }
        }catch (Exception e){
            Toast.makeText(context, "PackageDetailListAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tvNumber,tvProjectNum,tvProjectName,tvProjectPrice,tvProjectDuration;
        private ImageView ivImg,ivProjcetImg;
        private RelativeLayout rlItem;
    }
}