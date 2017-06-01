package com.mdj.moudle.mine.comment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.mine.comment.presenter.CommentContract;
import com.mdj.moudle.mine.comment.presenter.CommentPresenter;
import com.mdj.moudle.mine.share.RedPacketsShareActivity;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.TextViewUtil;
import com.mdj.view.RoundImageView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.flowlayout.FlowLayout;
import com.zhy.flowlayout.TagAdapter;
import com.zhy.flowlayout.TagFlowLayout;

import java.util.List;

/**
 * @author 吴世文
 * @Description: 上门评价详情
 */
public class InHomeCommentDetailsActivity extends BaseActivity implements View.OnClickListener, CommentContract.View {
    private String[] ratedTag = new String[]{"","好评","中评","差评"};
    private RoundImageView riBeauticianIcon;
    private TextView tvBeauticianName, tvOrderNumber, tvFavourableComment;
    private EditText etSuggests;
    private RadioButton rbRated;
    private CheckBox cbNick;
    private LayoutInflater mInflater;
    private TagFlowLayout tagFlowLayout;
    private LinearLayout ll_status;
    private boolean iskitkat;
    private CommentContract.Presenter presenter;
    private Button btnSubmit;
    private String orderId;
    private String beautyId;
    private String projectName;

    @Override
    public void findViews() {
        setContentView(R.layout.inhome_commentdetails_activity);
        mContext = this;
        presenter = new CommentPresenter(this);
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_status.setVisibility(View.VISIBLE);
        //美容师信息
        tvOrderNumber = (TextView) findViewById(R.id.tvOrderNumber);
        tvBeauticianName = (TextView) findViewById(R.id.tvBeauticianName);
        riBeauticianIcon = (RoundImageView) findViewById(R.id.riBeauticianIcon);
        tvFavourableComment = (TextView) findViewById(R.id.tvFavourableComment);

        cbNick = (CheckBox) findViewById(R.id.cbNick);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        rbRated = (RadioButton) findViewById(R.id.rbRated);
        etSuggests = (EditText) findViewById(R.id.etSuggests);
        tagFlowLayout = (TagFlowLayout) findViewById(R.id.tagFlowLayout);
        btnSubmit.setOnClickListener(this);
        initData();

    }

    private void initData() {
        beautyId =  getIntent().getStringExtra(BundleConstant.BEAUTICIAN_ID);
        presenter.getBeautyInfo(beautyId);
        orderId =  getIntent().getStringExtra(BundleConstant.ORDER_ID);
        presenter.evaluateInitialize("1", orderId);
        projectName = getIntent().getStringExtra(BundleConstant.PROJCET_NAME);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                Intent intent = new Intent(mContext,RedPacketsShareActivity.class);
                intent.putExtra(BundleConstant.ORDER_ID,orderId);
                intent.putExtra(BundleConstant.PROJCET_NAME,projectName);
                startActivity(intent);

                break;
        }
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        CommentBean commentBean = (CommentBean) data;
        if (!TextUtils.isEmpty(commentBean.getContent())) {
            etSuggests.setVisibility(View.VISIBLE);
            etSuggests.setText(commentBean.getContent());
        }
        if (commentBean.isAnonymous()) {
            cbNick.setText("匿名评价");
        }
        rbRated.setText(ratedTag[Integer.parseInt(commentBean.getServiceleve())]);

        tagFlowLayout.setMaxSelectCount(0);// 不能选，只能看
        mInflater = LayoutInflater.from(this);
        tagFlowLayout.setAdapter(new myAdapter(commentBean.getImpressionList()));
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void setBeautyInfo(Object object) {
        BeauticianBean object1 = (BeauticianBean) object;
        MyApp.instance.getImageLoad().displayImage(object1.getImgUrl(), riBeauticianIcon, DisplayImageOptionsUtil.getCommonCacheOptions());
        tvBeauticianName.setText(object1.getName());
        tvOrderNumber.setText("月订单"+object1.getOrderNum());
        tvFavourableComment.setText("月好评"+object1.getGoodAppraiseNum());
    }

    @Override
    public void submitCommentCallBack() {

    }


    class myAdapter extends TagAdapter<String> {
        public myAdapter(List<String> datas) {
            super(datas);
        }

        @Override
        public View getView(FlowLayout parent, int position, String s) {
            TextView tv = (TextView) mInflater.inflate(R.layout.rated_tags, tagFlowLayout, false);
            tv.setText(s);
            return tv;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
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
}
