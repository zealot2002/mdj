package com.mdj.moudle.mine.invitation.presenter;

import com.mdj.application.MyApp;
import com.mdj.constant.HttpConstant;
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
public class InvitationPresenter implements InvitationContract.Presenter,
        HttpInterface.JsonParser, HttpInterface.DataCallback, HttpInterface.Validator{

    private InvitationContract.View view;

    public InvitationPresenter(InvitationContract.View view) {
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
    public void submitPhoneNumber(String phone, String userId) {
        if (CommonUtil.isNetWorkConnected(MyApp.getInstance())) {
            view.showLoading();
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put("phone", phone);
            map.put("userId", userId);

            String url = HttpConstant.NEW_SERVER_URL + HttpConstant.INVITATION_FRIEND;
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
    public void requestCallback(int result,Object data,Object tagData) {
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
            return new Object[]{HttpConstant.SUCCESS, ""};
        } else {
            return new Object[]{HttpConstant.FAIL, obj.getString("msg")};
        }
    }

}
