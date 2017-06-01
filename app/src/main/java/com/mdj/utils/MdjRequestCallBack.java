package com.mdj.utils;


/**
 * 网络请求回调接口
* @Title: MdjRequest.java 
* @Package com.mdj.utils 
* @Description: 
* @author Jalen  c9n9m@163.com  
* @date 2015年4月17日 下午1:54:40 
* @version V1.0
 */
public interface MdjRequestCallBack {
	void onSuccess(String response);

	void onError(String error);
}
