package com.mdj.moudle.mine.share;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

/**
 * @author 吴世文
 * @Description: 红包分享
 */
public class RedPacketsShareActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnVX, btnCF;
    private ImageButton btnClose;
    private String targetUrl;
    private String projectName;


    @Override
    public void findViews() {
        setContentView(R.layout.share_acitivty);
        mContext = this;
        btnVX = (ImageButton) findViewById(R.id.btnVX);
        btnCF = (ImageButton) findViewById(R.id.btnCF);
        btnClose = (ImageButton) findViewById(R.id.btnClose);

        targetUrl = HttpConstant.RED_PACKETS_TARGET_URL + getIntent().getStringExtra(BundleConstant.ORDER_ID);
        projectName = getIntent().getStringExtra(BundleConstant.PROJCET_NAME);

        btnVX.setOnClickListener(this);
        btnCF.setOnClickListener(this);
        btnClose.setOnClickListener(this);

    }


    // 标题：我帮你抢了个大红包，银子不够，红包来凑！
    // 简介：我做了 ”项目名称” 感觉美美哒~还有红包拿，快来试试。
    // 缩略图：美道家Logo
    // url：领取红包页面url

    private String title = "我帮你抢了个大红包，银子不够，红包来凑！";
    private String con1 = "我做了 “";
    private String con2 = "” 感觉美美哒~还有红包拿，快来试试。美道家-女神最爱用的上门美容神器 ";
    private String picUrl = "http://static.emeidaojia.com/static/wx_share.jpg";
    private String target = "http://m.emeidaojia.com/Static/appDistributionRedShow/orderSn/";
//  private String pName = "";

    UMImage image = new UMImage(RedPacketsShareActivity.this, HttpConstant.RED_PACKETS_PIC_URL);

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnVX:
                shareToWeiXin(); //微信好友
                break;
            case R.id.btnCF:
                shareToWeiXin_Circle(); //微信朋友圈
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }

    private void shareToWeiXin_Circle() {
        new ShareAction(this)
                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .withText(con1 + projectName + con2)
                .withTargetUrl(targetUrl)
                .withMedia(image)
                .withTitle(title)
                .setCallback(umShareListener)
                .share();
    }

    private void shareToWeiXin() {
        new ShareAction(this)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .withText(con1 + projectName + con2)
                .withTargetUrl(targetUrl)
                .withMedia(image)
                .withTitle(title)
                .setCallback(umShareListener)
                .share();
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
            finish();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, "分享取消啦", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MobclickAgent.onPageStart(CommonUtil.generateTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPageEnd(CommonUtil.generateTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
