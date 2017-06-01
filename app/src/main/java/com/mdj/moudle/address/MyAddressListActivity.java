package com.mdj.moudle.address;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;

import com.mdj.R;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.address.presenter.MyAddressListContract;
import com.mdj.moudle.address.presenter.MyAddressListPresenter;
import com.mdj.utils.AlertDialogUtils;
import com.mdj.view.MyListView;

import java.util.List;

public class MyAddressListActivity extends BaseActivity implements View.OnClickListener, MyAddressListAdapter.OnClickListener
                    ,MyAddressListContract.View{
    public static final String EDIT_ADDRESS = "edit_address";
    private static final int REQUEST_CODE_ADD_OR_EDIT_ADDRESS = 1;

    private List<AddressBean> dataList;
    private MyAddressListAdapter adapter;
    private MyListView lvAddressList;
    private Button btnAddAddress;

    private MyAddressListContract.Presenter presenter;
    /***************************************************方法区**********************************************************/
    @Override
    public void findViews() {
        mContext = this;
        presenter = new MyAddressListPresenter(this);
        presenter.start();
        setContentView(R.layout.my_address_list);
        lvAddressList = (MyListView)findViewById(R.id.lvAddressList);
        /*设置滑动监听*/
        lvAddressList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount){
                    presenter.loadMoreData();
                }
            }
        });

        btnAddAddress = (Button)findViewById(R.id.btnAddAddress);
        btnAddAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddAddress:
                startActivityForResult(new Intent(mContext, EditAddressActivity.class),REQUEST_CODE_ADD_OR_EDIT_ADDRESS);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_ADD_OR_EDIT_ADDRESS == requestCode&& Activity.RESULT_OK==resultCode){
            presenter.start();
        }
    }

    @Override
    public void onEditButtonClick(int position) {
        Intent intent = new Intent(mContext, EditAddressActivity.class);
        intent.putExtra(EDIT_ADDRESS,dataList.get(position));
        startActivityForResult(intent, REQUEST_CODE_ADD_OR_EDIT_ADDRESS);
    }

    @Override
    public void onDeleteButtonClick(final int position) {
        AlertDialogUtils.onPopupButtonClick(mContext,"确认要删除该地址吗？", new AlertDialogUtils.doSomethingListener() {
            @Override
            public void doSomething() {
                presenter.deleteAddress(dataList.get(position).getId());
            }
        });
    }

    @Override
    public void onDefaultCheckBoxClick(int position) {
        for(int i=0;i<dataList.size();i++){
            dataList.get(i).setIsDefault(false);
        }
        dataList.get(position).setIsDefault(true);
        adapter.setDataList(dataList);
        adapter.notifyDataSetChanged();
        presenter.setDefaultAddress(dataList.get(position).getId());
    }
    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }
    @Override
    public void updateUI(Object dataList) {
        this.dataList = (List<AddressBean>)dataList;
        if(adapter==null){
            adapter = new MyAddressListAdapter(this,this);
            lvAddressList.setAdapter(adapter);
        }
        adapter.setDataList(this.dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void setDefaultAddressDone(boolean result) {
        if(result){

        }
    }
}
