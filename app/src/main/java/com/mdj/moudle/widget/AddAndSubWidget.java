package com.mdj.moudle.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mdj.R;
import com.mdj.utils.ToastUtils;

/**
 * Created by tt on 2016/6/27.
 */
public class AddAndSubWidget extends LinearLayout implements View.OnClickListener{
    public static final String BROADCAST_ACTION_OPERATION = "com.vmei.broadcast.AddAndSubWidgetAction";
    public static final String DATA_TAG = "dataTag";
    public static final String ACTION = "action";

    public enum ButtonAction{
        ActionAdd,
        ActionSub,
    }

    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;

    private ImageButton btnSub,btnAdd;
    private EditText etNum;

    private String dataTag;
    private String widgetUser;
    private boolean customCal = false;
/*****************************************************************************************************/
    public AddAndSubWidget(Context context){
        this(context,null);
    }
    public AddAndSubWidget(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public AddAndSubWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.add_and_sub_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);
        initView();
    }

    public void setDataTagAndWidgetUser(String dataTag,String widgetUser){
        this.dataTag = dataTag;
        this.widgetUser = widgetUser;
    }
    public String getDataTag(){
        return dataTag;
    }
    private void initView(){
        btnSub = (ImageButton)findViewById(R.id.btnSub);
        btnAdd = (ImageButton)findViewById(R.id.btnAdd);
        etNum = (EditText)findViewById(R.id.etNum);

        btnSub.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }
/*
* customCal:true    组件不负责计算，只把事件消息传到外部，应该配合updateWidgetValue使用；
* customCal:false   组件负责计算，并且更新value，也把事件消息传到外部
* */
    public void setCustomCalMode(boolean customCal){
        this.customCal = customCal;
    }

    public void setValue(int value){
        if(value == 0){
            etNum.setVisibility(View.GONE);
            btnSub.setVisibility(View.GONE);
            return;
        }
        if(etNum.getVisibility()!=View.VISIBLE){
            etNum.setVisibility(View.VISIBLE);
        }
        if(btnSub.getVisibility()!=View.VISIBLE){
            btnSub.setVisibility(View.VISIBLE);
        }
        etNum.setText(value+"");
    }
    @Override
    public void onClick(View v) {
        try {
            int value = Integer.valueOf(etNum.getText().toString().trim());
            switch (v.getId()) {
                case R.id.btnSub:
                    if(customCal){
                        //do nothing..
                    }else{
                        if(value>0){
                            value--;
                            if(value == 0){
                                btnSub.setVisibility(View.GONE);
                                etNum.setVisibility(View.GONE);
                            }
                            etNum.setText(value+"");
                        }
                    }
                    sendBroadcast(ButtonAction.ActionSub);
                    break;

                case R.id.btnAdd:
                    if(customCal){
                        //do nothing..
                    }else{
                        if(value==99){
                            return;
                        }
                        value++;
                        if(value == 1){//只有等于1的时候，才需要再次打开，否则就是可见的
                            btnSub.setVisibility(View.VISIBLE);
                            etNum.setVisibility(View.VISIBLE);
                        }
                        etNum.setText(value+"");
                    }
                    sendBroadcast(ButtonAction.ActionAdd);
                    break;

                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.showShort(context,"error:"+e.toString());
        }
    }

    private void sendBroadcast(ButtonAction action){
        Intent broadcast = new Intent();
        broadcast.putExtra(DATA_TAG,dataTag);
        broadcast.putExtra(ACTION,action.toString());
        broadcast.setAction(BROADCAST_ACTION_OPERATION+widgetUser);
        context.sendBroadcast(broadcast);
    }
}
