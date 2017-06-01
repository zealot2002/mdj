package com.mdj.moudle;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.utils.MdjLog;
import com.mdj.view.toast.TipsToast;

public abstract class BaseFragment extends Fragment{
	public View view;
	private Dialog loadingDialog = null;//new dialog
    public Context mContext;
    public static TipsToast tipsToast;

/*****************************************************************************************************/
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
	}

    public void showLoading(){
        showLoading(mContext,"加载中...");
    }
	/** 打开进度条 */
    protected void showLoading(Context context, String message) {
        MdjLog.logE("BaseActivity loadingDialog", "showLoading ");
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局

        if (loadingDialog == null) {
            loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
            loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        }
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.show();
	}

    public void closeLoading() {
        MdjLog.logE("BaseActivity", "closeLoading ");
        try{
            if (loadingDialog != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
	}
    protected void showTips(Context context, int iconResId, String tips) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(context, tips, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(tips);
	}
}
