package com.mdj.moudle.network.util;

import com.mdj.application.MyApp;
import com.mdj.constant.HttpConstant;
import com.mdj.constant.SPConstant;
import com.mdj.utils.AESUtils;
import com.mdj.utils.Constant;
import com.mdj.utils.MdjLog;
import com.mdj.utils.MdjUtils;
import com.mdj.utils.SPUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//请求上下文
public class RequestCtx {
    private final String url;
    private final String method;//请求方法
    private final LinkedHashMap<String, String> params;//参数
    private final HttpInterface.JsonParser jsonParser;
    private final HttpInterface.Validator validator;
    private final HttpInterface.DataCallback callback;
    private final Map<String ,String> headerMap;
    private final HttpInterface.Decrypter decrypter;
    private final int timerout;
    private final Object tagObj;//自定义数据

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public HttpInterface.JsonParser getJsonParser() {
        return jsonParser;
    }

    public HttpInterface.Validator getValidator() {
        return validator;
    }

    public HttpInterface.DataCallback getCallback() {
        return callback;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public HttpInterface.Decrypter getDecrypter() {
        return decrypter;
    }

    public int getTimerout() {
        return timerout;
    }

    public Object getTagObj() {
        return tagObj;
    }

    public static class Builder {
        private String url;
        private String method;//请求方法
        private LinkedHashMap<String, String> params;//参数
        private HttpInterface.JsonParser jsonParser;
        private HttpInterface.Validator validator;
        private HttpInterface.DataCallback callback;
        private Map<String, String> headerMap;
        private HttpInterface.Decrypter decrypter;
        private int timerout;
        private Object tagObj;//自定义数据

        public Builder(LinkedHashMap<String, String> params) {
            //请求参数必须首先给出
            this.params = params;
            //默认的header，可覆盖
            headerMap = new HashMap<>();
            headerMap.put("User-Agent", MdjUtils.getMdjUserAgent(MyApp.getInstance()));
            headerMap.put("Auth", MdjUtils.getMDJAuth(params));
            String token = SPUtils.getString(MyApp.getInstance(), SPConstant.TOKEN, "");
            MdjLog.log("token:["+token+"]");
            headerMap.put("Token", token);

            //默认超时，可覆盖
            this.timerout = 15*1000;
            //默认的解密器，可覆盖
            decrypter = new HttpInterface.Decrypter() {
                @Override
                public String decrypt(String s) {
                return AESUtils.decrypt(s, Constant.AES_PASSWORD, Constant.AES_IV);
                }
            };
        }

        public Builder url(String val) {
            url = val;
            return this;
        }
        //method和url需要一起给
        public Builder methodAndUrl(String method,String url){
            this.method = method;
            if(method.equals(HttpConstant.HTTP_METHOD_GET)
                    ||method.equals(HttpConstant.HTTP_METHOD_DEL)){
                this.url = MdjUtils.jointUrl(params,url);
            }else{
                this.url = url;
            }
            return this;
        }

        public Builder params(LinkedHashMap<String, String> val) {
            params = val;
            return this;
        }

        public Builder jsonParser(HttpInterface.JsonParser val) {
            jsonParser = val;
            return this;
        }

        public Builder validator(HttpInterface.Validator val) {
            validator = val;
            return this;
        }

        public Builder callback(HttpInterface.DataCallback val) {
            callback = val;
            return this;
        }

        public Builder headerMap(Map<String, String> val) {
            headerMap = val;
            return this;
        }

        public Builder decrypter(HttpInterface.Decrypter val) {
            decrypter = val;
            return this;
        }

        public Builder timerout(int val) {
            timerout = val;
            return this;
        }
        public Builder tagObj(Object val) {
            tagObj = val;
            return this;
        }

        public RequestCtx build() {
            return new RequestCtx(this);
        }
    }
    public RequestCtx(Builder b) {
        url = b.url;
        method = b.method;
        params = b.params;
        jsonParser = b.jsonParser;
        validator = b.validator;
        callback = b.callback;
        headerMap = b.headerMap;
        decrypter = b.decrypter;
        timerout = b.timerout;
        tagObj = b.tagObj;
    }
}
