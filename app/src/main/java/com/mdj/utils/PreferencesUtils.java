package com.mdj.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import com.lidroid.xutils.util.LogUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

public class PreferencesUtils {

    public static void setStringPreferences(Context context, String preference, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringPreference(Context context, String preference, String key, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void setLongPreference(Context context, String preference, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLongPreference(Context context, String preference, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }
    public static void setObject(Context context,String preference, String key, Object object) throws IOException{  
        SharedPreferences preferences = context.getSharedPreferences(preference,Context.MODE_PRIVATE);
        // 创建字节输出流  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {  
            // 创建对象输出流，并封装字节流  
            ObjectOutputStream oos = new ObjectOutputStream(baos);  
            // 将对象写入字节流  
            oos.writeObject(object);  
            // 将字节流编码成base64的字符窜  
            String objBase64 = new String(Base64.encodeToString(baos.toByteArray(), 0));  
            Editor editor = preferences.edit();  
            editor.putString(key, objBase64);  
            editor.commit();
        } catch (IOException e){
        	throw e;
        }  
        LogUtils.d("onActivityResult");
    }  
    
    public static Object getObject(Context context, String preference, String key){  
    	Object object = null;  
        SharedPreferences preferences = context.getSharedPreferences(preference,context.MODE_PRIVATE);  
        String productBase64 = preferences.getString(key, "");  
                  
        //读取字节  
        byte[] base64 = Base64.decode(productBase64, 0);  
          
        //封装到字节流  
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);  
        try {  
            //再次封装  
            ObjectInputStream bis = new ObjectInputStream(bais);  
            try {  
                //读取对象  
            	object = (Object) bis.readObject();  
            } catch (ClassNotFoundException e){
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        } catch (StreamCorruptedException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return object;  
    } 
}
