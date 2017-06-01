package com.mdj.wxapi;

import com.mdj.utils.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {

//	@Override
//	public void onResp(BaseResp resp) {
//		// TODO Auto-generated method stub
//		super.onResp(resp);
//		switch (resp.errCode) {
//		case BaseResp.ErrCode.ERR_OK://分享成功
//			ToastUtils.show(getApplicationContext(), "分享成功啦", 0);
//			finish();
//			break;
//		case BaseResp.ErrCode.ERR_USER_CANCEL:
//			ToastUtils.show(getApplicationContext(), "分享取消啦", 0);
//			finish();
//			// 分享取消
//			break;
//		case BaseResp.ErrCode.ERR_AUTH_DENIED:
//			ToastUtils.show(getApplicationContext(), "分享失败啦", 0);
//			finish();
//			// 分享拒绝
//			break;
//		}
//	}

}