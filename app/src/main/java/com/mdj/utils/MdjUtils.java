package com.mdj.utils;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.userPackage.PackageBean;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Title: MdjUtils.java
 * @Package com.mdj.utils
 * @Description: 专用帮助类
 * @author Jalen c9n9m@163.com
 * @date 2015年3月27日 下午8:52:31
 * @version V1.0
 */
public class MdjUtils {
    public static int getCurrentPackagePrice(final PackageBean bean,StringBuilder isNormal){
        int price = bean.getPrice();
        if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_NORMAL)){
            price = bean.getPrice();
            isNormal.append(true);
        }else if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_PRIVILEGE)){
            price = bean.getPrivilegePrice();
            isNormal.append(false);
        }else{
            try{
                String now = DateUtil.getCurrentTime();
                if(DateUtil.compareDate(now, bean.getStartTime())==-1/*当前时间小于开始时间、未开始*/
                        ||DateUtil.compareDate(now,bean.getEndTime())==1/*当前时间大于结束时间、已结束*/
                        ){
                    price = bean.getPrice();
                    isNormal.append(true);
                }else{
                    int soldNum = Integer.valueOf(bean.getSoldNum());
                    int totalNum = Integer.valueOf(bean.getTotal());
                    if(soldNum<totalNum){//没有抢完
                        price = bean.getPrivilegePrice();
                        isNormal.append(false);
                    }else{//倒计时中，但是已经抢完
                        price = bean.getPrice();
                        isNormal.append(true);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showLong(MyApp.getInstance(),"getCurrentProjectPrice err:"+e.toString());
            }
        }
        return price;
    }

    public static int getCurrentProjectPrice(final ProjectBean bean,StringBuilder isNormal){
        int price = bean.getPrice();
        if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_NORMAL)){
            price = bean.getPrice();
            isNormal.append(true);
        }else if(bean.getType().equals(CommonConstant.PROJECT_OR_PACKAGE_TYPE_PRIVILEGE)){
            price = bean.getPrivilegePrice();
            isNormal.append(false);
        }else{
            try{
                String now = DateUtil.getCurrentTime();
                if(DateUtil.compareDate(now, bean.getStartTime())==-1/*当前时间小于开始时间、未开始*/
                        ||DateUtil.compareDate(now,bean.getEndTime())==1/*当前时间大于结束时间、已结束*/
                        ){
                    price = bean.getPrice();
                    isNormal.append(true);
                }else{
                    int soldNum = Integer.valueOf(bean.getSoldNum());
                    int totalNum = Integer.valueOf(bean.getTotal());
                    if(soldNum<totalNum){//没有抢完
                        price = bean.getPrivilegePrice();
                        isNormal.append(false);
                    }else{//倒计时中，但是已经抢完
                        price = bean.getPrice();
                        isNormal.append(true);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showLong(MyApp.getInstance(),"getCurrentProjectPrice err:"+e.toString());
            }
        }
        return price;
    }
    public static boolean isOver6hours(List<GoodsWrapperBean> goodsList){
        int time = 0;
        for(GoodsWrapperBean bean:goodsList){
            time+=bean.getGoodsBean().getDuration()*bean.getNum();
        }
        return time>6*60;
    }
    public static boolean isOnlyExtraProject(List<GoodsWrapperBean> goodsList){
        if(goodsList.size()==1&&goodsList.get(0).getNum()==1){
            return goodsList.get(0).getGoodsBean().isExtraProject();
        }
        return false;
    }
	public static LinkedHashMap<String, String> listToMap(List<NameValuePair> list) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		try {
			for (int i = 0; i < list.size(); i++) {
				NameValuePair nameValuePair = list.get(i);
				String name = nameValuePair.getName();
				String value = nameValuePair.getValue();
				map.put(name, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

    public static List<NameValuePair> mapToList(Map<String,String> map){
        List<NameValuePair> list = new ArrayList<>();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            list.add(new NameValuePair() {
                @Override
                public String getName() {
                    return entry.getKey();
                }
                @Override
                public String getValue() {
                    return entry.getValue();
                }
            });
        }
        return list;
    }
	/**
	 * get请求的时候获取http请求的头信息Auth
	 */
	public static String getMDJAuth(final LinkedHashMap<String, String> fMap) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.putAll(fMap);
		// 拼接
		String mdjAuth = "";
		try {
			String deviceType = Constant.DEVICE_TYPE;
			String imei = DeviceInfoUtil.getImei(MyApp.getInstance());
			String timestamp = MdjUtils.getTimes();

			map.put("deviceType", deviceType);
			map.put("imei", imei);
			map.put("timestamp", timestamp);

			MdjUtils.sortMap(map);// 排序
			String sign = MdjUtils.getSign(map);// 签名
			mdjAuth = "deviceType=" + deviceType + "&imei=" + imei + "&sign=" + sign + "&timestamp=" + timestamp;
		} catch (Exception e) {
			e.printStackTrace();
			mdjAuth = "MDJAuth";
		}
		return mdjAuth;
	}

	/** 对LinkHashMap集合进行排序并输出 **/
	public static LinkedHashMap<String, String> sortMap(LinkedHashMap<String, String> map) {
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
		try {
			// 排序
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});

			map.clear();
			// 排序后
			for (int i = 0; i < infoIds.size(); i++) {
				String key = infoIds.get(i).getKey();
				String value = infoIds.get(i).getValue();
				map.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	/** get请求拼接URL **/
	public static String jointUrl(LinkedHashMap<String, String> params, String url) {
		try {
			// 添加url参数
			if (params != null) {
				Iterator<String> it = params.keySet().iterator();
				StringBuffer sb = null;
				while (it.hasNext()) {
					String key = it.next();
					String value = params.get(key);
					if (sb == null) {
						sb = new StringBuffer();
						sb.append("?");
					} else {
						sb.append("&");
					}
					sb.append(key);
					sb.append("=");
					sb.append(value);
				}
				url += sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
	public static String getTimes() {
		String s = "";
		String cm = System.currentTimeMillis() + "";
		s = cm.substring(0, 10);
		return s;
	}
	public static String getSign(LinkedHashMap<String, String> map) {
		String s = "";
		if (map != null && map.size() > 0) {
			String replace = MdjUtils.transMapValueTostr(map);
			s = Md5Utils.MD5(replace);
			StringBuilder sb = new StringBuilder();
			sb.append(s.charAt(2)).append(s.charAt(6)).append(s.charAt(10)).append(s.charAt(12)).append(s.charAt(16)).append(s.charAt(18)).append(s.charAt(22))
					.append(s.charAt(28));
			s = sb.toString();
		}
		return s;
	}
	public static String transMapValueTostr(LinkedHashMap<String, String> map) {
		String s = "";
		StringBuilder sb = new StringBuilder();
		if (map != null && map.size() > 0) {
			for (Entry<String, String> en : map.entrySet()) {
				sb.append(en.getValue()).append("@");
			}
			s = sb.toString();
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}
    static String ua = null;
	public static String getMdjUserAgent(Context context) {
        if(ua!=null){
            MdjLog.log("ua:["+ua+"]");
            return ua;
        }
        int versionCode = CommonUtil.getAppVersionCode(context);    //版本号
        String versionName = CommonUtil.getAppVersionName(context); //版本名字
        try {
            ua = DeviceInfoUtil.getDeviceInfo(context) + "|MDJ_ANDROID_USER" + " V" + versionName+ "_" + versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ua;
	}

	/**
	 * 设置cookie
	 */
	public static void synCookies(Context context, String url) {
		// 设置cookie
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		// cookieManager.removeAllCookie();

		cookieManager.setCookie(url, "_cityId=" + CacheManager.getInstance().getGlobalCity().getId() + ";");
		cookieManager.setCookie(url, "_uid=" + CacheManager.getInstance().getUserBean().getId() + ";");
		CookieSyncManager.getInstance().sync();
	}
}
