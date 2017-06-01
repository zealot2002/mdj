package com.mdj.moudle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.beautyParlor.BeautyParlorDetailActivity;
import com.mdj.utils.ToastUtils;
import com.mdj.view.toast.TipsToast;

public abstract class BaseActivity extends FragmentActivity {
    private Dialog loadingDialog;
    public Context mContext;

    public static TipsToast tipsToast;
    public abstract void findViews();

/***************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        findViews();
        MyApp.instance.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void showShortToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

    public void showLoading() {
        showLoading(mContext,"加载中...");
    }
    /** 打开进度条 */
    protected void showLoading(Context context, String message) {
        if(loadingDialog!=null&&loadingDialog.isShowing()){
            return ;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局

        if (loadingDialog == null) {
            loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
            loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        loadingDialog.setContentView(layout,params);// 设置布局

        loadingDialog.show();
    }

    public void closeLoading() {
        try{
            if (loadingDialog != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showTips(Context mContext, int iconResId, String tips) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }
        } else {
            tipsToast = TipsToast.makeText(mContext, tips, TipsToast.LENGTH_SHORT);
        }
        tipsToast.show();
        tipsToast.setIcon(iconResId);
        tipsToast.setText(tips);
    }

    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (CommonConstant.REQUEST_CODE.SCAN_QRCODE.value() == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String result = data.getExtras().getString("result");
                    if(!result.contains("homeId")){
                        showShortToast("请扫描美道家门店二维码");
                        return;
                    }
                    int targetP = result.lastIndexOf("/");
                    String beautyParlorId = result.substring(targetP+1, result.length());
                    Intent intent = new Intent(mContext, BeautyParlorDetailActivity.class);
                    intent.putExtra(BundleConstant.BEAUTY_PARLOR_ID,beautyParlorId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showLong(mContext, "BaseActivity onActivityResult:" + e.toString());
                }
            }
        }
    }

    public void showError(String msg){

    }
}
