package com.mdj.moudle.mine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.cache.CacheManager;
import com.mdj.constant.CommonConstant;
import com.mdj.constant.HttpConstant;
import com.mdj.moudle.BaseFragment;
import com.mdj.moudle.address.MyAddressListActivity;
import com.mdj.moudle.beautician.MyBeauticianListActivity;
import com.mdj.moudle.coupon.MyCouponListActivity;
import com.mdj.moudle.login.LoginActivity;
import com.mdj.moudle.mine.exchange.ExchangeActivity;
import com.mdj.moudle.mine.feedback.FeedbackActivity;
import com.mdj.moudle.mine.invitation.InvitationActivity;
import com.mdj.moudle.mine.presenter.MineContract;
import com.mdj.moudle.mine.presenter.MinePresenter;
import com.mdj.moudle.mine.setPhoto.SetPhotosActivity;
import com.mdj.moudle.mine.setting.SetActivity;
import com.mdj.moudle.user.UserBean;
import com.mdj.moudle.userPackage.MyPackageListActivity;
import com.mdj.moudle.webview.BaseWebviewActivity;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RefreshableView;
import com.mdj.view.roundHead.RoundImageView;
import com.umeng.analytics.MobclickAgent;

public class MineFragment extends BaseFragment implements View.OnClickListener,MineContract.View, RefreshableView.RefreshListener {
    private static final int REQUEST_CODE_LOGIN = 1;
    /*title*/
    private ImageButton btnTitleRight;
    /*我的优惠券、我的套餐*/
    private RelativeLayout rlMyCoupon,rlMyPackage;
    /*gridview*/
    private GridViewAdapter adapter;
    private GridView gvMenu;
    private String[] gvMenuNames;
    private int[] gvMenuIcons;

    private Button btnLogin;

    /*用户信息*/
    private TextView tvName,tvPhone;
    private RoundImageView ivImg;

    /*套餐、优惠券 hint*/
    private RelativeLayout rlCouponHint,rlPackageHint;
    private TextView tvCouponHint,tvPackageHint;

    private RelativeLayout rlOpen;
    private RefreshableView refreshableView;// 下拉刷新

    private MineContract.Presenter presenter;

    /*******************************************************************************************************/
    @Override
    public void setPresenter(Object presenter) {

    }
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate(R.layout.mine_fragment, container, false);
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
        findViews();

        refreshableView = (RefreshableView) view.findViewById(R.id.refresh_root);
        refreshableView.setRefreshListener(this);

        presenter = new MinePresenter(this);
        presenter.start();
		return view;
	}
    public void findViews() {
        mContext = getActivity();
        initTitle();
        initCouponAndPackageView();
        initGridView();
        initUserView();
        btnLogin = (Button)view.findViewById(R.id.btnLogin);
        rlOpen = (RelativeLayout) view.findViewById(R.id.rlOpen);
        btnLogin.setOnClickListener(this);
        rlOpen.setOnClickListener(this);
    }
    private void initUserView() {
        tvName = (TextView)view.findViewById(R.id.tvName);
        tvPhone = (TextView)view.findViewById(R.id.tvPhone);
        ivImg = (RoundImageView)view.findViewById(R.id.ivImg);
    }

    private void initTitle() {
        btnTitleRight = (ImageButton)view.findViewById(R.id.btnTitleRight);
        btnTitleRight.setOnClickListener(this);
    }

    private void initCouponAndPackageView() {
        rlCouponHint = (RelativeLayout)view.findViewById(R.id.rlCouponHint);
        rlPackageHint = (RelativeLayout)view.findViewById(R.id.rlPackageHint);
        tvCouponHint = (TextView)view.findViewById(R.id.tvCouponHint);
        tvPackageHint = (TextView)view.findViewById(R.id.tvPackageHint);

        rlMyCoupon = (RelativeLayout)view.findViewById(R.id.rlMyCoupon);
        rlMyPackage = (RelativeLayout)view.findViewById(R.id.rlMyPackage);
        rlMyCoupon.setOnClickListener(this);
        rlMyPackage.setOnClickListener(this);
    }

    private void initGridView() {
        //item 图标
        gvMenuIcons = new int[]{R.drawable.exchange_icon_selector,R.drawable.address_icon_selector,R.drawable.beautician_icon_selector,
                R.drawable.feedback_icon_selector,R.drawable.protocal_icon_selector, R.drawable.invitation_icon_selector,
                R.drawable.datum_icon_selector,R.drawable.setting_icon_selector};
        //item 文字
        gvMenuNames = getResources().getStringArray(R.array.mine_gv_menu_names);
        gvMenu = (GridView)view.findViewById(R.id.gvMenu);
        adapter = new GridViewAdapter();
        gvMenu.setAdapter(adapter);
    }
    @Override
    public void showLogin(){
        btnLogin.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.GONE);
        tvPhone.setVisibility(View.GONE);
        ivImg.setImageResource(R.mipmap.main_header);
    }

    private void hideLogin(){
        btnLogin.setVisibility(View.GONE);
        tvName.setVisibility(View.VISIBLE);
        tvPhone.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDisconnect(String msg) {
        refreshableView.finishRefresh();
        ToastUtils.showLong(mContext, msg);
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void updateUI(Object userBean) {
        refreshableView.finishRefresh();
        UserBean bean = (UserBean)userBean;
        if(bean==null|| TextUtils.isEmpty(bean.getId())){
            return;
        }
        hideLogin();
        MyApp.instance.getImageLoad().displayImage(bean.getImgUrl(), ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
        tvName.setText(bean.getName());
        tvPhone.setText(bean.getPhone());
        if(bean.getAvailableCouponNum()>0){
            rlCouponHint.setVisibility(View.VISIBLE);
            tvCouponHint.setText(bean.getAvailableCouponNum()+"");
        }else{
            rlCouponHint.setVisibility(View.GONE);
        }
        if(bean.getAvailablePackageNum()>0){
            rlPackageHint.setVisibility(View.VISIBLE);
            tvPackageHint.setText(bean.getAvailablePackageNum()+"");
        }else{
            rlPackageHint.setVisibility(View.GONE);
        }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.btnTitleRight:// 打电话
                mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CommonConstant.MDJ_HOTLINE)));
                break;

            case R.id.rlMyCoupon:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
                    startActivity(new Intent(mContext, MyCouponListActivity.class));
                break;

            case R.id.rlMyPackage:
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
                    startActivity(new Intent(mContext, MyPackageListActivity.class));

                break;

            case R.id.btnLogin:
                startActivityForResult(new Intent(mContext, LoginActivity.class),REQUEST_CODE_LOGIN);

                break;
            case R.id.rlOpen:
                if(!TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
                    startActivityForResult(new Intent(mContext, SetPhotosActivity.class), REQUEST_CODE_LOGIN);
                }
                break;
            default:
                break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
        if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
            showLogin();
            rlCouponHint.setVisibility(View.GONE);
            rlPackageHint.setVisibility(View.GONE);
        }
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

    private void onMenuItemClicked(int position){
        int menu = gvMenuIcons[position];
        switch (menu){
            case R.drawable.exchange_icon_selector: //兑换
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
                    startActivity(new Intent(mContext, ExchangeActivity.class));
                break;

            case R.drawable.address_icon_selector: //我的地址
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
                    startActivity(new Intent(mContext, MyAddressListActivity.class));
                break;

            case R.drawable.beautician_icon_selector://我的美容师
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
                    startActivity(new Intent(mContext, MyBeauticianListActivity.class));
                break;

            case R.drawable.feedback_icon_selector: //意见反馈
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
                    startActivity(new Intent(mContext, FeedbackActivity.class));
                break;

            case R.drawable.protocal_icon_selector: //用户协议
                Intent it = new Intent(mContext, BaseWebviewActivity.class);
                it.putExtra("url", HttpConstant.PROTOCAL_URL);
                it.putExtra("title", "用户协议");
                startActivity(it);
                break;

            case R.drawable.invitation_icon_selector: //邀请好友
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
                    startActivity(new Intent(mContext, InvitationActivity.class));
                break;

            case R.drawable.datum_icon_selector: //用户档案
                if(TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId()))
                    ToastUtils.showLong(mContext,"请登录");
                else
//                    ToastUtils.showLong(mContext, "建设中");
                startActivity(new Intent(mContext,UserDatumActivity.class));
                break;

            case R.drawable.setting_icon_selector: //设置
                startActivity(new Intent(mContext, SetActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_LOGIN == requestCode&& Activity.RESULT_OK==resultCode){//登陆成功
            presenter.getUserInfo(true);
        }
    }

    @Override
    public void onRefresh(RefreshableView view) {
        if(!TextUtils.isEmpty(CacheManager.getInstance().getUserBean().getId())){
            presenter.getUserInfo(false);
        }else{
            refreshableView.finishRefresh();
        }
    }

    @Override
    public boolean canPullDownRefresh() {
        return true;
    }

    private class GridViewAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return gvMenuNames !=null? gvMenuNames.length : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHoder hoder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.mine_grid_menu_item, null);
                hoder = new ViewHoder();
                hoder.btnMenuItem = (ImageButton) convertView.findViewById(R.id.btnMenuItem);
                hoder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(hoder);
            } else {
                hoder = (ViewHoder) convertView.getTag();
            }
            hoder.tvName.setText(gvMenuNames[position]);
            hoder.btnMenuItem.setImageResource(gvMenuIcons[position]);
            hoder.btnMenuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMenuItemClicked(position);
                }
            });
            return convertView;
        }
    }

    class ViewHoder {
        private ImageButton btnMenuItem;
        private TextView tvName;
    }
}
