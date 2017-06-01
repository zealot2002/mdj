package com.mdj.moudle.city;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.city.BladeView.OnItemClickListener;
import com.mdj.moudle.city.presenter.CityListContract;
import com.mdj.moudle.city.presenter.CityListPresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.utils.ListViewUtils;
import com.umeng.analytics.MobclickAgent;
import com.zhy.flowlayout.FlowLayout;
import com.zhy.flowlayout.TagFlowLayout.OnTagClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CityListActivity extends BaseActivity implements
        android.widget.AdapterView.OnItemClickListener, OnTagClickListener,View.OnClickListener,
                                CityListContract.View{
	private LinearLayout llDisconnectTipsLayout, llBody;

	private MySectionIndexer mIndexer;
	private List<CityBean> otherCityList = new ArrayList<CityBean>();
	private List<CityBean> hotCityList = new ArrayList<CityBean>();
	
	private CityListAdapter mAdapter;
	private int[] counts;
	private List<String> sections = new ArrayList<>();
	private PinnedHeaderListView listView;
	private BladeView mLetterListView;

    private CityListContract.Presenter presenter;

    @Override
    public void findViews() {
        mContext = this;
        presenter = new CityListPresenter(this);
        setContentView(R.layout.new_city_list);

        llDisconnectTipsLayout = (LinearLayout) findViewById(R.id.llDisconnectTipsLayout);
        llBody = (LinearLayout) findViewById(R.id.llBody);
        llDisconnectTipsLayout.setOnClickListener(this);

        listView = (PinnedHeaderListView) findViewById(R.id.mListView);
        mLetterListView = (BladeView) findViewById(R.id.mLetterListView);
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
    public void updateUI(Object cityList) {
		showActivityBody();
		
		//热门城市
		List<String> hotCityArray = new ArrayList<>();
		for(CityBean b:(List<CityBean>)cityList){
			if(b.getIsHot().equals(1)){
				hotCityArray.add(b.getName());
				hotCityList.add(b);
			}else{
				otherCityList.add(b);
			}
		}
//其他城市
		//排序列表
		Collections.sort(otherCityList, new MyComparator()); 
		//统计列表中有多少个不同字母
		sections.clear();
		sections.add("#");//加入第一项
		for(CityBean c : otherCityList){
			if(!sections.contains(c.getPys()))
				sections.add(c.getPys());//列表排序过了，所以字母也是排序好的，直接add
		}
		// 初始化每个字母有多少个item
		counts = new int[sections.size()];
		counts[0] = 1;//加入第一项
		for (CityBean city : otherCityList) { // 计算全部城市
			String sk = city.getSortKey();
			int index = sections.indexOf(sk);
			counts[index]++;
		}
		mIndexer = new MySectionIndexer((String[]) sections.toArray(new String[sections.size()]), counts);
		mAdapter = new CityListAdapter(hotCityArray,otherCityList, mIndexer,this);
		
		listView.setAdapter(mAdapter);
		ListViewUtils.fixListViewHeight(listView);
		listView.setOnScrollListener(mAdapter);

		// 设置顶部固定头部
		listView.setPinnedHeaderView(LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.list_group_item,
						listView, false));

		mLetterListView.setKeys((String[]) sections.toArray(new String[sections.size()]));
		mLetterListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String s) {
                if (s != null) {
                    int section = sections.indexOf(s);
                    int position = mIndexer.getPositionForSection(section);
                    if (position != -1) {
                        listView.setSelection(position);
                    }
                }
            }
        });
        mLetterListView.setVisibility(View.VISIBLE);
        mLetterListView.invalidate();
	}

    @Override
    public void showDisconnect(String msg) {
        showTips(mContext, R.mipmap.tips_warning, getResources()
                .getString(R.string.net_error));
        llBody.setVisibility(View.INVISIBLE);
        llDisconnectTipsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    public class MyComparator implements Comparator<CityBean> {
		@Override
		public int compare(CityBean c1, CityBean c2) {
			return c1.getSortKey().compareTo(c2.getSortKey());
		}
	}

	private void showActivityBody() {
		llBody.setVisibility(View.VISIBLE);
		llDisconnectTipsLayout.setVisibility(View.GONE);
	}

	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.llDisconnectTipsLayout:
                presenter.start();
                break;
		default:
			break;
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		returnSelectedCity(otherCityList.get(position - 1));
	}

	@Override
	public boolean onTagClick(View view, int position, FlowLayout parent) {
        if(hotCityList.get(position).getName().equals(CacheManager.getInstance().getGlobalCity().getName())){
            return false;
        }
		returnSelectedCity(hotCityList.get(position));
		return false;
	}
	
	private void returnSelectedCity(CityBean cityBean){
        // 设置返回值
        setResult(Activity.RESULT_OK, new Intent().putExtra(BundleConstant.CITY_BEAN,cityBean));
		finish();
	}
}
