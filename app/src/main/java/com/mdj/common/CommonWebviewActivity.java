package com.mdj.common;

import android.os.Build;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.UMConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.MdjUtils;
import com.mdj.view.ProgressWebView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

//首页活动模块专用webactivity
public class CommonWebviewActivity extends BaseActivity implements View.OnClickListener {
    private ProgressWebView webview;
    private TitleWidget titleWidget;
    private String flag;
    private String url;
    private String imageUrl;
    private String targetUrl;
    private String title;
    private String content;

    @Override
    public void findViews() {
        setContentView(R.layout.common_webview);
        titleWidget = (TitleWidget) findViewById(R.id.titleWidget);
        titleWidget.setTitle("美道家");
        titleWidget.setCurrentScreen(TitleWidget.SCREEN_ID.SCREEN_SHARE);
        titleWidget.setShareListener(this);
        LinearLayout ll_status = (LinearLayout) findViewById(R.id.ll_status);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            ll_status.setVisibility(View.VISIBLE);
        initData();
    }

    public void initData() {
        flag = getIntent().getStringExtra(CommonConstant.FLAG);

        if (flag.equals(CommonConstant.SHARE_BANNER)) { // 0是 banner 分享
            url = getIntent().getStringExtra(BundleConstant.LINK_URL);
            imageUrl = getIntent().getStringExtra(BundleConstant.IMAGE_URL);
            targetUrl = getIntent().getStringExtra(BundleConstant.TARGET_URl);
            title = getIntent().getStringExtra(BundleConstant.TITLE);
            content = getIntent().getStringExtra(BundleConstant.CONTENT);
        } else if (flag.equals(CommonConstant.SHARE_PUSH)) { // 2是 push 分享
            url = getIntent().getStringExtra(BundleConstant.LINK_URL);
            imageUrl = getIntent().getStringExtra(BundleConstant.IMAGE_URL);
            targetUrl = getIntent().getStringExtra(BundleConstant.TARGET_URl);
            title = getIntent().getStringExtra(BundleConstant.TITLE);
            content = getIntent().getStringExtra(BundleConstant.CONTENT);
        }

        webview = (ProgressWebView) findViewById(R.id.webview);
        webview.getSettings().setUserAgentString(MdjUtils.getMdjUserAgent(this));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        // 设置cookie
        MdjUtils.synCookies(mContext, url);

        webview.loadUrl(url);
        MobclickAgent.onEvent(this, UMConstant.UM_EVENT_BROWSE_ACTIVITY_INFO, url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMenuShare:
                share();
                break;
        }
    }

    private void share() {
        UMImage image = new UMImage(mContext, imageUrl);
        new ShareAction(this).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                .withText(title)
                .withMedia(image)
                .withTargetUrl(targetUrl)
                .setCallback(umShareListener)
                .open();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat", "platform" + platform);
            Toast.makeText(mContext, "分享成功啦", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, "分享失败啦", Toast.LENGTH_SHORT).show();
            finish();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, "分享取消啦", Toast.LENGTH_SHORT).show();
        }
    };


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
}
