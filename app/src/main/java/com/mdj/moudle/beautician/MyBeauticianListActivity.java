package com.mdj.moudle.beautician;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.beautician.bean.BeauticianBean;
import com.mdj.moudle.beautician.presenter.MyBeauticianListContract;
import com.mdj.moudle.beautician.presenter.MyBeauticianListPresenter;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.utils.CommonUtil;
import com.mdj.view.MyListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class MyBeauticianListActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener,MyBeauticianListContract.View{
    private LinearLayout llDisconnectTipsLayout, llBody;
    private List<BeauticianBean> dataList;
    private MyListView lvBeauticianList;
    private BeauticianListAdapter adapter;
    private TitleWidget titleWidget;

    private MyBeauticianListContract.Presenter presenter;
/****************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showDisconnect(String msg) {
        llBody.setVisibility(View.INVISIBLE);
        llDisconnectTipsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateUI(Object data) {
        this.dataList = (List<BeauticianBean>)data;
        if(dataList.size()==0){
            return;
        }
        showActivityBody();
        if (adapter == null) {
            adapter = new BeauticianListAdapter(this, dataList);
            lvBeauticianList = (MyListView) this.findViewById(R.id.lvBeauticianList);
            lvBeauticianList.setOnItemClickListener(this);
        }
        lvBeauticianList.setAdapter(adapter);
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();
    }

    private void showActivityBody() {
        llBody.setVisibility(View.VISIBLE);
        llDisconnectTipsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDisconnectTipsLayout:
//                getMyCouponList();
                break;

            default:
                break;
        }
    }

    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.activity_my_beautician);

        titleWidget = (TitleWidget)findViewById(R.id.titleWidget);
        titleWidget.setCurrentScreen(TitleWidget.SCREEN_ID.SCREEN_MY_BEAUTICIAN);

        llBody = (LinearLayout) findViewById(R.id.llBody);
        llDisconnectTipsLayout = (LinearLayout) findViewById(R.id.llDisconnectTipsLayout);
        llDisconnectTipsLayout.setOnClickListener(this);

        presenter = new MyBeauticianListPresenter(this);
        presenter.start();
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
        Intent intent = new Intent(mContext,BeauticianDetailActivity.class);
        intent.putExtra(BundleConstant.BEAUTICIAN_ID,dataList.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void setPresenter(Object presenter) {

    }
}
