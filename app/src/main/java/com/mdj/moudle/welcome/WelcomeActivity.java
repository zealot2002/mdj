package com.mdj.moudle.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.mdj.R;
import com.mdj.constant.SPConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.home.MainActivity;
import com.mdj.moudle.userPackage.MyPackageListActivity;
import com.mdj.utils.SPUtils;

public class WelcomeActivity extends BaseActivity {
	private AlphaAnimation start_anima;
	private View view;

/************************************************************************************************/

	public void initData() {
		try {
			start_anima = new AlphaAnimation(0.3f, 1.0f);
			start_anima.setDuration(3000);
			view.startAnimation(start_anima);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setListener() {
		start_anima.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!SPUtils.getBoolean(mContext, SPConstant.IS_FIRST_RUN, false)) {// 非第一次
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra("FLAG", 0);
                    startActivity(intent);
                } else {// 第一次
                    startActivity(new Intent(mContext, GuideActivity.class));
                    SPUtils.putBoolean(mContext, SPConstant.IS_FIRST_RUN, true);
                }
                finish();
            }
        });
    }

    @Override
    public void findViews() {
        mContext = this;
        view = View.inflate(mContext, R.layout.activity_welcome, null);
        setContentView(view);

        //快捷方式：
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            if (name != null) {
                Intent redirectIntent = new Intent();
                redirectIntent.setClass(WelcomeActivity.this, MyPackageListActivity.class);
                redirectIntent.putExtra("name", name);
                startActivity(redirectIntent);
            }
        }
        initData();
        setListener();
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    }

    // 友盟统计
	@Override
	public void onResume() {
		super.onResume();
	
	}

	@Override
	public void onPause() {
		super.onPause();
	
	}
	
}
