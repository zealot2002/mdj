package com.mdj.moudle.home;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.common.AbstractPagerListAdapter;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.widget.AddAndSubWidget;
import com.mdj.moudle.widget.sale.SaleBean;
import com.mdj.moudle.widget.sale.SaleCountDownWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestProjectPagerAdapter extends AbstractPagerListAdapter<ProjectBean> {
    public static interface OnItemClickedListener{
        void onItemClicked(String projectId);
    }
    private Context context;
    private OnItemClickedListener onItemClickedListener;

    /******************************************************************************************************/
    public SuggestProjectPagerAdapter(Context context) {
        this.context = context;
    }
    public void setDataList(List<ProjectBean> dataList){
        super.setDataList(dataList);
    }
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener){
        this.onItemClickedListener = onItemClickedListener;
    }
    @Override
    public View newView(final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.suggest_project_paper_item,null);
        try{
            ViewHolder holder = new ViewHolder();

            holder.ivProjectPic = (ImageView)view.findViewById(R.id.ivProjectPic);
            holder.tvCurrentPage = (TextView)view.findViewById(R.id.tvCurrentPage);
            holder.tvProjectDuration = (TextView)view.findViewById(R.id.tvProjectDuration);
            holder.tvPeopleNum = (TextView)view.findViewById(R.id.tvPeopleNum);
            holder.tvProjectName = (TextView)view.findViewById(R.id.tvProjectName);

            holder.tvSymbol = (TextView)view.findViewById(R.id.tvSymbol);
            holder.tvProjectPrice = (TextView)view.findViewById(R.id.tvProjectPrice);
            holder.tvProjectOldPrice = (TextView)view.findViewById(R.id.tvProjectOldPrice);

            holder.tvEfficiency1 = (TextView)view.findViewById(R.id.tvEfficiency1);
            holder.tvEfficiency2 = (TextView)view.findViewById(R.id.tvEfficiency2);
            holder.tvEfficiency3 = (TextView)view.findViewById(R.id.tvEfficiency3);

            holder.addAndSubWidget = (AddAndSubWidget)view.findViewById(R.id.addAndSubWidget);
            holder.saleCountDownWidget = (SaleCountDownWidget)view.findViewById(R.id.saleCountDownWidget);
            holder.rlItem = (RelativeLayout)view.findViewById(R.id.rlItem);

            ProjectBean bean = dataList.get(position);
            MyApp.instance.getImageLoad().displayImage(bean.getImageUrl(), holder.ivProjectPic, DisplayImageOptionsUtil.getCommonCacheOptions());

            holder.tvCurrentPage.setText((position + 1) + "/" + dataList.size());

            Typeface newWeiTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/balzac.TTF");
            holder.tvCurrentPage.setTypeface(newWeiTypeFace);
            holder.tvCurrentPage.setText((position + 1) + "/" + dataList.size());

            holder.tvProjectDuration.setText(bean.getDuration() + "分钟");
            holder.tvPeopleNum.setText(bean.getRecommendNum()+"人做过");
            holder.tvProjectName.setText(bean.getName());

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

            holder.tvProjectOldPrice.setText(context.getResources().getString(R.string.symbol_rmb) + bean.getOldPrice());
            holder.tvProjectOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线

            int efficiencyNum = bean.getEfficiencySet().size();
            if(efficiencyNum>0){
                holder.tvEfficiency1.setText(bean.getEfficiencySet().get(0));
                holder.tvEfficiency1.setVisibility(View.VISIBLE);
                if(efficiencyNum>1){
                    holder.tvEfficiency2.setText(bean.getEfficiencySet().get(1));
                    holder.tvEfficiency2.setVisibility(View.VISIBLE);
                    if(efficiencyNum>2){
                        holder.tvEfficiency3.setText(bean.getEfficiencySet().get(2));
                        holder.tvEfficiency3.setVisibility(View.VISIBLE);
                    }
                }
            }
            holder.addAndSubWidget.setCustomCalMode(true);
            Map<String,String> tagMap = new HashMap<>();
            tagMap.put("position", position + "");

            holder.addAndSubWidget.setDataTagAndWidgetUser(CommonUtil.transMapToString(tagMap),
                    HomeFragment.ADD_AND_SUB_USER_HOME_SUGGEST_LIST);//私有协议

            /*倒计时*/
            if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_LIMIT_PRIVILEGE)) {//限时限量
                holder.saleCountDownWidget.setVisibility(View.VISIBLE);
                holder.saleCountDownWidget.show(new SaleBean.Builder()
                        .startTime(bean.getStartTime())
                        .endTime(bean.getEndTime())
                        .total(bean.getTotal())
                        .soldNum(bean.getSoldNum())
                        .perCount(bean.getPerCount())
                        .privilegePrice(bean.getPrivilegePrice())
                        .build());
            }else{
                holder.saleCountDownWidget.setVisibility(View.GONE);
            }
            holder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickedListener!=null){
                        onItemClickedListener.onItemClicked(dataList.get(position).getId());
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    public static class ViewHolder {
        private ImageView ivProjectPic;
        private TextView tvCurrentPage,tvProjectDuration,tvPeopleNum,tvProjectName,
                tvSymbol,tvProjectPrice,tvProjectOldPrice,tvEfficiency1,tvEfficiency2,tvEfficiency3;
        private AddAndSubWidget addAndSubWidget;
        private SaleCountDownWidget saleCountDownWidget;
        private RelativeLayout rlItem;
    }
}
