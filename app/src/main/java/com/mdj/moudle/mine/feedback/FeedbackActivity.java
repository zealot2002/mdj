package com.mdj.moudle.mine.feedback;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.mine.feedback.presenter.FeedBackContract;
import com.mdj.moudle.mine.feedback.presenter.FeedBackPresenter;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("HandlerLeak")
public class FeedbackActivity extends BaseActivity implements View.OnClickListener, FeedBackContract.View {
	private EditText etContent;
	private Button btnOk;
    private TitleWidget titleWidget;
	private FeedBackContract.Presenter presenter;
	private boolean iskitkat;
	private LinearLayout ll_status;

	@Override
	public void findViews() {
		setContentView(R.layout.feedback);
		mContext = this;
		presenter = new FeedBackPresenter(this);
		ll_status = (LinearLayout) findViewById(R.id.ll_status);
		etContent = (EditText) findViewById(R.id.etContent);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOk:
			String userId = CacheManager.getInstance().getUserBean().getId();
			String feedBack = etContent.getText().toString().trim();
			if(TextUtils.isEmpty(userId)){//没登录
				showShortToast("请登录");
				return;
			}
			if (TextUtils.isEmpty(feedBack)) {  //没填写意见
                showShortToast("请输入内容");
				return;
			}
			presenter.sendFeedBack(userId,feedBack);
			break;

		}
	}

	@Override
	public void showDisconnect(String msg) {
		showShortToast(msg);
	}

	@Override
	public void updateUI(Object data) {
        showShortToast(getResources().getString(R.string.feeddack_success));
        finish();
	}

	@Override
	public void setPresenter(Object presenter) {

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
