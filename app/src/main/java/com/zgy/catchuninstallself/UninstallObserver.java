package com.zgy.catchuninstallself;

/**
 * C代码参考网上资料进行修改的，国外开源代码
 * 
 * @Title: UninstallObserver.java
 * @Package com.zgy.catchuninstallself
 * @Description:
 * @author Jalen c9n9m@163.com
 * @date 2015年5月11日 下午5:36:06
 * @version V1.0
 * 
 * 调用方法：
 * TODO// UninstallObserver.startWork("/data/data/" + getPackageName(),
 * // "http://emeidaojia.com/", android.os.Build.VERSION.SDK_INT);
 */
public class UninstallObserver {

	static {
		System.loadLibrary("observer");
	}

	public static native String startWork(String path, String url, int version);// path：data/data/[packageNmae]
																				// ；
																			// url:跳转的页面，需要http://或https://开头
}
