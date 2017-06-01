package com.mdj.moudle.address.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mdj.R;
import com.mdj.moudle.address.AddressBean;
import com.mdj.view.ClearEditText;

import java.util.List;


public class AddressWidgetListAdapter extends BaseAdapter {
    private static final String TAG = "AddressListAdapter";

    public interface AddressListEventListener {
		void OnEditButtonClicked(int position);
        void OnRadioButtonClicked(int position);
        void OnSearchAddress(int position);
	}

    private enum TextWatcherType{
        USER_NAME,USER_PHONE,ROUGHLY_ADDRESS,DETAIL_ADDRESS
    }
    private Context context;
	private List<AddressBeanWrapper> dataList;
    private AddressListEventListener listEventListener;
    private AddressBeanWrapper tmpAddressBeanWrapper;
    private AddressTextWatcher textWatcherUserName,textWatcherUserPhone,textWatcherDetailAddress;

/**************************************************************************************************/
    public AddressWidgetListAdapter(Context context) {
        this.context = context;
        tmpAddressBeanWrapper = new AddressBeanWrapper();
        //创建监听器
        textWatcherUserName = new AddressTextWatcher(TextWatcherType.USER_NAME);
        textWatcherUserPhone = new AddressTextWatcher(TextWatcherType.USER_PHONE);
        textWatcherDetailAddress = new AddressTextWatcher(TextWatcherType.DETAIL_ADDRESS);
    }

    public void setListEventListener(AddressListEventListener listEventListener) {
        this.listEventListener = listEventListener;
    }
/*更新tmpAddressBean并返回*/
    public AddressBean updateCurrentBean(AddressBean addressBean) {
        tmpAddressBeanWrapper.getAddressBean().setId(addressBean.getId());
        tmpAddressBeanWrapper.getAddressBean().setName(addressBean.getName());
        tmpAddressBeanWrapper.getAddressBean().setLng(addressBean.getLng());
        tmpAddressBeanWrapper.getAddressBean().setLat(addressBean.getLat());
        return tmpAddressBeanWrapper.getAddressBean();
    }

    //更新数据、清除临时buffer
    public void setDataList(List<AddressBeanWrapper> dataList) {
		this.dataList = dataList;
        for(AddressBeanWrapper wrapper:dataList){ //tmp bean 永远指向选中状态的bean
            if(wrapper.isSelected()){
                tmpAddressBeanWrapper.copyAddressBean(wrapper.getAddressBean());
            }
        }
	}

    public AddressBeanWrapper getSelectAddressBeanWrapper(){
        return tmpAddressBeanWrapper;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
	public int getCount() {
		if (dataList == null) {
			return 0;
		}
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.address_item, null);
			holder = new ViewHolder();

            holder.ivRadio = (ImageView) convertView.findViewById(R.id.ivRadio);
            holder.btnEdit = (ImageButton) convertView.findViewById(R.id.btnEdit);
            holder.rlNormal = (RelativeLayout) convertView.findViewById(R.id.rlNormal);
            holder.rlSearchAddress = (RelativeLayout) convertView.findViewById(R.id.rlSearchAddress);
            holder.llExpand = (LinearLayout) convertView.findViewById(R.id.llExpand);

            holder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            holder.tvUserPhone = (TextView) convertView.findViewById(R.id.tvUserPhone);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            holder.tvAddressLocation = (TextView) convertView.findViewById(R.id.tvAddressLocation);

			holder.etUserName = (ClearEditText) convertView.findViewById(R.id.etUserName);
            holder.etUserPhone = (ClearEditText) convertView.findViewById(R.id.etUserPhone);
            holder.etDetailAddress = (ClearEditText) convertView.findViewById(R.id.etDetailAddress);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
//            if(dataList.size()==1&&dataList.get(0).isNew()){
//                //没有radio按钮
//                holder.ivRadio.setVisibility(View.GONE);//隐藏radio
//                holder.rlNormal.setVisibility(View.GONE);//隐藏normal
//                holder.llExpand.setVisibility(View.VISIBLE);//显示expand
//                //添加监听
//                setEditorWatcher(holder.etUserName,textWatcherUserName);
//                setEditorWatcher(holder.etUserPhone,textWatcherUserPhone);
//                setEditorWatcher(holder.etDetailAddress,textWatcherDetailAddress);
//                return convertView; //直接返回即可
//            }

            if(dataList.size()==1&&dataList.get(0).isNew()){
                holder.ivRadio.setVisibility(View.GONE);//隐藏radio
            }else{
                holder.ivRadio.setVisibility(View.VISIBLE);//显示radio
            }
            if(dataList.get(position).isEditing()){//是编辑状态
                holder.rlNormal.setVisibility(View.GONE);
                holder.llExpand.setVisibility(View.VISIBLE);

                holder.etUserName.setText(dataList.get(position).getAddressBean().getUserName());
                holder.etUserPhone.setText(dataList.get(position).getAddressBean().getUserPhone());
                if(!TextUtils.isEmpty(dataList.get(position).getAddressBean().getName())){
                    holder.tvAddressLocation.setText(dataList.get(position).getAddressBean().getName());
                    holder.tvAddressLocation.setTextColor(context.getResources().getColor(R.color.text_black));
                }

                holder.etDetailAddress.setText(dataList.get(position).getAddressBean().getDoorNum());

                //添加监听
                setEditorWatcher(holder.etUserName,textWatcherUserName);
                setEditorWatcher(holder.etUserPhone,textWatcherUserPhone);
                setEditorWatcher(holder.etDetailAddress,textWatcherDetailAddress);

                holder.rlSearchAddress.setTag(holder);
                holder.rlSearchAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (listEventListener != null) {
                            listEventListener.OnSearchAddress(position);
                            tmpAddressBeanWrapper.getAddressBean().setUserName(holder.etUserName.getText().toString().trim());
                            tmpAddressBeanWrapper.getAddressBean().setUserPhone(holder.etUserPhone.getText().toString().trim());
                            tmpAddressBeanWrapper.getAddressBean().setDoorNum(holder.etDetailAddress.getText().toString().trim());
                        }
                    }
                });
            }else{
                holder.rlNormal.setVisibility(View.VISIBLE);
                holder.llExpand.setVisibility(View.GONE);

                holder.tvUserName.setText(dataList.get(position).getAddressBean().getUserName());
                holder.tvUserPhone.setText(dataList.get(position).getAddressBean().getUserPhone());
                holder.tvAddress.setText(dataList.get(position).getAddressBean().getName() + "  " + dataList.get(position).getAddressBean().getDoorNum());
            }

            if(dataList.get(position).isSelected()){//选中radio状态
                holder.ivRadio.setBackgroundResource(R.mipmap.radio_selected);
            }else{//只有未选中的radio响应点击事件
                holder.ivRadio.setBackgroundResource(R.mipmap.radio_unselected);
                holder.ivRadio.setTag(position);
                holder.ivRadio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = (int) view.getTag();
                        if (listEventListener != null) {
                            listEventListener.OnRadioButtonClicked(position);
                        }
                    }
                });
            }

            holder.btnEdit.setTag(position);
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    if (listEventListener != null) {
                        listEventListener.OnEditButtonClicked(position);
                    }
                }
            });
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "AddressListAdapter Exception: e :" + e.getMessage());
		}
		return convertView;
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


    public class AddressTextWatcher implements TextWatcher {
        private TextWatcherType type;

        public AddressTextWatcher(TextWatcherType type) {
            this.type = type;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            Log.e(TAG, "beforeTextChanged :" + s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            Log.e(TAG, "onTextChanged :" + s.toString());
            switch (type){
                case USER_NAME:
                    tmpAddressBeanWrapper.getAddressBean().setUserName(s.toString());
                    break;

                case USER_PHONE:
                    tmpAddressBeanWrapper.getAddressBean().setUserPhone(s.toString());
                    break;

                case DETAIL_ADDRESS:
                    tmpAddressBeanWrapper.getAddressBean().setDoorNum(s.toString());
                    break;
                default:
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.e(TAG, "beforeTextChanged :" + s.toString());
        }
    }
    public class ViewHolder {
        private TextView tvUserName,tvUserPhone,tvAddress,tvAddressLocation;
        private ClearEditText etUserName,etUserPhone,etDetailAddress;
        private ImageView ivRadio;
        private ImageButton btnEdit;
        private RelativeLayout rlNormal,rlSearchAddress;
        private LinearLayout llExpand;
    }
}
