package com.mdj.moudle.webview;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mdj.R;
import com.mdj.moudle.BaseActivity;
import com.mdj.view.ProgressWebView;
import com.mdj.moudle.widget.TitleWidget;

/**
 * Created by tt on 2016/7/12.
 */
public class BaseWebviewActivity extends BaseActivity {
    private ProgressWebView webview;
    private String url,title;
    private TitleWidget titleWidget;

    @Override
    public void findViews() {
        setContentView(R.layout.common_webview);
        titleWidget = (TitleWidget) findViewById(R.id.titleWidget);
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        try{
            url = bundle.getString("url");
            title = bundle.getString("title");
        }catch (Exception e){
            e.printStackTrace();
        }

        titleWidget.setTitle(title);

        webview = (ProgressWebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.loadUrl(url);
    }

}