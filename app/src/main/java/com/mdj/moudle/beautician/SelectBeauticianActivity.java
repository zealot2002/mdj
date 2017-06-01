package com.mdj.moudle.beautician;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.mdj.R;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.beautician.presenter.SelectBeauticianContract;
import com.mdj.moudle.beautician.presenter.SelectBeauticianPresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.view.ListenedScrollView;
import com.mdj.view.MyListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class SelectBeauticianActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener
            ,SelectBeauticianContract.View, ListenedScrollView.OnScrollListener {
    public static final String RESULT_CODE_SELECT_BEAUTICIAN = "select_beautician";

    private Button btnAll,btnOrderMost,btnAppriaseMost;

    private List<BeauticianBean> dataList;
    private MyListView lvBeauticianList;
    private BeauticianListAdapter adapter;

    private ListenedScrollView observableScrollView;

    private String location,projectParams,orderDate,startTime,beautyParlorId;
    private int serviceType;
    private int currentType = CommonConstant.BEAUTICIAN_LIST_TYPE_ALL;
    private SelectBeauticianContract.Presenter presenter;

/***************************************************************************************************/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAll:
                updateButton(R.id.btnAll);
                currentType = CommonConstant.BEAUTICIAN_LIST_TYPE_ALL;
                break;

            case R.id.btnOrderMost:
                updateButton(R.id.btnOrderMost);
                currentType = CommonConstant.BEAUTICIAN_LIST_TYPE_MOST_ORDER_NUM;
                break;

            case R.id.btnAppriaseMost:
                updateButton(R.id.btnAppriaseMost);
                currentType = CommonConstant.BEAUTICIAN_LIST_TYPE_MOST_APPRAISE_NUM;
                break;
            default:
                break;
        }
        presenter.getAvailableBeauticianListPre(serviceType,
                beautyParlorId,location, projectParams, orderDate, startTime, currentType);
    }

    private void updateButton(int buttonId) {
        btnAll.setBackgroundResource(R.drawable.left_round_gray_hollow_bg);
        btnAll.setTextColor(getResources().getColor(R.color.text_gray));
        btnOrderMost.setBackgroundResource(R.drawable.gray_hollow_bg);
        btnOrderMost.setTextColor(getResources().getColor(R.color.text_gray));
        btnAppriaseMost.setBackgroundResource(R.drawable.right_round_gray_hollow_bg);
        btnAppriaseMost.setTextColor(getResources().getColor(R.color.text_gray));

        switch (buttonId) {
            case R.id.btnAll:
                btnAll.setBackgroundResource(R.drawable.left_round_gray_bg);
                btnAll.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.btnOrderMost:
                btnOrderMost.setBackgroundResource(R.drawable.gray_bg);
                btnOrderMost.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.btnAppriaseMost:
                btnAppriaseMost.setBackgroundResource(R.drawable.right_round_gray_bg);
                btnAppriaseMost.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MobclickAgent.onPageStart(CommonUtil.generateTag());
            MobclickAgent.onResume(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            MobclickAgent.onPageEnd(CommonUtil.generateTag());
            MobclickAgent.onPause(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //传回给调用者
        setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_CODE_SELECT_BEAUTICIAN,
                dataList.get(position)));
        finish();
    }

    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.select_beautician);

        btnAll = (Button) findViewById(R.id.btnAll);
        btnOrderMost = (Button) findViewById(R.id.btnOrderMost);
        btnAppriaseMost = (Button) findViewById(R.id.btnAppriaseMost);

        btnAll.setOnClickListener(this);
        btnOrderMost.setOnClickListener(this);
        btnAppriaseMost.setOnClickListener(this);

        observableScrollView = (ListenedScrollView)findViewById(R.id.observableScrollView);
        //设置监听。
        observableScrollView.setOnScrollListener(this);

        presenter = new SelectBeauticianPresenter(this);

        serviceType = getIntent().getIntExtra("serviceType", CommonConstant.SERVICE_TYPE_IN_HOME);
        if(serviceType == CommonConstant.SERVICE_TYPE_IN_HOME){
            location = getIntent().getStringExtra("location");
        }else{
            beautyParlorId = getIntent().getStringExtra("beautyParlorId");
        }
        projectParams = getIntent().getStringExtra("projectParams");
        orderDate = getIntent().getStringExtra("orderDate");
        startTime = getIntent().getStringExtra("startTime");

        presenter.getAvailableBeauticianListPre(serviceType,
                beautyParlorId,location, projectParams, orderDate, startTime, CommonConstant.BEAUTICIAN_LIST_TYPE_ALL);
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        dataList = (List<BeauticianBean>) data;
        if (adapter == null) {
            adapter = new BeauticianListAdapter(this, dataList);
            lvBeauticianList = (MyListView) this.findViewById(R.id.lvBeauticianList);
            lvBeauticianList.setOnItemClickListener(this);
        }
        lvBeauticianList.setAdapter(adapter);
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void onBottomArrived() {
        presenter.loadMorePre(currentType);
    }

    @Override
    public void onScrollStateChanged(ListenedScrollView view, int scrollState) {

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {

    }
}
