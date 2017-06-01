package com.mdj.moudle.mine;

import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.mdj.R;
import com.mdj.constant.H5Constant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.MdjLog;
import com.mdj.utils.MdjUtils;
import com.mdj.view.ProgressWebView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author 吴世文
 * @Description: 用户档案页面
 */

public class UserDatumActivity extends BaseActivity {
    private ProgressWebView webview;
    private TitleWidget titleWidget;

/***************************************************************************/
    @Override
    public void findViews() {
        setContentView(R.layout.common_webview);
        mContext = this;
        webview = (ProgressWebView) findViewById(R.id.webview);
        titleWidget = (TitleWidget) findViewById(R.id.titleWidget);
        titleWidget.setTitle("用户档案");

        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setUserAgentString(MdjUtils.getMdjUserAgent(mContext));
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setUseWideViewPort(true);
        webview.setWebViewClient(new WebViewClientDemo());
        webview.setWebChromeClient(new WebChromeClient());
        webview.addJavascriptInterface(new JavaScriptInterface(), "jsObj");
        MdjUtils.synCookies(mContext, HttpConstant.URL_USER_DATUM);
        webview.loadUrl(HttpConstant.URL_USER_DATUM);

    }

    private class WebViewClientDemo extends WebViewClient {
        @Override// 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class JavaScriptInterface {
        JavaScriptInterface() {
        }

        @JavascriptInterface
        public void onH5Event(final String data) {
            MdjLog.log("onH5Event:" + data);
            try {
                JSONTokener jsonParser = new JSONTokener(data);
                JSONObject obj = (JSONObject) jsonParser.nextValue();
                String action = obj.getString("action");
                JSONObject dataObj = null;
                if (obj.has("data")) {
                    dataObj = obj.getJSONObject("data");
                }
                handleH5Event(Integer.valueOf(action), dataObj);
            } catch (Exception e) {
                e.printStackTrace();
                showShortToast("onH5Event Exception: " + e.toString());
            }
        }
    }

    private void handleH5Event(int action, JSONObject data) {
        if(action == H5Constant.ActionEnum.CLOSE_SELF_WINDOW.value()){ //关闭当前页面
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MobclickAgent.onPageStart(CommonUtil.generateTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPageEnd(CommonUtil.generateTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
