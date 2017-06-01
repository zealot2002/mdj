package com.mdj.moudle.mine.about;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.webview.BaseWebviewActivity;
import com.mdj.utils.CommonUtil;


/**
 * @author 吴世文
 * @Description: 关于我们
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvVX,tvCreateTime;
    private Button btnCopyVX;
    private TextView tvVersion;
    private RelativeLayout rlRecommend;
    private boolean iskitkat;
    private LinearLayout ll_status;

    @Override
    public void findViews() {
        setContentView(R.layout.activity_set_about);
        mContext = this;
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_status.setVisibility(View.VISIBLE);
        tvVX = (TextView) findViewById(R.id.tvVX);
        btnCopyVX = (Button) findViewById(R.id.btnCopyVX);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvCreateTime = (TextView) findViewById(R.id.tvCreateTime);
        rlRecommend = (RelativeLayout) findViewById(R.id.rlRecommend);
        btnCopyVX.setOnClickListener(this);
        rlRecommend.setOnClickListener(this);

        tvVersion.setText("V"+ CommonUtil.getAppVersionName(mContext));
        tvCreateTime.setText(CacheManager.getInstance().getVersionBean().getBundle().getCreateTime());//发版日期
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCopyVX:
                CommonUtil.copyText(tvVX.getText().toString().trim(), getApplicationContext());
                showTips(mContext,R.mipmap.tips_success,"已复制到剪切板");
                break;
            case R.id.rlRecommend:
                Intent in3 = new Intent(this, BaseWebviewActivity.class);
                in3.putExtra("url", HttpConstant.URL_ABOUT);
                in3.putExtra("title", "介绍我们");
                startActivity(in3);
                break;
        }
    }
}
