//package com.mdj.common;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
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
//import com.mdj.moudle.login.LoginActivity;
//import com.mdj.moudle.project.ProjectBean;
//import com.mdj.utils.CommonUtil;
//import com.mdj.utils.MdjLog;
//import com.mdj.utils.MdjUtils;
//import com.mdj.view.ProgressWebView;
//import com.umeng.analytics.MobclickAgent;
//
////套餐详情页
//public class PackageInfoWebviewActivity extends BaseActivity {
//	private ProgressWebView webview;
//	private Button btnBuy;
//	private ProjectBean projectOrPackageBean;
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
//			if (!TextUtils.isEmpty(MyApp.getInstance().getUid())) {
//				gotoAffirmPackage();
//				try {
//					MobclickAgent.onEvent(this, UMConstant.UM_EVENT_BUY_PACKAGE_FROM_MY_PACKAGE_LIST, projectOrPackageBean.getName());
//					CacheManager.getInstance().getStatisticsPackageBean().setId(projectOrPackageBean.getId());
//					CacheManager.getInstance().getStatisticsPackageBean().setName(projectOrPackageBean.getName());
//					MobclickAgent.onEvent(this, "进入套餐购买页:" + projectOrPackageBean.getName(), projectOrPackageBean.getName());
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} else {
//				startActivityForResult(new Intent(mContext, LoginActivity.class), 100);
//			}
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	/** 进入到套餐购买页面 **/
//	private void gotoAffirmPackage() {
//		Intent it = new Intent(this, AffirmPackageActivity.class);
//		it.putExtra("packageId", projectOrPackageBean.getId());
//		startActivity(it);
//	}
//
//	@Override
//	public void setMyContentView() {
//		mContext = this;
//		Bundle bundle = getIntent().getExtras();
//		projectOrPackageBean = (ProjectBean) bundle.getSerializable("projectOrPackageBean");
//
//		setContentView(R.layout.project_or_package_info_webview);
//
//		TextView tv_title = (TextView) findViewById(R.id.tv_title);
//		tv_title.setText("套餐详情");
//		ll_back = (LinearLayout) findViewById(R.id.ll_back);
//
//		ll_next = (LinearLayout) findViewById(R.id.ll_next);
//
//		btnBuy = (Button) findViewById(R.id.btnBuy);
//
//		LinearLayout ll_status = (LinearLayout) findViewById(R.id.ll_status);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//			ll_status.setVisibility(View.VISIBLE);
//	}
//
//	@Override
//	public void initHandler() {
//
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
//				MdjLog.logE("zzy", "ProjectInfoWebviewActivity 套餐详情页打开失败  failingUrl ：" + failingUrl);
//				MdjLog.logE("zzy", "ProjectInfoWebviewActivity 套餐详情页打开失败  errorCode ：" + errorCode);
//				btnBuy.setVisibility(View.GONE);
//			}
//		});
//
//		String url = "";
//		url += HttpConstant.PACKAGE_DETAIL_URL + projectOrPackageBean.getId() + "?from=app&cityId=" + CacheManager.getInstance().getGlobalCity().getId();
//		Log.d("zzy", "ProjectOrPackageInfoWebviewActivity url :" + url);
//		webview.loadUrl(url);
//		MobclickAgent.onEvent(this, UMConstant.UM_EVENT_BROWSE_PACKAGE_INFO, projectOrPackageBean.getName());// 统计查看此套餐详情的次数
//		MobclickAgent.onEvent(this, "进入套餐详情页:" + projectOrPackageBean.getName(), projectOrPackageBean.getName());// 动态漏斗——统计每个套餐详情
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
//}
