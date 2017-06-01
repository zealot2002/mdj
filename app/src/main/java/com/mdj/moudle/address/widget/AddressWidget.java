package com.mdj.moudle.address.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mdj.R;
import com.mdj.moudle.address.AddressBean;
import com.mdj.view.MyListView;

import java.util.ArrayList;
import java.util.List;

/*
* 地址小组件
* 它只与它的AddressWidgetAdapter通信（单向），不直接与activity通信
* 倒计时逻辑、手机号和验证码的基本容错，都在组件内部处理
* */
public class AddressWidget extends LinearLayout implements View.OnClickListener,AddressWidgetListAdapter.AddressListEventListener{
    public interface AddressWidgetListener{
        void onSearchAddress(int position);
    }
    private Context context;
    private LayoutInflater inflater;
    private LayoutParams layoutParams;
    private LinearLayout main;

    private MyListView lvAddressList;
    private AddressWidgetListAdapter addressListAdapter;
    private LinearLayout llAddAddress;
    private RelativeLayout rlMore;
    private ImageView ivMore;
    private View lineMore,lineAdd;
    private List<AddressBeanWrapper> addressBeanWrapperList/*实际的数据结构*/,tmpAddressBeanWrapperList/*临时的，供显示使用*/;

    private AddressWidgetListener addressWidgetListener;
/*******************************************************************************************************************************/
    public AddressWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.context = context;
        inflater = LayoutInflater.from(context);
        main = (LinearLayout)inflater.inflate(R.layout.address_widget, null);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(main, layoutParams);

        //移到另外的方法中，由调用者传入
//        getTestAddressData(1);
//        initAddressView();
    }
/*更新一条数据*/
    public void updateData(AddressBean addressBean,int position){
        AddressBean tmpAddressBean = addressListAdapter.updateCurrentBean(addressBean);
        /*更新数据：来自adressBean*/
        addressBeanWrapperList.get(position).getAddressBean().setName(addressBean.getName());
        addressBeanWrapperList.get(position).getAddressBean().setLng(addressBean.getLng());
        addressBeanWrapperList.get(position).getAddressBean().setLat(addressBean.getLat());
        /*更新数据：来自临时buffer*/
        addressBeanWrapperList.get(position).getAddressBean().setUserName(tmpAddressBean.getUserName());
        addressBeanWrapperList.get(position).getAddressBean().setUserPhone(tmpAddressBean.getUserPhone());
        addressBeanWrapperList.get(position).getAddressBean().setDoorNum(tmpAddressBean.getDoorNum());

        addressListAdapter.notifyDataSetChanged();
    }
    public void setDataList(List<AddressBean> addressBeanList){
        tmpAddressBeanWrapperList = new ArrayList<>();
        addressBeanWrapperList = new ArrayList<>();

        for(AddressBean addressBean:addressBeanList){
            addressBeanWrapperList.add(new AddressBeanWrapper(false, false, false,
                    new AddressBean(addressBean.getId(),
                            addressBean.getUserName(),
                            addressBean.getUserPhone(),
                            addressBean.getName(),
                            addressBean.getDoorNum(),
                            addressBean.getLat(),
                            addressBean.getLng(),
                            addressBean.getCityId())));
        }
        /*设置选中第一项*/
        if(addressBeanWrapperList.size()>0){
            addressBeanWrapperList.get(0).setIsSelected(true);
        }
        initAddressView();
    }
    public void setAddressWidgetListener(AddressWidgetListener addressWidgetListener){
        this.addressWidgetListener = addressWidgetListener;
    }
    private void initAddressView() {
        lineMore = (View)findViewById(R.id.lineMore);
        lineAdd = (View)findViewById(R.id.lineAdd);
        llAddAddress = (LinearLayout)findViewById(R.id.llAddAddress);
        rlMore = (RelativeLayout)findViewById(R.id.rlMore);
        ivMore = (ImageView)findViewById(R.id.ivMore);

        if(addressBeanWrapperList.size()==0){
            hideMoreButton();
            hideAddButton();
            addressBeanWrapperList.add(new AddressBeanWrapper(true,true,true,new AddressBean()));//加入一个默认对象
        }else if(addressBeanWrapperList.size()==1){
            showAddButton();
            hideMoreButton();
        }else{
            showAddButton();
            showMoreButton();
        }
        tmpAddressBeanWrapperList.add(addressBeanWrapperList.get(0));//临时list只显示第一个

        lvAddressList = (MyListView)findViewById(R.id.lvAddressList);
        addressListAdapter = new AddressWidgetListAdapter(context);
        addressListAdapter.setDataList(tmpAddressBeanWrapperList);

        lvAddressList.setFooterDividersEnabled(false);//隐藏底部分割线
        lvAddressList.setAdapter(addressListAdapter);
        addressListAdapter.notifyDataSetChanged();
        addressListAdapter.setListEventListener(this);
    }

    private void hideMoreButton(){
        rlMore.setVisibility(View.GONE);
        lineMore.setVisibility(View.GONE);
    }
    private void showMoreButton(){
        rlMore.setVisibility(View.VISIBLE);
        rlMore.setOnClickListener(this);
        lineMore.setVisibility(View.VISIBLE);
    }
    private void hideAddButton(){
        llAddAddress.setVisibility(View.GONE);
        lineAdd.setVisibility(View.GONE);
    }
    private void showAddButton(){
        llAddAddress.setVisibility(View.VISIBLE);
        llAddAddress.setOnClickListener(this);
        lineAdd.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnEditButtonClicked(int position) {
        if(position != tmpAddressBeanWrapperList.size()-1){//选中其他条，则恢复增加按钮
            restoreAddButton();
        }
        for(int i=0;i<addressBeanWrapperList.size();i++){
            addressBeanWrapperList.get(i).setIsSelected(false);
            addressBeanWrapperList.get(i).setIsEditing(false);
        }
        for(int i=0;i<tmpAddressBeanWrapperList.size();i++){
            tmpAddressBeanWrapperList.get(i).setIsSelected(false);
            tmpAddressBeanWrapperList.get(i).setIsEditing(false);
        }
        tmpAddressBeanWrapperList.get(position).setIsEditing(true);
        tmpAddressBeanWrapperList.get(position).setIsSelected(true);
        addressBeanWrapperList.get(position).setIsEditing(true);
        addressBeanWrapperList.get(position).setIsSelected(true);

        addressListAdapter.setDataList(tmpAddressBeanWrapperList);
        addressListAdapter.notifyDataSetChanged();
    }

    private void restoreAddButton(){
        //new出来的 只能是最后一条 remove掉
        if(tmpAddressBeanWrapperList.get(tmpAddressBeanWrapperList.size()-1).isNew()){
            tmpAddressBeanWrapperList.remove(tmpAddressBeanWrapperList.size()-1);
        }
        if(addressBeanWrapperList.get(addressBeanWrapperList.size()-1).isNew()){
            addressBeanWrapperList.remove(addressBeanWrapperList.size()-1);
        }
        //恢复UI
        showAddButton();
    }

    @Override
    public void OnRadioButtonClicked(int position) {
        if(position != tmpAddressBeanWrapperList.size()-1){//选中其他条，则恢复增加按钮
            restoreAddButton();
        }
        if(tmpAddressBeanWrapperList.size()==1){//只有1条的时候，去掉new的item(此时其处于隐藏状态)
            restoreAddButton();
        }
        for(int i=0;i<tmpAddressBeanWrapperList.size();i++){
            tmpAddressBeanWrapperList.get(i).setIsSelected(false);
        }
        tmpAddressBeanWrapperList.get(position).setIsSelected(true);
        addressListAdapter.setDataList(tmpAddressBeanWrapperList);
        addressListAdapter.notifyDataSetChanged();

        //同步数据源
        for(int i=0;i<addressBeanWrapperList.size();i++){
            addressBeanWrapperList.get(i).setIsSelected(false);
        }
        addressBeanWrapperList.get(position).setIsSelected(true);
    }

    @Override
    public void OnSearchAddress(int position) {
        if(addressWidgetListener!=null){
            addressWidgetListener.onSearchAddress(position);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llAddAddress:
                hideAddButton();
                //更新数据源
                for(int i=0;i<addressBeanWrapperList.size();i++){
                    addressBeanWrapperList.get(i).setIsSelected(false);
                    addressBeanWrapperList.get(i).setIsEditing(false);
                }
                addressBeanWrapperList.add(new AddressBeanWrapper(true,true,true,new AddressBean()));
                //重新设置tmp数据
                tmpAddressBeanWrapperList.clear();
                for(AddressBeanWrapper bean:addressBeanWrapperList){
                    tmpAddressBeanWrapperList.add(bean);
                }

                addressListAdapter.setDataList(tmpAddressBeanWrapperList);
                addressListAdapter.notifyDataSetChanged();
                break;

            case R.id.rlMore:
                if(tmpAddressBeanWrapperList.size()==1){//收起状态
                    tmpAddressBeanWrapperList.clear();
                    for(AddressBeanWrapper bean:addressBeanWrapperList){
                        tmpAddressBeanWrapperList.add(bean);
                    }
                    addressListAdapter.setDataList(tmpAddressBeanWrapperList);
                    addressListAdapter.notifyDataSetChanged();
                    ivMore.setBackgroundResource(R.mipmap.arrow_up);
                }else{
                    tmpAddressBeanWrapperList.clear();
                    tmpAddressBeanWrapperList.add(addressBeanWrapperList.get(0));//临时list只显示第一个
                    addressListAdapter.setDataList(tmpAddressBeanWrapperList);
                    addressListAdapter.notifyDataSetChanged();
                    ivMore.setBackgroundResource(R.mipmap.arrow_down);
                }
                break;
            default:
                break;
        }
    }

    public AddressBeanWrapper getSelectAddressBeanWrapper(){
        return addressListAdapter == null?null:addressListAdapter.getSelectAddressBeanWrapper();
    }
}
