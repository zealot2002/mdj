package com.mdj.moudle.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.mdj.R;
import com.mdj.application.MyApp;
import com.mdj.constant.SPConstant;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.appUpdate.AppUpdateManagement;
import com.mdj.moudle.mine.MineFragment;
import com.mdj.moudle.order.MyOrderListFragment;
import com.mdj.utils.MdjLog;
import com.mdj.utils.SPUtils;
import com.push.PushHelper;

public class MainActivity extends BaseActivity {
    public static final String MAIN_FRAGMENT_INDEX = "main_fragment_index";
	private Fragment[] fragments;
	private Button[] mTabs;
	private int currentTabIndex;
    private LinearLayout tabMenu;

/*******************************************************************************************************/

    private void initFragment() {
        fragments = new Fragment[] { new HomeFragment(), new MyOrderListFragment(), new MineFragment() };
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0]).show(fragments[0]).commitAllowingStateLoss();
    }

    private void initTabButton() {
        mTabs = new Button[3];
        mTabs[0] = (Button) findViewById(R.id.btnMain);
        mTabs[1] = (Button) findViewById(R.id.btnOrder);
        mTabs[2] = (Button) findViewById(R.id.btnMine);

        mTabs[0].setSelected(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int index = intent.getExtras().getInt(MAIN_FRAGMENT_INDEX);
        showFragment(index);
    }
    @Override
    protected void onStart() {
        super.onStart();
        //打开push
        boolean isOpenPush = SPUtils.getBoolean(mContext, SPConstant.OPEN_PUSH, true);
        MdjLog.log("isOpenPush :"+isOpenPush);
        if(isOpenPush){
               PushHelper.getInstance().start();
         }
    }
	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
        int selected = 0;
		switch (view.getId()) {
            case R.id.btnMain:
                selected = 0;
                break;
            case R.id.btnOrder:
                selected = 1;
                break;
            case R.id.btnMine:
                selected = 2;
                break;
            }
        showFragment(selected);
	}

    private void showFragment(int index){
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
        if(index == 0){
            tabMenu.setVisibility(View.GONE);
        }else{
            tabMenu.setVisibility(View.VISIBLE);
        }
    }

	private long exitTime;
	private static final int EXIT_INTERVAL_TIME = 2000;
	
	public void onBackPressed() {
    	if ((System.currentTimeMillis() - exitTime) > EXIT_INTERVAL_TIME) {
    		Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
        	finish();
            MyApp.getInstance().onTerminate();
        }
    }

    @Override
    public void findViews() {
        SDKInitializer.initialize(getApplicationContext());//百度地图sdk init
        AppUpdateManagement.checkAppUpdate(this);
        setContentView(R.layout.activity_main);
        currentTabIndex = 0;
        initTabButton();
        initFragment();

        tabMenu = (LinearLayout)findViewById(R.id.tabMenu);
        showFragment(0);
    }
}
