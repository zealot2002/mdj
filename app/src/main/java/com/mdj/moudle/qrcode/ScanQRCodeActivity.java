package com.mdj.moudle.qrcode;


import android.content.Intent;
import android.os.Vibrator;
import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.utils.MdjLog;
import com.mdj.utils.ToastUtils;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanQRCodeActivity extends BaseActivity implements QRCodeView.Delegate {
    private QRCodeView mQRCodeView;
    private TitleWidget titleWidget;
    private String tagStr;
/**********************************************************************************************************/
    @Override
    public void findViews() {
        MdjLog.log("setMyContentView");
        mContext = this;
        setContentView(R.layout.scan_qrcode);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(ScanQRCodeActivity.this);
        titleWidget = (TitleWidget)findViewById(R.id.titleWidget);
        titleWidget.setCurrentScreen(TitleWidget.SCREEN_ID.SCREEN_SCAN);

        tagStr = getIntent().getStringExtra(BundleConstant.TAG_STR);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        MdjLog.log("result:" + result);
//        vibrate();
//        mQRCodeView.startSpot();
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", result);
        intent.putExtra(BundleConstant.TAG_STR,tagStr);
        //设置返回数据
        setResult(RESULT_OK, intent);
        //关闭Activity
        finish();

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        MdjLog.log("打开相机出错");
        ToastUtils.showLong(mContext,"打开相机出错");
    }

}