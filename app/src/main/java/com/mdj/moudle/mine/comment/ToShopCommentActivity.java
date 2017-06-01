package com.mdj.moudle.mine.comment;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.mine.comment.presenter.CommentContract;
import com.mdj.moudle.mine.comment.presenter.CommentPresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.MdjLog;
import com.mdj.view.RoundImageView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.flowlayout.FlowLayout;
import com.zhy.flowlayout.TagAdapter;
import com.zhy.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 吴世文
 * @Description: 到店评价
 */
public class ToShopCommentActivity extends BaseActivity implements CommentContract.View {
    private String[] ratingBarNotes = new String[]
            {"", "差极了", "不满意", "感觉一般", "比较满意", "非常满意"};
    private RoundImageView riBeauticianIcon;
    private TextView tvBeauticianName, tvBeauticianShop, tvOrderNumber, tvFavourableComment;
    private Button btnSubmit;
    private String shopScore = "0";
    private EditText etSuggests;
    private RatingBar rbRatingBar;
    private TextView tvLimitNumber;
    private TextView tvRatingBarDes;
    private TagFlowLayout tagFlowLayout;
    //好评 中评 差评
    private RadioGroup rgGroup;
    private RadioButton rbGood;
    private RadioButton rbRates;
    private RadioButton rbMedium;

    private LayoutInflater mInflater;
    private List<String> impressionList = new ArrayList<>();

    //选择的评价
    String serviceLeve = "1";
    private CheckBox cbNick;
    private String isAnonymous = "1";
    private boolean iskitkat;
    private LinearLayout ll_status;
    private CommentContract.Presenter presenter;
    private CommentBean commentBean;
    private String content;
    private String orderId;
    private String beautyId;
    private String projectName;

    @Override
    public void findViews() {
        setContentView(R.layout.toshop_comment_activity);
        mContext = this;
        presenter = new CommentPresenter(this);
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_status.setVisibility(View.VISIBLE);
        cbNick = (CheckBox) findViewById(R.id.cbNick);
        rbGood = (RadioButton) findViewById(R.id.rbGood);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        rgGroup = (RadioGroup) findViewById(R.id.rgGroup);
        rbRates = (RadioButton) findViewById(R.id.rbRates);
        rbMedium = (RadioButton) findViewById(R.id.rbMedium);
        etSuggests = (EditText) findViewById(R.id.etSuggests);
        rbRatingBar = (RatingBar) findViewById(R.id.rbRatingBar);
        tvLimitNumber = (TextView) findViewById(R.id.tvLimitNumber);
        tvRatingBarDes = (TextView) findViewById(R.id.tvRatingBarDes);
        tagFlowLayout = (TagFlowLayout) findViewById(R.id.tagFlowLayout);
        //美容师信息
        tvOrderNumber = (TextView) findViewById(R.id.tvOrderNumber);
        tvBeauticianName = (TextView) findViewById(R.id.tvBeauticianName);
        tvBeauticianShop = (TextView) findViewById(R.id.tvBeauticianShop);
        riBeauticianIcon = (RoundImageView) findViewById(R.id.riBeauticianIcon);
        tvFavourableComment = (TextView) findViewById(R.id.tvFavourableComment);

        cbNick.setChecked(true);

        initData();

        btnSubmit.setOnClickListener(new MyListener());
        etSuggests.setOnClickListener(new MyListener());
        etSuggests.addTextChangedListener(new MyTextWatcher());//字数限制
        cbNick.setOnCheckedChangeListener(new MyCheckedListener());//匿名选择
        rgGroup.setOnCheckedChangeListener(new MyCheckedListener());  // 评价
        rbRatingBar.setOnRatingBarChangeListener(new myRatingBarListener()); //RatingBar
    }

    private void initData() {
        beautyId =  getIntent().getStringExtra(BundleConstant.BEAUTICIAN_ID);
        presenter.getBeautyInfo(beautyId);
        orderId =  getIntent().getStringExtra(BundleConstant.ORDER_ID);
        presenter.evaluateInitialize("0",orderId);
        projectName = getIntent().getStringExtra(BundleConstant.PROJCET_NAME);
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        commentBean = (CommentBean) data;
        if(commentBean != null){
            mInflater = LayoutInflater.from(this);
            tagFlowLayout.setAdapter(new myTagAdapter(commentBean.getGoodsImpressionList()));
            tagFlowLayout.setOnTagClickListener(new myTagListener());
        }

    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void setBeautyInfo(Object object) {
        BeauticianBean object1 = (BeauticianBean) object;
        MyApp.instance.getImageLoad().displayImage(object1.getImgUrl(), riBeauticianIcon, DisplayImageOptionsUtil.getCommonCacheOptions());
        tvBeauticianName.setText(object1.getName());
        tvBeauticianShop.setText(object1.getDepartment());
        tvOrderNumber.setText("月订单"+object1.getOrderNum());
        tvFavourableComment.setText("月好评"+object1.getGoodAppraiseNum());
    }

    @Override
    public void submitCommentCallBack() {
            //评论成功——>评价详情
            Intent intent = new Intent(mContext, ToShopCommentDetailsActivity.class);
            intent.putExtra(BundleConstant.ORDER_ID,orderId);
            intent.putExtra(BundleConstant.BEAUTICIAN_ID,beautyId);
            intent.putExtra(BundleConstant.PROJCET_NAME,projectName);
            startActivity(intent);
            setResult(Activity.RESULT_OK);
            finish();
    }


    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.etSuggests:
                    etSuggests.setFocusable(true);
                    etSuggests.setFocusableInTouchMode(true);
                    etSuggests.requestFocus();
                    etSuggests.requestFocusFromTouch();
                    break;
                case R.id.btnSubmit:
                    content = etSuggests.getText().toString().trim();
                    String impreLists = JSON.toJSONString(impressionList);
                    MdjLog.log("orderID="+orderId+" isAnomyous="+isAnonymous+" serviceleve="+serviceLeve+" shopscore="+shopScore+" content="+content+" impreLists="+impreLists);
                    presenter.submitComment(orderId, String.valueOf(CommonConstant.SERVICE_TYPE_TO_SHOP), isAnonymous, serviceLeve,
                            shopScore, content, impressionList);
                    break;
            }
        }
    }

    private class MyTextWatcher implements TextWatcher {
        //最大建议字数
        int maxLength = 140;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            tvLimitNumber.setText(charSequence.length() + "/" + maxLength);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > maxLength) {
                editable.delete(maxLength, editable.length());
                showShortToast("您输入的字数超过限制");
            }
        }
    }


    private class MyCheckedListener implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
            if (checkId == rbGood.getId()) {
                serviceLeve = "1";
                impressionList.clear();//清楚之前已选择的标签
                tagFlowLayout.setAdapter(new myTagAdapter(commentBean.getGoodsImpressionList()));
            } else if (checkId == rbMedium.getId()) {
                serviceLeve = "2";
                impressionList.clear();//清楚之前已选择的标签
                tagFlowLayout.setAdapter(new myTagAdapter(commentBean.getMiddleImpressionList()));
            } else if (checkId == rbRates.getId()) {
                serviceLeve = "3";
                impressionList.clear();//清楚之前已选择的标签
                tagFlowLayout.setAdapter(new myTagAdapter(commentBean.getBadImpressionList()));
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            if (checked) {
                isAnonymous = "1";
            } else {
                isAnonymous = "0";
            }
        }
    }

    private class myRatingBarListener implements RatingBar.OnRatingBarChangeListener {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if ((int) ratingBar.getRating() == 0) {
                tvRatingBarDes.setText(ratingBarNotes[0]);
            } else if ((int) ratingBar.getRating() == 1) {
                tvRatingBarDes.setText(ratingBarNotes[1]);
            } else if ((int) ratingBar.getRating() == 2) {
                tvRatingBarDes.setText(ratingBarNotes[2]);
            } else if ((int) ratingBar.getRating() == 3) {
                tvRatingBarDes.setText(ratingBarNotes[3]);
            } else if ((int) ratingBar.getRating() == 4) {
                tvRatingBarDes.setText(ratingBarNotes[4]);
            } else if ((int) ratingBar.getRating() == 5) {
                tvRatingBarDes.setText(ratingBarNotes[5]);
            }
            shopScore = String.valueOf((int) ratingBar.getRating());
        }
    }

    private class myTagAdapter extends TagAdapter<String> {
        public myTagAdapter(List<String> datas) {
            super(datas);
        }

        @Override
        public View getView(FlowLayout parent, int position, String s) {
            TextView tv = (TextView) mInflater.inflate(R.layout.rated_tags, tagFlowLayout, false);
            tv.setText(s);
            return tv;
        }
    }

    private class myTagListener implements TagFlowLayout.OnTagClickListener {
        @Override
        public boolean onTagClick(View view, int position, FlowLayout parent) {
            TextView view1 = (TextView) view;
            if (impressionList.contains(view1.getText())) {
                impressionList.remove(view1.getText());//如果有,移除
                return true;
            }
            impressionList.add((String) view1.getText()); //添加
            return true;
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
