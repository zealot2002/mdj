package com.mdj.moudle.coupon.presenter;
import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.coupon.CouponBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SelectCouponListPresenter implements SelectCouponListContract.Presenter{
    private final SelectCouponListContract.View view;

    public SelectCouponListPresenter(@NonNull SelectCouponListContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void getAvailableCouponList(String projectParams) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            map.put("userId", CacheManager.getInstance().getUserBean().getId());
            map.put("cityId", CacheManager.getInstance().getGlobalCity().getId());
            map.put("projectParams",projectParams);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.GET_AVAILABLE_COUPON_LIST;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_GET, url)
                    .callback(getAvailableCouponListDataCallback)
                    .jsonParser(getAvailableCouponListJsonParser)
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
    HttpInterface.DataCallback getAvailableCouponListDataCallback = new HttpInterface.DataCallback() {
        @Override
        public void requestCallback(int result,Object data,Object tagObj) {
            view.closeLoading();
            if(result == HttpConstant.SUCCESS){
                view.updateUI(data);
            }else{
                view.showDisconnect(data.toString());
            }
        }
    };
    HttpInterface.JsonParser getAvailableCouponListJsonParser = new HttpInterface.JsonParser() {
        @Override
        public Object[] parse(String str) throws JSONException {
            List<CouponBean> dataList = new ArrayList<>();
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject obj = (JSONObject) jsonParser.nextValue();
            int errno = obj.getInt("err");
            if (errno == 0) {
                JSONArray couponList = obj.getJSONArray("data");
                for(int i=0;i<couponList.length();i++){
                    JSONObject obj2 = couponList.getJSONObject(i);
                    /*List<String> fitableProjectList = new ArrayList<>();
                    List<String> fitableCityList = new ArrayList<>();

                    JSONArray projectList = obj2.getJSONArray("fitableProjectList");
                    for(int m = 0;m<projectList.length();m++){
                        fitableProjectList.add(projectList.getString(m));
                    }
                    JSONArray cityList = obj2.getJSONArray("fitableCityList");
                    for(int m = 0;m<cityList.length();m++){
                        fitableCityList.add(cityList.getString(m));
                    }*/

                    CouponBean bean = new CouponBean.Builder(obj2.getString("title"))
                            .id(obj2.getString("id"))
                            .useScope(obj2.getInt("appScope"))
                            .expiredTime(obj2.getString("startDate")+"至"+obj2.getString("endDate"))
                            .remark(obj2.getString("notice"))
                            .serviceType(obj2.getInt("serviceType"))
                            .state(obj2.getInt("state"))
                            .price(obj2.getString("beneFit"))
                            .type(obj2.getInt("type"))
                            .fitableCity(obj2.getString("fitableCity"))
                            .fitableProject(obj2.getString("fitableProject"))
                            .build();
                    dataList.add(bean);
                }
            } else {
                String msg = obj.getString("msg");
                return new Object[]{HttpConstant.FAIL,msg};
            }
            return new Object[]{HttpConstant.SUCCESS,dataList};
        }
    };
}
