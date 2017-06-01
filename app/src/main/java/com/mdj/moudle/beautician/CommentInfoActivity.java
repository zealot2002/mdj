package com.mdj.moudle.beautician;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;

import com.alipay.sdk.util.LogUtils;
import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseFlatActivity;
import com.mdj.moudle.beautician.bean.BeauticianCommentNumBean;
import com.mdj.moudle.beautician.bean.CommentBean;
import com.mdj.moudle.beautician.presenter.CommentInfoContract;
import com.mdj.moudle.beautician.presenter.CommentInfoPresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.ListViewUtils;
import com.mdj.utils.TextViewUtil;
import com.mdj.view.ListenedScrollView;
import com.mdj.view.NestedListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class CommentInfoActivity extends BaseFlatActivity implements CommentInfoContract.View,
        View.OnClickListener, ListenedScrollView.OnScrollListener {
    enum ButtonSelect{
        ButtonSelectAll,
        ButtonSelectGood,
        ButtonSelectMiddle,
        ButtonSelectBad,
        ButtonSelectNone
    }
    private ButtonSelect currentSelect = ButtonSelect.ButtonSelectNone;
//    private

    private NestedListView lvCommentList;
    private List<CommentBean> dataList;
    private CommentListAdapter adapter;
//    private ListenedScrollView observableScrollView;

	private Button btnAll,btnGood,btnMiddle,btnBad;

    private String beauticianId;
    private CommentInfoContract.Presenter presenter;
    /**********************************************************************************************************/
    @Override
    public void findViews(){
        mContext = this;
        setTitle("评价详情");
        beauticianId = getIntent().getStringExtra(BundleConstant.BEAUTICIAN_ID);
        presenter = new CommentInfoPresenter(this);
        presenter.getAllData(beauticianId);
    }

    public void findAllViews() {
        if(btnAll!=null){
            return;
        }
        setContentView(R.layout.comment_info);

        btnAll = (Button) findViewById(R.id.btnAll);
        btnAll.setOnClickListener(this);

        btnGood = (Button) findViewById(R.id.btnGood);
        btnGood.setOnClickListener(this);

        btnMiddle = (Button) findViewById(R.id.btnMiddle);
        btnMiddle.setOnClickListener(this);

        btnBad = (Button) findViewById(R.id.btnBad);
        btnBad.setOnClickListener(this);

        lvCommentList = (NestedListView) findViewById(R.id.lvCommentList);
        adapter = new CommentListAdapter(this);
        lvCommentList.setAdapter(adapter);
        lvCommentList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount) {
                    if (totalItemCount == 0) {
                        return;
                    }
                    View lastItemView = (View) listView.getChildAt(listView.getChildCount() - 1);
                    if ((listView.getBottom()) == lastItemView.getBottom()) {
                        presenter.loadMoreData(getCurrentCommentType());
                    }
                }
            }
        });
    }

    @Override
    protected void refreshAllData(){
        presenter.getAllData(beauticianId);
    }
    @Override
    public void showDisconnect(String msg) {
        super.showDisconnect();
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        findAllViews();
        BeauticianCommentNumBean bean = (BeauticianCommentNumBean)data;
        btnGood.setText("好评("+bean.getGoodNum()+")");
        btnMiddle.setText("中评(" + bean.getMiddleNum() + ")");
        btnBad.setText("差评(" + bean.getBadNum() + ")");
        updateButton(ButtonSelect.ButtonSelectAll);
    }
    @Override
    public void refreshCommentList(int commentType,List<CommentBean> data){
        findAllViews();
        if(commentType == getCurrentCommentType()){
            if(data.isEmpty()&&lvCommentList.getEmptyView()==null){
                lvCommentList.setEmptyView(ListViewUtils.getEmptyView(mContext,"没有数据",lvCommentList));
            }
            adapter.setDataList(data);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void setPresenter(Object presenter) {

    }

	@Override
	public void onClick(View v) {
		LogUtils.d(v.getId() + "");
		switch (v.getId()) {
            case R.id.btnAll:
                updateButton(ButtonSelect.ButtonSelectAll);
                presenter.getBeauticianCommentListPre(CommonConstant.COMMENT_TYPE_ALL);
                break;

            case R.id.btnGood:
                updateButton(ButtonSelect.ButtonSelectGood);
                presenter.getBeauticianCommentListPre(CommonConstant.COMMENT_TYPE_GOOD);
                break;

            case R.id.btnMiddle:
                updateButton(ButtonSelect.ButtonSelectMiddle);
                presenter.getBeauticianCommentListPre(CommonConstant.COMMENT_TYPE_MIDDLE);
                break;

            case R.id.btnBad:
                updateButton(ButtonSelect.ButtonSelectBad);
                presenter.getBeauticianCommentListPre(CommonConstant.COMMENT_TYPE_BAD);
                break;

            case R.id.llDisconnectTipsLayout:
                presenter.getAllData(beauticianId);
                break;

            default:
                break;
		}
        super.onClick(v);
	}

    private void updateButton(ButtonSelect select) {
        if(currentSelect == select)
            return;
        int specialStart = 2;
        String s = null;
        //clear all

        btnGood.setBackgroundResource(R.drawable.tag_bg);
        s = btnGood.getText().toString().trim();
        TextViewUtil.setSpecialTextColor(btnGood,
                s,
                getResources().getColor(R.color.text_black),
                getResources().getColor(R.color.text_gray),
                specialStart,
                s.length()
        );

        btnMiddle.setBackgroundResource(R.drawable.tag_bg);
        s = btnMiddle.getText().toString().trim();
        TextViewUtil.setSpecialTextColor(btnMiddle,
                s,
                getResources().getColor(R.color.text_black),
                getResources().getColor(R.color.text_gray),
                specialStart,
                s.length()
        );

        btnBad.setBackgroundResource(R.drawable.tag_bg);
        s = btnBad.getText().toString().trim();
        TextViewUtil.setSpecialTextColor(btnBad,
                s,
                getResources().getColor(R.color.text_black),
                getResources().getColor(R.color.text_gray),
                specialStart,
                s.length()
        );

        btnAll.setBackgroundResource(R.drawable.tag_bg);
        btnAll.setTextColor(getResources().getColor(R.color.text_black));

        //赋值
        currentSelect = select;

        switch (select){
            case ButtonSelectGood:
                btnGood.setBackgroundResource(R.drawable.black_round_bg);
//                btnGood.setTextColor(getResources().getColor(R.color.white)); doesn't work
                s = btnGood.getText().toString().trim();
                TextViewUtil.setSpecialTextColor(btnGood,
                        s,
                        getResources().getColor(R.color.white),
                        getResources().getColor(R.color.white),
                        specialStart,
                        s.length()
                );
                break;
            case ButtonSelectMiddle:
                btnMiddle.setBackgroundResource(R.drawable.black_round_bg);
                s = btnMiddle.getText().toString().trim();
                TextViewUtil.setSpecialTextColor(btnMiddle,
                        s,
                        getResources().getColor(R.color.white),
                        getResources().getColor(R.color.white),
                        specialStart,
                        s.length()
                );
                break;
            case ButtonSelectBad:
                btnBad.setBackgroundResource(R.drawable.black_round_bg);
                s = btnBad.getText().toString().trim();
                TextViewUtil.setSpecialTextColor(btnBad,
                        s,
                        getResources().getColor(R.color.white),
                        getResources().getColor(R.color.white),
                        specialStart,
                        s.length()
                );
                break;
            case ButtonSelectAll:
                btnAll.setBackgroundResource(R.drawable.black_round_bg);
                btnAll.setTextColor(getResources().getColor(R.color.white));
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
    public void onBottomArrived() {

    }

    @Override
    public void onScrollStateChanged(ListenedScrollView view, int scrollState) {
        if(ListenedScrollView.OnScrollListener.SCROLL_STATE_IDLE == scrollState){
            if(ListViewUtils.isBottom(lvCommentList)){
                presenter.loadMoreData(getCurrentCommentType());
            }
        }
    }

    private int getCurrentCommentType(){
        switch (currentSelect){
            case ButtonSelectGood:return CommonConstant.COMMENT_TYPE_GOOD;
            case ButtonSelectMiddle:return CommonConstant.COMMENT_TYPE_MIDDLE;
            case ButtonSelectBad:return CommonConstant.COMMENT_TYPE_BAD;
            case ButtonSelectAll:return CommonConstant.COMMENT_TYPE_ALL;
            default:
                break;
        }
        return CommonConstant.COMMENT_TYPE_ALL;
    }
    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {

    }
}
