package com.mdj.moudle.address.widget;

import com.mdj.moudle.address.AddressBean;

/**
 * Created by tt on 2016/6/12.
 */
public class AddressBeanWrapper {
    private boolean isNew;//是否新增
    private boolean isSelected;//是否选中
    private boolean isEditing;//是否正在编辑中
    private AddressBean addressBean;

    public AddressBeanWrapper(){
        this(false,false,false,new AddressBean());
    }
    public AddressBeanWrapper(boolean isNew,boolean isSelected,boolean isEditing,AddressBean addressBean) {
        this.isNew = isNew;
        this.isSelected = isSelected;
        this.isEditing = isEditing;
        this.addressBean = addressBean;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }

    public AddressBean getAddressBean() {
        return addressBean;
    }

    public void copyAddressBean(AddressBean addressBean){
        this.addressBean.setId(addressBean.getId());
        this.addressBean.setUserName(addressBean.getUserName());
        this.addressBean.setUserPhone(addressBean.getUserPhone());
        this.addressBean.setName(addressBean.getName());
        this.addressBean.setDoorNum(addressBean.getDoorNum());
        this.addressBean.setLat(addressBean.getLat());
        this.addressBean.setLng(addressBean.getLng());
        this.addressBean.setCityId(addressBean.getCityId());
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String toString(){
        return addressBean.toString();
    }

    public void clear() {
        this.isNew = false;
        this.isSelected = false;
        this.isEditing = false;
        this.addressBean = new AddressBean();
    }
}
