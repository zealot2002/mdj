package com.mdj.moudle.mine.setting;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.constant.SPConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.mine.about.AboutActivity;
import com.mdj.moudle.webview.BaseWebviewActivity;
import com.mdj.utils.MdjLog;
import com.mdj.utils.SPUtils;
import com.push.PushHelper;

/**
 * @author 吴世文
 * @Description: 设置界面
 */
public class SetActivity extends BaseActivity implements View.OnClickListener {

    private boolean iskitkat;
    private LinearLayout ll_status;
    private ToggleButton infrom;
    private RelativeLayout common_problem;
    private RelativeLayout terms_service;
    private RelativeLayout promise;
    private RelativeLayout grade;
    private RelativeLayout about;
    private TextView log_out;


    @Override
    public void findViews() {
        setContentView(R.layout.activity_setting);
        mContext = this;
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_status.setVisibility(View.VISIBLE);
        infrom = (ToggleButton) findViewById(R.id.infrom);
        common_problem = (RelativeLayout) findViewById(R.id.common_problem);
        terms_service = (RelativeLayout) findViewById(R.id.terms_service);
        promise = (RelativeLayout) findViewById(R.id.promise);
        grade = (RelativeLayout) findViewById(R.id.grade);
        about = (RelativeLayout) findViewById(R.id.about);
        log_out = (TextView) findViewById(R.id.log_out);
        if (!TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())) {
            log_out.setVisibility(View.VISIBLE);
        }
        infrom.setChecked(SPUtils.getBoolean(mContext,SPConstant.OPEN_PUSH,true));//默认是接受Push消息
        infrom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setOpenC(isChecked);
            }
        });
        common_problem.setOnClickListener(this);
        terms_service.setOnClickListener(this);
        promise.setOnClickListener(this);
        grade.setOnClickListener(this);
        about.setOnClickListener(this);
        log_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.common_problem:// 常见问题
                gotoWeb(HttpConstant.URL_COMMON_QA, "常见问题解答");
                break;
            case R.id.terms_service:
                gotoWeb(HttpConstant.URL_AGREEMENT, "服务条款");
                break;
            case R.id.promise:// 承诺书
                gotoWeb(HttpConstant.URL_PROMISE, "保险承诺书");
                break;
            case R.id.grade:// 评分
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.about:
                Intent in = new Intent(mContext, AboutActivity.class);
                startActivity(in);
                break;
            case R.id.log_out:
                logout();
                break;
        }
    }

    private void logout() {
        showShortToast("注销成功");
        CacheManager.getInstance().clearUserCache();
        SPUtils.putString(mContext, SPConstant.TOKEN, "");

        log_out.setVisibility(View.INVISIBLE);
        finish();
    }

    /**
     * 跳转到web页面
     */
    private void gotoWeb(String url, String title) {
        Intent in = new Intent(this, BaseWebviewActivity.class);
        in.putExtra("url", url);
        in.putExtra("title", title);
        startActivity(in);
    }
    private void setOpenC(boolean isChecked) {
        if (isChecked) {
            MdjLog.log("isChecked :"+isChecked);
            PushHelper.getInstance().start();
            SPUtils.putBoolean(mContext, SPConstant.OPEN_PUSH, true);
        } else {
            PushHelper.getInstance().stop();
            MdjLog.log("isChecked :"+isChecked);
            SPUtils.putBoolean(mContext, SPConstant.OPEN_PUSH, false);
        }
    }
}
