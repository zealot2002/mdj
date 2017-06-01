package com.mdj.moudle.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.constant.CommonConstant;
import com.mdj.moudle.beautician.MyBeauticianListActivity;
import com.mdj.moudle.home.MainActivity;
import com.mdj.moudle.qrcode.ScanQRCodeActivity;

/**
 * Created by tt on 2016/6/27.
 */
public class TitleWidget extends LinearLayout implements View.OnClickListener{
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;

    private TextView tvTitle,tvLeft,tvRight;
    private View vLine;
    private LinearLayout llTitleBar;
    private ImageButton btnTitleLeft,btnTitleRight;
    private OnClickListener customLeftBtnListener,customRightBtnListener, shareMenuBtnListener;
    private RelativeLayout rlMenuShare;

    /******************************************************************************************************/
    public TitleWidget(Context context){
        this(context,null);
    }
    public TitleWidget(Context context,AttributeSet attrs){
        this(context, attrs, 0);
    }

    public TitleWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.title_bar, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.gravity = Gravity.BOTTOM;
        addView(main, layoutParams);

        vLine = (View)findViewById(R.id.vLine);
        tvLeft = (TextView)findViewById(R.id.tvLeft);
        tvLeft.setOnClickListener(this);
        tvRight = (TextView)findViewById(R.id.tvRight);
        llTitleBar = (LinearLayout)findViewById(R.id.llTitleBar);
        btnTitleLeft = (ImageButton)findViewById(R.id.btnTitleLeft);
        btnTitleRight = (ImageButton)findViewById(R.id.btnTitleRight);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TitleWidget);
        String title = typedArray.getString(R.styleable.TitleWidget_title_text);
        typedArray.recycle();
        initTitle(title);
    }
    public void hideLeftBtn(){
        btnTitleLeft.setVisibility(View.GONE);
    }
    public void hideRightBtn(){
        btnTitleRight.setVisibility(View.GONE);
    }
/*****custome left button start */
    public void setCustomLeftBtnBackground(int resourceId){
        btnTitleLeft.setImageResource(resourceId);
    }

    public void setCustomLeftBtnText(String text){
        tvLeft.setText(text);
        tvLeft.setVisibility(VISIBLE);
    }
    public void setCustomLeftBtnListener(OnClickListener customListener){
        customLeftBtnListener = customListener;
    }
    /*****custome left button end */

    /*****custome right button start */
    public void setCustomRightBtnBackground(int resourceId){
        btnTitleRight.setImageResource(resourceId);
    }

    public void setCustomRightBtnText(String text){
        tvRight.setText(text);
        tvRight.setVisibility(VISIBLE);
    }
    public void setCustomRightBtnListener(OnClickListener customListener){
        customRightBtnListener = customListener;
    }

    /*****custome right button end */

    /*****shareMenu button start */
    public void setShareListener(OnClickListener shareMenuListener){
        shareMenuBtnListener = shareMenuListener;
    }
    /*****shareMenu button end */
    public void setFooterDividersEnabled(boolean enabled){
        if(enabled)
            vLine.setVisibility(View.VISIBLE);
        else
            vLine.setVisibility(View.GONE);
    }
    public void setBackgroundResource(int resid){
        llTitleBar.setBackgroundResource(resid);
    }
    public void setTitle(String title){
        tvTitle.setText(title);
    }
    private void initTitle(String title){
        btnTitleLeft.setOnClickListener(this);
        btnTitleRight.setOnClickListener(this);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLeft:
            case R.id.btnTitleLeft:
                if(customLeftBtnListener!=null){
                    customLeftBtnListener.onClick(v);
                    break;
                }
                try{
                    ((Activity)context).finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case R.id.btnTitleRight:
                if(customRightBtnListener!=null){
                    customRightBtnListener.onClick(v);
                    break;
                }
                onPopupButtonClick(v);
                break;
            default:
                break;
        }
    }

    private RelativeLayout rlMenuMain,rlMenuOrder,rlMenuMine,rlMenuHotLine,rlMenuMyBeautician,rlMenuScan;
    private PopupWindow popup;

    public enum SCREEN_ID{
        SCREEN_MAIN,
        SCREEN_ORDER,
        SCREEN_MY_BEAUTICIAN,
        SCREEN_SCAN,
        SCREEN_SHARE
    }
    private SCREEN_ID currentScreen;
    public void setCurrentScreen(SCREEN_ID currentScreen){
        this.currentScreen = currentScreen;
    }
    public void onPopupButtonClick(View v)
    {
        if(popup != null&&popup.isShowing()){
            return;
        }
        popup = new PopupWindow(context);
		View layout = inflater.inflate(R.layout.popup_window, null);
		popup.setContentView(layout);
		// Set content width and height
		popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		// Closes the popup window when touch outside of it - when looses focus
		popup.setOutsideTouchable(true);
		popup.setFocusable(true);
		// Show anchored to button
		popup.setBackgroundDrawable(new BitmapDrawable());
		popup.showAsDropDown(v);

        getMenuView(layout);
        startShowAnim();//显示展开动画
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                startDismissAnim();//显示消失动画
            }
        });
    }

    private void startShowAnim(){
//        AnimationSet aset = new AnimationSet(true);
//        RotateAnimation rAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_PARENT,1f,Animation.RELATIVE_TO_PARENT,0f);
////设置动画执行过程用的时间,单位毫秒
//        rAnimation.setDuration(1000);
////将动画加入动画集合中
//        aset.addAnimation(rAnimation);
////imageView是要旋转的控件的引用.
//        btnTitleRight.startAnimation(aset);
        btnTitleRight.setImageResource(R.drawable.menu_icon_open_selector);
    }
    private void startDismissAnim(){
        btnTitleRight.setImageResource(R.drawable.menu_icon_selector);
//        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_popup_dismiss);
//        LinearInterpolator lin = new LinearInterpolator();
//        anim.setInterpolator(lin);
    }
    private void getMenuView(View view) {
        rlMenuMain = (RelativeLayout)view.findViewById(R.id.rlMenuMain);
        rlMenuOrder = (RelativeLayout)view.findViewById(R.id.rlMenuOrder);
        rlMenuMine = (RelativeLayout)view.findViewById(R.id.rlMenuMine);
        rlMenuHotLine = (RelativeLayout)view.findViewById(R.id.rlMenuHotLine);
        rlMenuMyBeautician = (RelativeLayout)view.findViewById(R.id.rlMenuMyBeautician);
        rlMenuScan = (RelativeLayout)view.findViewById(R.id.rlMenuScan);
        rlMenuShare = (RelativeLayout) view.findViewById(R.id.rlMenuShare);
        View lastLine = view.findViewById(R.id.lastLine);

        if(currentScreen == SCREEN_ID.SCREEN_MAIN){
            rlMenuMain.setVisibility(View.GONE);
        }else if(currentScreen == SCREEN_ID.SCREEN_ORDER){
            rlMenuOrder.setVisibility(View.GONE);
        }else if(currentScreen == SCREEN_ID.SCREEN_MY_BEAUTICIAN){
            rlMenuMyBeautician.setVisibility(View.GONE);
        }else if(currentScreen == SCREEN_ID.SCREEN_SCAN){
            rlMenuScan.setVisibility(View.GONE);
        }else if(currentScreen == SCREEN_ID.SCREEN_SHARE){
            lastLine.setVisibility(View.VISIBLE);
            rlMenuShare.setVisibility(View.VISIBLE);
        }
        rlMenuMain.setOnClickListener(menuOnClickListener);
        rlMenuOrder.setOnClickListener(menuOnClickListener);
        rlMenuMine.setOnClickListener(menuOnClickListener);
        rlMenuHotLine.setOnClickListener(menuOnClickListener);
        rlMenuMyBeautician.setOnClickListener(menuOnClickListener);
        rlMenuScan.setOnClickListener(menuOnClickListener);
        rlMenuShare.setOnClickListener(menuOnClickListener);
    }

    private OnClickListener menuOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            popup.dismiss();
            Intent intent = null;
            switch (view.getId()) {
                case R.id.rlMenuMain:
                    intent = new Intent(context, MainActivity.class);
                    intent.putExtra(MainActivity.MAIN_FRAGMENT_INDEX, 0);
                    break;

                case R.id.rlMenuOrder:
                    intent = new Intent(context, MainActivity.class);
                    intent.putExtra(MainActivity.MAIN_FRAGMENT_INDEX, 1);
                    break;

                case R.id.rlMenuMine:
                    intent = new Intent(context, MainActivity.class);
                    intent.putExtra(MainActivity.MAIN_FRAGMENT_INDEX, 2);
                    break;

                case R.id.rlMenuHotLine:
                    intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + CommonConstant.MDJ_HOTLINE));
                    break;

                case R.id.rlMenuMyBeautician:
                    intent = new Intent(context, MyBeauticianListActivity.class);
                    break;

                case R.id.rlMenuShare:
                    if(shareMenuBtnListener != null){
                        shareMenuBtnListener.onClick(view);
                    }
                    return;

                case R.id.rlMenuScan:
                    Intent it = new Intent(context, ScanQRCodeActivity.class);
                    ((Activity)context).startActivityForResult(it, CommonConstant.REQUEST_CODE.SCAN_QRCODE.value());
                    return;
//                    intent = new Intent(context, BeautyParlorDetailActivity.class);
//                    break;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    };
}
