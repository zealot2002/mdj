package com.mdj.moudle.userPackage;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.order.ConfirmOrderPackageActivity;
import com.mdj.moudle.project.ProjectDetailActivity;
import com.mdj.moudle.userPackage.presenter.PackageDetailContract;
import com.mdj.moudle.userPackage.presenter.PackageDetailPresenter;
import com.mdj.moudle.webview.BaseWebviewActivity;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.moudle.widget.sale.SaleBean;
import com.mdj.moudle.widget.sale.SaleCountDownWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.TextViewUtil;
import com.umeng.analytics.MobclickAgent;
import com.zhy.flowlayout.FlowLayout;
import com.zhy.flowlayout.TagAdapter;
import com.zhy.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class PackageDetailActivity extends BaseActivity implements
        View.OnClickListener,AdapterView.OnItemClickListener,PackageDetailContract.View{
    private PackageBean packageBean;

    private TextView tvSoldNum,tvOriginPlace,tvPrice,tvOldPrice,
            tvExpireTime,tvRemarksTitle,tvRemarks,tvSaveMoney,
            tvQuality1,tvQuality2,tvQuality3;
    private ImageView ivPic1,ivQuality1,ivQuality2,ivQuality3;
    private TagFlowLayout mFlowLayout;
//
    private RelativeLayout rlTips;
    private PackageDetailListAdapter adapter;
    private ListView lvPackageDetailList;
    private View footerView;

    private TextView tvShoppingCartPrice,tvValidityDays;
    private RelativeLayout rlSubmit;

    private SaleCountDownWidget saleCountDownWidget;

    private PackageDetailContract.Presenter presenter;
    private String packageId;
    private String beautyParlorId;

    /*flat*/
    private TitleWidget titleWidget;
    private LinearLayout llDisconnectTipsLayout;
/*****************************************************************************************************/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDisconnectTipsLayout:
                presenter.getPackageDetail(packageId);
                break;

            case R.id.rlSubmit:
                {
                    Intent intent = new Intent(mContext, ConfirmOrderPackageActivity.class);
                    intent.putExtra(BundleConstant.PACKAGE_BEAN, packageBean);
                    intent.putExtra(BundleConstant.BEAUTY_PARLOR_ID, beautyParlorId);
                    startActivity(intent);
                }
                break;
            case R.id.rlTips:
                Intent it = new Intent(mContext, BaseWebviewActivity.class);
                it.putExtra(BundleConstant.URL, packageBean.getGuideHtml());
                it.putExtra(BundleConstant.TITLE, "使用说明");
                startActivity(it);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MobclickAgent.onPageStart(CommonUtil.generateTag());
            MobclickAgent.onResume(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPageEnd(CommonUtil.generateTag());
            MobclickAgent.onPause(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private void findAllViews(){
        setContentView(R.layout.activity_package_detail);

        lvPackageDetailList = (ListView)findViewById(R.id.lvPackageDetailList);

/*设置headerView*/
        View headerView = View.inflate(this, R.layout.activity_package_detail_header,null);
        findListHeaderViews(headerView);
        lvPackageDetailList.addHeaderView(headerView);

/*设置footerView*/
        footerView = View.inflate(this, R.layout.activity_package_detail_footer,null);
        tvRemarks = (TextView) footerView.findViewById(R.id.tvRemarks);
        tvRemarksTitle = (TextView) footerView.findViewById(R.id.tvRemarksTitle);
        rlTips = (RelativeLayout) footerView.findViewById(R.id.rlTips);
        rlTips.setOnClickListener(this);

        lvPackageDetailList.addFooterView(footerView);

/*设置adapter*/
        adapter = new PackageDetailListAdapter(mContext);
        adapter.setOnItemClickedListener(new PackageDetailListAdapter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(mContext,ProjectDetailActivity.class);
                intent.putExtra(BundleConstant.PROJCET_ID,packageBean.getProjectList().get(position).getId());
                intent.putExtra(BundleConstant.READ_ONLY,true);
                startActivity(intent);
            }
        });
        lvPackageDetailList.setAdapter(adapter);

/*购物车*/
        tvShoppingCartPrice = (TextView)findViewById(R.id.tvShoppingCartPrice);
        tvValidityDays = (TextView)findViewById(R.id.tvValidityDays);
        rlSubmit = (RelativeLayout)findViewById(R.id.rlSubmit);
        rlSubmit.setOnClickListener(this);

    }
    @Override
    public void findViews() {
        mContext = this;

        setContentView(R.layout.activity_flat);
        titleWidget = (TitleWidget)findViewById(R.id.titleWidget);
        titleWidget.setTitle("套餐详情");
        llDisconnectTipsLayout = (LinearLayout)findViewById(R.id.llDisconnectTipsLayout);
        llDisconnectTipsLayout.setOnClickListener(this);

        packageId = getIntent().getStringExtra(BundleConstant.PACKAGE_ID);
        if(getIntent().hasExtra(BundleConstant.BEAUTY_PARLOR_ID)){
            beautyParlorId = getIntent().getStringExtra(BundleConstant.BEAUTY_PARLOR_ID);
        }
        presenter = new PackageDetailPresenter(this);
        presenter.getPackageDetail(packageId);
    }

    private void findListHeaderViews(View headerView) {
        saleCountDownWidget = (SaleCountDownWidget)headerView.findViewById(R.id.saleCountDownWidget);

        tvSoldNum = (TextView) headerView.findViewById(R.id.tvSoldNum);

        tvOriginPlace = (TextView) headerView.findViewById(R.id.tvOriginPlace);
        tvPrice = (TextView) headerView.findViewById(R.id.tvPrice);
        tvOldPrice = (TextView) headerView.findViewById(R.id.tvOldPrice);
        tvExpireTime = (TextView) headerView.findViewById(R.id.tvExpireTime);

        tvQuality1 = (TextView) headerView.findViewById(R.id.tvQuality1);
        tvQuality2 = (TextView) headerView.findViewById(R.id.tvQuality2);
        tvQuality3 = (TextView) headerView.findViewById(R.id.tvQuality3);

        tvSaveMoney = (TextView) headerView.findViewById(R.id.tvSaveMoney);

        ivPic1 = (ImageView) headerView.findViewById(R.id.ivPic1);
        ivQuality1 = (ImageView) headerView.findViewById(R.id.ivQuality1);
        ivQuality2 = (ImageView) headerView.findViewById(R.id.ivQuality2);
        ivQuality3 = (ImageView) headerView.findViewById(R.id.ivQuality3);

        mFlowLayout = (TagFlowLayout) headerView.findViewById(R.id.id_flowlayout);
    }
    @Override
    public void showDisconnect(String msg) {
        llDisconnectTipsLayout.setVisibility(View.VISIBLE);
        showShortToast(msg);
    }

    //只更新上半部分
    @Override
    public void updateUI(Object data) {
        try{
            packageBean = (PackageBean)data;
            findAllViews();

            tvShoppingCartPrice.setText(MdjUtils.getCurrentPackagePrice(packageBean,new StringBuilder())+"");
            tvValidityDays.setText(packageBean.getValidityDays() + "天");

            tvSoldNum.setText("总销量:"+packageBean.getTotalOrderNum());
            tvOriginPlace.setText(packageBean.getOriginPlace());
            tvPrice.setText(MdjUtils.getCurrentPackagePrice(packageBean, new StringBuilder()) + "");

            tvOldPrice.setText(getString(R.string.symbol_rmb) + packageBean.getOldPrice());
            tvOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线

            tvExpireTime.setText("疗程使用期限: 自购买之日起" + packageBean.getValidityDays() + "天");

            String keyword = "累计減少";
            String str = keyword+ packageBean.getSaveMoney()+"元";
            TextViewUtil.setSpecialTextColor(tvSaveMoney,
                    str,
                    mContext.getResources().getColor(R.color.text_gray),
                    mContext.getResources().getColor(R.color.red),
                    keyword.length(),
                    str.length()-1);
            tvRemarks.setText(packageBean.getRemarks());
            tvRemarksTitle.setText(packageBean.getRemarksTitle());

            MyApp.instance.getImageLoad().displayImage(packageBean.getImgUrl()
                    , ivPic1, DisplayImageOptionsUtil.getCommonCacheOptions());

            final LayoutInflater mInflater = LayoutInflater.from(this);

            mFlowLayout.setMaxSelectCount(0);
            mFlowLayout.setAdapter(new TagAdapter<String>(packageBean.getTagList()) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv_for_project_detail, mFlowLayout, false);
                    tv.setText(s);
                    return tv;
                }
            });

            /*质量三包*/
            tvQuality1.setText(packageBean.getQualityList().get(0).getName());
            tvQuality2.setText(packageBean.getQualityList().get(1).getName());
            tvQuality3.setText(packageBean.getQualityList().get(2).getName());

            MyApp.instance.getImageLoad().displayImage(packageBean.getQualityList().get(0).getImageUrl()
                    , ivQuality1, DisplayImageOptionsUtil.getCommonCacheOptions());
            MyApp.instance.getImageLoad().displayImage(packageBean.getQualityList().get(1).getImageUrl()
                    , ivQuality2, DisplayImageOptionsUtil.getCommonCacheOptions());
            MyApp.instance.getImageLoad().displayImage(packageBean.getQualityList().get(2).getImageUrl()
                    , ivQuality3, DisplayImageOptionsUtil.getCommonCacheOptions());

            /*倒计时*/
            if(packageBean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_LIMIT_PRIVILEGE)) {//限时限量
                saleCountDownWidget.setVisibility(View.VISIBLE);
                saleCountDownWidget.show(new SaleBean.Builder()
                        .startTime(packageBean.getStartTime())
                        .endTime(packageBean.getEndTime())
                        .total(packageBean.getTotal())
                        .soldNum(packageBean.getSoldNum())
                        .perCount(packageBean.getPerCount())
                        .privilegePrice(packageBean.getPrivilegePrice())
                        .build());
            }else{
                saleCountDownWidget.setVisibility(View.GONE);
            }

            /*更新list*/
            List<PackageDetailDataWrapperBean> wrapperList = new ArrayList<>();
            /*填充项目item*/
            for(int i=0;i<packageBean.getProjectList().size();i++){
                PackageDetailDataWrapperBean wrapperBean = new PackageDetailDataWrapperBean();
                wrapperBean.setDataType(PackageDetailDataWrapperBean.TYPE_PROJECT);

                wrapperBean.getProjectWapperBean().setName(packageBean.getProjectList().get(i).getName());
                wrapperBean.getProjectWapperBean().setImageUrl(packageBean.getProjectList().get(i).getImageUrl());
                wrapperBean.getProjectWapperBean().setPrice(packageBean.getProjectList().get(i).getPrice());
                wrapperBean.getProjectWapperBean().setDuration(packageBean.getProjectList().get(i).getDuration());
                wrapperBean.getProjectWapperBean().setNum(packageBean.getProjectList().get(i).getNum());
                wrapperList.add(wrapperBean);
            }
            /*填充图片item*/
            for(int i=0;i<packageBean.getExtraImgUrlList().size();i++){
                wrapperList.add(new PackageDetailDataWrapperBean(PackageDetailDataWrapperBean.TYPE_PIC,
                        packageBean.getExtraImgUrlList().get(i)));
            }
            adapter.setDataList(wrapperList);
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
            showShortToast("PackageDetailActivity:"+e.toString());
        }
    }
    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}