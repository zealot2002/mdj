package com.mdj.moudle.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.mdj.R;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.home.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: GuideActivity.java
 * @Package com.mdj.ui
 * @Description: 引导页。
 * @author hwk
 * @date 2015-3-24 下午2:10:05
 * @version V1.0
 */
public class GuideActivity extends BaseActivity implements OnTouchListener, OnClickListener {
	private LayoutInflater mInflater;
	private ViewPager pager_splash_ad;
	private ADPagerAdapter adapter;
	private int flaggingWidth;
	private int size = 0;
	private int lastX = 0;
	private int currentIndex = 0;
	private boolean locker = true;
	private View viewMain;
	private Button btn;// 跳转到主页

/***************************************************************************************************/
	private void initFir() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		flaggingWidth = dm.widthPixels / 2;

		// List<String> splash_ad = new ArrayList<String>();
		// splash_ad.add("splash_ad");
		pager_splash_ad = (ViewPager) findViewById(R.id.pager_splash_ad);

		List<View> views = new ArrayList<View>();
		// for (String str : splash_ad) {
		View view = LayoutInflater.from(this).inflate(R.layout.view_splash_ad, null);
		ImageView iv_ad = (ImageView) view.findViewById(R.id.iv_ad);
		iv_ad.setImageResource(R.mipmap.help1);
		views.add(view);// 第一张

		View view1 = LayoutInflater.from(this).inflate(R.layout.view_splash_ad, null);
		ImageView iv_ad1 = (ImageView) view1.findViewById(R.id.iv_ad);
		iv_ad1.setImageResource(R.mipmap.help2);
		views.add(view1);// 第二张

		View view2 = LayoutInflater.from(this).inflate(R.layout.view_splash_ad, null);
		ImageView iv_ad2 = (ImageView) view2.findViewById(R.id.iv_ad);
		iv_ad2.setImageResource(R.mipmap.help3);
		views.add(view2);// 第三张

		View view3 = LayoutInflater.from(this).inflate(R.layout.view_splash_ad, null);
		ImageView iv_ad3 = (ImageView) view3.findViewById(R.id.iv_ad);
		iv_ad3.setImageResource(R.mipmap.help4);
		views.add(view3);

		// View view4 =
		// LayoutInflater.from(this).inflate(R.layout.view_splash_ad, null);
		// ImageView iv_ad4 = (ImageView) view3.findViewById(R.id.iv_ad);
		// iv_ad4.setImageResource(R.drawable.help5);
		// views.add(view4);

		views.add(viewMain);
		// }
		size = views.size();
		adapter = new ADPagerAdapter(this, views);
		pager_splash_ad.setAdapter(adapter);
		if (views.size() == 1) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					gotoMain();
				}
			}, 1000);
		} else {
			// pager_splash_ad.setOnPageChangeListener(new
			// MypageChangeListener());
			pager_splash_ad.setOnTouchListener(this);
		}
	}

    @Override
    public void findViews() {
        setContentView(R.layout.activity_guide);
        if (mInflater == null) {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        viewMain = LayoutInflater.from(this).inflate(R.layout.view_splash_ad1, null);
        btn = (Button) viewMain.findViewById(R.id.btn);
        initFir();
        btn.setOnClickListener(this);
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if ((lastX - event.getX()) > flaggingWidth && (currentIndex == size - 1) && locker) {
				locker = false;
				gotoMain();
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn:
			gotoMain();
			break;
		default:
			break;
		}
	}

	private void gotoMain() {
		Intent intent = new Intent(GuideActivity.this, MainActivity.class);
		intent.putExtra("FLAG", 0);
		startActivity(intent);
		finish();
	}
}
