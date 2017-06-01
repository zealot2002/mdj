package com.mdj.moudle.project;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.beautician.BeauticianDetailActivity;
import com.mdj.moudle.beautyParlor.BeautyParlorDetailActivity;
import com.mdj.moudle.home.HomeFragment;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.widget.AddAndSubWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjUtils;
import com.mdj.view.RoundImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectListAdapter extends BaseAdapter{
    public interface OnItemClickedListener{
        void onItemClicked(String projectId);
    }
    private Context context;
    private List<ProjectBean> dataList;
    private int tabIndex;//首页专用;
    private OnItemClickedListener onItemClickedListener;

    private String addAndSubUser;
/********************************************************************************************************/
    public ProjectListAdapter(Context context) {
        this.context = context;

    }
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener){
        this.onItemClickedListener = onItemClickedListener;
    }
    public void setDataList(List<ProjectBean> dataList){
        this.dataList = dataList;
    }
    public void setTabIndex(int tabIndex){
        this.tabIndex = tabIndex;
    }
    public void setAddAndSubUser(String addAndSubUser){
        this.addAndSubUser = addAndSubUser;
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
                convertView = View.inflate(context, R.layout.home_project_list_item, null);
                holder = new ViewHolder();
                holder.tvProjectName = (TextView) convertView.findViewById(R.id.tvProjectName);
                holder.tvSymbol = (TextView) convertView.findViewById(R.id.tvSymbol);
                holder.tvProjectPrice = (TextView) convertView.findViewById(R.id.tvProjectPrice);
                holder.tvProjectOldPrice = (TextView) convertView.findViewById(R.id.tvProjectOldPrice);
                holder.tvProjectDuration = (TextView) convertView.findViewById(R.id.tvProjectDuration);
                holder.tvPeopleNum = (TextView) convertView.findViewById(R.id.tvPeopleNum);
                holder.tvEfficiency1 = (TextView) convertView.findViewById(R.id.tvEfficiency1);
                holder.tvEfficiency2 = (TextView) convertView.findViewById(R.id.tvEfficiency2);

                holder.rlItem = (RelativeLayout) convertView.findViewById(R.id.rlItem);
                holder.ivImg = (RoundImageView) convertView.findViewById(R.id.ivImg);
                holder.ivPreferential = (ImageView)convertView.findViewById(R.id.ivPreferential);
                holder.addAndSubWidget = (AddAndSubWidget) convertView.findViewById(R.id.addAndSubWidget);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ProjectBean bean = dataList.get(position);

            holder.tvProjectName.setText(bean.getName());

            holder.tvProjectOldPrice.setText(context.getResources().getString(R.string.symbol_rmb) + bean.getOldPrice());
            holder.tvProjectOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线

            holder.tvProjectDuration.setText(bean.getDuration() + "分钟");
            holder.tvPeopleNum.setText(bean.getRecommendNum() + "人做过");

            MyApp.instance.getImageLoad().displayImage(bean.getImageUrl(), holder.ivImg, DisplayImageOptionsUtil.getSmallImageOptions());

            int efficiencyNum = bean.getEfficiencySet().size();
            if(efficiencyNum>0){
                holder.tvEfficiency1.setText(bean.getEfficiencySet().get(0));
                holder.tvEfficiency1.setVisibility(View.VISIBLE);
                if(efficiencyNum>1){
                    holder.tvEfficiency2.setText(bean.getEfficiencySet().get(1));
                    holder.tvEfficiency2.setVisibility(View.VISIBLE);
                }
            }

            /*区分使用者*/
            Map<String,String> tagMap = new HashMap<>();
            if(HomeFragment.ADD_AND_SUB_USER_HOME_NORMAL_LIST.equals(addAndSubUser)){
                tagMap.put("tabIndex", tabIndex+"");
                tagMap.put("position", position + "");
            }else if(BeautyParlorDetailActivity.ADD_AND_SUB_USER_BEAUTY_PARLOR_DETAIL.equals(addAndSubUser)
                    || BeauticianDetailActivity.ADD_AND_SUB_USER_BEAUTICIAN_DETAIL.equals(addAndSubUser)
                    ){
                tagMap.put("position", position + "");
            }
            holder.addAndSubWidget.setCustomCalMode(true);
            holder.addAndSubWidget.setDataTagAndWidgetUser(CommonUtil.transMapToString(tagMap), addAndSubUser);//私有协议

            holder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickedListener != null) {
                        onItemClickedListener.onItemClicked(bean.getId());
                    }
                }
            });

            StringBuilder isNormal = new StringBuilder();
            int price = MdjUtils.getCurrentProjectPrice(bean,isNormal);
            holder.tvProjectPrice.setText(price + "");
            if(isNormal.toString().equals("true")){
                holder.tvProjectPrice.setTextColor(context.getResources().getColor(R.color.text_black));
                holder.tvSymbol.setTextColor(context.getResources().getColor(R.color.text_black));
            }else{
                holder.tvProjectPrice.setTextColor(context.getResources().getColor(R.color.red));
                holder.tvSymbol.setTextColor(context.getResources().getColor(R.color.red));
            }
            if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_NORMAL)){
                holder.ivPreferential.setVisibility(View.GONE);
            }else{
                holder.ivPreferential.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "ProjectListAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return convertView;
    }

    private class ViewHolder {
        private RoundImageView ivImg;
        private TextView tvProjectName,tvSymbol,tvProjectPrice,tvProjectOldPrice,
                tvProjectDuration,tvPeopleNum,tvEfficiency1,tvEfficiency2;
        private AddAndSubWidget addAndSubWidget;
        private RelativeLayout rlItem;
        private ImageView ivPreferential;
    }
}
