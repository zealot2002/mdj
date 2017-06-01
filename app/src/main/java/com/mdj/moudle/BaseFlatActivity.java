package com.mdj.moudle;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.moudle.widget.TitleWidget;

public abstract class BaseFlatActivity extends BaseActivity implements View.OnClickListener {
    /*flat*/
    private TitleWidget titleWidget;
    private LinearLayout llDisconnectTipsLayout;
    public abstract void findViews();

/***************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_flat);
        titleWidget = (TitleWidget)findViewById(R.id.titleWidget);
        llDisconnectTipsLayout = (LinearLayout)findViewById(R.id.llDisconnectTipsLayout);
        llDisconnectTipsLayout.setOnClickListener(this);
        super.onCreate(savedInstanceState);
    }

    protected void setTitle(String title){
        titleWidget.setTitle(title);
    }
    protected void refreshAllData(){};

    protected void showDisconnect(){
        llDisconnectTipsLayout.setVisibility(View.VISIBLE);
    }
    protected void showActivity(){
        llDisconnectTipsLayout.setVisibility(View.GONE);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llDisconnectTipsLayout:
                refreshAllData();
                break;
        }
    }
}
