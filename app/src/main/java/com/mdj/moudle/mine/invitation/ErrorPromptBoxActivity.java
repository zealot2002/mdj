package com.mdj.moudle.mine.invitation;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.moudle.BaseActivity;

/**
 * @author 吴世文
 * @Description:
 */
public class ErrorPromptBoxActivity extends BaseActivity implements View.OnClickListener {
    private  ImageButton btnClose;
    private TextView tvInvitation;

    @Override
    public void findViews() {
        setContentView(R.layout.errprompboxactivity);
        btnClose = (ImageButton) findViewById(R.id.btnClose);
        tvInvitation = (TextView) findViewById(R.id.tvInvitation);
        btnClose.setOnClickListener(this);
        String errMsg = getIntent().getStringExtra("ErrMsg");
        tvInvitation.setText(errMsg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnClose :
                finish();
                break;
        }
    }
}
