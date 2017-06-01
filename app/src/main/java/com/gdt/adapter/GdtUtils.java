package com.gdt.adapter;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Base64;
import android.util.Log;

import com.mdj.application.MyApp;
import com.mdj.constant.SPConstant;
import com.mdj.utils.DateUtil;
import com.mdj.utils.DeviceInfoUtil;
import com.mdj.utils.MD5Util;
import com.mdj.utils.MdjLog;
import com.mdj.utils.MyAsyncTask;
import com.mdj.utils.PreferencesUtils;

public class GdtUtils {
	private static final int CONNECTION_TIME_OUT = 15 * 1000;

	private static final String URL_PREFIX = "http://t.gdt.qq.com/conv/app/";
	private static final String appid = "1104617632"; // appid
	private static final String conv_type = "MOBILEAPP_ACTIVITE";
	private static final String app_type = "ANDROID";
	private static final String uid = "1172270"; // 广告主id
	private static final String encrypt_key = "bf7c25219ec7a887"; // 广告主提供
	private static final String sign_key = "d6e5d9c46ff87cc9"; // 广告主提供

	//对外接口
	public static void sendAppRegister() {
		try {
			String data = getEncryptData();
			String attachment = getAttachment(data);
			String url = URL_PREFIX + appid + "/conv?v=" + data + "&"+ attachment;

			MdjLog.logE("zzy", "GdtUtils sendAppRegister url: " + url);
			sendDataToGdt(url);
		} catch (Exception e) {
			MdjLog.logE("zzy",
					"GdtUtils sendAppRegister Exception: " + e.getMessage());
		}
	}

	private static String getAttachment(String data2)
			throws UnsupportedEncodingException {
		return "conv_type=" + URLEncoder.encode(conv_type, "UTF-8")
				+ "&app_type=" + URLEncoder.encode(app_type, "UTF-8")
				+ "&advertiser_id=" + URLEncoder.encode(uid, "UTF-8");
	}

	// 加密数据v=data
	private static String getEncryptData() throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		// 组装参数
		String queryString = getQueryString();
		// 参数签名
		String signature = getSignature(queryString);
		// 参数加密
		return constructFinalData(queryString, signature);
	}

	// 异或加密
	public static String simpleXor(String str, String key) {
		char[] strCharArray = str.toCharArray();
		char[] keyCharArray = key.toCharArray();
		int j = 0;
		for (int i = 0; i < strCharArray.length; i++) {
			strCharArray[i] = (char) (strCharArray[i] ^ keyCharArray[j]);
			j = (++j) % (key.length());
		}
		return new String(strCharArray);
	}

	// 获得最终的data
	private static String constructFinalData(String queryString,
			String signature) throws UnsupportedEncodingException {
		String baseData = queryString + "&sign="
				+ URLEncoder.encode(signature, "UTF-8");
		String data = Base64.encodeToString(
				simpleXor(baseData, encrypt_key).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);// 去掉换行符
//用文档中的数据，校验算法正确
//		String testBaseData = "muid=0f074dc8e1f0547310e729032ac0730b&conv_time=1422263664&client_ip=10.11.12.13&sign=8a4d7f5323fd91b37430d639e6f7371b";
//		String data = Base64.encodeToString(
//				simpleXor(testBaseData, "test_encrypt_key").getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
		
		MdjLog.logE("zzy", " data :[" + data + "]");
		return data;
	}

	// 参数签名
	private static String getSignature(String queryString)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String page = URL_PREFIX + appid + "/conv?" + queryString;
		String encodePage = URLEncoder.encode(page, "UTF-8");
		String property = sign_key + "&GET&" + encodePage;
		return MD5Util.Bit32(property).toLowerCase();
	}

	
	private static String getQueryString() throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		String muid = getMuid(); // muid
		String convTime = new String("" + System.currentTimeMillis() / 1000);// 当前时间戳
//		String clientIp = DeviceInfoUtil.GetNetIp();

		return "muid=" + URLEncoder.encode(muid, "UTF-8") + "&conv_time="
				+ URLEncoder.encode(convTime, "UTF-8");// + "&client_ip="
//				+ URLEncoder.encode(clientIp, "UTF-8");
	}

	private static String getMuid() throws NoSuchAlgorithmException {
		// 取得imei、并转化成小写
		String imei = DeviceInfoUtil.getImei(MyApp.getInstance()).toLowerCase();
		// String imei = "354649050046412";
		return MD5Util.Bit32(imei).toLowerCase();
	}

	private static void sendDataToGdt(final String url) {
		MdjLog.logE("zzy", "sendDataToGdt url = " + url);
		try {
			HttpGet httpRequest = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			// 4.1:设置请求超时
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT,
					CONNECTION_TIME_OUT);
			// 4.2:设置读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					CONNECTION_TIME_OUT);

			// 5:取得HTTP Response
			HttpResponse response = client.execute(httpRequest);
			// 6:获取状态码
			int statusCode = response.getStatusLine().getStatusCode();

			// 7:对状态码进行判断
			if (statusCode == 200) {
				MdjLog.logE("zzy", "sendDataToGdt statusCode == 200");
				// 8.1:分发处理不同的状态
				/** 服务器返回数据 **/
				String strResult = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
				parseResult(strResult);
				MdjLog.logE("zzy", "sendDataToGdt rsp before decrypt:"
						+ strResult);
			} else {
				MdjLog.logE("zzy",
						"sendDataToGdt statusCode != 200 statusCode = "
								+ statusCode);
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			MdjLog.logE("zzy",
					"sendDataToGdt ConnectTimeoutException = " + e.getMessage());
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			MdjLog.logE("zzy",
					"sendDataToGdt SocketTimeoutException = " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			MdjLog.logE("zzy", "sendDataToGdt Exception = " + e.getMessage());
		}
	}

	private static void parseResult(String msg) throws JSONException {
		JSONTokener jsonParser = new JSONTokener(msg);
		JSONObject obj = (JSONObject) jsonParser.nextValue();
		int err = obj.getInt("ret");
		if (err == 0) {
			PreferencesUtils.setStringPreferences(MyApp.getInstance(),
					SPConstant.SP_FLAG_APP_REGISTER,
					SPConstant.SP_FLAG_APP_REGISTER, "1");
			MdjLog.logE("zzy", "GdtUtils success!!");
		} else {
			String message = obj.getString("msg");
			MdjLog.logE("zzy", "GdtUtils Got errMsg from gdt!!err:" + err
					+ " message :" + message);
			// throw new Exception(message);
		}
	}
}
