package com.mdj.moudle.city;

import android.view.KeyEvent;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.mdj.R;
import com.mdj.moudle.BaseActivity;
import com.mdj.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class CityMapActivity extends BaseActivity {
	private MapView mMapView;
	private CityBean cityBean;

    @Override
    public void findViews() {
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.city_map);
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        cityBean = (CityBean) getIntent().getExtras().getSerializable(
                "cityBean");
        updateUI();

    }

	private void updateUI() {
		List<OverlayOptions> overlayOptionsList = new ArrayList<>();

		for (int i = 0; i < cityBean.getAreaPointList().size(); i++) {
			List<MapPoint> MapPointList = cityBean.getAreaPointList().get(i);
			List<LatLng> pts = new ArrayList<LatLng>();
			for (int j = 0; j < MapPointList.size(); j++) {
				try {
					MapPoint p = MapPointList.get(j);
					pts.add(new LatLng(Double.valueOf(p.getLat()), Double.valueOf(p
							.getLng())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(MapPointList.size()>2){//小于3个点的暂时不绘制
				OverlayOptions polygonOption = new PolygonOptions().points(pts)
						.stroke(new Stroke(5, 0xffff5c67)).fillColor(0x3fff5c67);//bd
				overlayOptionsList.add(polygonOption);
			}
		}
		
		BaiduMap mBaiduMap = mMapView.getMap();

		// 定义Maker坐标点
		LatLng point = new LatLng(Double.valueOf(cityBean.getAreaPointList().get(0).get(0).getLat()),
				Double.valueOf(cityBean.getAreaPointList().get(0).get(0).getLng()));

		// 设置地图中心位置
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(point, 12.0f);//12倍
		mBaiduMap.setMapStatus(mapStatusUpdate);
		
		// 普通地图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		for (OverlayOptions polygonOption:overlayOptionsList) {
			mBaiduMap.addOverlay(polygonOption);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			MobclickAgent.onPageStart(CommonUtil.generateTag());
			MobclickAgent.onResume(this);
			mMapView.onResume();
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
			mMapView.onPause();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

}