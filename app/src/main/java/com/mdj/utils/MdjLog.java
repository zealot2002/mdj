package com.mdj.utils;

import android.util.Log;

import com.lidroid.xutils.util.OtherUtils;

public class MdjLog {
	public static int logD(String tag,String msg){
		return Log.d(tag,msg);
//		return 0;
	}
	
	public static int logE(String tag,String msg){
		return Log.e(tag, msg);
//		return 0;
	}

	public static int log(String msg){
        StackTraceElement caller = OtherUtils.getCallerStackTraceElement();
        String tag = generateTag(caller);
        return Log.e(tag,msg);
    }
    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, new Object[]{callerClazzName, caller.getMethodName(), Integer.valueOf(caller.getLineNumber())});
        return tag;
    }
}
