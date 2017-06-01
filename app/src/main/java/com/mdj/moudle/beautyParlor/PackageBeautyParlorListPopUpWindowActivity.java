package com.mdj.moudle.beautyParlor;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.project.bean.ProjectBean;
import com.mdj.moudle.userPackage.presenter.MyPackageListContract;
import com.mdj.moudle.userPackage.presenter.MyPackageListPresenter;
import com.mdj.view.MyListView;

import java.util.ArrayList;
import java.util.List;

public class PackageBeautyParlorListPopUpWindowActivity extends BaseActivity implements View.OnClickListener, MyPackageListContract.View {
    private ImageButton btnClose;
    private MyListView lvBeautyParlorList;

    private List<String> beautyParlorNameList = new ArrayList<>();
    private PackageBeautyParlorListAdapter adapter;
    private TextView tvCityName;

    private String packageId;
    private MyPackageListContract.Presenter presenter;
/******************************************************************************************************/
    @Override
    public void findViews() {
        mContext = this;
        setContentView(R.layout.package_beauty_parlor_list_popup_window);

        packageId = getIntent().getStringExtra(BundleConstant.PACKAGE_ID);
        btnClose = (ImageButton)findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

        tvCityName = (TextView)findViewById(R.id.tvCityName);
        lvBeautyParlorList = (MyListView)findViewById(R.id.lvBeautyParlorList);

        presenter = new MyPackageListPresenter(this);
        presenter.getFitableBeautyParlorList(packageId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void updateProjectInfo(ProjectBean projectBean) {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void showDisconnect(String msg) {

    }

    @Override
    public void updateUI(Object data) {
        beautyParlorNameList.addAll((List<String>)data);
        tvCityName.setText(CacheManager.getInstance().getGlobalCity().getName());
        adapter = new PackageBeautyParlorListAdapter(beautyParlorNameList);
        lvBeautyParlorList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    public class PackageBeautyParlorListAdapter extends BaseAdapter {
        private List<String> dataList;

        public PackageBeautyParlorListAdapter(List<String> dataList) {
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList == null ? 0 : dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position == 0 ? null : dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setDataList(List<String> dataList){
            this.dataList = dataList;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                ViewHolder holder = null;
                if (convertView == null) {
                        convertView = View.inflate(mContext, R.layout.package_beauty_parlor_list_item, null);
                        holder = new ViewHolder();
                        holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                        convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.tvName.setText(dataList.get(position));
            }catch (Exception e){
                Toast.makeText(mContext, "PackageBeautyParlorListAdapter Exception :" + e.toString(), Toast.LENGTH_SHORT).show();
            }
            return convertView;
        }

        public class ViewHolder {
            private TextView tvName;
        }
    }
}
