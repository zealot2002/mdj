package com.mdj.moudle.address;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.moudle.BaseActivity;
import com.mdj.moudle.address.presenter.EditAddressContract;
import com.mdj.moudle.address.presenter.EditAddressPresenter;
import com.mdj.utils.CommonUtil;
import com.mdj.view.ClearEditText;

public class EditAddressActivity extends BaseActivity implements View.OnClickListener,EditAddressContract.View{
    private static final int REQUEST_CODE_SEARCH_ADDRESS = 1;
    private LinearLayout llEditAddress;
    private EditAddressContract.Presenter presenter;
    private AddressBean addressBean;

    private ClearEditText etDoorNum,etUserName,etUserPhone;
    private TextView tvAddress;
    private CheckBox cbDefault;
    private Button btnSave;

    private AddressTextWatcher textWatcherUserName,textWatcherUserPhone,textWatcherDoorNum;

    /***************************************************方法区**********************************************************/
    @Override
    public void findViews() {
        presenter = new EditAddressPresenter(this);
        setContentView(R.layout.edit_address);
        mContext = this;

        llEditAddress = (LinearLayout)findViewById(R.id.llEditAddress);
        llEditAddress.setOnClickListener(this);

        etDoorNum = (ClearEditText)findViewById(R.id.etDoorNum);
        etUserName = (ClearEditText)findViewById(R.id.etUserName);
        etUserPhone = (ClearEditText)findViewById(R.id.etUserPhone);
        tvAddress = (TextView)findViewById(R.id.tvAddress);

        //创建监听器
        textWatcherUserName = new AddressTextWatcher(TextWatcherType.USER_NAME);
        textWatcherUserPhone = new AddressTextWatcher(TextWatcherType.USER_PHONE);
        textWatcherDoorNum = new AddressTextWatcher(TextWatcherType.DOOR_NUM);

        setEditorWatcher(etUserName,textWatcherUserName);
        setEditorWatcher(etUserPhone,textWatcherUserPhone);
        setEditorWatcher(etDoorNum,textWatcherDoorNum);

        cbDefault = (CheckBox)findViewById(R.id.cbDefault);
        cbDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                addressBean.setIsDefault(b);
            }
        });
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        try{
            addressBean = (AddressBean)getIntent().getSerializableExtra(MyAddressListActivity.EDIT_ADDRESS);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(addressBean == null){
            addressBean = new AddressBean();
        }else{
            updateUI(addressBean);
        }
    }

    private void setEditorWatcher(final ClearEditText editText, final AddressTextWatcher watcher){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    editText.addTextChangedListener(watcher);
                }else{
                    editText.removeTextChangedListener(watcher);
                }
                editText.onFocusChange(view,b);//传递给控件自身
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llEditAddress:
                startActivityForResult(new Intent(mContext,SearchAddressActivity.class), REQUEST_CODE_SEARCH_ADDRESS);
                break;

            case R.id.btnSave:
                if(TextUtils.isEmpty(addressBean.getName())
                        ||TextUtils.isEmpty(addressBean.getUserName())
                        ||TextUtils.isEmpty(addressBean.getUserPhone())
                        ){

                    showShortToast("信息填写不完整");
                }else if(!CommonUtil.isMobileNO(addressBean.getUserPhone())){
                    showShortToast("无效的手机号码");
                }else{
                    presenter.addOrUpdateAddress(addressBean);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void done() {
        setResult(Activity.RESULT_OK);
        showShortToast("保存成功");
        finish();
    }

    @Override
    public void showDisconnect(String msg) {
        showShortToast(msg);
    }

    @Override
    public void updateUI(Object data) {
        addressBean = (AddressBean)data;
        etUserName.setText(addressBean.getUserName());
        etUserPhone.setText(addressBean.getUserPhone());
        tvAddress.setText(addressBean.getName());
        tvAddress.setTextColor(getResources().getColor(R.color.text_black));
        etDoorNum.setText(addressBean.getDoorNum());
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_SEARCH_ADDRESS == requestCode&& Activity.RESULT_OK == resultCode) {
            //保留用户填写的用户名和手机号
            String id = addressBean.getId();
            String userName = addressBean.getUserName();
            String userPhone = addressBean.getUserPhone();
            addressBean = (AddressBean)data.getSerializableExtra(SearchAddressActivity.RESULT_CODE_SEARCH_ADDRESS);
            //恢复数据
            addressBean.setId(id);
            addressBean.setUserPhone(userPhone);
            addressBean.setUserName(userName);
            updateUI(addressBean);
        }
    }
    private enum TextWatcherType{
        USER_NAME,USER_PHONE,DOOR_NUM
    }
    public class AddressTextWatcher implements TextWatcher {
        private TextWatcherType type;

        public AddressTextWatcher(TextWatcherType type) {
            this.type = type;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            switch (type){
                case USER_NAME:
                    addressBean.setUserName(s.toString());
                    break;

                case USER_PHONE:
                    addressBean.setUserPhone(s.toString());
                    break;

                case DOOR_NUM:
                    addressBean.setDoorNum(s.toString());
                    break;
                default:
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
