package com.mdj.moudle.order;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.cache.RefreshManager;
import com.mdj.constant.BundleConstant;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautician.BeauticianDetailActivity;
import com.mdj.moudle.mine.comment.InHomeCommentActivity;
import com.mdj.moudle.mine.comment.InHomeCommentDetailsActivity;
import com.mdj.moudle.mine.comment.ToShopCommentActivity;
import com.mdj.moudle.mine.comment.ToShopCommentDetailsActivity;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.presenter.OrderContract;
import com.mdj.moudle.order.presenter.OrderPresenter;
import com.mdj.moudle.order.util.OrderHelper;
import com.mdj.moudle.pay.PayActivity;
import com.mdj.moudle.project.ProjectDetailActivity;
import com.mdj.moudle.qrcode.QrCodeHelper;
import com.mdj.moudle.qrcode.ScanQRCodeActivity;
import com.mdj.moudle.referee.RefereeBean;
import com.mdj.moudle.referee.RefereePopUpWindowActivity;
import com.mdj.utils.DisplayImageOptionsUtil;
import com.mdj.utils.TextViewUtil;
import com.mdj.utils.ToastUtils;
import com.mdj.view.RoundImageView;

import java.util.List;

public class OrderDetailActivity extends BaseActivity implements OrderContract.View,View.OnClickListener{
	private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_SCAN_QRCODE = 2;
    private static final int REQUEST_CODE_COMMENT = 3;//评价
	private String orderId;
    private OrderBean orderBean;
    private TextView tvServiceType,tvState,tvOrderId,tvCreateTime,tvBeauticianName,
            tvProjectNames,tvPrice,tvContact,tvContactPhone,tvServiceTime,tvServiceAddress,tvRemark,tvBeautyParlorName,tvOrderStatusRemark,
            tvFlow1,tvFlow2,tvFlow3,tvFlow4,tvFlow5;
    private RoundImageView ivImg;
    private LinearLayout llComment,llBeauticianGo;
    private RelativeLayout rlComment;
    private Button btnAppend,btnOperation,btnSetReferee;
    private ImageView ivFlow1,ivFlow2,ivFlow3,ivFlow4,ivFlow5;
    private OrderContract.Presenter presenter;
/*********************************************************************************************************/
	@Override
	public void onResume() {
		super.onResume();
        if(RefreshManager.needRefresh()){
            RefreshManager.clearNeedRefreshFlag();
            presenter.getOrderDetail(orderId);
        }
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlComment:
            //打开评价详情
            Intent intent1;
            if(orderBean.getServiceType() == CommonConstant.SERVICE_TYPE_IN_HOME){
                intent1 = new Intent(mContext, InHomeCommentDetailsActivity.class);
            }else{
                intent1 = new Intent(mContext, ToShopCommentDetailsActivity.class);
            }
            intent1.putExtra(BundleConstant.ORDER_ID,orderBean.getId());
            intent1.putExtra(BundleConstant.BEAUTICIAN_ID,orderBean.getBeauticianId());
            intent1.putExtra(BundleConstant.PROJCET_NAME,orderBean.getProjectListStr());
            startActivity(intent1);
			break;

		case R.id.btnOperation:
            entryScreenByOrderState(orderBean);
			break;

        case R.id.btnSetReferee:
            Intent it = new Intent(mContext, ScanQRCodeActivity.class);
            startActivityForResult(it, REQUEST_CODE_SCAN_QRCODE);
            break;


        case R.id.btnAppend:
            int cancelAdd = (int) v.getTag();
            if(cancelAdd==CommonConstant.ORDER_DETAIL_CANCEL_OR_APPEND_CANCEL) {//取消订单
                Intent intent = new Intent(mContext,CancelOrderActivity.class);
                intent.putExtra("orderId",orderId);
                startActivity(intent);
            }else{  //追单
                Intent intent = new Intent(mContext,BeauticianDetailActivity.class);
                intent.putExtra(BundleConstant.ORDER_BEAN,orderBean);
                startActivity(intent);
            }
            break;

		default:
			break;
		}
	}

    private void entryScreenByOrderState(OrderBean orderBean){
        switch (orderBean.getState()){
            case CommonConstant.ORDER_STATUS_WAIT_TO_CHARGE:
                {
                    //去支付
                    Intent intent = new Intent(mContext, PayActivity.class);
                    intent.putExtra("orderBean",orderBean);
                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                }
                break;
            case CommonConstant.ORDER_STATUS_CHARGE_SUCCESS:
            case CommonConstant.ORDER_STATUS_BEAUTY_ALREADY_GO:
            case CommonConstant.ORDER_STATUS_BEAUTY_ARRIVED:
                //do nothing
                break;
            case CommonConstant.ORDER_STATUS_SERVICE_OVER:
                //确认
                presenter.finishOrder(orderBean.getId());
                break;
            case CommonConstant.ORDER_STATUS_WAIT_TO_COMMENT:
                //评价
                Intent intent1;
                if(orderBean.getServiceType() == CommonConstant.SERVICE_TYPE_IN_HOME){
                    intent1  = new Intent(mContext, InHomeCommentActivity.class);
                }else{
                    intent1 = new Intent(mContext, ToShopCommentActivity.class);
                }
                intent1.putExtra(BundleConstant.ORDER_ID,orderBean.getId());
                intent1.putExtra(BundleConstant.BEAUTICIAN_ID, orderBean.getBeauticianId());
                intent1.putExtra(BundleConstant.PROJCET_NAME,orderBean.getProjectListStr());
                startActivityForResult(intent1, REQUEST_CODE_COMMENT);
                break;

            case CommonConstant.ORDER_STATUS_COMMENT_OVER:
            case CommonConstant.ORDER_STATUS_FAIL_TO_CHARGE:
            case CommonConstant.ORDER_STATUS_CANCELED_BY_CUSTOMER:
            case CommonConstant.ORDER_STATUS_DRAWBACK_SUCCESS:
                //再次预约、进入项目详情
            {
                Intent intent = new Intent(mContext,ProjectDetailActivity.class);
                intent.putExtra(BundleConstant.PROJCET_ID,orderBean.getMainProjectId());
                intent.putExtra(BundleConstant.SERVICE_TYPE,orderBean.getServiceType()+"");
                startActivity(intent);
            }
                break;
            case CommonConstant.ORDER_STATUS_ERROR:
            default:
                //do nothing
                break;
        }
    }

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 支付页面返回处理
		if ((requestCode == REQUEST_CODE_PAYMENT&&resultCode == Activity.RESULT_OK)
                ||(requestCode == REQUEST_CODE_COMMENT&&resultCode == Activity.RESULT_OK)
                ) {
            //clear cache
            RefreshManager.setNeedRefreshFlag(RefreshManager.ORDER_LIST_FRAGMENT);
            RefreshManager.setNeedRefreshFlag();
		}else if(REQUEST_CODE_SCAN_QRCODE == requestCode){
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String result = data.getExtras().getString("result");
                    RefereeBean refereeBean = (RefereeBean) QrCodeHelper.parseQrCode(QrCodeHelper.QRCODE_TYPE_BEAUTICIAN, result);
                    refereeBean.setOrderId(orderId);

                    Intent intent = new Intent(mContext, RefereePopUpWindowActivity.class);
                    intent.putExtra(BundleConstant.REFEREE_BEAN, refereeBean);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showLong(mContext,"UnsupportedEncodingException:"+e.toString());
                }
            }
        }
	}

    @Override
    public void findViews() {
        orderId = getIntent().getStringExtra("orderId");
        mContext = this;
        setContentView(R.layout.activity_order_detail);
        presenter = new OrderPresenter(this);

        tvServiceType = (TextView)findViewById(R.id.tvServiceType);
        tvState = (TextView)findViewById(R.id.tvState);
        tvOrderId = (TextView)findViewById(R.id.tvOrderId);
        tvCreateTime = (TextView)findViewById(R.id.tvCreateTime);
        tvBeauticianName = (TextView)findViewById(R.id.tvBeauticianName);
        tvProjectNames = (TextView)findViewById(R.id.tvProjectNames);
        tvPrice = (TextView)findViewById(R.id.tvPrice);
        tvContact = (TextView)findViewById(R.id.tvContact);
        tvContactPhone = (TextView)findViewById(R.id.tvContactPhone);
        tvServiceTime = (TextView)findViewById(R.id.tvServiceTime);
        tvServiceAddress = (TextView)findViewById(R.id.tvServiceAddress);
        tvRemark = (TextView)findViewById(R.id.tvRemark);
        tvBeautyParlorName = (TextView)findViewById(R.id.tvBeautyParlorName);
        tvOrderStatusRemark = (TextView)findViewById(R.id.tvOrderStatusRemark);

        tvFlow1 = (TextView)findViewById(R.id.tvFlow1);
        tvFlow2 = (TextView)findViewById(R.id.tvFlow2);
        tvFlow3 = (TextView)findViewById(R.id.tvFlow3);
        tvFlow4 = (TextView)findViewById(R.id.tvFlow4);
        tvFlow5 = (TextView)findViewById(R.id.tvFlow5);

        ivImg = (RoundImageView)findViewById(R.id.ivImg);

        ivFlow1 = (ImageView)findViewById(R.id.ivFlow1);
        ivFlow2 = (ImageView)findViewById(R.id.ivFlow2);
        ivFlow3 = (ImageView)findViewById(R.id.ivFlow3);
        ivFlow4 = (ImageView)findViewById(R.id.ivFlow4);
        ivFlow5 = (ImageView)findViewById(R.id.ivFlow5);

        llBeauticianGo = (LinearLayout)findViewById(R.id.llBeauticianGo);
        llComment = (LinearLayout)findViewById(R.id.llComment);
        rlComment = (RelativeLayout)findViewById(R.id.rlComment);
        btnAppend = (Button)findViewById(R.id.btnAppend);
        btnOperation = (Button)findViewById(R.id.btnOperation);
        btnOperation.setOnClickListener(this);

        btnSetReferee = (Button)findViewById(R.id.btnSetReferee);
        btnSetReferee.setOnClickListener(this);

        RefreshManager.setNeedRefreshFlag();//在onResume时候刷新
    }



    @Override
    public void refreshList(int orderType, List<OrderBean> dataList) {

    }

    @Override
    public void finishOrderDone() {
        /*重新获取数据，刷新页面*/
        presenter.getOrderDetail(orderId);
        /*清除列表的缓存*/
        RefreshManager.setNeedRefreshFlag(RefreshManager.ORDER_LIST_FRAGMENT);
    }

    @Override
    public void cancelOrderDone() {

    }

    @Override
    public void updateOrderDetail(OrderBean orderBean) {
        tvServiceType.setVisibility(View.VISIBLE);
        tvServiceType.setText(orderBean.getServiceType() == CommonConstant.SERVICE_TYPE_IN_HOME ? "上门" : "到店");
        llBeauticianGo.setVisibility(orderBean.getServiceType() == CommonConstant.SERVICE_TYPE_IN_HOME ?View.VISIBLE:View.GONE);
        tvOrderStatusRemark.setText(orderBean.getOrderStatusRemark());

        String keyword = "订单状态: ";
        String str = keyword+OrderHelper.getOrderStateStr(orderBean.getState());
        TextViewUtil.setSpecialTextColor(tvState,
                str,
                mContext.getResources().getColor(R.color.text_black),
                mContext.getResources().getColor(R.color.red),
                keyword.length()-1,
                str.length());

        tvOrderId.setText("订  单  号: " + orderId);
        tvCreateTime.setText("下单时间: " + orderBean.getCreateTime());
        tvBeauticianName.setText(orderBean.getBeauticianName());

        tvProjectNames.setText(orderBean.getProjectListStr());

        keyword = "实付金额: ";
        str = keyword+getString(R.string.symbol_rmb)+orderBean.getPrice();
        TextViewUtil.setSpecialTextColor(tvPrice,
                str,
                mContext.getResources().getColor(R.color.text_black),
                mContext.getResources().getColor(R.color.red),
                keyword.length()-1,
                str.length());

        tvContact.setText("联  系  人: " + orderBean.getAddressBean().getUserName());
        tvContactPhone.setText("联系方式: "+orderBean.getAddressBean().getUserPhone());
        tvServiceTime.setText("服务时间: "+orderBean.getServiceStartTime()+"~"+orderBean.getServiceEndTime().subSequence(11,orderBean.getServiceEndTime().length()));
        tvServiceAddress.setText("服务地址: "+orderBean.getAddressBean().getName());
        if(orderBean.getServiceType()==CommonConstant.SERVICE_TYPE_TO_SHOP){
            tvBeautyParlorName.setVisibility(View.VISIBLE);
            tvBeautyParlorName.setText("门店名称: "+orderBean.getBeautyParlorBean().getName());
        }
        tvRemark.setText("备注: "+(orderBean.getRemarks()==null?"":orderBean.getRemarks()));

        MyApp.instance.getImageLoad().displayImage(orderBean.getBeauticianImgUrl(), ivImg, DisplayImageOptionsUtil.getCommonCacheOptions());
        if(orderBean.getState()==CommonConstant.ORDER_STATUS_COMMENT_OVER){
            llComment.setVisibility(View.VISIBLE);
            rlComment.setOnClickListener(this);
        }else{
            llComment.setVisibility(View.GONE);
        }
        /*更新底部button*/
        String buttonText = OrderHelper.getOrderDetailStateOperationStr(orderBean.getState());
        btnOperation.setText(buttonText);
        if(buttonText.equals("进行中")){
            btnOperation.setBackgroundResource(R.drawable.gray_round_bg);
            btnOperation.setEnabled(false);
        }else{
            btnOperation.setBackgroundResource(R.drawable.round_red_deepred_selector);
            btnOperation.setEnabled(true);
        }
        /*更新追单button*/
        if(orderBean.getCancelAdd()==CommonConstant.ORDER_DETAIL_CANCEL_OR_APPEND_NONE){
            btnAppend.setVisibility(View.GONE);
        }else if(orderBean.getCancelAdd()==CommonConstant.ORDER_DETAIL_CANCEL_OR_APPEND_CANCEL){
            btnAppend.setVisibility(View.VISIBLE);
            btnAppend.setText("取消订单");
            btnAppend.setTag(orderBean.getCancelAdd());
            btnAppend.setOnClickListener(this);
        }else if(orderBean.getCancelAdd()==CommonConstant.ORDER_DETAIL_CANCEL_OR_APPEND_APPEND){
            btnAppend.setVisibility(View.VISIBLE);
            btnAppend.setText("追加订单");
            btnAppend.setTag(orderBean.getCancelAdd());
            btnAppend.setOnClickListener(this);
        }
        /*更新关联推荐人button*/
        if(orderBean.getIsShowReferee()==1){
            btnSetReferee.setVisibility(View.VISIBLE);
        }
        this.orderBean = orderBean;
        updateOrderStateFlow(orderBean.getState());
    }

    private void updateOrderStateFlow(int state) {
        switch (state){
            case CommonConstant.ORDER_STATUS_WAIT_TO_COMMENT:
            case CommonConstant.ORDER_STATUS_COMMENT_OVER:
            case CommonConstant.ORDER_STATUS_SERVICE_OVER:
                tvFlow5.setTextColor(getResources().getColor(R.color.text_black));
                ivFlow5.setBackgroundResource(R.mipmap.order_progress_selected);
            case CommonConstant.ORDER_STATUS_BEAUTY_ARRIVED:
                tvFlow4.setTextColor(getResources().getColor(R.color.text_black));
                ivFlow4.setBackgroundResource(R.mipmap.order_progress_selected);
            case CommonConstant.ORDER_STATUS_BEAUTY_ALREADY_GO:
                tvFlow3.setTextColor(getResources().getColor(R.color.text_black));
                ivFlow3.setBackgroundResource(R.mipmap.order_progress_selected);
            case CommonConstant.ORDER_STATUS_CHARGE_SUCCESS:
                tvFlow2.setTextColor(getResources().getColor(R.color.text_black));
                ivFlow2.setBackgroundResource(R.mipmap.order_progress_selected);
            case CommonConstant.ORDER_STATUS_WAIT_TO_CHARGE:
                tvFlow1.setTextColor(getResources().getColor(R.color.text_black));
                ivFlow1.setBackgroundResource(R.mipmap.order_progress_selected);
                break;

            case CommonConstant.ORDER_STATUS_FAIL_TO_CHARGE:
            case CommonConstant.ORDER_STATUS_CANCELED_BY_CUSTOMER:
            case CommonConstant.ORDER_STATUS_DRAWBACK_SUCCESS:
            case CommonConstant.ORDER_STATUS_ERROR:
            default:
                tvFlow1.setTextColor(getResources().getColor(R.color.text_gray));
                ivFlow1.setBackgroundResource(R.mipmap.order_progress_normal);
                tvFlow2.setTextColor(getResources().getColor(R.color.text_gray));
                ivFlow2.setBackgroundResource(R.mipmap.order_progress_normal);
                tvFlow3.setTextColor(getResources().getColor(R.color.text_gray));
                ivFlow3.setBackgroundResource(R.mipmap.order_progress_normal);
                tvFlow4.setTextColor(getResources().getColor(R.color.text_gray));
                ivFlow4.setBackgroundResource(R.mipmap.order_progress_normal);
                tvFlow5.setTextColor(getResources().getColor(R.color.text_gray));
                ivFlow5.setBackgroundResource(R.mipmap.order_progress_normal);

                break;
        }
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {

    }

    @Override
    public void setPresenter(Object presenter) {

    }
}
