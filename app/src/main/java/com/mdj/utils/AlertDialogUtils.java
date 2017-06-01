package com.mdj.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdj.R;

/**
 * @author 吴世文
 * @Description:
 */

public class AlertDialogUtils {
    private static Context mContext;
    private static LinearLayout layout;
    private static LayoutInflater inflater;
    private static doSomethingListener DoSomethingListener;

    public static void onPopupButtonClick(Context context, String message, final doSomethingListener mDoSomethingListener) {
        mContext = context;
        DoSomethingListener = mDoSomethingListener;

        // 1. 布局文件转换为View对象
        inflater = LayoutInflater.from(mContext);
        layout = (LinearLayout) inflater.inflate(R.layout.dialog_utlis, null);

        // 2. 新建对话框对象
        final Dialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);


        TextView tvDoSomething = (TextView) layout.findViewById(R.id.tvDoSomething);
        TextView tvMessage = (TextView) layout.findViewById(R.id.tvMessage);
        ImageButton btnClose = (ImageButton) layout.findViewById(R.id.btnClose);

        //Message
        tvMessage.setText(message);

        //确认按钮
        tvDoSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (DoSomethingListener != null) {
                    DoSomethingListener.doSomething();
                }
            }
        });

        //取消按钮
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public interface doSomethingListener {
        void doSomething();
    }
}
