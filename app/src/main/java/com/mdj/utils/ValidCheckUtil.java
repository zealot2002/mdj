package com.mdj.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidCheckUtil {
	
	public static boolean isChinese(String strName) { 
		try {
			Pattern pattern = Pattern.compile("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]){2,6}$");
	    	Matcher matcher = pattern.matcher(strName);
	    	if(matcher.find()){
	    		return true;
	    	}
		} catch (Exception e) {
			return false;
		}
    	return false;
    } 
	
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static boolean checkUrl(String url){ 
		return url.matches("^((https|http|ftp|rtsp|mms)?://)" 
		     + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" 
		     + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" 
		     + "|" 
		     + "([0-9a-z_!~*'()-]+\\.)*" 
		     + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." 
		     + "[a-z]{2,6})" 
		     + "(:[0-9]{1,4})?" 
		     + "((/?)|" 
		     + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"); 
		}
	
	/**
	 * 验证手机号码
	 * 
	 * @param mobiles
	 * @return [0-9]{5,9}
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^(13|14|15|17|18)[0-9]{9}$");
//					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static boolean isNum(String number) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^[0-9]{5}$");
			Matcher m = p.matcher(number);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static boolean checkPassword(String password) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$");
			Matcher m = p.matcher(password);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	//提示：用户名只可包含中英文、数字、下划线，最长10个字符。
	public static boolean isValidUsername(String username){
		boolean flag = false;
		try{
			Pattern p = Pattern.compile("^([a-zA-Z0-9_\u4e00-\u9fa5]){1,10}$");
			Matcher m = p.matcher(username);
			flag = m.matches();
		}catch (Exception e){
			flag = false;
		}
		return flag;
	}
}
