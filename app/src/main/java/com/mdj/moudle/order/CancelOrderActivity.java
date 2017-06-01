package com.mdj.moudle.order;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mdj.R;
import com.mdj.cache.RefreshManager;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.order.bean.OrderBean;
import com.mdj.moudle.order.presenter.OrderContract;
import com.mdj.moudle.order.presenter.OrderPresenter;

import java.util.List;

public class CancelOrderActivity extends BaseActivity implements OrderContract.View,View.OnClickListener{
	private String orderId;
    private EditText etContent;
    private RelativeLayout rlReason1,rlReason2,rlReason3;
    private Button btnOk;
    private ImageView ivRadio1,ivRadio2,ivRadio3;

    private int currentSelect = 0;
    private static final String [] REASON_ARRAY = {"信息填写有误，重新预约","计划有变，下次再约了","其他"};
    private OrderContract.Presenter presenter;
/*********************************************************************************************************/
	@Override
	public void onResume() {
		super.onResume();
		// getOrderDetails();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		getOrderDetails();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.rlReason1:
                currentSelect = 0;
                ivRadio1.setBackgroundResource(R.mipmap.radio_selected);
                ivRadio2.setBackgroundResource(R.mipmap.radio_unselected);
                ivRadio3.setBackgroundResource(R.mipmap.radio_unselected);
                etContent.setVisibility(View.GONE);
                break;
            case R.id.rlReason2:
                currentSelect = 1;
                ivRadio1.setBackgroundResource(R.mipmap.radio_unselected);
                ivRadio2.setBackgroundResource(R.mipmap.radio_selected);
                ivRadio3.setBackgroundResource(R.mipmap.radio_unselected);
                etContent.setVisibility(View.GONE);
                break;
            case R.id.rlReason3:
                currentSelect = 2;
                ivRadio1.setBackgroundResource(R.mipmap.radio_unselected);
                ivRadio2.setBackgroundResource(R.mipmap.radio_unselected);
                ivRadio3.setBackgroundResource(R.mipmap.radio_selected);
                etContent.setVisibility(View.VISIBLE);
                break;
            case R.id.btnOk:
                StringBuilder reason = new StringBuilder();
                if(currentSelect == 2){
                    reason.append(REASON_ARRAY[currentSelect]+" "+etContent.getText().toString().trim());
                }else{
                    reason.append(REASON_ARRAY[currentSelect]);
                }
                presenter.cancelOrder(orderId,reason.toString());
                break;

		default:
			break;
		}
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// 支付页面返回处理
//		if (requestCode == REQUEST_CODE_PAYMENT) {
//            //clear cache
//            CacheManager.getInstance().getOrderListWrapperMap().clear();
//            finish();
//		}
	}


    @Override
    public void findViews() {
        orderId = getIntent().getStringExtra("orderId");
        mContext = this;
        setContentView(R.layout.activity_cancel_order);
        presenter = new OrderPresenter(this);

        rlReason1 = (RelativeLayout)findViewById(R.id.rlReason1);
        rlReason2 = (RelativeLayout)findViewById(R.id.rlReason2);
        rlReason3 = (RelativeLayout)findViewById(R.id.rlReason3);

        rlReason1.setOnClickListener(this);
        rlReason2.setOnClickListener(this);
        rlReason3.setOnClickListener(this);

        ivRadio1 = (ImageView)findViewById(R.id.ivRadio1);
        ivRadio2 = (ImageView)findViewById(R.id.ivRadio2);
        ivRadio3 = (ImageView)findViewById(R.id.ivRadio3);

        etContent = (EditText)findViewById(R.id.etContent);

        btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

    }

    @Override
    public void refreshList(int orderType, List<OrderBean> dataList) {

    }

    @Override
    public void finishOrderDone() {

    }

    @Override
    public void cancelOrderDone() {
        //clear cache
        RefreshManager.setNeedRefreshFlag(RefreshManager.ORDER_DETAIL_ACTIVITY);
        RefreshManager.setNeedRefreshFlag(RefreshManager.ORDER_LIST_FRAGMENT);
        finish();
    }

    @Override
    public void updateOrderDetail(OrderBean orderBean) {

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
