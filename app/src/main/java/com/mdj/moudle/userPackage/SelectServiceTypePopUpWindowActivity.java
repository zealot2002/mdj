package com.mdj.moudle.userPackage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.constant.BundleConstant;
import com.mdj.moudle.beautyParlor.BeautyParlorListActivity;
import com.mdj.moudle.order.ConfirmOrderInHomeActivity;
import com.mdj.moudle.widget.shoppingCart.GoodsBean;
import com.mdj.moudle.widget.shoppingCart.GoodsWrapperBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectServiceTypePopUpWindowActivity extends Activity implements View.OnClickListener {
    private ImageButton btnClose,btnInHome,btnToShop;
    private TextView tvInHome,tvToShop;
    private GoodsBean goodsBean;
    private String packageId;
/***************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_service_type_popup_window);

        goodsBean = (GoodsBean) getIntent().getSerializableExtra(BundleConstant.GOODS_BEAN);
        packageId = getIntent().getStringExtra(BundleConstant.PACKAGE_ID);

        btnClose = (ImageButton)findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

        btnInHome = (ImageButton)findViewById(R.id.btnInHome);
        btnInHome.setOnClickListener(this);

        btnToShop = (ImageButton)findViewById(R.id.btnToShop);
        btnToShop.setOnClickListener(this);

        tvInHome = (TextView)findViewById(R.id.tvInHome);
        tvToShop = (TextView)findViewById(R.id.tvToShop);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose:
                finish();
                break;

            case R.id.btnInHome: {
                tvInHome.setTextColor(getResources().getColor(R.color.text_black));
                List<GoodsWrapperBean> goodList = new ArrayList<>();
                goodList.add(new GoodsWrapperBean(goodsBean, 1));
                Intent intent = new Intent(this, ConfirmOrderInHomeActivity.class);
                intent.putExtra(BundleConstant.DATA_LIST, (Serializable) goodList);
                intent.putExtra(BundleConstant.PACKAGE_ID,packageId);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btnToShop:
                {
                    tvToShop.setTextColor(getResources().getColor(R.color.text_black));
                    Intent intent = new Intent(this, BeautyParlorListActivity.class);
                    List<GoodsWrapperBean> goodList = new ArrayList<>();
                    goodList.add(new GoodsWrapperBean(goodsBean, 1));
                    intent.putExtra(BundleConstant.DATA_LIST, (Serializable) goodList);
                    intent.putExtra(BundleConstant.PACKAGE_ID,packageId);
                    startActivity(intent);
                    finish();
                    break;
            }

            default:
                break;
        }
    }
}
