package com.mdj.moudle.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.ImageUtils;

import java.util.List;

public class ProjectDetailListAdapter extends BaseAdapter {
    private static final int TYPE_PIC = 0;
    private static final int TYPE_COMMENT = 1;
    private static final int TYPE_MAX_COUNT = TYPE_COMMENT + 1;

    private Context context;
    private List<ProjectDetailDataWrapperBean> dataList;
    private LayoutInflater mInflater;
/***********************************************************************************************/
    public ProjectDetailListAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataList(List<ProjectDetailDataWrapperBean> dataList){
        this.dataList = dataList;
    }
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList == null? TYPE_PIC:dataList.get(position).getDataType();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
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
//            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_PIC:
                        convertView = mInflater.inflate(R.layout.project_detail_data_item_1, null);
                        holder.ivImg = (ImageView)convertView.findViewById(R.id.ivImg);
                        break;
                    case TYPE_COMMENT:
                        convertView = mInflater.inflate(R.layout.project_detail_data_item_2, null);
                        holder.ivUserImg = (ImageView)convertView.findViewById(R.id.ivUserImg);
                        holder.ivBeauticianImg = (ImageView)convertView.findViewById(R.id.ivBeauticianImg);

                        holder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
                        holder.tvContent = (TextView)convertView.findViewById(R.id.tvContent);
                        holder.tvTime = (TextView)convertView.findViewById(R.id.tvTime);
                        holder.tvBeauticianName = (TextView)convertView.findViewById(R.id.tvBeauticianName);
                        break;
                }
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder)convertView.getTag();
//            }
            ProjectDetailDataWrapperBean projectDetailDataWrapperBean = dataList.get(position);
            if(type == TYPE_PIC){
                ImageUtils.scopImage(context,holder.ivImg);
                MyApp.instance.getImageLoad().displayImage(projectDetailDataWrapperBean.getImageUrl(),
                        holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            }else{
                holder.tvUserName.setText(projectDetailDataWrapperBean.getCommentBean().getUserName());
                holder.tvContent.setText(projectDetailDataWrapperBean.getCommentBean().getContent());
                holder.tvTime.setText(projectDetailDataWrapperBean.getCommentBean().getCreateTime());
                holder.tvBeauticianName.setText(projectDetailDataWrapperBean.getCommentBean().getBeauticianName());

                MyApp.instance.getImageLoad().displayImage(projectDetailDataWrapperBean.getCommentBean().getUserImageUrl(),
                        holder.ivUserImg, DisplayImageOptionsUtil.getCommonCacheOptions());

                MyApp.instance.getImageLoad().displayImage(projectDetailDataWrapperBean.getCommentBean().getBeauticianImageUrl(),
                        holder.ivBeauticianImg, DisplayImageOptionsUtil.getCommonCacheOptions());
            }
        }catch (Exception e){
            Toast.makeText(context, "HomeProjectListAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tvUserName,tvContent,tvTime,tvBeauticianName;
        private ImageView ivImg,ivUserImg,ivBeauticianImg;
    }
}