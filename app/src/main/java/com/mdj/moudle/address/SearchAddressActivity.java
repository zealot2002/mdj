package com.mdj.moudle.address;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.mdj.R;
import com.mdj.cache.CacheManager;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.city.CityBean;
import com.mdj.moudle.city.CityListActivity;
import com.mdj.moudle.widget.TitleWidget;
import com.mdj.utils.ToastUtils;
import com.mdj.view.ClearEditText;
import com.mdj.view.MyListView;

import java.util.ArrayList;
import java.util.List;

public class SearchAddressActivity extends BaseActivity implements View.OnClickListener, OnGetPoiSearchResultListener, OnGetGeoCoderResultListener, AdapterView.OnItemClickListener {
    public static final String RESULT_CODE_SEARCH_ADDRESS= "search_address";
    private static final int REQUEST_CODE_SELECT_CITY = 1;
    //搜索框
    private ClearEditText etKeyword;

    //搜索结果
    private MyListView lvSearchResultList;
    private TextView tvResultType;
    // POI检索
    private PoiSearch mPoiSearch = null;  /*兴趣点检索*/
    private int currentPageIndex = 0;

    private GeoCoder mSearch = null;      /*坐标点检索*/
    private LocationClient mLocClient;

    private double curLng,curLat;
    private List<AddressBean> searchAddressBeanList = new ArrayList<>();
    private SearchAddressListAdapter adapter;
    Handler handler = new Handler();
    private TitleWidget titleWidget;
    private CityBean selectCityBean;  //选择的城市
    private String locationCityName;  //定位的城市

    private boolean hideRight=false;
    private String tagStr;
    /***************************************************方法区**********************************************************/
    @Override
    public void findViews() {
        setContentView(R.layout.search_address);
        mContext = this;


        etKeyword = (ClearEditText)findViewById(R.id.etKeyword);
        lvSearchResultList = (MyListView)findViewById(R.id.lvSearchResultList);
        tvResultType = (TextView)findViewById(R.id.tvResultType);

        titleWidget = (TitleWidget)findViewById(R.id.titleWidget);
        try{
            hideRight = getIntent().getBooleanExtra(BundleConstant.HIDE_RIGHT,false);
            tagStr = getIntent().getStringExtra("tagStr");
        }catch (Exception e){
            e.printStackTrace();
        }
        if(hideRight){
            titleWidget.setCustomRightBtnBackground(0);
            titleWidget.setCustomRightBtnText("");
        }else{
            titleWidget.setCustomRightBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 跳转到城市列表选择
                    startActivityForResult(new Intent(mContext,
                            CityListActivity.class), REQUEST_CODE_SELECT_CITY);
                }
            });
            titleWidget.setCustomRightBtnBackground(R.mipmap.down_gray);
            titleWidget.setCustomRightBtnText(CacheManager.getInstance().getGlobalCity().getName());
        }
        setListener();
        initData();
        selectCityBean = CacheManager.getInstance().getGlobalCity();//使之等于全局city
    }

    public void initData() {
        initPoiSearch();
    }

    private void initPoiSearch(){
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mLocClient = new LocationClient(mContext);
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                closeLoading();
                //反注册位置信息监听
                if (mLocClient != null) {
                    mLocClient.stop();
                }
                mLocClient.unRegisterLocationListener(this);
                //
                if (location == null) {
                    ToastUtils.showLong(mContext,"获取位置信息失败,请刷新重试");
                    return;
                }
                locationCityName = location.getCity();
                curLat = location.getLatitude();
                curLng = location.getLongitude();

                if (isAllCitySame()) {
                    searchCurrentPosition(curLat,curLng);
                }else{
//                    showShortToast("您所在城市与首页城市不同");
                    showEmptyList();
                }
            }
        });
        //注册定位监听接口
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll"); //设置坐标类型
        option.setScanSpan(1000);
        option.setAddrType("all");
        option.setIsNeedAddress(true);
        option.setTimeOut(15);
        mLocClient.setLocOption(option);
        mLocClient.start();
        showLoading();
    }

    private void updateUI() {

    }

    //利用当前坐标搜索周边
    private void searchCurrentPosition(double curLat,double curLng){
        final LatLng ll = new LatLng(curLat,curLng);
        handler.post(new Runnable() {
            @Override
            public void run() {
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
            }
        });
        showLoading();
    }
    // 关键字搜索
    private void searchByKeyword(String keyword,int pageIndex){
        currentPageIndex = pageIndex;
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .city(selectCityBean.getName())
                .keyword(keyword)
                .pageNum(currentPageIndex));
        showLoading();
    }
    public void setListener() {
        etKeyword.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                searchAddressBeanList.clear();

                if (cs.length() > 0) {
                    tvResultType.setText("搜索结果");
                    try {
                        searchByKeyword(cs.toString(),0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isAllCitySame()) {
                        tvResultType.setText("当前位置");
                        // 调用当前位置搜索
                        searchCurrentPosition(curLat, curLng);
                    } else {
                        //show empty
                        tvResultType.setText("搜索结果");
                        showEmptyList();
                    }
                    return;
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(final Editable s) {
            }
        });
    }

    /*******
     *
     *
     *
     * */
    private boolean isAllCitySame(){
        return ((locationCityName.equals(CacheManager.getInstance().getGlobalCity().getName())  //等于或包含
                || locationCityName.contains(CacheManager.getInstance().getGlobalCity().getName()))
                &&
                (locationCityName.equals(selectCityBean.getName())  //等于或包含
                || locationCityName.contains(selectCityBean.getName()))
        );
    }
    private void showEmptyList() {
        refreshList(new ArrayList<PoiInfo>(),"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.rlNoInputSearch:
//                rlNoInputSearch.setVisibility(View.GONE);
//                llInputSearch.setVisibility(View.VISIBLE);
//
//                break;

            default:
                break;
        }
    }

    private void refreshList(List<PoiInfo> poiList,String keyword){
        if(poiList==null){
            showEmptyList();
            return;
        }
        for(PoiInfo poiInfo:poiList){
            AddressBean bean = new AddressBean();
            bean.setAddress(poiInfo.address);
            bean.setLat(poiInfo.location.latitude + "");
            bean.setLng(poiInfo.location.longitude+"");
            bean.setName(poiInfo.name);
            searchAddressBeanList.add(bean);
        }
        if(adapter == null){
            adapter = new SearchAddressListAdapter(mContext);
            lvSearchResultList.setAdapter(adapter);
            View emptyView = findViewById(R.id.rlEmpty);
            lvSearchResultList.setEmptyView(emptyView);
            lvSearchResultList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // 当不滚动时
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        // 判断是否滚动到底部
                        if (view.getLastVisiblePosition() == view.getCount() - 1) {
                            // 加载更多功能的代码
                            if (!TextUtils.isEmpty(etKeyword.getText().toString().trim())) {
                                currentPageIndex++;
                                searchByKeyword(etKeyword.getText().toString().trim(), currentPageIndex);
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
            lvSearchResultList.setOnItemClickListener(this);
        }

        adapter.setDataList(searchAddressBeanList,keyword);
        adapter.notifyDataSetChanged();
    }
    //根据keyword搜索结果
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        closeLoading();
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            if (poiResult != null) {
                refreshList(poiResult.getAllPoi(),etKeyword.getText().toString().trim());
            }
            return;
        }
        showTips(mContext, R.mipmap.tips_warning, "抱歉，未能找到结果");
        currentPageIndex--;
        showEmptyList();
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }
    //根据坐标搜索结果
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        closeLoading();
        if (reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
            if (reverseGeoCodeResult != null) {
                refreshList(reverseGeoCodeResult.getPoiList(),etKeyword.getText().toString().trim());
            }
            return;
        }
        showTips(mContext, R.mipmap.tips_warning, "抱歉，未能找到结果");
        showEmptyList();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_SELECT_CITY == requestCode
                && Activity.RESULT_OK == resultCode){
            selectCityBean = (CityBean)data.getSerializableExtra("cityBean");
            titleWidget.setCustomRightBtnText(selectCityBean.getName());

            searchAddressBeanList.clear();

            if(TextUtils.isEmpty(etKeyword.getText().toString().trim())){//keyword是空的
                if(isAllCitySame()){
                    searchCurrentPosition(curLat,curLng);
                }else{
                    showEmptyList();
                }
            }else {
                searchByKeyword(etKeyword.getText().toString().trim(),0);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //追加cityId
        searchAddressBeanList.get(position).setCityId(selectCityBean.getId());
        //传回给调用者
        Intent intent = new Intent();
        intent.putExtra(RESULT_CODE_SEARCH_ADDRESS, searchAddressBeanList.get(position));
        intent.putExtra("tagStr",tagStr);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
