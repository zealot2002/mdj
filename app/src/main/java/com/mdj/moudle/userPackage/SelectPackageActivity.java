package com.mdj.moudle.userPackage;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.userPackage.presenter.PackageContract;
import com.mdj.moudle.userPackage.presenter.PackagePresenter;
import com.mdj.utils.MdjLog;
import com.mdj.view.MyListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectPackageActivity extends BaseActivity implements SelectPackageListItemListEventListener,
        View.OnClickListener ,PackageContract.View{
	private MyListView lvPackageList;
	private SelectPackageListAdapter selectPackageListAdapter;
	private List<OrderProjectPackageVo> dataList = new ArrayList<>();
	private CheckBox cbNoUse;
    private RelativeLayout rlSubmit;
    private String projectParams;

    private PackageContract.Presenter presenter;
/***************************************************方法区**********************************************************/
    private void initListView() {
        lvPackageList = (MyListView) findViewById(R.id.lvPackageList);
    }

	@Override
	public void OnEvent(int list1Position, int list2Position, ButtonAction buttonAction) {
		MdjLog.log(" list1Position = " + list1Position + " list2Position = " + list2Position + " buttonAction = " + buttonAction);
		int projectNum = dataList.get(list1Position).getBuyNum();
		if (buttonAction.equals(ButtonAction.ActionAdd)) {// 加
			int freeNum = 0;
			for (OrderProjectPackageVo.PackageUseForOrderProjectVo vo : dataList.get(list1Position).getPackageUseForOrderProjectVoList()) {
				freeNum += vo.getAllocNum();
			}
			if (freeNum < projectNum) {// 总抵用数量 小于 购买数量
				OrderProjectPackageVo.PackageUseForOrderProjectVo vo = dataList.get(list1Position).getPackageUseForOrderProjectVoList().get(list2Position);
				if (vo.getAllocNum() < vo.getAvailableNum()) {// 分配次数 小于 可用次数
					// 修改详情数据
					dataList.get(list1Position).getPackageUseForOrderProjectVoList().get(list2Position).setAllocNum(1 + vo.getAllocNum());
					// 刷新list
                    selectPackageListAdapter.setInnerDataList(dataList.get(list1Position).getPackageUseForOrderProjectVoList(), list1Position);

                    // 修改上部数据
					dataList.get(list1Position).setFreeNum(1 + dataList.get(list1Position).getFreeNum());
					// 刷新list
					selectPackageListAdapter.setDataList(dataList);
					selectPackageListAdapter.notifyDataSetChanged();
				}
			} else {
				// do nothing..
			}
		} else {// 减
			OrderProjectPackageVo.PackageUseForOrderProjectVo vo = dataList.get(list1Position).getPackageUseForOrderProjectVoList().get(list2Position);
			if (vo.getAllocNum() > 0) {
				// 修改详情数据
				dataList.get(list1Position).getPackageUseForOrderProjectVoList().get(list2Position).setAllocNum(vo.getAllocNum() - 1);
                // 刷新list
                selectPackageListAdapter.setInnerDataList(dataList.get(list1Position).getPackageUseForOrderProjectVoList(), list1Position);

                // 修改上部数据
				dataList.get(list1Position).setFreeNum(dataList.get(list1Position).getFreeNum() - 1);
				// 刷新list
				selectPackageListAdapter.setDataList(dataList);
				selectPackageListAdapter.notifyDataSetChanged();
			} else {
				// do nothing..
			}
		}
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlSubmit:
                Intent intent = new Intent();
                if(cbNoUse.isChecked()){
                    //传回给调用者
                    intent.putExtra(BundleConstant.USE_PACKAGE, false);
                }else{
                    boolean hasUseItem = false;
                    for (OrderProjectPackageVo vo : dataList) {
                        if (vo.getFreeNum() > 0) {
                            hasUseItem = true;
                            break;
                        }
                    }
                    if (hasUseItem) {
                        intent.putExtra(BundleConstant.USE_PACKAGE, true);
                        intent.putExtra(BundleConstant.DATA_LIST, (Serializable) dataList);
                    } else {
                        intent.putExtra(BundleConstant.USE_PACKAGE, false);
                    }
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void findViews() {
        this.setContentView(R.layout.select_package);
        mContext = this;
        projectParams = getIntent().getStringExtra(BundleConstant.PROJCET_PARAMS);

        cbNoUse = (CheckBox) findViewById(R.id.cbNoUse);
        rlSubmit = (RelativeLayout) findViewById(R.id.rlSubmit);
        rlSubmit.setOnClickListener(this);

        initListView();
        presenter = new PackagePresenter(this);
        presenter.getAvailablePackageList(projectParams);
    }

    @Override
    public void updateSuggestPackageList(List<PackageBean> suggestPackageList) {

    }

    @Override
    public void updateNormalPackageList(List<PackageBean> packageList) {

    }

    @Override
    public void updateBeautyParlorPackageList(List<PackageBean> packageList) {

    }

    @Override
    public void updateAvailablePackageList(List<OrderProjectPackageVo> dataList) {
        this.dataList = dataList;
        selectPackageListAdapter = new SelectPackageListAdapter(this,dataList);
        lvPackageList.setAdapter(selectPackageListAdapter);
        selectPackageListAdapter.notifyDataSetChanged();
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
