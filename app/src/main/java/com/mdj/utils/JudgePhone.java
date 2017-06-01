package com.mdj.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mdj.R;
import com.mdj.view.toast.TipsToast;

import android.content.Context;
import android.os.Build;

/**
 * @Title: JedgePhone.java
 * @Package com.mdj.utils
 * @Description: 判断手机号的正确性
 * @author hwk
 * @date 2015-4-10 上午11:43:25
 * @version V1.0
 */
public class JudgePhone {
	
	public  static TipsToast tipsToast;
	
	/** 判断电话号码的正确性和位数，合法性 */
	public static boolean jedge(Context mContext, String number) {
		boolean a=false;
		if (number.length() < 11) {
			a = false;
			showTips(mContext, R.mipmap.tips_warning, "您输入的手机号长度不够");
		} else {
			if (isMobileNO(number)) {
				a = true;
			} else {
				a = false;
				showTips(mContext, R.mipmap.tips_warning, "手机号格式不对");
			}
		}
		return a;
	}

	/** 判断手机号的合法性 */
	private static boolean isMobileNO(String mobiles) {
		//String regExp = "^[1]([3][0-9]{1}|47|50|51|52|53|55|56|57|58|59|76|77|80|81|82|83|85|86|87|88|89)[0-9]{8}$";
		String regExp = "^[1][0-9]{10}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(mobiles);
		return m.find();
	}
	
	/**
	 * 自定义toast
	 * 
	 * @param iconResId
	 *            图片
	 * @param msgResId
	 *            提示文字
	 */
	public static void showTips(Context context,int iconResId, String tips) {
		
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(context,tips, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(tips);
	}
}
