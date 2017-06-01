package com.mdj.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
//	没有小数点，返回原串
//	有小数点，小数点之前没有字符串，返回原串
//	有小数点，小数点之前有字符串，返回此字符串
	public static String getPriceInt(final String s){
		if(s==null)
			return s;
		if(!s.contains("."))
			return s;
		
		int i = s.lastIndexOf(".");
		if(i==0)
			return s;
		return s.substring(0, i);
	}

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
