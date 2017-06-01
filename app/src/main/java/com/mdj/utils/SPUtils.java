package com.mdj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Title: SharedPreferencesUtils.java
 * @Package com.mdj.utils
 * @Description:保存本地文件SharePregerences的工具类
 * @author hwk
 * @date 2015-3-24 下午2:11:29
 * @version V1.0
 */
public class SPUtils {

	private final static String CONFIG = "config";
	private static SharedPreferences sp;

	public static void putBoolean(Context context, String key, boolean defaultvalue) {
		if (sp == null) {
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, defaultvalue).commit();
	}

	public static boolean getBoolean(Context context, String key, boolean defaultvalue) {
		if (sp == null) {
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defaultvalue);
	}

	public static void putString(Context context, String key, String defaultvalue) {
		if (sp == null) {
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, defaultvalue).commit();
	}

	public static String getString(Context context, String key, String defaultvalue) {
		if (sp == null) {
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sp.getString(key, defaultvalue);
	}

	public static void putInt(Context context, String key, int defaultvalue) {
		if (sp == null) {
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, defaultvalue).commit();
	}

	public static int getInt(Context context, String key, int defaultvalue) {
		if (sp == null) {
			sp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defaultvalue);
	}
}
