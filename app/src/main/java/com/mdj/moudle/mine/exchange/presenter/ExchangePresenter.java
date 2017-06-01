package com.mdj.moudle.mine.exchange.presenter;

import android.support.annotation.NonNull;

import com.mdj.application.MyApp;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.coupon.CouponBean;
import com.mdj.moudle.network.HttpUtil;
import com.mdj.moudle.network.util.HttpInterface;
import com.mdj.moudle.network.util.NetDataInvalidException;
import com.mdj.moudle.network.util.RequestCtx;
import com.mdj.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedHashMap;


/**
 * @author 吴世文
 * @Description:
 */
public class ExchangePresenter implements ExchangeContract.Presenter
        , HttpInterface.JsonParser, HttpInterface.DataCallback, HttpInterface.Validator {
    private static final String TAG = "ExchangePresenter";

    private final ExchangeContract.View view;

    public ExchangePresenter(@NonNull ExchangeContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void exchange(String phone, String redeemCode) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("phone", phone);
            map.put("code", redeemCode);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.EXCHANGE_CODE;
            RequestCtx ctx = new RequestCtx.Builder(map)
                    .methodAndUrl(HttpConstant.HTTP_METHOD_POST, url)
                    .callback(this)
                    .jsonParser(this)
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
    public void start() {

    }

    @Override
    public void requestCallback(int result, Object data,Object tagData) {
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
            JSONObject dataObj = obj.getJSONObject("data");
            JSONObject couponInfo = dataObj.getJSONObject("couponInfo");
            CouponBean bean = new CouponBean.Builder(couponInfo.getString("title"))
                    .id(couponInfo.getString("coupon_id"))
                    .type(Integer.valueOf(couponInfo.getString("type")))
                    .price(couponInfo.getString("price"))
                    .build();

            return new Object[]{HttpConstant.SUCCESS, bean};

        } else {
            JSONObject data = obj.getJSONObject("data");
            return new Object[]{HttpConstant.FAIL, data.getString("message")};
        }
    }

    @Override
    public void validate(Object obj) throws NetDataInvalidException {

    }
}
