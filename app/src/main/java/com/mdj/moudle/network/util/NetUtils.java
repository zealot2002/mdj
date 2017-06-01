package com.mdj.moudle.network.util;

import android.text.TextUtils;

import com.mdj.constant.HttpConstant;
import com.mdj.utils.MdjLog;
import com.mdj.utils.MdjUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.Map;

public class NetUtils {
	@SuppressWarnings("deprecation")
	public static String sendGetRequest(RequestCtx ctx) throws Exception {
        MdjLog.log("sendGetRequest "+ctx.getUrl());
		HttpGet httpRequest = new HttpGet(ctx.getUrl());
		// 2:设置头信息
        for (Map.Entry<String, String> entry : ctx.getHeaderMap().entrySet()) {
            httpRequest.setHeader(entry.getKey(), entry.getValue());
        }
		// 3: 获取默认的请求client
		DefaultHttpClient client = new DefaultHttpClient();
		// 4.1:设置请求超时
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT,ctx.getTimerout());
		// 4.2:设置读取超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ctx.getTimerout());

		// 5:取得HTTP Response
		HttpResponse response = client.execute(httpRequest);
		// 6:获取状态码
		int statusCode = response.getStatusLine().getStatusCode();

		/** 服务器返回数据 **/
		String strResult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
        if(strResult.contains(HttpConstant.HTML_DATA_PRE)){
            return HttpConstant.HTML_DATA_ERROR;
        }
        if(TextUtils.isEmpty(strResult)||strResult.equals("null")){
            return HttpConstant.EMPTY_DATA_ERROR;
        }
		// 7:对状态码进行判断
		if (statusCode == 200) {
			/** 解密数据 **/
            if(ctx.getDecrypter()!=null){
                return ctx.getDecrypter().decrypt(strResult);
            }
			return strResult;
		} else {
			return HttpConstant.HTTP_ERROR + statusCode;
		}
	}

    public static String sendPostRequest(RequestCtx ctx) throws Exception {
        HttpPost httpRequest = new HttpPost(ctx.getUrl());

        if (ctx.getParams() != null && ctx.getParams().size() > 0) {
            httpRequest.setEntity(new UrlEncodedFormEntity(MdjUtils.mapToList(ctx.getParams()), HTTP.UTF_8));
            MdjLog.log("客户端发送请求: " + ctx.getParams().toString());
        }
        // 设置头信息
        for (Map.Entry<String, String> entry : ctx.getHeaderMap().entrySet()) {
            httpRequest.setHeader(entry.getKey(), entry.getValue());
        }

        // 3: 获取默认的请求client
        DefaultHttpClient client = new DefaultHttpClient();
        // 4.1:设置请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ctx.getTimerout());
        // 4.2:设置读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ctx.getTimerout());

        // 5:取得HTTP Response
        HttpResponse response = client.execute(httpRequest);
        /** 6.服务器返回状态吗 **/
        int statusCode = response.getStatusLine().getStatusCode();
        String strResult = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        MdjLog.log("接口返回：解密前"+strResult);
        if(strResult.contains(HttpConstant.HTML_DATA_PRE)){
            return HttpConstant.HTML_DATA_ERROR;
        }
        if(TextUtils.isEmpty(strResult)||strResult.equals("null")){
            return HttpConstant.EMPTY_DATA_ERROR;
        }

        if (statusCode == 200) {
            /** 解密数据 **/
            if(ctx.getDecrypter()!=null){
                strResult = ctx.getDecrypter().decrypt(strResult);
                MdjLog.log("接口返回：解密后"+strResult);
                return strResult;
            }
            return strResult;
        } else {
            return HttpConstant.HTTP_ERROR + statusCode;
        }
	}

    //除了第一句话之外，put和post方法都是一模一样的，但是暂时先不合并到一起
    public static String sendPutRequest(RequestCtx ctx) throws Exception {
        HttpPut httpRequest = new HttpPut(ctx.getUrl());

        if (ctx.getParams() != null && ctx.getParams().size() > 0) {
            httpRequest.setEntity(new UrlEncodedFormEntity(MdjUtils.mapToList(ctx.getParams()), HTTP.UTF_8));
            MdjLog.log("客户端发送请求: " + ctx.getParams().toString());
        }
        // 设置头信息
        for (Map.Entry<String, String> entry : ctx.getHeaderMap().entrySet()) {
            httpRequest.setHeader(entry.getKey(), entry.getValue());
        }

        // 3: 获取默认的请求client
        DefaultHttpClient client = new DefaultHttpClient();
        // 4.1:设置请求超时
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, ctx.getTimerout());
        // 4.2:设置读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ctx.getTimerout());

        // 5:取得HTTP Response
        HttpResponse response = client.execute(httpRequest);
        /** 6.服务器返回状态吗 **/
        int statusCode = response.getStatusLine().getStatusCode();
        String strResult = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        MdjLog.log("接口返回：解密前"+strResult);
        if(strResult.contains(HttpConstant.HTML_DATA_PRE)){
            return HttpConstant.HTML_DATA_ERROR;
        }
        if(TextUtils.isEmpty(strResult)||strResult.equals("null")){
            return HttpConstant.EMPTY_DATA_ERROR;
        }
        if (statusCode == 200) {
            /** 解密数据 **/
            if(ctx.getDecrypter()!=null){
                strResult = ctx.getDecrypter().decrypt(strResult);
                MdjLog.log("接口返回：解密后"+strResult);
                return strResult;
            }
            return strResult;
        } else {
            return HttpConstant.HTTP_ERROR + statusCode;
        }
    }

    public static String sendDelRequest(RequestCtx ctx) throws Exception {
        HttpDelete httpRequest = new HttpDelete(ctx.getUrl());

        // 2:设置头信息
        for (Map.Entry<String, String> entry : ctx.getHeaderMap().entrySet()) {
            httpRequest.setHeader(entry.getKey(), entry.getValue());
        }
        // 3: 获取默认的请求client
        DefaultHttpClient client = new DefaultHttpClient();
        // 4.1:设置请求超时
        client.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT,ctx.getTimerout());
        // 4.2:设置读取超时
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, ctx.getTimerout());

        // 5:取得HTTP Response
        HttpResponse response = client.execute(httpRequest);
        // 6:获取状态码
        int statusCode = response.getStatusLine().getStatusCode();

        /** 服务器返回数据 **/
        String strResult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
        if(strResult.contains(HttpConstant.HTML_DATA_PRE)){
            return HttpConstant.HTML_DATA_ERROR;
        }
        if(TextUtils.isEmpty(strResult)||strResult.equals("null")){
            return HttpConstant.EMPTY_DATA_ERROR;
        }
        // 7:对状态码进行判断
        if (statusCode == 200) {
            /** 解密数据 **/
            if(ctx.getDecrypter()!=null){
                return ctx.getDecrypter().decrypt(strResult);
            }
            return strResult;
        } else {
            return HttpConstant.HTTP_ERROR + statusCode;
        }
    }

}
