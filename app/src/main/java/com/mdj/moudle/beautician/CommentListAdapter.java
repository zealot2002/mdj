package com.mdj.moudle.beautician;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.moudle.beautician.bean.CommentBean;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.view.RoundImageView;

import java.util.List;

public class CommentListAdapter extends BaseAdapter {
    private Context context;
    private List<CommentBean> dataList;

    public CommentListAdapter(Context context) {
        this.context = context;

    }
    public void setDataList(List<CommentBean> dataList){
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
                convertView = View.inflate(context, R.layout.comment_list_item, null);
                holder = new ViewHolder();
                holder.tvProjectName = (TextView) convertView.findViewById(R.id.tvProjectName);
                holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

                holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final CommentBean bean = dataList.get(position);

            holder.tvProjectName.setText(bean.getProjectName());
            holder.tvUserName.setText(bean.getUserName());

            holder.tvContent.setText(bean.getContent());
            holder.tvTime.setText(bean.getTime());

            MyApp.instance.getImageLoad().displayImage(bean.getUserHeaderUrl(), holder.ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());

        }catch (Exception e){
            Toast.makeText(context, "CommentListAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    private class ViewHolder {
        private RoundImageView ivImg;
        private TextView tvProjectName,tvUserName,tvContent,tvTime;
    }
}