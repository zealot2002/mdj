package com.mdj.moudle.mine.comment.presenter;

import com.alibaba.fastjson.JSON;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.beautician.bean.BeauticianTag;
import com.mdj.moudle.mine.comment.CommentBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author 吴世文
 * @Description:
 */
public class CommentPresenter implements CommentContract.Presenter,
        HttpInterface.JsonParser, HttpInterface.DataCallback, HttpInterface.Validator {
    private CommentContract.View view;

    public CommentPresenter(CommentContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void validate(Object obj) throws NetDataInvalidException {

    }

    @Override
    public void evaluateInitialize(String type, String orderSn) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            if (type.equals("0")) { //获取评价页默认数据
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                map.put("orderSn", orderSn);
                String url = HttpConstant.NEW_SERVER_URL + HttpConstant.DEFAULT_IMPRESSION_LIST;
                RequestCtx ctx = new RequestCtx.Builder(map)
                        .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                        .callback(this)
                        .jsonParser(this)
                        .build();
                try {
                    HttpUtil.getInstance().request(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                map.put("orderSn", orderSn);
                String url = HttpConstant.NEW_SERVER_URL + HttpConstant.SHOPRATED_DETAILS_INFO;
                RequestCtx ctx = new RequestCtx.Builder(map)
                        .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                        .callback(inHomeEvaluateDetailsCallBack)
                        .jsonParser(inHomeEvaluateDetailsJsonParser)
                        .build();
                try {
                    HttpUtil.getInstance().request(ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            view.showDisconnect("请检查您的网络后再试");
        }

    }

    @Override
    public void requestCallback(int result, Object data, Object tagData) {
        view.closeLoading();
        if (result == HttpConstant.SUCCESS) {
            view.updateUI(data);
        } else {
            view.showDisconnect(data.toString());
        }
    }

    @Override
    public Object[] parse(String str) throws JSONException {
        JSONTokener jsonParser = new JSONTokener(str);
        JSONObject obj = (JSONObject) jsonParser.nextValue();
        int err = obj.getInt("err");
        if (err == 0) {
            CommentBean commentBean = new CommentBean();
            JSONObject dataObj = obj.getJSONObject("data");
            commentBean.setServiceType(dataObj.getString("serviceType"));//1上门  2到店

            //好评标签选项
            List<String> goodsList = new ArrayList<>();
            JSONArray goodsImpressionArray = dataObj.getJSONArray("goodsImpressionList");
            for (int i = 0; i < goodsImpressionArray.length(); i++) {
                goodsList.add(goodsImpressionArray.get(i).toString());
            }
            commentBean.setGoodsImpressionList(goodsList);

            //中评标签选项
            List<String> middleList = new ArrayList<>();
            JSONArray middleImpressionArray = dataObj.getJSONArray("middleImpressionList");
            for (int i = 0; i < middleImpressionArray.length(); i++) {
                middleList.add(middleImpressionArray.get(i).toString());
            }
            commentBean.setMiddleImpressionList(middleList);

            //差评标签选项
            List<String> badList = new ArrayList<>();
            JSONArray badImpressionListArray = dataObj.getJSONArray("badImpressionList");
            for (int i = 0; i < badImpressionListArray.length(); i++) {
                badList.add(badImpressionListArray.get(i).toString());
            }
            commentBean.setBadImpressionList(badList);
            return new Object[]{HttpConstant.SUCCESS, commentBean};
        } else {
            return new Object[]{HttpConstant.FAIL, obj.getString("msg")};
        }
    }

    HttpInterface.DataCallback inHomeEvaluateDetailsCallBack = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result, Object data, Object tagData) {
            view.closeLoading();
            if (result == HttpConstant.SUCCESS) {
                view.updateUI(data);
            } else {
                view.showDisconnect(data.toString());
            }
        }
    };

    HttpInterface.JsonParser inHomeEvaluateDetailsJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int err = obj.getInt("err");
            if (err == 0) {
                CommentBean bean = new CommentBean();
                JSONObject data = obj.getJSONObject("data");
                JSONObject info = data.getJSONObject("commentInfo");
                bean.setContent(info.getString("content"));
                bean.setServiceleve(info.getString("serviceLeve"));
                bean.setShopscore(info.getString("shopScore"));
                bean.setAnonymous(info.getString("isAnonymous").equals("1") ? true : false);
                JSONArray infoList = info.getJSONArray("impressionList");
                //中评标签选项
                List<String> impressionList = new ArrayList<>();
                for (int i = 0; i < infoList.length(); i++) {
                    impressionList.add(infoList.get(i).toString());
                }
                bean.setImpressionList(impressionList);

                return new Object[]{HttpConstant.SUCCESS, bean};
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL, "err: " + msg};
            }
        }
    };


    @Override//提交评价内容
    public void submitComment(String orderSn, String serviceType, String isAnonymous,
                              String serviceLeve, String shopScore, String content, List<String> impressionList) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            String impressionLists = JSON.toJSONString(impressionList);
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("orderSn", orderSn);
            map.put("userId", CacheManager.getInstance().getUserBean().getId());
            map.put("serviceType", serviceType);
            map.put("isAnonymous", isAnonymous);
            map.put("serviceLeve", serviceLeve);
            map.put("shopScore", shopScore);
            map.put("content", content);

            map.put("impressionList", impressionLists);
            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.SUBMIT_RATED;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_POST, url)
                    .callback(submitEvaluateCallBack)
                    .jsonParser(submitEvaluateJsonParser)
                    .build();
            try {
                HttpUtil.getInstance().request(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            view.showDisconnect("请检查您的网络后再试");
        }
    }

    @Override
    public void getBeautyInfo(String beautyId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("beauticianId",beautyId);
            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_BEAUTICIAN_DETAIL;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getBeauticianDetailDataCallback)
                    .jsonParser(getBeauticianDetailJsonParser)
                    .build();
            try {
                HttpUtil.getInstance().request(ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            view.showDisconnect("请检查您的网络再试");
        }
    }
    HttpInterface.DataCallback getBeauticianDetailDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.setBeautyInfo(data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getBeauticianDetailJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            BeauticianBean beauticianBean;
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONObject dataObj = obj.getJSONObject("data");
                //tag
                Set<BeauticianTag> tagSet = new HashSet<>();
                JSONArray tagList = dataObj.getJSONArray("tagList");
                for(int i=0;i<tagList.length();i++){
                    JSONObject tagObj = tagList.getJSONObject(i);
                    tagSet.add(new BeauticianTag(tagObj.getString("tagName"),tagObj.getString("tagNum")));
                }
                //beauticianInfo
                JSONObject beauticianInfo = dataObj.getJSONObject("beauticianInfo");
                beauticianBean = new BeauticianBean.Builder(beauticianInfo.getString("name"))
                        .id(beauticianInfo.getString("id"))
                        .imgUrl(beauticianInfo.getString("headImg"))
                        .experience(beauticianInfo.getString("experience"))
                        .intruduction(beauticianInfo.getString("introduction"))
                        .orderNum(beauticianInfo.getString("orderNum"))
                        .goodAppraiseNum(beauticianInfo.getString("goodAppraiseNum"))
                        .tagSet(tagSet)
                        .department(dataObj.getJSONObject("shopInfo").getString("home_name"))
                        .build();

            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,"errno:"+errno+" msg:"+msg};
            }
            return new Object[]{HttpConstant.SUCCESS,beauticianBean};
        }
    };
    HttpInterface.DataCallback submitEvaluateCallBack = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result, Object data, Object tagData) {
            view.closeLoading();
            if (result == HttpConstant.SUCCESS) {
                view.submitCommentCallBack();
            } else {
                view.showDisconnect(data.toString());
            }
        }
    };

    HttpInterface.JsonParser submitEvaluateJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int err = obj.getInt("err");
            if (err != 0) {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL, msg};
            } else {
                return new Object[]{HttpConstant.SUCCESS, ""};
            }
        }
    };

}
