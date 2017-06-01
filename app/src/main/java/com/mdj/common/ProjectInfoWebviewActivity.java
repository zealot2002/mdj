//package com.mdj.common;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.webkit.CookieManager;
//import android.webkit.CookieSyncManager;
//import android.webkit.JavascriptInterface;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.alipay.sdk.util.LogUtils;
//import com.mdj.R;
//import com.mdj.application.MyApp;
//import com.mdj.cache.CacheManager;
//import com.mdj.constant.HttpConstant;
//import com.mdj.constant.UMConstant;
//import com.mdj.moudle.BaseActivity;
//import com.mdj.moudle.project.ProjectBean;
//import com.mdj.utils.CommonUtil;
//import com.mdj.utils.MdjLog;
//import com.mdj.utils.MdjUtils;
//import com.mdj.view.ProgressWebView;
//import com.umeng.analytics.MobclickAgent;
//
////项目详情页
//public class ProjectInfoWebviewActivity extends BaseActivity {
//	private ProgressWebView webview;
//	private Button btnBuy;
//	private String projectId, projectName, packageId;
//	private int serviceType = 0;
//
//	@Override
//	public void onClick(View v) {
//		LogUtils.d("onClick :" + v.getId());
//		switch (v.getId()) {
//		case R.id.ll_back:
//			finish();
//			break;
//
//		case R.id.ll_next:
//			callPhone(mContext);
//			break;
//
//		case R.id.btnBuy:
//			next();
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	private void next() {
////		if (!TextUtils.isEmpty(MyApp.getInstance().getUid())) {
//			Intent it = null;
//			if (serviceType == ProjectBean.SERVICE_TYPE_IN_HOME_AND_TO_SHOP) {
//				it = new Intent(this, ProjectDetailPopupWindow.class);
//			} else {
//				it = new Intent(this, ProjectMoreActivity.class);
//				it.putExtra("serviceType", serviceType + "");
//			}
//			it.putExtra("projectId", projectId);
//			startActivity(it);
////		} else {
////		}
//		// 统计
//		CacheManager.getInstance().getStatisticsProjectBean().setId(projectId);
//		CacheManager.getInstance().getStatisticsProjectBean().setName(projectName);
//		MobclickAgent.onEvent(this, "进入项目多选页:" + projectName, projectName);
//	}
//
//	@Override
//	public void setMyContentView() {
//		try {
//			mContext = this;
//			Bundle bundle = getIntent().getExtras();
//			projectId = bundle.getString("projectId");
//			projectName = bundle.getString("projectName");
//			packageId = bundle.getString("packageId");
//			serviceType = bundle.getInt("serviceType");
//
//			setContentView(R.layout.project_or_package_info_webview);
//			TextView tv_title = (TextView) findViewById(R.id.tv_title);
//			tv_title.setText("项目详情");
//			ll_back = (LinearLayout) findViewById(R.id.ll_back);
//			ll_next = (LinearLayout) findViewById(R.id.ll_next);
//			btnBuy = (Button) findViewById(R.id.btnBuy);
//			LinearLayout ll_status = (LinearLayout) findViewById(R.id.ll_status);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//				ll_status.setVisibility(View.VISIBLE);
//		} catch (Exception e) {
//			MdjLog.logE("zzy", "ProjectInfoWebviewActivity e: " + e.getMessage());
//		}
//	}
//
//	@SuppressLint("SetJavaScriptEnabled")
//	@Override
//	public void initData() {
//		webview = (ProgressWebView) findViewById(R.id.webview);
//		webview.getSettings().setUserAgentString(MdjUtils.getMdjUserAgent(mContext));
//		webview.getSettings().setJavaScriptEnabled(true);
//		webview.addJavascriptInterface(new JavaScriptInterface(), "jsObj");// 监听404返回
//
//		webview.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				view.loadUrl(url);
//				return true;
//			}
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				super.onPageFinished(view, url);
//				// btnBuy.setVisibility(View.VISIBLE);
//			}
//
//			@Override
//			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				super.onReceivedError(view, errorCode, description, failingUrl);
//				MdjLog.logE("zzy", "ProjectInfoWebviewActivity 项目详情页打开失败  errorCode ：" + errorCode);
//				btnBuy.setVisibility(View.GONE);
//			}
//		});
//
//		String url = "";
//		url += HttpConstant.PROJECT_DETAIL_URL + projectId + "/" + "userPackageId/" + packageId + "?from=app";
//
//		// 设置cookie
//		CookieSyncManager.createInstance(this);
//		CookieManager cookieManager = CookieManager.getInstance();
//		cookieManager.setAcceptCookie(true);
//        cookieManager.removeAllCookie();
//
//		cookieManager.setCookie(url, "_cityId=" + CacheManager.getInstance().getGlobalCity().getId() + " ;" );
////        cookieManager.setCookie(url, "_cityId=" + 1263 + ";");
//		cookieManager.setCookie(url, "_uid=" + MyApp.getInstance().getUid() + ";");
//		CookieSyncManager.getInstance().sync();
//
//		MdjLog.logE("zzy", "ProjectOrPackageInfoWebviewActivity url :" + url);
//		webview.loadUrl(url);
//		MobclickAgent.onEvent(this, UMConstant.UM_EVENT_BROWSE_PROJECT_INFO, projectName);// 统计浏览此项目详情的次数
//		MobclickAgent.onEvent(this, "进入项目详情页:" + projectName, projectName);// 动态漏斗——统计每个项目详情的次数
//	}
//
//	@Override
//	public void setListener() {
//		ll_back.setOnClickListener(this);
//		ll_next.setOnClickListener(this);
//		btnBuy.setOnClickListener(this);
//	}
//
//	class JavaScriptInterface {
//		JavaScriptInterface() {
//		}
//
//		@JavascriptInterface
//		public void htmlErr() {
//			btnBuy.setVisibility(View.GONE);
//		}
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == 100) {
//			if (resultCode == 200) {// 未登录返回
//				showTips(mContext, R.mipmap.tips_warning, getResources().getString(R.string.plase_login));
//			} else if (resultCode == 100) {// 已登录
//				next();
//			}
//		}
//	}
//
//    @Override
//    public void findViews() {
//
//    }
//
//    @Override
//	public void onResume() {
//		super.onResume();
//		try {
//			MobclickAgent.onPageStart(CommonUtil.generateTag());
//			MobclickAgent.onResume(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		try {
//			MobclickAgent.onPageEnd(CommonUtil.generateTag());
//			MobclickAgent.onPause(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
